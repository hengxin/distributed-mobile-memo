/**
 * run the verification of atomicity
 */
package ics.mobilememo.verification;

import ics.mobilememo.benchmark.workload.RequestRecord;

import java.util.List;

public class VerifierMain
{

	public static void main(String[] args)
	{
		ExecutionLogReader exe_log_reader = new ExecutionLogReader("test/execution.txt");
		List<RequestRecord> request_record_list = exe_log_reader.loadRequestRecords();
		System.out.println(new AtomicityVerifier(request_record_list).verifyAtomicity());
	}

}
