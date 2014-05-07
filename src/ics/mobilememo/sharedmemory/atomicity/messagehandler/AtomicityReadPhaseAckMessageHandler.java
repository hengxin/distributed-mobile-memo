/**
 * @author hengxin
 * @date May 7, 2014
 * @description handling with messages of type {@link AtomicityReadPhaseAckMessage} 
 */
package ics.mobilememo.sharedmemory.atomicity.messagehandler;

import ics.mobilememo.sharedmemory.atomicity.message.AtomicityMessage;
import ics.mobilememo.sharedmemory.atomicity.message.AtomicityReadPhaseAckMessage;

public class AtomicityReadPhaseAckMessageHandler implements IAtomicityMessageHandler
{
	/**
	 * handle with the message {@link AtomicityReadPhaseAckMessage}
	 * it is invoked by the client @see AtomicityRegisterClient
	 * 
	 * @param atomicityMessage message of type {@link AtomicityReadPhaseAckMessage} to handle with
	 */
	@Override
	public void handleAtomicityMessage(AtomicityMessage atomicityMessage)
	{
		// TODO:
	}
}
