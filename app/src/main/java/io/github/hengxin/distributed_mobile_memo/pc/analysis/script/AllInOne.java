package io.github.hengxin.distributed_mobile_memo.pc.analysis.script;

import android.annotation.TargetApi;
import android.os.Build;

import org.apache.commons.lang3.time.DurationFormatUtils;

import java.io.File;
import java.io.IOException;

import io.github.hengxin.distributed_mobile_memo.pc.PCConstants;
import io.github.hengxin.distributed_mobile_memo.pc.analysis.quantification.quantify2atomicity.ONITriple;
import io.github.hengxin.distributed_mobile_memo.pc.analysis.quantification.quantify2atomicity.Quantifying2Atomicity;
import io.github.hengxin.distributed_mobile_memo.pc.analysis.quantification.quantifyrwn.QuantifyingRWN;
import io.github.hengxin.distributed_mobile_memo.pc.analysis.quantification.quantifyrwn.StalenessViolationMap;
import io.github.hengxin.distributed_mobile_memo.pc.analysis.verification.AtomicityVerifier;
import io.github.hengxin.distributed_mobile_memo.utility.filesys.FilesCombiner;

/**
 * Process the execution analysis tasks all in one,
 * consisting of:
 * (1) collect sub-executions on separate mobile phones and store them in computer disk
 * (2) extract "delay" values from separate sub-executions and combine them
 * (3) combine sub-executions into one
 * (4) verify atomicity and 2-atomicity against the combined execution
 * (5) quantify 2-atomicity (i.e., counting occurrences of old-new inversions)
 * (6) quantifying rwn, if the result of verifying 2-atomicity is {@code false}
 * (7) remove sub-executions in separate mobile phones
 * (8) uninstall apks
 *
 * @author hengxin
 * @date Jul 1, 2014
 */
public class AllInOne {

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void main(String[] args) throws IOException {

        if (args.length != 2) {
            throw new IllegalArgumentException("Parameters: <Path of adb> <PC Dir>");
        }

        String adb_path = args[0];
        String pc_dir = args[1];

        // (1) collect sub-executions on separate mobile phones and store them in computer disk
        System.out.println("[[[ 1. Collecting. ]]]");
        new ExecutionCollector(adb_path).collect(PCConstants.DEFAULT_EXECUTION_SDCARD_DIR, pc_dir);

        // (2) extract "delay" values from separate sub-executions
        System.out.println("[[[ 2. Extracting and combining delay. ]]]");
        new ExecutionDelayExtractor().extract(pc_dir, PCConstants.INDIVIDUAL_EXECUTION_FILE,
                PCConstants.INDIVIDUAL_DELAY_FILE);
        String allinone_delay_file = new FilesCombiner().combine(pc_dir, PCConstants.INDIVIDUAL_DELAY_FILE,
                PCConstants.ALLINONE_DELAY_FILE);
        System.out.println("AllInOne delay is in: " + allinone_delay_file);

        // (3) combine sub-executions into one
        System.out.println("[[[ 3. Combine. ]]]");
        String allinone_exec_file = new FilesCombiner().combine(pc_dir, PCConstants.INDIVIDUAL_EXECUTION_FILE,
                PCConstants.ALLINONE_EXECUTION_FILE);
        System.out.println("AllInOne execution is in: " + allinone_exec_file);

        // (4) verify atomicity and 2-atomicity against the combined execution
        System.out.println("[[[ 4.1 Verifying atomicity. ]]]");
        AtomicityVerifier atomicity_verifier = new AtomicityVerifier(allinone_exec_file);
        System.out.println("Verifying atomicity: " + atomicity_verifier.verifyAtomicity());

        System.out.println("[[[ 4.2 Verifying 2-atomicity. ]]]");
        long start_time = System.currentTimeMillis();
        boolean is_2atomicity = new AtomicityVerifier(allinone_exec_file).verify2Atomicity();
        long finish_time = System.currentTimeMillis();
        System.out.println("Verifying 2-atomicity: " + is_2atomicity + " in " + DurationFormatUtils.formatDurationHMS
                (finish_time - start_time));

        // (5) quantify 2-atomicity and get the number of "concurrency patterns" and "old-new inversions"
        System.out.println("[[[ 5. Quantifying 2-atomicity. ]]]");
        Quantifying2Atomicity quantifer = new Quantifying2Atomicity();

        start_time = System.currentTimeMillis();
        quantifer.quantify(allinone_exec_file);
        finish_time = System.currentTimeMillis();

        System.out.println("Time for quantifying 2-atomicity: " + DurationFormatUtils.formatDurationHMS(finish_time -
                start_time));

        int cp_count = quantifer.getCPCount();
        int oni_count = quantifer.getONICount();

        System.out.println("The number of \"concurrency patterns\" is: " + cp_count);
        System.out.println("The number of \"old new inversions\" is: " + oni_count);
        System.out.println("ONI / CP = " + (oni_count * 1.0) / cp_count);

        String parent_path = new File(allinone_exec_file).getParent();
        // store concurrency patterns
        String cp_file = parent_path + File.separator + PCConstants.CP_FILE_NAME;
        System.out.println("Store concurrency patterns into file: " + cp_file);
        ONITriple.write2File(quantifer.getCPList(), cp_file);

        // store old-new inversions
        String oni_file = parent_path + File.separator + PCConstants.ONI_FILE_NAME;
        System.out.println("Store oni into file: " + oni_file);
        ONITriple.write2File(quantifer.getONIList(), oni_file);

        // (6) quantifying rwn, if the result of verifying 2-atomicity is {@code false}
        if (! is_2atomicity) {
            System.out.println("Quantifying RWN execution ...");
            start_time = System.currentTimeMillis();
            QuantifyingRWN quantifier = new QuantifyingRWN();
            quantifier.quantify(allinone_exec_file);
            finish_time = System.currentTimeMillis();
            System.out.println("Time is: " + DurationFormatUtils.formatDurationHMS(finish_time - start_time));

            StalenessViolationMap violation_map = quantifier.getViolationMap();
            String staleness_file = parent_path + File.separator + PCConstants.STALE_MAP_FILE_NAME;
            violation_map.write2File(staleness_file, true);
            System.out.println("Write StalenessViolationMap into file: " + staleness_file);
        }

        // (7) remove sub-executions in separate mobile phones
        System.out.println("[[[ 6. Remove files. ]]]");
        System.out.println("Remove files in " + PCConstants.DEFAULT_EXECUTION_SDCARD_DIR);
        new ExecutionsRemover(adb_path).remove(PCConstants.DEFAULT_EXECUTION_SDCARD_DIR);

        // (8) uninstall apks
        System.out.println("[[[ 7. Uninstall apks. ]]]");
        new APKUninstaller(adb_path).uninstall(PCConstants.DEFAULT_APK);
    }

}
