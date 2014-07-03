package ics.mobilememo.script;

import ics.mobilememo.verification.VerifierMain;

/**
 * process the execution-related tasks all in one,
 * consisting of:
 * (1) collect
 * (2) sync.
 * (3) combine 
 * (4) verify
 * (5) remove
 * (6) uninstall 
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
		
		// (3) combine sub-executions into one
		String combined_execution_file = new SyncedExecutionCombiner(destination_directory).combine();
		
		// (4) verify atomicity and 2-atomicity against the combined execution
		System.out.println("Verifiying atomicity: " + new VerifierMain(combined_execution_file).verifyAtomicity());
		System.out.println("Verifiying 2-atomicity: " + new VerifierMain(combined_execution_file).verifyAtomicity());
		
		/**
		 * clean up
		 */
		
		// (5) remove sub-executions in separate mobile phones
		new ExecutionsRemover().remove();
		
		// (6) uninstall apks
		new APKUninstaller().uninstall();
	}
}
