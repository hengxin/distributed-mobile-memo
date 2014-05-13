/**
 * @author hengxin
 * @creation 2013-8-13; 2014-05-08
 * @file AtomicRegisterServer.java
 *
 * @description server part (replica) of the "client/server" architecture;
 *  the server replica passively receives messages of type 
 *  {@link AtomicityReadPhaseMessage} and {@link AtomicityWritePhaseMessage} and responds with
 *  messages of type {@link AtomicityReadPhaseAckMessage} and {@link AtomicityWritePhaseAckMessage}, respectively.
 */
package ics.mobilememo.sharedmemory.atomicity;

import ics.mobilememo.login.SessionManager;
import ics.mobilememo.sharedmemory.architecture.communication.IPMessage;
import ics.mobilememo.sharedmemory.architecture.communication.MessagingService;
import ics.mobilememo.sharedmemory.architecture.config.NetworkConfig;
import ics.mobilememo.sharedmemory.atomicity.message.AtomicityMessage;
import ics.mobilememo.sharedmemory.atomicity.message.AtomicityReadPhaseAckMessage;
import ics.mobilememo.sharedmemory.atomicity.message.AtomicityReadPhaseMessage;
import ics.mobilememo.sharedmemory.atomicity.message.AtomicityWritePhaseAckMessage;
import ics.mobilememo.sharedmemory.atomicity.message.AtomicityWritePhaseMessage;
import ics.mobilememo.sharedmemory.atomicity.messagehandler.IAtomicityMessageHandler;
import ics.mobilememo.sharedmemory.data.kvs.Key;
import ics.mobilememo.sharedmemory.data.kvs.VersionValue;
import ics.mobilememo.sharedmemory.data.kvs.kvstore.KVStoreInMemory;

/**
 * Singleton design pattern with Java Enum which is simple and thread-safe
 */
public enum AtomicityRegisterServer implements IAtomicityMessageHandler
{
	INSTANCE;	// it is thread-safe

	private static final String TAG = AtomicityRegisterServer.class.getName();

	/**
	 * receiving READ_PHASE or WRITE_PHASE messages
	 * from the client part (of the client/server architecture)
	 *
	 * dispatched from @see {@link MessagingService}{@link #onReceive(IPMessage)}
	 */
/*	public void onReceive(IPMessage msg)
	{
		String from_ip = msg.getSenderIP();
		String own_ip = Configuration.getInstance().getIp();

		RegisterMessage rmsg = (RegisterMessage) msg;
		Key key = rmsg.getKey();
		int cnt = rmsg.getCnt();

		Log.i(TAG, own_ip + " receiving the message " + rmsg.toString());

		switch (msg.getType())
		{
			*//**
			 * responds to the READ_PHASE message with READ_PHASE_ACK message,
			 * including the Key and the VersionValue (may be null) found
			 * in the server replica
			 *//*
			case READ_PHASE:
				VersionValue vval = KVStoreInMemory.INSTANCE.getVersionValue(key);
				IPMessage read_phase_ack_rmsg = new RegisterMessage(MessageTypeEnum.READ_PHASE_ACK, own_ip, cnt, key, vval);
				MessagingService.INSTANCE.sendOneWay(from_ip, read_phase_ack_rmsg);
				break;

				*//**
				 * responds to the WRITE_PHASE message with WRITE_PHASE_ACK message,
				 * while writing the key-value pair carried with the WRITE_PHASE into
				 * the key-value store maintained by the server replica
				 *//*
			case WRITE_PHASE:
				VersionValue vval_now = KVStoreInMemory.INSTANCE.getVersionValue(key);
				VersionValue vval_max = VersionValue.max(rmsg.getVersionValue(), vval_now);
				KVStoreInMemory.INSTANCE.put(key, vval_max);
				IPMessage write_phase_ack_rmsg = new RegisterMessage(MessageTypeEnum.WRITE_PHASE_ACK, own_ip, cnt, null, null);
				MessagingService.INSTANCE.sendOneWay(from_ip, write_phase_ack_rmsg);
				break;

			default:
				break;
		}
	}*/

	/**
	 * the server replica passively receives messages of type 
	 * {@link AtomicityReadPhaseMessage} and {@link AtomicityWritePhaseMessage} and responds with
	 * messages of type {@link AtomicityReadPhaseAckMessage} and {@link AtomicityWritePhaseAckMessage}, respectively.
	 */
	@Override
	public void handleAtomicityMessage(AtomicityMessage atomicityMessage)
	{
		String from_ip = atomicityMessage.getSenderIP();
//		String my_ip = SystemConfig.INSTANCE.getIP();
		String my_ip = new SessionManager().getNodeIp();
		int cnt = atomicityMessage.getCnt();
		Key key = atomicityMessage.getKey();
		
		/**
		 * responds to the message of type {@link AtomicityReadPhaseMessage} 
		 * with message of type {@link AtomicityReadAckPhaseMessage},
		 * including the {@link Key} and the {@link VersionValue} (may be null) found
		 * in the server replica
		 */
		if (atomicityMessage instanceof AtomicityReadPhaseMessage)
		{	
			// TODO: refactor KVStore
			VersionValue vval = KVStoreInMemory.INSTANCE.getVersionValue(key);
			IPMessage atomicity_read_phase_ack_msg = new AtomicityReadPhaseAckMessage(my_ip, cnt, key, vval);
			MessagingService.INSTANCE.sendOneWay(from_ip, atomicity_read_phase_ack_msg);
		}
		else // (atomicityMessage instanceof AtomicityWritePhaseMessage)
		{
			VersionValue vval_now = KVStoreInMemory.INSTANCE.getVersionValue(key);
			VersionValue vval_max = VersionValue.max(atomicityMessage.getVersionValue(), vval_now);
			KVStoreInMemory.INSTANCE.put(key, vval_max);
			IPMessage atomicity_write_phase_ack_rmsg = new AtomicityWritePhaseAckMessage(my_ip, cnt);
			MessagingService.INSTANCE.sendOneWay(from_ip, atomicity_write_phase_ack_rmsg);
		}
			
	}
}
