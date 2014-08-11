package ics.mobilememo.script;

import ics.mobilememo.utility.filesys.FilesCombiner;

/**
 * combine the separately (synchronized or not) sub-executions into one
 * 
 * @author hengxin
 * @date Jul 1, 2014
 */
public class ExecutionCombiner
{
	private final String execution_directory;

	// file containing sub-execution
	private final String single_execution_sync_file_name = "execution_sync.txt";
	private final String single_execution_file_name = "execution.txt";
	private final String EXECUTION_FILE_BE_COMBINED;
	
	// file to store the combined execution
	private final String allinone_execution_file_name = "execution.txt";
	
	/**
	 * Constructor of {@link ExecutionCombiner}
	 *
	 * @param directory directory on which the combination is performed
	 * @param isSynced To combine the synchronized sub-executions (isSynced = true)
	 * 	or the original ones (isSynced = false) 
	 */
	public ExecutionCombiner(final String directory, final boolean isSynced)
	{
		this.execution_directory = directory;
		
		if (isSynced)
			this.EXECUTION_FILE_BE_COMBINED = this.single_execution_sync_file_name;
		else
			this.EXECUTION_FILE_BE_COMBINED = this.single_execution_file_name;
	}

	/**
	 * Combine the separately (synchronized or not) sub-executions into one
	 * 
	 * @return 
	 *  the (absolute) path of the execution file containing combined execution
	 */
	public String combine()
	{
		System.out.println("Combine executions in this directory: " + this.execution_directory);
		String allinone_execution_file_path = new FilesCombiner(this.execution_directory, EXECUTION_FILE_BE_COMBINED, this.allinone_execution_file_name).combine();
		System.out.println("Combination Finished.");

		return allinone_execution_file_path;
	}
	
	/**
	 * Test of {@link ExecutionCombiner}
	 * @param args
	 */
	public static void main(String[] args)
	{
		new ExecutionCombiner("C:\\Users\\ics-ant\\Desktop\\executions\\allinonetest", false).combine();
	}
}
