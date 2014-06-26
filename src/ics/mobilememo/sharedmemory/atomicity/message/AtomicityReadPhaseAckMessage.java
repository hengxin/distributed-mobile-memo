/**
 * @author hengxin
 * @date May 7, 2014
 * @description {@link AtomicityReadPhaseAckMessage} is sent from server to client,
 *   responding to {@link AtomicityReadPhaseMessage} with the newest value associated with
 *   a specified key.
 *   {@see AtomicityReadPhaseMessage}
 */

package ics.mobilememo.sharedmemory.atomicity.message;

import ics.mobilememo.sharedmemory.data.kvs.Key;
import ics.mobilememo.sharedmemory.data.kvs.VersionValue;

public class AtomicityReadPhaseAckMessage extends AtomicityMessage
{
	private static final long serialVersionUID = 3408265304038339576L;

	/**
	 * constructor: {@link AtomicityReadPhaseMessage} with a specified {@link Key};
	 * the {@link AtomicityMessage#vval} field is useless and is set to be null
	 * 
	 * @param ip IPMessage
	 * @param cnt @see IPMessage#cnt
	 * @param key {@link Key} @see AtomicityMessage#key
	 * @param vval {@link VersionValue} @see AtomicityMessage#vval 
	 */
	public AtomicityReadPhaseAckMessage(String ip, int cnt, Key key, VersionValue vval)
	{
		super(ip, cnt, key, vval);
	}
	
	@Override
	public String toString()
	{
		return "READ_PHASE_ACK: " + super.toString();
	}
}
