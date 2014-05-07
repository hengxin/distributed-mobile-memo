/**
 * @author hengxin
 * @date May 7, 2014
 * @description handling with messages of type {@link AtomicityReadPhaseMessage} 
 */
package ics.mobilememo.sharedmemory.atomicity.messagehandler;

import ics.mobilememo.sharedmemory.atomicity.message.AtomicityMessage;
import ics.mobilememo.sharedmemory.atomicity.message.AtomicityReadPhaseMessage;

public class AtomicityReadPhaseMessageHandler implements IAtomicityMessageHandler
{
	/**
	 * handle with the message {@link AtomicityReadPhaseMessage}
	 * it is invoked by @see AtomicityRegisterServer
	 * @param atomicityMessage message of type {@link AtomicityReadPhaseMessage} to handle with
	 */
	@Override
	public void handleAtomicityMessage(AtomicityMessage atomicityMessage)
	{
		// TODO Auto-generated method stub
		
	}
}
