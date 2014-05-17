/**
 * @author hengxin
 * @date 2014-04-22
 * @description record for each request: type (read/write), start time, finish time, 
 *  delay (finish time - start time), and value (with version number)
 */
package ics.mobilememo.benchmark.workload;

import ics.mobilememo.sharedmemory.data.kvs.Key;
import ics.mobilememo.sharedmemory.data.kvs.VersionValue;

public class RequestRecord
{
	// request type: 0 for write; 1 for read
	private int type = -1; 
	private long start_time = 0;
	private long finish_time = 0;
	// delay = finish_time - start_time
	private long delay = 0;	
	private Key key = null;
	private VersionValue vvalue = null;
	
	/**
	 * construct record for a request
	 * @param type 0 for write; 1 for read
	 * @param start start time
	 * @param finish finish time
	 * @param vvalue value (with version number)
	 * 
	 * delay = finish - start will be calculated and recorded
	 */
	public RequestRecord(int type, long start, long finish, Key key, VersionValue vvalue)
	{
		this.type = type;
		this.start_time = start;
		this.finish_time = finish;
		this.delay = this.finish_time - this.start_time;
		this.key = key;
		this.vvalue = vvalue;
	}
	
	/**
	 * String of RequestRecord: type \t start_time \t finish_time \t delay \t key \t vvalue
	 */
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append(this.type).append('\t').append(this.start_time).append('\t')
			.append(this.finish_time).append('\t').append(this.delay).append('\t')
			.append(this.key).append('\t')
			.append(this.vvalue);
		
		return sb.toString();
	}
}
