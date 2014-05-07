/**
 * @author hengxin
 * @date May 7, 2014
 * @description {@link AtomicityWritePhaseAckMessage} is sent from server to client, 
 *   responding to {@link AtomicityWritePhaseMessage}.
 *   {@see AtomicityWritePhaseMessage}
 */

package ics.mobilememo.sharedmemory.atomicity.message;

import ics.mobilememo.sharedmemory.data.kvs.Key;
import ics.mobilememo.sharedmemory.data.kvs.VersionValue;

public class AtomicityWritePhaseAckMessage extends AtomicityMessage
{
	private static final long serialVersionUID = 2410975240256459093L;

	/**
	 * constructor: {@link AtomicityReadPhaseMessage} with a specified {@link Key};
	 * the {@link AtomicityMessage#key} field is useless and is set to be null,
	 * the {@link AtomicityMessage#vval} field is useless and is set to be null.
	 * 
	 * @param ip IPMessage
	 * @param cnt @see IPMessage#cnt
	 * @param key {@link Key} @see AtomicityMessage#key
	 * @param vval {@link VersionValue} @see AtomicityMessage#vval 
	 */	
	public AtomicityWritePhaseAckMessage(String ip, int cnt, Key key,
			VersionValue vval)
	{
		super(ip, cnt, key, vval);
		this.key = null;
		this.vval = null;
	}
}
