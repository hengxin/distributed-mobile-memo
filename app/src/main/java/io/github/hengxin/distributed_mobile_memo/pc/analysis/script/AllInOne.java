package io.github.hengxin.distributed_mobile_memo.pc.analysis.script;

import org.apache.commons.lang3.time.DurationFormatUtils;

import java.io.IOException;

import io.github.hengxin.distributed_mobile_memo.pc.analysis.quantification.Quantifying2Atomicity;
import io.github.hengxin.distributed_mobile_memo.pc.analysis.verification.AtomicityVerifier;
import io.github.hengxin.distributed_mobile_memo.utility.filesys.FilesCombiner;

/**
 * Process the execution analysis tasks all in one,
 * consisting of:
 * (1) collect sub-executions on separate mobile phones and store them in computer disk
 * (2) extract "delay" values from separate sub-executions and combine them
 * (3) combine sub-executions into one
 * (4) verify atomicity and 2-atomicity against the combined execution
 * (5) quantify 2-atomicity
 * (6) remove sub-executions in separate mobile phones
 * (7) uninstall apks
 *
 * @author hengxin
 * @date Jul 1, 2014
 */
public class AllInOne {
    public static final String DEFAULT_EXECUTION_SDCARD_DIR = "/sdcard/single_execution";
    public static final String INDIVIDUAL_EXECUTION_FILE = "execution.txt";
    public static final String ALLINONE_EXECUTION_FILE = "execution.txt";
    public static final String INDIVIDUAL_DELAY_FILE = "execution_delay.txt";
    public static final String ALLINONE_DELAY_FILE = "delay.txt";
    public static final String DEFAULT_APK = "io.github.hengxin.distributed_mobile_memo";

    public static void main(String[] args) throws IOException {

        if (args.length != 2) {
            throw new IllegalArgumentException("Parameters: <Path of adb> <PC Dir>");
        }

        String adb_path = args[0];
        String pc_dir = args[1];

        // (1) collect sub-executions on separate mobile phones and store them in computer disk
        System.out.println("[[[ 1. Collecting. ]]]");
        new ExecutionCollector(adb_path).collect(DEFAULT_EXECUTION_SDCARD_DIR, pc_dir);

        // (2) extract "delay" values from separate sub-executions
        System.out.println("[[[ 2. Extracting and combining delay. ]]]");
        new ExecutionDelayExtractor().extract(pc_dir, INDIVIDUAL_EXECUTION_FILE, INDIVIDUAL_DELAY_FILE);
        String allinone_delay_file = new FilesCombiner().combine(pc_dir, INDIVIDUAL_DELAY_FILE,
                ALLINONE_DELAY_FILE);
        System.out.println("AllInOne delay is in: " + allinone_delay_file);

        // (3) combine sub-executions into one
        System.out.println("[[[ 3. Combine. ]]]");
        String allinone_exec_file = new FilesCombiner().combine(pc_dir, INDIVIDUAL_EXECUTION_FILE,
                ALLINONE_EXECUTION_FILE);
        System.out.println("AllInOne execution is in: " + allinone_exec_file);

        // (4) verify atomicity and 2-atomicity against the combined execution
        System.out.println("[[[ 4.1 Verifying atomicity. ]]]");
        AtomicityVerifier atomicity_verifier = new AtomicityVerifier(allinone_exec_file);
        System.out.println("Verifying atomicity: " + atomicity_verifier.verifyAtomicity());

        System.out.println("[[[ 4.2 Verifying 2-atomicity. ]]]");
        System.out.println("Verifying 2-atomicity: " + new AtomicityVerifier(allinone_exec_file).verify2Atomicity());

        // (5) quantify 2-atomicity and get the number of "concurrency patterns" and "old-new inversions"
        System.out.println("[[[ 5. Quantifying 2-atomicity. ]]]");
        Quantifying2Atomicity quantifer = new Quantifying2Atomicity(allinone_exec_file);

        long start_time = System.currentTimeMillis();
        quantifer.quantify();
        long finish_time = System.currentTimeMillis();
        long quantify_time = finish_time - start_time;

        System.out.println("Time for quantifying 2-atomicity: " + DurationFormatUtils
                .formatDuration(quantify_time, "HH:mm:ss.S"));
        System.out.println("The number of \"concurrency patterns\" is: " + quantifer.getCPCount());
        System.out.println("The number of \"old new inversions\" is: " + quantifer.getONICount());

        // (6) remove sub-executions in separate mobile phones
        System.out.println("[[[ 6. Remove files. ]]]");
        System.out.println("Remove files in " + DEFAULT_EXECUTION_SDCARD_DIR);
        new ExecutionsRemover(adb_path).remove(DEFAULT_EXECUTION_SDCARD_DIR);

        // (7) uninstall apks
        System.out.println("[[[ 7. Uninstall apks. ]]]");
        new APKUninstaller(adb_path).uninstall(DEFAULT_APK);
    }
}
