/**
 * @author hengxin
 * @creation 2013-8-11
 * @file KeyMap.java
 *
 * @description
 */
package ics.mobilememo.sharedmemory.data.kvs.kvstore;

import ics.mobilememo.sharedmemory.data.kvs.Key;
import ics.mobilememo.sharedmemory.data.kvs.VersionValue;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author hengxin
 * @date 2013-8-10 2013-8-28
 * @description collection of key-value pairs stored;
 * 	each server replica holds its own local key-value store.
 *
 * Note: the KVS may be accessed concurrently.
 *
 * Singleton design pattern with Java enum which is simple and thread-safe
 */
public enum KVStoreInMemory implements IKVStore
{
	INSTANCE;	// it is thread-safe

	private static final String TAG = KVStoreInMemory.class.getName();

	// Using the thread-safe ConcurrentHashMap to cope with the multi-thread concurrency.
	private ConcurrentMap<Key, VersionValue> key_vval_map = new ConcurrentHashMap<Key, VersionValue>();
	
	/**
	 * @author hengxin
	 * @date 2013-9-2, 2014-05-15
	 * @description multiple separate locks for concurrent reads and concurrent writes
	 * 	when some write is synchronized such as in {@link #put(Key, VersionValue)} method
	 * @see http://vanillajava.blogspot.com/2010/05/locking-concurrenthashmap-for-exclusive.html
	 */
	private final Object[] locks = new Object[16];
	{
		for(int i = 0; i < locks.length; i++) 
			locks[i] = new Object();
	}

	/**
	 * multi-thread access:
	 * Invariant: the sequence of timestamp values taken on by the replica on any server
	 * is nondecreasing during any execution of the algorithm
	 * To ensure the invariant, the if-then pattern should be locked.
	 *
	 * put the key-value pair into the key-value store
	 * @param key Key to identify
	 * @param vval VersionValue associated with the Key
	 */
	@Override
	public void put(Key key, VersionValue vval)
	{
		/**
		 * instead of <code>VersionValue current_vval = this.key_vval_map.get(key);</code>
		 * 
		 * maybe return {@link VersionValue#NULL_VERSIONVALUE}
		 */
		VersionValue current_vval = this.getVersionValue(key);	

		int hash = key.hashCode() & 0x7FFFFFFF;
		synchronized (this.locks[hash % locks.length])	// lock the "if-then" pattern
		{
			if (current_vval.compareTo(vval) < 0)	//	newer VersionValue
				this.key_vval_map.put(key, vval);
		}
	}

	/**
	 * multi-thread access:
	 * 	concurrent reads are not necessarily synchronized.
	 *
	 * given Key, return the VersionValue associated;
	 * if no mapping for the specified key is found, return NULL_VERSIONVALUE
	 *
	 * @param key Key to identify
	 * @return VersionValue associated
	 */
	@Override
	public VersionValue getVersionValue(Key key)
	{
		VersionValue vval = this.key_vval_map.get(key);
		if (vval == null)	// no synchronization is needed
			return VersionValue.RESERVED_VERSIONVALUE;
		return vval;
	}

//	/**
//	 * given the Key, return the Version of the value associated.
//	 *
//	 * @param key Key to identify
//	 * @return the version of the key-value pair
//	 * 	if the key does not exist, return the initial version @see Version
//	 */
//	public Version getVersion(Key key)
//	{
//		VersionValue vval = this.key_vval_map.get(key);
//		if (vval == null)
//			return Version.INIT_VERSION;	// return the initial Version
//		return vval.getVersion();
//	}

	/**
	 * remove the key and associated value from the kvs
	 * @param key key to identify and remove
	 */
	@Override
	public void remove(Key key)
	{
		this.key_vval_map.remove(key);
	}
}
