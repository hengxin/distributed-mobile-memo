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
import ics.mobilememo.sharedmemory.architecture.communication.MessagingService;
import ics.mobilememo.sharedmemory.atomicity.AbstractAtomicityRegisterClient;
import ics.mobilememo.sharedmemory.atomicity.AtomicityRegisterClient;
import ics.mobilememo.sharedmemory.atomicity.AtomicityRegisterClientFactory;
import ics.mobilememo.sharedmemory.atomicity.AtomicityRegisterServer;

import java.util.Random;

public enum AtomicityMessagingService implements IReceiver
{
	INSTANCE;
	
	/**
	 * dispatch messages of type {@link AtomicityMessage} 
	 * to {@link AbstractAtomicityRegisterClient} or {@link AtomicityRegisterServer}.
	 */
	@Override
	public void onReceive(IPMessage msg)
	{
		// it only receives messages of type type {@link AtomicityMessage} from {@link MessagingService} 
		assert (msg instanceof AtomicityMessage);
		
		/**
		 * Simulating out of (receiving) order delivery by introducing random latency
		 * to create more "old-new inversions".
		 * 
		 * The average latency of read operations in 2-atomicity is about 80ms.
		 * Set the random delivery latency to 100ms.
		 * 
		 * @author hengxin
		 * @date Aug 15, 2014
		 */
		
		try
		{
			Thread.sleep(new Random().nextInt(100));
		} catch (InterruptedException ie)
		{
			ie.printStackTrace();
		}
		
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
