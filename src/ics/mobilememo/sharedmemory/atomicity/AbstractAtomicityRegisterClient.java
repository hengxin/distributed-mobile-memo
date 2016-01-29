package ics.mobilememo.sharedmemory.atomicity;

import ics.mobilememo.group.GroupConfig;
import ics.mobilememo.group.member.SystemNode;
import ics.mobilememo.login.SessionManager;
import ics.mobilememo.sharedmemory.architecture.IRegisterClient;
import ics.mobilememo.sharedmemory.architecture.communication.IPMessage;
import ics.mobilememo.sharedmemory.architecture.communication.IReceiver;
import ics.mobilememo.sharedmemory.architecture.communication.MessagingService;
import ics.mobilememo.sharedmemory.atomicity.message.AtomicityMessage;
import ics.mobilememo.sharedmemory.atomicity.message.AtomicityMessagingService;
import ics.mobilememo.sharedmemory.atomicity.message.AtomicityReadPhaseAckMessage;
import ics.mobilememo.sharedmemory.atomicity.message.AtomicityReadPhaseMessage;
import ics.mobilememo.sharedmemory.atomicity.message.AtomicityWritePhaseAckMessage;
import ics.mobilememo.sharedmemory.atomicity.message.AtomicityWritePhaseMessage;
import ics.mobilememo.sharedmemory.atomicity.messagehandler.IAtomicityMessageHandler;
import ics.mobilememo.sharedmemory.data.kvs.Key;
import ics.mobilememo.sharedmemory.data.kvs.Version;
import ics.mobilememo.sharedmemory.data.kvs.VersionValue;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import android.content.res.Configuration;
import android.util.Log;

/**
 * @author hengxin
 * @date Jun 27, 2014
 * @date Jul 3, 2014
 * 
 * @description Abstract implementation of clients of atomic register. All the
 *              implementations of clients performing on atomic registers
 *              involve both READ_PHASE ( {@link #readPhase(Key)} ) and
 *              WRITE_PHASE ( {@link #writePhase(Key, VersionValue)} ) .
 * 
 *              Furthermore, both READ_PHASE and WRITE_PHASE rely on the
 *              {@link Communication} sub-procedure.
 */
public abstract class AbstractAtomicityRegisterClient implements
		IRegisterClient, IAtomicityMessageHandler
{
	private static final String TAG = AbstractAtomicityRegisterClient.class.getName();

	private Communication comm = null; // {@link Communication} instance for read phase/write phase
	
	/**
	 * Counter of operations invoked by this client
	 * 
	 * This field is only accessed (read/written) by this client.
	 * It is communicated via {@link IPMessage}.
	 * No synchronization needed.
	 */
	protected int op_cnt; 

	@Override
	public abstract VersionValue get(Key key);

	@Override
	public abstract VersionValue put(Key key, String val);

	/**
	 * read phase: contact a quorum of the processors, querying for the latest
	 * value and version
	 * 
	 * @param key
	 *            {@link Key} to identify
	 * @return an array of messages of type {@link AtomicityMessage} (actually
	 *         {@link AtomicityReadPhaseAckMessage}) each of which comes from a
	 *         server replica specified by its ip address
	 */
	public Map<String, AtomicityMessage> readPhase(Key key)
	{
		AtomicityMessage atomicity_read_phase_message = new AtomicityReadPhaseMessage(new SessionManager().getNodeIp(), this.op_cnt, key);
		this.comm = new Communication(atomicity_read_phase_message);
		return this.comm.communicate();
	}

	/**
	 * write phase: write a {@link Key} + {@link VersionValue} pair into a
	 * quorum of the server replicas
	 * 
	 * @param key
	 *            {@link Key} to identify
	 * @param vval
	 *            {@link VersionValue} associated with the {@link Key} to be
	 *            written
	 */
	public void writePhase(Key key, VersionValue vval)
	{
		AtomicityMessage atomicity_write_phase_message = new AtomicityWritePhaseMessage(new SessionManager().getNodeIp(), this.op_cnt, key, vval);
		this.comm = new Communication(atomicity_write_phase_message);
		this.comm.communicate();
	}

	/**
	 * extract the max VersionValue from acks
	 * 
	 * @param acks
	 *            acks which is a map of {@link Key}-{@link RegisterMessage}
	 *            pairs
	 * @return max VersionValue contained in the acks
	 */
	public VersionValue extractMaxVValFromAcks(Map<String, AtomicityMessage> acks)
	{
		VersionValue[] vvals = AtomicityMessage.extractVersionValues(acks.values().toArray(new AtomicityMessage[acks.size()]));
		VersionValue max_vval = VersionValue.max(vvals);
		return max_vval;
	}

	/**
	 * get the next Version for the client
	 * 
	 * @param old_ver
	 *            old Version
	 * @return next new Version for the client Note: this method should be
	 *         called after @see {@link Configuration#setPid(int)} has finished
	 */
	public Version getNextVersion(Version old_ver)
	{
		return old_ver.increment(new SessionManager().getNodeId());
	}

	/**
	 * it receives messages of type {@link AtomicityMessage} from
	 * {@link AtomicityMessagingService} and dispatch them to its inner/private
	 * {@link Communication} instance.
	 * 
	 * assert (atomicityMessage instanceof AtomicityReadPhaseAckMessage || atomicityMessage instanceof AtomicityWritePhaseAckMessage);
	 */
	@Override
	public void handleAtomicityMessage(AtomicityMessage atomicityMessage)
	{
		this.comm.onReceive(atomicityMessage);
	}

	/**
	 * 
	 * @author hengxin
	 * @description the basic primitive for communication in the atomic register
	 *              simulation algorithm. It enables a processor to send a
	 *              message and get acknowledgments from a majority of the
	 *              processors.
	 * 
	 *              FIXME: check the "synchronized" keyword in the class
	 */
	public class Communication implements IReceiver // IClientReceiver
	{
		private final String TAG = Communication.class.getName();

		private AtomicityMessage atomicity_message = null; // message to send: READ_PHASE or WRITE_PHASE message

		// status used to control the sending of messages
		private static final int NOT_SENT = 0; // message was not sent yet
		private static final int NOT_ACK = 1; // message was sent but not yet
												// acknowledged
		private static final int ACK = 2; // message was acknowledged

		// to implement the ping-pong communication mechanism
		private static final int HERE = 4;
		private static final int THERE = 8;

		private final int replicas_num; // number of processors in the system
		private final int proc_majority; // counter indicating a majority of
											// processors
		private CountDownLatch latch_majority; // to coordinate the threads

		/**
		 * Using thread-safe counterpart to {@link HashMap}:
		 * {@link ConcurrentHashMap}
		 * 
		 * @author hengxin
		 * @date Jul 3, 2014
		 */
		private final Map<String, Integer> turn = new ConcurrentHashMap<String, Integer>();
		private final Map<String, Integer> status = new ConcurrentHashMap<String, Integer>();
		private final Map<String, AtomicityMessage> info = new ConcurrentHashMap<String, AtomicityMessage>();

		/**
		 * Custom locks for modifying {@link #turn}, {@link #status}, and
		 * {@link #info} atomically.
		 * 
		 * @see http://vanillajava.blogspot.com/2010/05/locking-concurrenthashmap-for-exclusive.html
		 * 
		 * @author hengxin
		 * @date Jul 14, 2014
		 */
		private final Object[] locks = new Object[10];
		{
			for (int i = 0; i < locks.length; i++)
				locks[i] = new Object();
		}

		/**
		 * Constructor of {@link Communication} Called by only the client 
		 * ({@link AbstractAtomicityRegisterClient})
		 * 
		 * @param rmsg
		 *            {@link AtomicityMessage} to communicate
		 */
		public Communication(AtomicityMessage rmsg)
		{
			this.atomicity_message = rmsg;

			this.replicas_num = GroupConfig.INSTANCE.getGroupSize(); // number of clients/server replicas in the system
			this.proc_majority = this.replicas_num / 2 + 1; // counter indicating a majority of server replicas
			// Log.d(TAG, "The majority number is: " + this.proc_majority);
			this.latch_majority = new CountDownLatch(proc_majority); // wait for a majority of acks

			// initialization
			List<SystemNode> replica_list = GroupConfig.INSTANCE.getGroupMembers();

			for (int i = 0; i < this.replicas_num; i++)
			{
				final String replica_ip = replica_list.get(i).getNodeIp();
				
				final int hash = replica_ip.hashCode() & 0x7FFFFFFF;
				synchronized (this.locks[hash % locks.length])
				{
					turn.put(replica_ip, Communication.HERE);
					status.put(replica_ip, Communication.NOT_SENT);
	
					/**
					 * {@link ConcurrentHashMap} does not accept
					 * <code>null<code> value (nor <code>null<code> key).
					 * 
					 * @author hengxin
					 * @date Jul 3, 2014
					 */
					// info.put(replica_ip, null);
				}
			}
		}

		/**
		 * A processor use the "communicate" primitive to broadcast a message to
		 * all the processors and then to collect ACKs from a majority of them.
		 * 
		 * @return collection of corresponding ACK messages
		 * 
		 *         Note: the method is invoked by only the client (i.e., a single thread).
		 */
		public Map<String, AtomicityMessage> communicate()
		{
			final List<SystemNode> replica_list = GroupConfig.INSTANCE.getGroupMembers();

			/**
			 * Modified.
			 * Sending messages to a random majority of server replicas 
			 * instead of sending them to all and waiting for a majority of acks.
			 * 
			 * The purpose is to produce more "old-new inversions".
			 * 
			 * To get a random majority of samples from a list 'L' with size 'n':
			 * call Collections.shuffle(L) and take the first $n / 2 + 1$ elements.
			 * See http://stackoverflow.com/a/4702061/1833118
			 * 
			 * @author hengxin
			 * @date Aug 16, 2014
			 */
			Collections.shuffle(replica_list);
			
			// broadcast
			for (int i = 0; /** i < this.replicas_num;**/ i < this.proc_majority; i++)
			{
				final String replica_ip = replica_list.get(i).getNodeIp();

				final int hash = replica_ip.hashCode() & 0x7FFFFFFF;
				synchronized (this.locks[hash % locks.length])
				{
					if (turn.get(replica_ip) == Communication.HERE) // it is my turn (PING)
					{
						MessagingService.INSTANCE.sendOneWay(replica_ip, atomicity_message); // send message to each server replica

						turn.put(replica_ip, Communication.THERE); // it is your turn now (PONG)
						status.put(replica_ip, Communication.NOT_ACK);
					}
				}
			}

			/**
			 * @author hengxin
			 * @date 2013-8-14 modified to use the "CountDownLatch" mechanism to
			 *       coordinate the threads, replacing the wait/notifyAll
			 *       approach
			 */
			try
			{
				this.latch_majority.await();
			} catch (InterruptedException ie)
			{
				ie.printStackTrace();
			}

//			Log.d(TAG, "Collecting a majority of ACKs: " + this.info.size());

			return this.info;
		}

		/**
		 * Handler of received messages (READ_PHASE_ACK and WRITE_PHASE_ACK)
		 * from other processors: (1) implementing the ping-pong mechanism (2)
		 * control of the sending of message (3) collecting of acks
		 * 
		 * Note: the method may be invoked concurrently by multiple threads.
		 * Synchronized.
		 */
		@Override
		public void onReceive(final IPMessage msg)
		{
//			Log.i(TAG, "Who is in charge of onReceive in Communication: " + Thread.currentThread().getName());

			// discard the delayed message
			if (this.isDeprecated((AtomicityMessage) msg)) 
			{
				Log.i(TAG, "The message is obsolete: " + msg.toString());
				return;
			}
			
			final String from_ip = msg.getSenderIP();

			final int hash = from_ip.hashCode() & 0x7FFFFFFF;
			synchronized (this.locks[hash % locks.length])
			{
				switch (this.status.get(from_ip))
				{
					case Communication.NOT_SENT: // ack of an old message
						MessagingService.INSTANCE.sendOneWay(from_ip, this.atomicity_message); // re-send the rmsg
						this.turn.put(from_ip, Communication.THERE);
						this.status.put(from_ip, Communication.NOT_ACK);
						// this.latch_majority.countDown();	// Don't count acks of old messages (Jul 2, 2014)
						break;

					case Communication.NOT_ACK:
						this.status.put(from_ip, Communication.ACK);
						this.info.put(from_ip, (AtomicityMessage) msg);

//						 Log.d(TAG, "Receiving an ACK message from: " + from_ip + "; It is " + msg.toString());

						/**
						 * Replacing "wait/notifyAll" by "CountDownLatch"
						 * @author hengxin
						 * @date 2013-8-14
						 */
						this.latch_majority.countDown();
						break;

					default:
						break;
				}
			}
		}

		/**
		 * @FIXME check this method!!!
		 * 
		 *        to check whether the received message is a delayed one (and
		 *        should be discarded)
		 * 
		 * @param received_rmsg
		 *            received message of type {@link AtomicityMessage}
		 * @return true, if the message is deprecated; false, otherwise.
		 */
		private boolean isDeprecated(final AtomicityMessage received_rmsg)
		{
			return !( // negation
						(this.atomicity_message.getCnt() == received_rmsg.getCnt()) // belong to the same operation
						&& 
						(	// belong to the same phase
							(this.atomicity_message instanceof AtomicityReadPhaseMessage && received_rmsg instanceof AtomicityReadPhaseAckMessage) 
							|| 
							(this.atomicity_message instanceof AtomicityWritePhaseMessage && received_rmsg instanceof AtomicityWritePhaseAckMessage)
						)
					);
		}
	}
}
