/**
 * @author hengxin
 * @date May 7, 2014
 * @description handling with messages of type {@link AtomicityWritePhaseAckMessage} 
 */
package ics.mobilememo.sharedmemory.atomicity.messagehandler;

import ics.mobilememo.sharedmemory.atomicity.message.AtomicityMessage;
import ics.mobilememo.sharedmemory.atomicity.message.AtomicityWritePhaseAckMessage;

public class AtomicityWritePhaseAckMessageHandler implements
		IAtomicityMessageHandler
{

	/**
	 * handle with the message {@link AtomicityWritePhaseAckMessage}
	 * it is invoked by @see AtomicityRegisterClient
	 * @param atomicityMessage message of type {@link AtomicityWritePhaseAckMessage} to handle with
	 */
	@Override
	public void handleAtomicityMessage(AtomicityMessage atomicityMessage)
	{
		// TODO Auto-generated method stub

	}

}
