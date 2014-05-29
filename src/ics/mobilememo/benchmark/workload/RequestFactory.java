/**
 * @author hengxin
 * @date May 28, 2014
 * @description Generate {@link Request} according to its type (Write[0], Read[1]) 
 */
package ics.mobilememo.benchmark.workload;

import ics.mobilememo.sharedmemory.data.kvs.Key;

import java.util.Random;


public enum RequestFactory
{
	INSTANCE;
	
	public static final int KEY_RANGE = 100;
	
	/**
	 * generate a {@link Request} according to the type (W[0], R[1])
	 * @param type type of the {@link Request} to generate
	 * @return {@link Request} generated
	 */
	public Request generateRequest(int type)
	{
		int key_num = new Random().nextInt(RequestFactory.KEY_RANGE);
		String key_str = "KEY_" + key_num;
		Key key = new Key(key_str);
		
		if (type == Request.READ_TYPE)	// generate a Read request
			return new ReadRequest(key);
		else
			return new WriteRequest(key, 
					String.valueOf(new Random().nextInt(RequestFactory.KEY_RANGE)));
	}
}
