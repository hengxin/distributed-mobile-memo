/**
 * @author hengxin
 * @creation 2013-8-8
 * @file AtomicRegisterClient.java
 *
 * @description
 */
package ics.mobilememo.sharedmemory.atomicity;

import ics.mobilememo.sharedmemory.architecture.IRegisterClient;
import ics.mobilememo.sharedmemory.atomicity.message.AtomicityMessage;
import ics.mobilememo.sharedmemory.atomicity.messagehandler.IAtomicityMessageHandler;
import ics.mobilememo.sharedmemory.data.kvs.Key;
import ics.mobilememo.sharedmemory.data.kvs.Version;
import ics.mobilememo.sharedmemory.data.kvs.VersionValue;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import android.content.res.Configuration;
import android.util.Log;

/**
 * @author hengxin
 * @description the client is responsible for handling the invocation of
 * 	operations on simulated registers
 *
 * Singleton design pattern with Java Enum which is simple and thread-safe
 */
public enum AtomicityRegisterClient implements IRegisterClient, IAtomicityMessageHandler //, IClientReceiver
{
	INSTANCE;

	private static final String TAG = AtomicityRegisterClient.class.getName();

	private Communication comm;	// Communication instance for read phase/write phase
	private int op_cnt;	// counter of operations invoked by this client

	/*
	 * @see ics.mobilesharedmemory.register.client.IAtomicRegisterClient#read(ics.mobilesharedmemory.register.Key)
	 * "get" operation: read phase + local computation + write phase
	 */
	@Override
	public VersionValue get(Key key)
	{
		this.op_cnt++;

		// read phase: contact a quorum of the server replicas for the latest value and version
		Map<String, RegisterMessage> read_phase_acks = this.readPhase(key);

		// local computation: extract the latest VersionValue (value and its version)
		VersionValue max_vval = this.extractMaxVValFromAcks(read_phase_acks);

		// write phase: write-back the VersionValue into a quorum of the server replicas
		this.writePhase(key, max_vval);

		// return the latest VersionValue
		return max_vval;
	}

	/*
	 * @see IAtomicRegisterClient#write(Key, String)
	 * "put" operation: read phase + local computation + write phase
	 */
	@Override
	public VersionValue put(Key key, String val)
	{
		this.op_cnt++;

		// read phase: contact a quorum of the server replicas for the latest value and version
		Map<String, RegisterMessage> read_phase_acks = this.readPhase(key);

		// local computation: extract the latest VersionValue; increment the version; construct the new VersionValue to write
		VersionValue max_vval = this.extractMaxVValFromAcks(read_phase_acks);

		Version max_version = max_vval.getVersion();
		VersionValue new_vval = new VersionValue(this.getNextVersion(max_version), val);

		// write phase: write-back the VersionValue into a quorum of the server replicas
		this.writePhase(key, new_vval);

		return new_vval;
	}

	/**
	 * extract max VersionValue from acks
	 * @param acks acks which contain a map of key-registermessage pairs
	 * @return max VersionValue contained in the acks
	 */
	private VersionValue extractMaxVValFromAcks(Map<String, RegisterMessage> acks)
	{
		VersionValue[] vvals = RegisterMessage.extractVValue(acks.values().toArray(new RegisterMessage[acks.size()]));
		VersionValue max_vval = VersionValue.max(vvals);
		return max_vval;
	}

	/**
	 * get the next Version for the client
	 * @param old_ver old Version
	 * @return next new Version for the client
	 * Note: this method should be called after @see {@link Configuration#setPid(int)} has finished
	 */
	private Version getNextVersion(Version old_ver)
	{
		return old_ver.increment(Configuration.getInstance().getPid());
	}

	/**
	 * handle the RegisterMessage: READ_PHASE_ACK or WRITE_PHASE_ACK
	 *
	 * TODO: check the logical flow or unify the two cases
	 */
	@Override
	public void onReceive(IPMessage msg)
	{
		Log.d(TAG, "Receiving message : " + msg.toString());

		switch (msg.getType())
		{
			case READ_PHASE_ACK:	// during the read phase
			case WRITE_PHASE_ACK:	// during the write phase
				this.comm.onReceive(msg);	// dispatch to the Communication component
				break;

			default:
				break;
		}
	}

	/**
	 * read phase: contact a quorum of the processors for the latest value and version
	 * @param key Key to identify
	 * @return array of   (subtype of RegisterMessage)
	 */
	private Map<String, RegisterMessage> readPhase(Key key)
	{
		RegisterMessage query_rmsg = new RegisterMessage(MessageTypeEnum.READ_PHASE, Configuration.getInstance().getIp(), this.op_cnt, key, null);
		this.comm = new Communication(query_rmsg);
		return this.comm.communicate();
	}

	/**
	 * write phase: write the value and version into a quorum of the processors
	 * @param key Key to identify
	 * @param vval VersionValue to be written
	 */
	private void writePhase(Key key, VersionValue vval)
	{
		RegisterMessage write_back_rmsg = new RegisterMessage(MessageTypeEnum.WRITE_PHASE, Configuration.getInstance().getIp(), this.op_cnt, key, vval);
		this.comm = new Communication(write_back_rmsg);
		this.comm.communicate();
	}

	/**
	 *
	 * @author hengxin
	 * @description the basic primitive for communication in the atomic register simulation algorithm.
	 * 	It enables a processor to send a message and get acknowledgments from a majority of the processors.
	 *
	 * FIXME: check the "synchronized" keyword in the class
	 */
	public class Communication implements IReceiver // IClientReceiver
	{
		private final String TAG = Communication.class.getName();

		private RegisterMessage rmsg = null;	// message to send: READ_PHASE or WRITE_PHASE message

		// status used to control the sending of messages
		private static final int NOT_SENT = 0;	// message was not sent yet
		private static final int NOT_ACK = 1;	// message was sent but not yet acknowledged
		private static final int ACK = 2;		// message was acknowledged

		// to implement the ping-pong communication mechanism
		private static final int HERE = 4;
		private static final int THERE = 8;

		private int replicas_num;	// number of processors in the system
		private int proc_majority;	// counter indicating a majority of processors
		private CountDownLatch latch_majority;	// to coordinate the threads

//		int[] turn = new int[replicas_num];	// to implement the ping-pong communication mechanism
//		RegisterMessage[] info = new RegisterMessage[replicas_num];	// to store the ack messages
//		int[] status = new int[replicas_num];	// to control the sending of messages
		Map<String, Integer> turn = new HashMap<String, Integer>();
		Map<String, Integer> status = new HashMap<String, Integer>();
		Map<String, RegisterMessage> info = new HashMap<String, RegisterMessage>();

		public Communication(RegisterMessage rmsg)
		{
			this.rmsg = rmsg;

//			this.ack_num = 0;	// number of acks collected
			this.replicas_num = Configuration.getInstance().getReplicasNum();	// number of processors in the system
			this.proc_majority = replicas_num / 2 + 1;	// counter indicating a majority of processors
			this.latch_majority = new CountDownLatch(proc_majority);	// wait for a majority of acks

			// initialization
			for (int i = 0; i < replicas_num; i++)
			{
				String ip = Configuration.getInstance().getReplicaList().get(i).getIp();
				turn.put(ip, Communication.HERE);
				status.put(ip, Communication.NOT_SENT);
				info.put(ip, null);
//				info[i] = null;
//				turn[i] = Communication.HERE;
//				status[i] = Communication.NOT_SENT;
			}
		}

		/**
		 * A processor use the "communicate" primitive to broadcast a message to all the processors
		 * and then to collect a corresponding ACK message from a majority of them.
		 *
		 * @return collection of corresponding ACK messages
		 * Note: the method is invoked by only ONE thread. No synchronization is needed.
		 */
		public Map<String, RegisterMessage> communicate()
		{
			// broadcast
			for (int i = 0; i < replicas_num; i++)
			{
				String ip = Configuration.getInstance().getReplicaList().get(i).getIp();
				if (turn.get(ip) == Communication.HERE)	// it is my turn (ping)
				{
					MessagingService.INSTANCE.sendOneWay(ip, rmsg);	// send message to each replica

					turn.put(ip, Communication.THERE);	// it is your turn now (pong)
					status.put(ip, Communication.NOT_ACK);
				}
			}

			/**
			 * @author hengxin
			 * @date 2013-8-14
			 * modified to use the "CountDownLatch" mechanism to coordinate the threads,
			 * replacing the wait/notifyAll approach
			 */
			try
			{
				this.latch_majority.await();
			} catch (InterruptedException ie)
			{
				Log.e(TAG, ie.getMessage());
				ie.printStackTrace();
			}

			// TODO: the communication instance is done. do something.
			if (this.rmsg.getType() == MessageTypeEnum.READ_PHASE)
				Log.i(TAG, "Read phase finished.");
			else //
				Log.i(TAG, "Write phase finished.");

			return this.info;
		}

		/**
		 * handler of received messages (READ_PHASE_ACK and WRITE_PHASE_ACK) from other processors:
		 * 	(1) implementing the ping-pong mechanism
		 *  (2) control of the sending of message
		 *  (3) collecting of acks
		 *
		 *  Note: the method may be invoked concurrently by multiple threads.
		 *  However, the resources accessed and modified by each thread are separated.
		 *  Therefore, no synchronization is needed.
		 */
		public void onReceive(IPMessage msg)
		{
			if (this.isDeprecated((RegisterMessage) msg))	// discard the delayed message
				return;

			String from_ip = msg.getSenderIp();

			switch (this.status.get(from_ip))
			{
				case Communication.NOT_SENT:	// ack of an old message
					MessagingService.INSTANCE.sendOneWay(from_ip, this.rmsg);	// re-send the rmsg
					this.turn.put(from_ip, Communication.THERE);
					this.status.put(from_ip, Communication.NOT_ACK);

					/**
					 * @author hengxin
					 * @date 2013-8-14
					 * modified to use the "CountDownLatch" mechanism to coordinate the threads,
					 * replacing the wait/notifyAll approach
					 */
					this.latch_majority.countDown();
					break;

				case Communication.NOT_ACK:
					this.status.put(from_ip, Communication.ACK);
					info.put(from_ip, (RegisterMessage) msg);
//					info[from_id] = msg;

					/**
					 * @author hengxin
					 * @date 2013-8-14
					 * modified to use the "CountDownLatch" mechanism to coordinate the threads,
					 * replacing the wait/notifyAll approach
					 */
					this.latch_majority.countDown();
					break;

				default:
					break;
			}
		}

		/**
		 * to check whether the received message is a delayed one (deprecated).
		 * @param received_rmsg received message
		 * @return true, if the message is deprecated; false, otherwise.
		 */
		private boolean isDeprecated(RegisterMessage received_rmsg)
		{
			return ! ( 	// negation
					(this.rmsg.getCnt() == received_rmsg.getCnt())	// belong to the same operation
					&& ( (this.rmsg.getType() == MessageTypeEnum.READ_PHASE && received_rmsg.getType() == MessageTypeEnum.READ_PHASE_ACK) // belong to the same read/write phase
							|| (this.rmsg.getType() == MessageTypeEnum.WRITE_PHASE && received_rmsg.getType() == MessageTypeEnum.WRITE_PHASE_ACK) )
					 );
		}
	}

	@Override
	public void handleAtomicityMessage(AtomicityMessage atomicityMessage)
	{
		// TODO Auto-generated method stub
		
	}

}