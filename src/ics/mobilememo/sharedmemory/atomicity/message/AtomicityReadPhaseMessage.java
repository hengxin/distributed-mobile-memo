/**
 * @author hengxin
 * @date May 7, 2014
 * @description {@link AtomicityReadPhaseMessage} is sent from client to server, 
 * 	querying the newest value with a specified key. 
 *  {@see AtomicityReadPhaseAckMessage}
 */

package ics.mobilememo.sharedmemory.atomicity.message;

import ics.mobilememo.sharedmemory.data.kvs.Key;
import ics.mobilememo.sharedmemory.data.kvs.VersionValue;

public class AtomicityReadPhaseMessage extends AtomicityMessage
{
	private static final long serialVersionUID = 1444946327284724651L;

	/**
	 * constructor: {@link AtomicityReadPhaseMessage} with a specified {@link Key};
	 * the {@link AtomicityReadPhaseMessage#vval} field is useless and is set to be null
	 * 
	 * @param ip IPMessage
	 * @param cnt @see IPMessage#cnt
	 * @param key {@link Key}  to query from @see AtomicityMessage#key
	 * @param vval {@link VersionValue} @see AtomicityMessage#vval 
	 */
	public AtomicityReadPhaseMessage(String ip, int cnt, Key key, VersionValue vval)
	{
		super(ip, cnt, key, vval);
		super.vval = null;
	}
	
}
