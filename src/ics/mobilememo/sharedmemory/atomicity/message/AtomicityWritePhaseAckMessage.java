/**
 * @author hengxin
 * @date May 7, 2014
 * @description {@link AtomicityWritePhaseAckMessage} is sent from server to client, 
 *   responding to {@link AtomicityWritePhaseMessage}.
 *   {@see AtomicityWritePhaseMessage}
 */

package ics.mobilememo.sharedmemory.atomicity.message;

import ics.mobilememo.sharedmemory.data.kvs.Key;

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
	 */	
	public AtomicityWritePhaseAckMessage(String ip, int cnt)
	{
		super(ip, cnt, null, null);
	}
}
