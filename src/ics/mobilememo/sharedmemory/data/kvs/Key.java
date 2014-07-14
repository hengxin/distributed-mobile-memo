/**
 * @author hengxin
 * @creation 2013-8-8
 * @file Key.java
 *
 * @description
 */
package ics.mobilememo.sharedmemory.data.kvs;

import java.io.Serializable;

/**
 * @author hengxin
 * @description the "Key" part of the "key-value" store;
 * 	it is just a String NOW.
 */
public class Key implements Serializable
{
	private static final long serialVersionUID = -1479354097038655441L;

	private String key_str;	// key to identify (String format)

	/**
	 * Reserved key with string literal "RESERVED_KEY"
	 */
	public static final Key RESERVED_KEY = new Key("RESERVED_KEY");
			
	/**
	 * Constructor for Key: it is just a String now
	 * @param key key in String
	 */
	public Key(String key)
	{
		this.key_str = key;
	}

	/**
	 * String format of Key for output: Key : {@link #key_str}
	 */
	@Override
	public String toString()
	{
		return "Key: " + this.key_str;
	}

	/**
	 * Override the hashCode() method for HashMap
	 * 
	 * Guarantee: Two {@link Key}s regarded equal semantically hold the same hash code.
	 */
	@Override
	public int hashCode()
	{
		int result = 17;
		result = 37 * result + this.key_str.hashCode();
		return result;
	}

	/**
	 * For serializable interface (necessary for network communication)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (! (obj instanceof Key))
			return false;

		Key key = (Key) obj;
		return this.key_str.equals(key.key_str);
	}
}
