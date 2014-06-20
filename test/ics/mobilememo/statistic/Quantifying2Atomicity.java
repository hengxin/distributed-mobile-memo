/**
 * Given an execution satisfying 2-atomicity, the
 * statistics for 2-atomicity including:
 * (1) number of occurrences of "concurrency pattern" (CP)
 * (2) number of occurrences of "old-new inversion" (ONI)
 */
package ics.mobilememo.statistic;

import ics.mobilememo.benchmark.workload.RequestRecord;
import ics.mobilememo.execution.Execution;

public class Quantifying2Atomicity
{
	private Execution execution = null;
	
	/**
	 * constructor of {@link Quantifying2Atomicity}
	 * @param rr_list a list of {@link RequestRecord}s constituting an execution
	 */
	public Quantifying2Atomicity(Execution execution)
	{
		this.execution = execution;
	}
	
	/**
	 * @return number of occurrences of "concurrency pattern" (CP)
	 */
	public int countCP()
	{
		// TODO:
		long read_start_time = 0;
		for (RequestRecord read_request_record : this.execution.getReadRequestRecordList())
		{
			read_start_time = read_request_record.getStartTime();
			
			for (RequestRecord write_request_record : this.execution.getWriteRequestRecordList())
			{
				if (write_request_record.getStartTime() > read_start_time)
					break;
				/**
				 * Condition (1) for "concurrency pattern": the read operation starts during the interval
				 * of a write operation.
				 */
				if (write_request_record.getFinishTime() > read_start_time)
				{
					
				}
			}
		}
		return 0;
	}
	
	public int countONI()
	{
		return 0;
	}
}
