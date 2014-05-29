/**
 * @author hengxin
 * @date May 28, 2014
 * @description {@link WriteRequest}
 */
package ics.mobilememo.benchmark.workload;

import ics.mobilememo.sharedmemory.data.kvs.Key;

public class WriteRequest extends Request
{

	/**
	 * constructor of {@link WriteRequest}
	 * @param key {@link Key} to write
	 * @param val value to write
	 */
	public WriteRequest(Key key, String val)
	{
		super(key);
		super.type = Request.WRITE_TYPE;
		super.val = val;
	}

}
