/**
 * @author hengxin
 * @date 2014-04-24
 * @description each request consists of its type (W[0], R[1]), key, and value (if it is of type W)
 */
package ics.mobilememo.benchmark.workload;

import ics.mobilememo.sharedmemory.data.kvs.Key;

public class Request
{
	private int type = -1;
	private Key key = null;
	private String val = null;
	
	/**
	 * construct the request in workload
	 * @param type W[0], R[1]
	 * @param key key to write to or read from
	 * @param val value to write if the request is of type W
	 */
	public Request(int type, Key key, String val)
	{
		this.type = type;
		this.key = key;
		this.val = val;
	}

	/**
	 * get the type of the request: W[0], R[1]
	 * @return type of the request: W[0], R[1]
	 */
	public int getType()
	{
		return type;
	}

	/**
	 * get the key of the request
	 * @return key of the request
	 */
	public Key getKey()
	{
		return key;
	}

	/**
	 * if the request is a W[0], get its value to be written
	 * @return value to write
	 */
	public String getValue()
	{
		return val;
	}
}	
