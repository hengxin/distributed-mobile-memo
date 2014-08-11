package ics.mobilememo.script;

import ics.mobilememo.verification.AtomicityVerifier;
import ics.mobilememo.verification.VerifierMain;

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
		
		// (4) combine sub-executions into one
		System.out.println("[[[ 4. Combine. ]]]");
		String combined_execution_file = new ExecutionCombiner(destination_directory, false).combine();
		
		// (5) verify atomicity and 2-atomicity against the combined execution
		System.out.println("[[[ 5. Verifying atomicity. ]]]");
		AtomicityVerifier atomicity_verifier = new AtomicityVerifier(combined_execution_file);
		System.out.println("Verifying atomicity: " + atomicity_verifier.verifyAtomicity());
		System.out.println("Verifying atomicity is done. The number of \"old-new inversion\" is " + atomicity_verifier.getONICount());
		
		System.out.println("[[[ 6. Verifying 2-atomicity. ]]]");
		System.out.println("Verifying 2-atomicity: " + new AtomicityVerifier(combined_execution_file).verify2Atomicity());
		
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
