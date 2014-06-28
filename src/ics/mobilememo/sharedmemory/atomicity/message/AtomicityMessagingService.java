/**
 * @author hengxin
 * @date May 8, 2014
 * @description receive messages of type {@link AtomicityMessage} from {@link MessagingService}
 * and dispatch them to {@link AtomicityRegisterClient} or {@link AtomicityRegisterServer}.
 * @see MessagingService
 */
package ics.mobilememo.sharedmemory.atomicity.message;

import ics.mobilememo.sharedmemory.architecture.communication.IPMessage;
import ics.mobilememo.sharedmemory.architecture.communication.IReceiver;
import ics.mobilememo.sharedmemory.atomicity.AtomicityRegisterClient;
import ics.mobilememo.sharedmemory.atomicity.AtomicityRegisterClientFactory;
import ics.mobilememo.sharedmemory.atomicity.AtomicityRegisterServer;

public enum AtomicityMessagingService implements IReceiver
{
	INSTANCE;
	
	/**
	 * dispatch messages of type {@link AtomicityMessage} 
	 * to {@link AtomicityRegisterClient} or {@link AtomicityRegisterServer}.
	 */
	@Override
	public void onReceive(IPMessage msg)
	{
		// it only receives messages of type type {@link AtomicityMessage} from {@link MessagingService} 
		assert (msg instanceof AtomicityMessage);
		
		/**
		 *  {@link AtomicityRegisterServer} is responsible for handling with messages 
		 *  of types of {@link AtomicityReadPhaseMessage} and {@link AtomicityWritePhaseMessage}
		 *  while
		 *  {@link AtomicityRegisterClient} is responsible for handling with messages
		 *  of types of {@link AtomicityReadPhaseAckMessage} and {@link AtomicityWritePhaseAckMessage}
		 */
		if (msg instanceof AtomicityReadPhaseMessage || msg instanceof AtomicityWritePhaseMessage)
			AtomicityRegisterServer.INSTANCE.handleAtomicityMessage((AtomicityMessage) msg);
		else // (msg instanceof AtomicityReadAckPhaseMessage || msg instanceof AtomicityWriteAckPhaseMessage)
//			AtomicityRegisterClient.INSTANCE.handleAtomicityMessage((AtomicityMessage) msg);
			AtomicityRegisterClientFactory.INSTANCE.getAtomicityRegisterClient().handleAtomicityMessage((AtomicityMessage) msg);
	}

}
