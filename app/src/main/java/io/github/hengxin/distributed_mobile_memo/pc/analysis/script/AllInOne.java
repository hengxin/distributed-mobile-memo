package io.github.hengxin.distributed_mobile_memo.pc.analysis.script;

import io.github.hengxin.distributed_mobile_memo.pc.analysis.quantification.Quantifying2Atomicity;
import io.github.hengxin.distributed_mobile_memo.pc.analysis.verification.AtomicityVerifier;

/**
 * process the execution-related tasks all in one,
 * consisting of:
 * (1) collect sub-executions on separate mobile phones and store them in computer disk
 * (2) extract "delay" values from separate sub-executions
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
    public static void main(String[] args) {
        /**
         *  Combine sub-executions on separate mobile phones into
         *  a single execution and verify atomicity/2-atomicity on it
         */

        // (1) collect sub-executions on separate mobile phones and store them in computer disk
        System.out.println("[[[ 1. Collecting. ]]]");
        ExecutionCollector executionCollector = new ExecutionCollector();
        executionCollector.collect();
        String execDir = executionCollector.getExecDir();

        // (2) extract "delay" values from separate sub-executions
        System.out.println("[[[ 3. Extracting delay. ]]]");
        new ExecutionDelayExtractor(execDir).extract();
        new ExecutionDelayCombiner(execDir).combine();

        // (3) combine sub-executions into one
        System.out.println("[[[ 4. Combine. ]]]");
        String combined_execution_file = new ExecutionCombiner(execDir, false).combine();

        // (4) verify atomicity and 2-atomicity against the combined execution
        System.out.println("[[[ 5.1 Verifying atomicity. ]]]");
        AtomicityVerifier atomicity_verifier = new AtomicityVerifier(combined_execution_file);
        System.out.println("Verifying atomicity: " + atomicity_verifier.verifyAtomicity());

        System.out.println("[[[ 5.2 Verifying 2-atomicity. ]]]");
        System.out.println("Verifying 2-atomicity: " + new AtomicityVerifier(combined_execution_file).verify2Atomicity());

        // (5) quantify 2-atomicity and get the number of "concurrency patterns" and "old-new inversions"
        System.out.println("[[[ 6. Quantifying 2-atomicity. ]]]");
        Quantifying2Atomicity quantifer = new Quantifying2Atomicity(combined_execution_file);
        quantifer.quantify();
        System.out.println("The number of \"concurrency patterns\" is: " + quantifer.getCPCount());
        System.out.println("The number of \"old new inversions\" is: " + quantifer.getONICount());

        /**
         * clean up
         */

        // (6) remove sub-executions in separate mobile phones
        System.out.println("[[[ 7. Remove files. ]]]");
        new ExecutionsRemover().remove();

        // (7) uninstall apks
        System.out.println("[[[ 8. Uninstall apks. ]]]");
        new APKUninstaller().uninstall();
    }
}
