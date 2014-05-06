/**
 * @author hengxin
 * @creation 2013-8-28
 * @file KVPair.java
 *
 * @description
 */
package ics.mobilememo.sharedmemory.data.kvs;

/**
 * @author hengxin
 * @date 2013-8-28
 * @description single key-value pair
 */
public class KVPair
{
	private Key key;
	private VersionValue vval;

	public KVPair(Key key, VersionValue vval)
	{
		this.key = key;
		this.vval = vval;
	}

	/**
	 * @return {@link #key}: of Key
	 */
	public Key getKey()
	{
		return this.key;
	}

	/**
	 * @return {@link #vval}: of VersionValue
	 */
	public VersionValue getVVal()
	{
		return this.vval;
	}
	
	/**
	 * Key : key; VersionValue
	 */
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Key : ").append(this.key).append(';').append(this.vval.toString());
		return sb.toString();
	}
}
