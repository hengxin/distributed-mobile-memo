/**
 * @author hengxin
 * @date May 28, 2014
 * @description {@ReadRequest}
 */
package ics.mobilememo.benchmark.workload;

import ics.mobilememo.sharedmemory.data.kvs.Key;

public class ReadRequest extends Request
{

	/**
	 * constructor of {@link ReadRequest}
	 * @param key {@link Key} to read
	 */
	public ReadRequest(Key key)
	{
		super(key);
		super.type = Request.READ_TYPE;
	}

}
