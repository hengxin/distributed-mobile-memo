package ics.mobilememo.script;

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
		String destination_directory = new ExecutionCollector().collect();
		
		// (2) sync. separate sub-executions
		new ExecutionTimeSynchronizer(destination_directory).sync();
		
		// (3) extract "delay" values from separate sub-executions
		new ExecutionDelayExtractor(destination_directory).extract();
		
		// (4) combine sub-executions into one
		String combined_execution_file = new SyncedExecutionCombiner(destination_directory).combine();
		
		// (5) verify atomicity and 2-atomicity against the combined execution
		System.out.println("Verifiying atomicity: " + new VerifierMain(combined_execution_file).verifyAtomicity());
		System.out.println("Verifiying 2-atomicity: " + new VerifierMain(combined_execution_file).verifyAtomicity());
		
		/**
		 * clean up
		 */
		
		// (6) remove sub-executions in separate mobile phones
		new ExecutionsRemover().remove();
		
		// (7) uninstall apks
		new APKUninstaller().uninstall();
	}
}
