/**
 * @author hengxin
 * @creation 2013-8-8; 2014-05-07
 * @file IAtomicRegisterClient.java
 *
 * @description interface for the "client" part of the "client/server" architecture
 *  of the simulated register system model;
 *  it is responsible for handling the invocations of operations on simulated register
 */
package ics.mobilememo.sharedmemory.architecture;

import ics.mobilememo.sharedmemory.data.kvs.Key;
import ics.mobilememo.sharedmemory.data.kvs.VersionValue;

public interface IRegisterClient
{
	/**
	 * "get" operation invocation
	 * @param key key to get
	 * @return versioned value associated with the key
	 */
	public VersionValue get(Key key);

	/**
	 * "put" operation invocation
	 * @param key key to put
	 * @param val non-versioned value associated with the key
	 * @return VersionValue to put associated with the key
	 */
	public VersionValue put(Key key, String val);

}
