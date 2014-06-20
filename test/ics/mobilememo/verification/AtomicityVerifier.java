/**
 * Verification of atomicity with the following constraints
 * 1. the execution includes a single writer
 * 2. write distinct values
 * 
 * It includes both atomicity verifier and 2-atomicity verifier.
 * 
 * For the atomicity verifier ( @see method verifyAtomicity() ) :
 * The principle of the atomicity verifier is referred to the book
 * (section 4.1, page 76) "The Art of Multiprocessor Programming".
 * In the case with a single writer, there is a natural total order 
 * on the Write operations.
 * Three conditions are sufficient and necessary:
 * (1) no read operation returns a value out of thin air. ( @see method #isValueFromNowhere() )
 *   (1.1) no read operation returns a value which has not been written at all
 *   (1.2) no read operation returns a value from the future
 * (2) no read operation returns an overwritten value. ( @see method #isValueOverwritten() ) 
 * (3) an earlier read cannot return a value later than that returned by a later read.
 *   ( @see method #hasOldNewInversion())
 *   
 * For the 2-atomicity verifier ( @see method verify2Atomicity() ):
 * For an execution to be 2-atomicity, 
 * (1) the first condition mentioned above must still be hold.
 * (2) for general executions, a read operation (r^{i}) satisfying 2-atomicity is allowed 
 *   to return an overwritten value, provided that there is only one Write operation between
 *   r^{i} and its corresponding Write w^{i}. However, due to the quorum-implementation
 *   of 2-atomicity, we do not want this to happen. Therefore, we keep the second condition
 *   the same.
 * (3) in a 2-atomicity execution, a read operation is allowed to return a stale value.
 * However, the staleness is (highly) bounded: any <it>preceding</it> read operations cannot
 * return a value <it>more than 2 versions</it> later than that returned by a later read. 
 * Otherwise, we call that the read operation has a <bold>bad</bold> old-new inversion.
 * ( @see method #hasBadOldNewInversion() ) 
 * 
 */
package ics.mobilememo.verification;

import ics.mobilememo.benchmark.workload.Request;
import ics.mobilememo.benchmark.workload.RequestRecord;
import ics.mobilememo.benchmark.workload.RequestTypeNotDefinedException;
import ics.mobilememo.sharedmemory.data.kvs.Version;

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
	private List<RequestRecord> write_request_record_list = new ArrayList<>();
	private List<RequestRecord> read_request_record_list = new ArrayList<>();
	
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
		
		try
		{
			this.splitByType();
		} catch (RequestTypeNotDefinedException rtnde)
		{
			rtnde.printStackTrace();
		}
	}
	
	/**
	 * split the list of requests according to their types
	 * @throws RequestTypeNotDefinedException 
	 */
	private void splitByType() throws RequestTypeNotDefinedException
	{
		Iterator<RequestRecord> iter = this.request_record_list.iterator();
		RequestRecord request_record = null;
		
		while (iter.hasNext())
		{
			request_record = (RequestRecord) iter.next();
			int type = request_record.getType();
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
	
	/**
	 * verify atomicity against execution represented by {@link #request_record_list}
	 * @return <code>true</code> if the execution (represented by {@link #request_record_list}) satisfies atomicity;
	 * 	<code>false</code>, otherwise.
	 */
	public boolean verifyAtomicity()
	{
		Iterator<RequestRecord> read_iter = this.read_request_record_list.iterator();
		RequestRecord cur_read_request_record = null;
		
		while (read_iter.hasNext())
		{
			// for each read operation
			cur_read_request_record = (RequestRecord) read_iter.next();
			
			// no read operation can return some value out of thin air
			if (this.isValueFromNowhere(cur_read_request_record))
			{	
				System.err.println(cur_read_request_record.toString() + " reads value from nowhere.");
				return false;
			}
			// no read operation returns a value from the distinct past, that is,
			// one that precedes the most recently written non-overlapping value 
			if (this.isValueOverwritten(cur_read_request_record))
			{
				System.err.println(cur_read_request_record.toString() + " reads overwritten value.");
				return false;
			}
			
			// check old-new-inversion
			if (this.hasOldNewInversion(cur_read_request_record))
			{
				System.err.println(cur_read_request_record.toString() + " has old new inversion.");
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * verify 2-atomicity against an execution represented by {@link #request_record_list}
	 * @return <code>true</code>, if such execution satisfies 2-atomicity;
	 * 	<code>false</code>, otherwise.
	 */
	public boolean verify2Atomicity()
	{
		Iterator<RequestRecord> read_iter = this.read_request_record_list.iterator();
		RequestRecord cur_read_request_record = null;
		
		while (read_iter.hasNext())
		{
			// for each read operation
			cur_read_request_record = (RequestRecord) read_iter.next();
			
			// no read operation can return some value out of thin air
			if (this.isValueFromNowhere(cur_read_request_record))
			{	
				System.err.println(cur_read_request_record.toString() + " reads value from nowhere.");
				return false;
			}
			// no read operation returns a value from the distinct past, that is,
			// one that precedes the most recently written non-overlapping value 
			if (this.isValueOverwritten(cur_read_request_record))
			{
				System.err.println(cur_read_request_record.toString() + " reads overwritten value.");
				return false;
			}
			
			// check "bad" old-new-inversion
			if (this.hasBadOldNewInversion(cur_read_request_record))
			{
				System.err.println(cur_read_request_record.toString() + " has BAD old new inversion.");
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * is the version (value) returned by the read operation 
	 * (specified by the @param cur_read_request_record) out of thin air?
	 * 
	 * In other words, an execution of an atomic register should satisfy the following
	 * two conditions:
	 * (1) no read operation returns a value which has not been written at all
	 * (2) no read operation returns a value from the future:
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
			 * no read operation returns a value from the future:
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
	 * no read operation returns a value from the distinct past, that is,
	 * one that precedes the most recently written non-overlapping value:
	 * it is never the case that for some j: w^{i} precedes w^{j} precedes r
	 * 
	 * Notice: this function assume that no read operation returns a value out of 
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
	
	/**
	 * Violation of Condition (3) for 2-atomicity:
	 * (3) in a 2-atomicity execution, a read operation is allowed to return a stale value.
	 * However, the staleness is (highly) bounded: any <it>preceding</it> read operations cannot
	 * return a value <it>more than 2 versions</it> later than that returned by a later read. 
	 * Otherwise, we call that the read operation has a <bold>bad</bold> old-new inversion.
	 * 
	 * @param cur_read_request_record read operation to check
	 * @return <code>true</code>, if this operation specified by @param cur_read_request_record 
	 *   has "bad" old-new inversion; <code>false</code>, otherwise. 
	 */
	private boolean hasBadOldNewInversion(RequestRecord cur_read_request_record)
	{
		// check the read operations
		Iterator<RequestRecord> read_iter = this.read_request_record_list.iterator();
		RequestRecord pre_read_request_record = null;
		
		Version cur_read_version = null;
		Version pre_read_version = null;
		
		while (read_iter.hasNext())
		{
			pre_read_request_record = (RequestRecord) read_iter.next();
			
			// only need to check the requests which start before it 
			if (pre_read_request_record == cur_read_request_record)
				break;
			
			// skip the (read) operations which perform on different keys; Key is a String.
			if (! cur_read_request_record.getKey().equals(pre_read_request_record.getKey()))
				continue;
			
			// any <it>preceding</it> read operations cannot 
			// return a value <it>more than 2 versions</it> later than that returned by a later read. 
			if (pre_read_request_record.precedes(cur_read_request_record))
			{
				cur_read_version = cur_read_request_record.getVersion();
				pre_read_version = pre_read_request_record.getVersion();
				int cur_read_version_seqno = cur_read_version.getSeqno();
				int pre_read_version_seqno = pre_read_version.getSeqno();
				
				if (pre_read_version_seqno - cur_read_version_seqno >= 2)
					return true;
			}
		}
		
		return false;
	}
}
