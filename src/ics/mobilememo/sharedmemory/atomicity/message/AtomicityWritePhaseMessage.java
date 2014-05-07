/**
 * @author hengxin
 * @date May 7, 2014
 * @description {@link AtomicityWritePhaseMessage} is sent from client to server, asking 
 *   it to store the value associated with a specified key.
 *   {@see AtomicityWritePhaseAckMessage}
 */

package ics.mobilememo.sharedmemory.atomicity.message;

import ics.mobilememo.sharedmemory.data.kvs.Key;
import ics.mobilememo.sharedmemory.data.kvs.VersionValue;

public class AtomicityWritePhaseMessage extends AtomicityMessage
{
	private static final long serialVersionUID = -1324534020658757819L;

	/**
	 * constructor: {@link AtomicityReadPhaseMessage} with a specified {@link Key};
	 * the {@link AtomicityReadPhaseMessage#vval} field is useless and is set to be null
	 * 
	 * @param ip IPMessage
	 * @param cnt @see IPMessage#cnt
	 * @param key {@link Key} to store @see AtomicityMessage#key 
	 * @param vval {@link VersionValue} to store @see AtomicityMessage#vval 
	 */
	public AtomicityWritePhaseMessage(String ip, int cnt, Key key,
			VersionValue vval)
	{
		super(ip, cnt, key, vval);
	}
}
