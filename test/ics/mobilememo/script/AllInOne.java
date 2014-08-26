package ics.mobilememo.script;

import ics.mobilememo.statistic.atomicity.Quantifying2Atomicity;
import ics.mobilememo.verification.AtomicityVerifier;

/**
 * process the execution-related tasks all in one,
 * consisting of:
 * (1) collect sub-executions on separate mobile phones and store them in computer disk
 * (2) sync. separate sub-executions
 * (3) extract "delay" values from separate sub-executions
 * (4) combine sub-executions into one 
 * (5) verify atomicity and 2-atomicity against the combined execution
 * (6) remove sub-executions in separate mobile phones
 * (7) uninstall apks
 * 
 * @author hengxin
 * @date Jul 1, 2014
 */
public class AllInOne
{
	public static void main(String[] args)
	{
		/**
		 *  Combine sub-executions on separate mobile phones into 
		 *  a single execution and verify atomicity/2-atomicity on it 
		 */
		
		// (1) collect sub-executions on separate mobile phones and store them in computer disk
		System.out.println("[[[ 1. Collecting. ]]]");
		String destination_directory = new ExecutionCollector().collect();
		
		// (2) sync. separate sub-executions
//		System.out.println("[[[ 2. Sync. ]]]");
//		new ExecutionTimeSynchronizer(destination_directory).sync();
		
		// (3) extract "delay" values from separate sub-executions
		System.out.println("[[[ 3. Extracting delay. ]]]");
		new ExecutionDelayExtractor(destination_directory).extract();
		new ExecutionDelayCombiner(destination_directory).combine();
		
		// (4) combine sub-executions into one
		System.out.println("[[[ 4. Combine. ]]]");
		String combined_execution_file = new ExecutionCombiner(destination_directory, false).combine();
		
		// (5) verify atomicity and 2-atomicity against the combined execution
		System.out.println("[[[ 5.1 Verifying atomicity. ]]]");
		AtomicityVerifier atomicity_verifier = new AtomicityVerifier(combined_execution_file);
		System.out.println("Verifying atomicity: " + atomicity_verifier.verifyAtomicity());
		System.out.println("Verifying atomicity is done. The number of \"old-new inversion\" is " + atomicity_verifier.getONICount());
		
		System.out.println("[[[ 5.2 Verifying 2-atomicity. ]]]");
		System.out.println("Verifying 2-atomicity: " + new AtomicityVerifier(combined_execution_file).verify2Atomicity());
		
		// (6) quantify 2-atomicity
		System.out.println("[[[ 6. Quantifying 2-atomicity. ]]]");
		Quantifying2Atomicity quantifer = new Quantifying2Atomicity(combined_execution_file);
		quantifer.quantify();
		System.out.println("The number of \"concurrency patterns\" is: " + quantifer.getCPCount());
		System.out.println("The number of \"old new inversions\" is: " + quantifer.getONICount());

		/**
		 * clean up
		 */
		
		// (7) remove sub-executions in separate mobile phones
		System.out.println("[[[ 7. Remove files. ]]]");
		new ExecutionsRemover().remove();
		
		// (8) uninstall apks
		System.out.println("[[[ 8. Uninstall apks. ]]]");
		new APKUninstaller().uninstall();
	}
}
