package io.github.hengxin.distributed_mobile_memo.sharedmemory.atomicity;

import android.util.Log;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import io.github.hengxin.distributed_mobile_memo.group.GroupConfig;
import io.github.hengxin.distributed_mobile_memo.group.member.SystemNode;
import io.github.hengxin.distributed_mobile_memo.login.SessionManager;
import io.github.hengxin.distributed_mobile_memo.quorum.QuorumSystem;
import io.github.hengxin.distributed_mobile_memo.sharedmemory.architecture.IRegisterClient;
import io.github.hengxin.distributed_mobile_memo.sharedmemory.architecture.communication.IPMessage;
import io.github.hengxin.distributed_mobile_memo.sharedmemory.architecture.communication.IReceiver;
import io.github.hengxin.distributed_mobile_memo.sharedmemory.architecture.communication.MessagingService;
import io.github.hengxin.distributed_mobile_memo.sharedmemory.atomicity.message.AtomicityMessage;
import io.github.hengxin.distributed_mobile_memo.sharedmemory.atomicity.message.AtomicityMessagingService;
import io.github.hengxin.distributed_mobile_memo.sharedmemory.atomicity.message.AtomicityReadPhaseAckMessage;
import io.github.hengxin.distributed_mobile_memo.sharedmemory.atomicity.message.AtomicityReadPhaseMessage;
import io.github.hengxin.distributed_mobile_memo.sharedmemory.atomicity.message.AtomicityWritePhaseAckMessage;
import io.github.hengxin.distributed_mobile_memo.sharedmemory.atomicity.message.AtomicityWritePhaseMessage;
import io.github.hengxin.distributed_mobile_memo.sharedmemory.atomicity.messagehandler.IAtomicityMessageHandler;
import io.github.hengxin.distributed_mobile_memo.sharedmemory.data.kvs.Key;
import io.github.hengxin.distributed_mobile_memo.sharedmemory.data.kvs.Version;
import io.github.hengxin.distributed_mobile_memo.sharedmemory.data.kvs.VersionValue;

/**
 * @author hengxin
 * @date Jun 27, 2014
 * @date Jul 3, 2014
 * @description Abstract implementation of clients of atomic register. All the
 * implementations of clients performing on atomic registers
 * involve both READ_PHASE ( {@link #readPhase(Key)} ) and
 * WRITE_PHASE ( {@link #writePhase(Key, VersionValue)} ) .
 * <p>
 * Furthermore, both READ_PHASE and WRITE_PHASE rely on the
 * {@link Communication} sub-procedure.
 */
public abstract class AbstractAtomicityRegisterClient implements
        IRegisterClient, IAtomicityMessageHandler {
    private static final String TAG = AbstractAtomicityRegisterClient.class.getName();

    private Communication comm;
    protected QuorumSystem quorum;

    /**
     * Counter of operations invoked by this client.
     * <p>
     * This field is only accessed (read/written) by this client. No synchronization needed.
     */
    protected int op_cnt;

    public QuorumSystem initQuorumSystem() {
        this.quorum = this.configQuorumSystem();
        return this.quorum;
    }

    /**
     * @return  a {@link QuorumSystem} that will be used in protocol. By default, it returns a
     * majority quorum system. The subclasses that want to have different quorum systems should
     * override this method.
     */
    public QuorumSystem configQuorumSystem() {
        return QuorumSystem.createMajorityQuorumSystem(GroupConfig.INSTANCE.getGroupSize());
    }

    public void setQuorumSystem(QuorumSystem quorum_system) {
        this.quorum = quorum_system;
    }

    /**
     * Read phase: contact all replicas to query their latest value and version, wait for responses from a (read) quorum
     * of them, and return the responses to caller (usually read/write operations).
     *
     * <p>The communication mechanism utilizes {@link io.github.hengxin.distributed_mobile_memo.sharedmemory
     * .atomicity.AbstractAtomicityRegisterClient.Communication}</p>
     *
     * @param key {@link Key} to read
     * @return an array of messages of type {@link AtomicityMessage} (actually
     * {@link AtomicityReadPhaseAckMessage}) each of which comes from a server replica specified by its ip address)
     */
    public Map<String, AtomicityMessage> readPhase(Key key) {
        AtomicityMessage atomicity_read_phase_message = new AtomicityReadPhaseMessage(new SessionManager().getNodeIp(), this.op_cnt, key);
        this.comm = new Communication(atomicity_read_phase_message, this.quorum.getReadQuorumSize());
        return this.comm.communicate();
    }

    /**
     * Write phase: contact all replicas to write a @param key + @param vval pair into them, wait for acks from a
     * (write) quorum of them, and return <i>nothing</i> to caller (usually read/write operations).
     *
     * <p>The communication mechanism utilizes {@link io.github.hengxin.distributed_mobile_memo.sharedmemory
     * .atomicity.AbstractAtomicityRegisterClient.Communication}</p>
     *
     * @param key  {@link Key} to write
     * @param vval {@link VersionValue} associated with the {@link Key} to be written
     */
    public void writePhase(Key key, VersionValue vval) {
        AtomicityMessage atomicity_write_phase_message = new AtomicityWritePhaseMessage(new SessionManager().getNodeIp(), this.op_cnt, key, vval);
        this.comm = new Communication(atomicity_write_phase_message, this.quorum.getWriteQuorumSize());
        this.comm.communicate();
    }

    /**
     * extract the max VersionValue from acks
     *
     * @param acks acks which is a map of {@link Key}-{@link AtomicityMessage}
     *             pairs
     * @return max VersionValue contained in the acks
     */
    public VersionValue extractMaxVValFromAcks(Map<String, AtomicityMessage> acks) {
        VersionValue[] vvals = AtomicityMessage.extractVersionValues(acks.values().toArray(new AtomicityMessage[acks.size()]));
        VersionValue max_vval = VersionValue.max(vvals);
        return max_vval;
    }

    /**
     * get the next Version for the client
     *
     * @param old_ver old Version
     * @return next new Version for the client
     */
    public Version getNextVersion(Version old_ver) {
        return old_ver.increment(new SessionManager().getNodeId());
    }

    /**
     * it receives messages of type {@link AtomicityMessage} from
     * {@link AtomicityMessagingService} and dispatch them to its inner/private
     * {@link Communication} instance.
     * <p>
     * assert (atomicityMessage instanceof AtomicityReadPhaseAckMessage || atomicityMessage instanceof AtomicityWritePhaseAckMessage);
     */
    @Override
    public void handleAtomicityMessage(AtomicityMessage atomicityMessage) {
        this.comm.onReceive(atomicityMessage);
    }

    /**
     * @author hengxin
     * @description the basic primitive for communication in the atomic register
     * simulation algorithm. It enables a processor to send a
     * message and get acknowledgments from a majority of the
     * processors.
     * <p>
     * FIXME: check the "synchronized" keyword in the class
     */
    public class Communication implements IReceiver {
        private final String TAG = Communication.class.getName();

        private AtomicityMessage atomicity_message = null; // message to send: READ_PHASE or WRITE_PHASE message

        // TODO using Java enum to represent status
        // status used to control the sending of messages
        private static final int NOT_SENT = 0; // message was not sent yet
        private static final int NOT_ACK = 1; // message was sent but not yet acknowledged
        private static final int ACK = 2; // message was acknowledged

        // to implement the ping-pong communication mechanism
        private static final int HERE = 4;
        private static final int THERE = 8;

        private final int replicas_num;     // number of processors in the system
        // quorum size: number of replicas from which the client needs to receive acks before completing this
        // communication phase
        private final int quorum_size;
        private CountDownLatch latch_for_quorum; // {@link CountDownLatch} associated with quorum

        /**
         * Using thread-safe counterpart to {@link HashMap} {@link ConcurrentHashMap}
         *
         * @author hengxin
         * @date Jul 3, 2014
         */
        private final Map<String, Integer> turn = new ConcurrentHashMap<>();
        private final Map<String, Integer> status = new ConcurrentHashMap<>();
        private final Map<String, AtomicityMessage> info = new ConcurrentHashMap<>();

        /**
         * Custom locks for modifying {@link #turn}, {@link #status}, and
         * {@link #info} atomically.
         *
         * @author hengxin
         * @date Jul 14, 2014
         * @see <a href="http://vanillajava.blogspot.com/2010/05/locking-concurrenthashmap-for-exclusive.html">
         *     Locking ConcurrentHashMap for Exclusive</a>
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
         * @param rmsg {@link AtomicityMessage} to communicate
         * @param quorum_size   quorum size: number of replicas from which the client needs to receive acks before
         *                      completing this communication phase
         */
        public Communication(AtomicityMessage rmsg, int quorum_size) {
            this.atomicity_message = rmsg;

            this.replicas_num = GroupConfig.INSTANCE.getGroupSize();
            this.quorum_size = quorum_size;
            this.latch_for_quorum = new CountDownLatch(quorum_size);

            // initialization
            List<SystemNode> replica_list = GroupConfig.INSTANCE.getGroupMembers();

            for (int i = 0; i < this.replicas_num; i++) {
                final String replica_ip = replica_list.get(i).getNodeIp();

                final int hash = replica_ip.hashCode() & 0x7FFFFFFF;
                synchronized (this.locks[hash % locks.length]) {
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
         * A processor uses the "communicate" primitive to broadcast a message to
         * all the processors and then to collect ACKs from a majority of them.
         *
         * @return collection of corresponding ACK messages
         * <p>
         * Note: the method is invoked by only the client (i.e., a single thread).
         */
        public Map<String, AtomicityMessage> communicate() {
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
            for (int i = 0; /** i < this.replicas_num;**/i < this.quorum_size; i++) {
                final String replica_ip = replica_list.get(i).getNodeIp();

                final int hash = replica_ip.hashCode() & 0x7FFFFFFF;
                synchronized (this.locks[hash % locks.length]) {
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
            try {
                this.latch_for_quorum.await();
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }

//			Log.d(TAG, "Collecting a majority of ACKs: " + this.info.size());

            return this.info;
        }

        /**
         * Handler of received messages (READ_PHASE_ACK and WRITE_PHASE_ACK)
         * from other processors: (1) implementing the ping-pong mechanism (2)
         * control of the sending of message (3) collecting of acks
         * <p>
         * Note: the method may be invoked concurrently by multiple threads.
         * Synchronized.
         */
        @Override
        public void onReceive(final IPMessage msg) {
//			Log.i(TAG, "Who is in charge of onReceive in Communication: " + Thread.currentThread().getName());

            // discard the delayed message
            if (this.isDeprecated((AtomicityMessage) msg)) {
                Log.i(TAG, "The message is obsolete: " + msg.toString());
                return;
            }

            final String from_ip = msg.getSenderIP();

            final int hash = from_ip.hashCode() & 0x7FFFFFFF;
            synchronized (this.locks[hash % locks.length]) {
                switch (this.status.get(from_ip)) {
                    case Communication.NOT_SENT: // ack of an old message
                        MessagingService.INSTANCE.sendOneWay(from_ip, this.atomicity_message); // re-send the rmsg
                        this.turn.put(from_ip, Communication.THERE);
                        this.status.put(from_ip, Communication.NOT_ACK);
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
                        this.latch_for_quorum.countDown();
                        break;

                    default:
                        break;
                }
            }
        }

        /**
         * @param received_rmsg received message of type {@link AtomicityMessage}
         * @return true, if the message is deprecated; false, otherwise.
         * @FIXME check this method!!!
         * <p>
         * to check whether the received message is a delayed one (and
         * should be discarded)
         */
        private boolean isDeprecated(final AtomicityMessage received_rmsg) {
            return !( // negation
                    (this.atomicity_message.getCnt() == received_rmsg.getCnt()) // belong to the same operation
                            &&
                            (    // belong to the same phase
                                    (this.atomicity_message instanceof AtomicityReadPhaseMessage && received_rmsg instanceof AtomicityReadPhaseAckMessage)
                                            ||
                                            (this.atomicity_message instanceof AtomicityWritePhaseMessage && received_rmsg instanceof AtomicityWritePhaseAckMessage)
                            )
            );
        }
    }
}
