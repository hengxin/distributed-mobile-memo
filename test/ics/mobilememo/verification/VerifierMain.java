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

	public static void main(String[] args)
	{
		ExecutionLogHandler exe_log_reader = new ExecutionLogHandler("test/executions/execution.txt");
		List<RequestRecord> request_record_list = exe_log_reader.loadRequestRecords();
		System.out.println(new AtomicityVerifier(new Execution(request_record_list)).verifyAtomicity());
	}

}
