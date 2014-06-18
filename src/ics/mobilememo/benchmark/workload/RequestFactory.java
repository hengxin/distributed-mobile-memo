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
	
	/**
	 * generate a {@link Request} according to the type (W[0], R[1])
	 * @param type type of the {@link Request} to generate
	 * @param key_range
	 * 	range of keys
	 * @param value_range
	 *  range of values
	 * @return {@link Request} generated
	 * @throws RequestTypeNotDefinedException 
	 */
	public Request generateRequest(int type, int key_range, int value_range) throws RequestTypeNotDefinedException
	{
		int key_num = new Random().nextInt(key_range);
		String key_str = "KEY_" + key_num;
		Key key = new Key(key_str);
		
		switch (type)
		{
			case Request.READ_TYPE:
				return new ReadRequest(key);

			case Request.WRITE_TYPE:
				return new WriteRequest(key, String.valueOf(new Random().nextInt(value_range)));
				
			default:
				throw new RequestTypeNotDefinedException("Not such request type: " + type);
		}
			
	}
	
	public class RequestTypeNotDefinedException extends Exception
	{
		private static final long serialVersionUID = -5325597649044168116L;

		public RequestTypeNotDefinedException(String msg)
		{
			super(msg);
		}
	}
}
