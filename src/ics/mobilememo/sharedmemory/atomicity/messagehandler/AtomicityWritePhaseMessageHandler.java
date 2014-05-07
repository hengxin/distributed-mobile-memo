/**
 * @author hengxin
 * @date May 7, 2014
 * @description handling with messages of type {@link AtomicityWritePhaseMessage} 
 */
package ics.mobilememo.sharedmemory.atomicity.messagehandler;

import ics.mobilememo.sharedmemory.atomicity.message.AtomicityMessage;
import ics.mobilememo.sharedmemory.atomicity.message.AtomicityWritePhaseMessage;

public class AtomicityWritePhaseMessageHandler implements IAtomicityMessageHandler
{
	/**
	 * handle with the message {@link AtomicityWritePhaseMessage}
	 * it is invoked by @see AtomicityRegisterServer
	 * @param atomicityMessage message of type {@link AtomicityWritePhaseMessage} to handle with
	 */
	@Override
	public void handleAtomicityMessage(AtomicityMessage atomicityMessage)
	{
		// TODO Auto-generated method stub
		
	}
}
