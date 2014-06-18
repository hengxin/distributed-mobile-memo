/**
 * verification of atomicity with the following constraints
 * 1. the execution includes a single writer
 * 2. write distinct values
 */
package ics.mobilememo.verification;

import ics.mobilememo.benchmark.workload.Request;
import ics.mobilememo.benchmark.workload.RequestRecord;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class AtomicityVerifier
{
	/**
	 * the execution to be verified is represented by a list of {@link RequestRecord}s
	 */
	private List<RequestRecord> request_record_list = new ArrayList<>();
	
	private List<RequestRecord> write_request_record_list;
	
	private List<RequestRecord> read_request_record_list;
	
	/**
	 * constructor of {@link AtomicityVerifier}
	 * @param request_record_list
	 */
	public AtomicityVerifier(List<RequestRecord> request_record_list)
	{
		this.request_record_list = request_record_list;
		
		/**
		 * sort the list of {@link RequestRecord}s by their start-time
		 * @see RequestRecord#compareTo(RequestRecord)
		 */
		Collections.sort(this.request_record_list);
		
		this.splitByType();
	}
	
	/**
	 * split the list of requests according to their types
	 */
	private void splitByType()
	{
		Iterator<RequestRecord> iter = this.request_record_list.iterator();
		
		while (iter.hasNext())
		{
			RequestRecord requestRecord = (RequestRecord) iter.next();
			if (requestRecord.getType() == Request.WRITE_TYPE)
				this.write_request_record_list.add(requestRecord);
			else
				this.read_request_record_list.add(requestRecord);
		}
	}
	
	/**
	 * verify atomicity against execution represented by {@link #request_record_list}
	 * @return <code>true</code> if the execution (represented by {@link #request_record_list}) satisfies atomicity;
	 * 	<code>false</code>, otherwise.
	 */
	public boolean verify()
	{
		Iterator<RequestRecord> read_outer_iter = this.read_request_record_list.iterator();
		RequestRecord cur_read_request_record = null;
		
		while (read_outer_iter.hasNext())
		{
			// for each read operation
			cur_read_request_record = (RequestRecord) read_outer_iter.next();
			
			// no read call can return some value out of thin air
			if (this.isValueFromNowhere(cur_read_request_record))
				return false;
			
			// no read call returns a value from the distinct past, that is,
			// one that precedes the most recently written non-overlapping value 
			if (this.isValueOverwritten(cur_read_request_record))
				return false;
			
			// check old-new-inversion
			if (this.hasOldNewInversion(cur_read_request_record))
				return false;
		}
		
		return true;
	}
	
	/**
	 * is the version (value) returned by the read operation 
	 * (specified by the @param cur_read_request_record) out of thin air?
	 * 
	 * In other words, an execution of an atomic register should satisfy the following
	 * two conditions:
	 * (1) no read call returns a value which has not been written at all
	 * (2) no read call returns a value from the future:
	 *   it is never the case that r^{i} precede w^{i}
	 * @param cur_read_request_record
	 *   the read operation of type {@link RequestRecord} to check
	 * @return
	 *   <code>true</code> if the above two conditions are violated;
	 *   <code>false</code>, otherwise.
	 */
	private boolean isValueFromNowhere(RequestRecord cur_read_request_record)
	{
		Iterator<RequestRecord> write_iter = this.write_request_record_list.iterator();
		RequestRecord pre_write_request_record = null;
		
		while (write_iter.hasNext())
		{
			pre_write_request_record = write_iter.next();
			
			/**
			 * no read call returns a value from the future:
			 * it is never the case that r^{i} precede w^{i}
			 */
			if (cur_read_request_record.precedes(pre_write_request_record))
				return true;
			
			if (pre_write_request_record.getKey().equals(cur_read_request_record.getKey()) 
					&& pre_write_request_record.getVersion().equals(cur_read_request_record.getVersion()))
				return false;
		}

		return true;
	}
	
	/**
	 * no read call returns a value from the distinct past, that is,
	 * one that precedes the most recently written non-overlapping value:
	 * it is never the case that for some j: w^{i} precedes w^{j} precedes r
	 * 
	 * Notice: this function assume that no read call returns a value out of 
	 * thin air. That is, {@link #isValueFromNowhere(RequestRecord)} = false.
	 * 
	 * @param cur_read_request_record
	 * 	a read operation of type {@link RequestRecord} to check
	 * @return
	 *  <code>true</code> if the read operation has returned a overwritten value;
	 *  <code>false</code>, otherwise.
	 */
	private boolean isValueOverwritten(RequestRecord cur_read_request_record)
	{
		Iterator<RequestRecord> write_iter = this.write_request_record_list.iterator();
		RequestRecord pre_write_request_record = null;
		
		while (write_iter.hasNext())
		{
			pre_write_request_record = (RequestRecord) write_iter.next();
			
			/**
			 * We assume that {@link #isValueFromNowhere(cur_read_request_record)} = false.
			 * Therefore the following "if" condition must be satisfied for some "pre_write_request_record".
			 */
			if (pre_write_request_record.getKey().equals(cur_read_request_record.getKey()) 
					&& pre_write_request_record.getVersion().equals(cur_read_request_record.getVersion()))
			{
				// check the "overwritten" condition
				if (write_iter.hasNext())
				{
					pre_write_request_record = write_iter.next();
					if (pre_write_request_record.precedes(cur_read_request_record))
						return true;
					else
						return false;
				}
				else
					return false;
			}
		}
		
		return false;
	}
	
	/**
	 * Old-new inversion should be avoided in the executions of an atomic register.
	 * Formally, if r^{i} precedes r^{j} => i <= j.
	 * This condition states that an earlier read cannot return a value later than 
	 * that returned by a later read. Otherwise we can it an old-new inversion.
	 * 
	 * @param cur_read_request_record 
	 * 	a read operation of type {@link RequestRecord} to check
	 * @return <code>true</code> if an inversion occurs; <code>false</code>, otherwise
	 */
	private boolean hasOldNewInversion(RequestRecord cur_read_request_record)
	{
		// check the read operations
		Iterator<RequestRecord> read_iter = this.read_request_record_list.iterator();
		RequestRecord pre_read_request_record = null;
		
		while (read_iter.hasNext())
		{
			pre_read_request_record = (RequestRecord) read_iter.next();
			
			// only need to check the requests which start before it 
			if (pre_read_request_record == cur_read_request_record)
				break;
			
			// skip the (read) operations which perform on different keys; Key is a String.
			if (! cur_read_request_record.getKey().equals(pre_read_request_record.getKey()))
				continue;
			
			// check the condition: if r^{i} precedes r^{j} => i <= j.
			if (pre_read_request_record.precedes(cur_read_request_record))
				if (pre_read_request_record.getVersion().compareTo(cur_read_request_record.getVersion()) > 0)
					return true;
		}
		
		return false;
	}
}
