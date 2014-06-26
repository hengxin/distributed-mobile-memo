/**
 * @author hengxin
 * @date May 7, 2014
 * @description {@link AtomicityMessage} is used for atomic consistency implementation.
 */
package ics.mobilememo.sharedmemory.atomicity.message;

import ics.mobilememo.sharedmemory.architecture.communication.IPMessage;
import ics.mobilememo.sharedmemory.data.kvs.Key;
import ics.mobilememo.sharedmemory.data.kvs.VersionValue;

public class AtomicityMessage extends IPMessage
{
	private static final long serialVersionUID = 851435561377468450L;

	protected Key key;	// key (representing the simulated register) to put/get/remove
	protected VersionValue vval;	// versioned value carried with the message
	
	/**
	 * constructor of {@link AtomicityMesssage}
	 * @param ip {@link IPMessage#sender_ip}
	 * @param key {@link Key} to put/get/remove
	 * @param vval {@link VersionValue} carried with the message
	 */
	public AtomicityMessage(String ip, int cnt, Key key, VersionValue vval)
	{
		super(ip, cnt);
		this.key = key;
		this.vval = vval;
	}

	/**
	 * @return {@link #key}
	 * @see Key
	 */
	public Key getKey()
	{
		return this.key;
	}
	
	/**
	 * @return {@link #vval}
	 * @see VersionValue
	 */
	public VersionValue getVersionValue()
	{
		return this.vval;
	}
	
	/**
	 * extract the {@link VersionValue}s carried with a set of {@link AtomicityMessage}s
	 * 
	 * @param atomicity_messages an array of {@link AtomicityMessage}s
	 * @return an array of {@link VersionValue} carried with the set of @param atomicity_messages
	 */
	public static VersionValue[] extractVersionValues(AtomicityMessage[] atomicity_messages)
	{
		VersionValue[] vvals = new VersionValue[atomicity_messages.length];
		for (int i = 0; i < atomicity_messages.length; i++)
			vvals[i] = atomicity_messages[i].vval;
		return vvals;
	}
	
	/**
	 * show {@link #key} and {@link #vval}
	 */
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append(super.toString()).append('\t').append(this.key).append('\t').append(this.vval);
		
		return sb.toString();
	}
}
