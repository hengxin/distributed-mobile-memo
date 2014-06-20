/**
 * @author hengxin
 * @date Jun 20, 2014
 * @description An execution consists of a list of {@link RequestRecord}s.
 *  This class provides a pre-processing of executions, including sorting,
 *  extracting READ operations, extracting WRITE operations.
 */
package ics.mobilememo.execution;

import ics.mobilememo.benchmark.workload.Request;
import ics.mobilememo.benchmark.workload.RequestRecord;
import ics.mobilememo.benchmark.workload.RequestTypeNotDefinedException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Execution
{
	private List<RequestRecord> request_record_list = new ArrayList<>();
	private List<RequestRecord> write_request_record_list = new ArrayList<>();
	private List<RequestRecord> read_request_record_list = new ArrayList<>();
	
	public Execution(List<RequestRecord> rr_list)
	{
		this.request_record_list = rr_list;
		
		/**
		 * sort the list of {@link RequestRecord}s by their start-time
		 * @see RequestRecord#compareTo(RequestRecord)
		 */
		Collections.sort(this.request_record_list);
		
		try
		{
			this.splitByType();
		} catch (RequestTypeNotDefinedException rtnde)
		{
			rtnde.printStackTrace();
		}
	}
	
	/**
	 * @return {@link #write_request_record_list} consisting only WRITE {@link RequestRecord}s
	 *  extracted from {@link #request_record_list}
	 */
	public List<RequestRecord> getWriteRequestRecordList()
	{
		return this.write_request_record_list;
	}
	
	/**
	 * @return {@link #read_request_record_list} consisting only READ {@link RequestRecord}s
	 *  extracted from {@link #request_record_list}
	 */
	public List<RequestRecord> getReadRequestRecordList()
	{
		return this.read_request_record_list;
	}
	
	/**
	 * split the list of requests into two sublists according to their types
	 * 
	 * @throws RequestTypeNotDefinedException
	 */
	private void splitByType() throws RequestTypeNotDefinedException
	{
		int type = -1;
		
		for (RequestRecord request_record : this.request_record_list)
		{
			type = request_record.getType();
			switch (type)
			{
				case Request.WRITE_TYPE:
					this.write_request_record_list.add(request_record);
					break;
				case Request.READ_TYPE:
					this.read_request_record_list.add(request_record);
					break;
				default:
					throw new RequestTypeNotDefinedException("No such request type: " + type);
			}
		}
	}
}
