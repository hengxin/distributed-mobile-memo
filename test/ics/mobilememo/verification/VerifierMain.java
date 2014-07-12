/**
 * run the verification of atomicity
 */
package ics.mobilememo.verification;

import ics.mobilememo.benchmark.workload.RequestRecord;
import ics.mobilememo.execution.Execution;
import ics.mobilememo.execution.ExecutionLogHandler;

import java.util.List;

public class VerifierMain
{
	private String execution_file_to_verify = null;
	
	/**
	 * Constructor of {@link VerifierMain}
	 * @param file path of the file containing the execution to be verified
	 */
	public VerifierMain(String file)
	{
		this.execution_file_to_verify = file;
	}
	
	/**
	 * Verify atomicity against an execution contained in the file
	 * specified by {@link #execution_file_to_verify}
	 * @return <code>true<code> if the execution satisfies atomicity;
	 *  <code>false<code>, otherwise.
	 */
	public boolean verifyAtomicity()
	{
		ExecutionLogHandler exe_log_reader = new ExecutionLogHandler(this.execution_file_to_verify);
		List<RequestRecord> request_record_list = exe_log_reader.loadRequestRecords();
		boolean verify_result = new AtomicityVerifier(new Execution(request_record_list)).verifyAtomicity();
		
		return verify_result;
	}
	
	/**
	 * Verify 2-atomicity against an execution contained in the file
	 * specified by {@link #execution_file_to_verify}
	 * @return <code>true<code> if the execution satisfies 2-atomicity;
	 *  <code>false<code>, otherwise.
	 */
	public boolean verify2Atomicity()
	{
		ExecutionLogHandler exe_log_reader = new ExecutionLogHandler(this.execution_file_to_verify);
		List<RequestRecord> request_record_list = exe_log_reader.loadRequestRecords();
		boolean verify_result = new AtomicityVerifier(new Execution(request_record_list)).verify2Atomicity();
		
		return verify_result;
	}
	
	public static void main(String[] args)
	{
//		new VerifierMain("C:\\Users\\ics-ant\\Desktop\\executions\\SWMR\\execution-0703-2108\\execution.txt").verifyAtomicity();
//		new VerifierMain("C:\\Users\\ics-ant\\Desktop\\executions\\SWMR\\execution-0703-2108\\execution.txt").verify2Atomicity();
		new VerifierMain("C:\\Users\\ics-ant\\Desktop\\executions\\SWMR-2Atomicity\\execution-0712-1713\\execution.txt").verify2Atomicity();
	}
	
}
