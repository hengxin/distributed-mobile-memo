package io.github.hengxin.distributed_mobile_memo.sharedmemory.atomicity.messagehandler;

import android.util.Log;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import io.github.hengxin.distributed_mobile_memo.group.GroupConfig;
import io.github.hengxin.distributed_mobile_memo.group.member.SystemNode;
import io.github.hengxin.distributed_mobile_memo.sharedmemory.architecture.communication.IPMessage;
import io.github.hengxin.distributed_mobile_memo.sharedmemory.architecture.communication.IReceiver;
import io.github.hengxin.distributed_mobile_memo.sharedmemory.architecture.communication.MessagingService;
import io.github.hengxin.distributed_mobile_memo.sharedmemory.atomicity.AbstractAtomicityRegisterClient;
import io.github.hengxin.distributed_mobile_memo.sharedmemory.atomicity.message.AtomicityMessage;
import io.github.hengxin.distributed_mobile_memo.sharedmemory.atomicity.message.AtomicityReadPhaseAckMessage;
import io.github.hengxin.distributed_mobile_memo.sharedmemory.atomicity.message.AtomicityReadPhaseMessage;
import io.github.hengxin.distributed_mobile_memo.sharedmemory.atomicity.message.AtomicityWritePhaseAckMessage;
import io.github.hengxin.distributed_mobile_memo.sharedmemory.atomicity.message.AtomicityWritePhaseMessage;

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

    private final AtomicityMessage atomicity_message;

    // status used to control the sending of messages
    private static final int NOT_SENT = 0; // message was not sent yet
    private static final int NOT_ACK = 1; // message was sent but not yet acknowledged
    private static final int ACK = 2; // message was acknowledged

    // to implement the ping-pong communication mechanism
    private static final int HERE = 4;
    private static final int THERE = 8;

    private static final List<SystemNode> replica_list = GroupConfig.INSTANCE.getGroupMembers();
    private static final int replicas_num = GroupConfig.INSTANCE.getGroupSize();

    private final int quorum_size;
    private final CountDownLatch latch_for_quorum; // {@link CountDownLatch} associated with quorum

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

        this.quorum_size = quorum_size;
        this.latch_for_quorum = new CountDownLatch(this.quorum_size);

        for (int i = 0; i < replicas_num; i++) {
            final String replica_ip = replica_list.get(i).getNodeIp();

            final int hash = replica_ip.hashCode() & 0x7FFFFFFF;
            synchronized (this.locks[hash % locks.length]) {
                turn.put(replica_ip, Communication.HERE);
                status.put(replica_ip, Communication.NOT_SENT);
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

        /**
         * Sending messages to a random majority of server replicas
         * instead of sending them to all and waiting for a majority of acks.
         * The purpose is to produce more "old-new inversions".
         *
         * @author hengxin
         * @date Aug 16, 2014
         */
        Collections.shuffle(replica_list);

        // TODO: 2016/3/27 contact all replicas!
        // broadcast
//        for (int i = 0; /** i < this.replicas_num;**/ i < this.quorum_size; i++) {
        for (int i = 0; i < this.replicas_num; i++) {
            final String replica_ip = replica_list.get(i).getNodeIp();

            final int hash = replica_ip.hashCode() & 0x7FFFFFFF;
            synchronized (this.locks[hash % locks.length]) {
                if (turn.get(replica_ip) == Communication.HERE) { // it is my turn (PING)
                    MessagingService.INSTANCE.sendOneWay(replica_ip, atomicity_message);

                    turn.put(replica_ip, Communication.THERE); // it is your turn now (PONG)
                    status.put(replica_ip, Communication.NOT_ACK);
                }
            }
        }

        try {
            this.latch_for_quorum.await();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }

        return this.info;
    }

    /**
     * Handler of received messages (READ_PHASE_ACK and WRITE_PHASE_ACK)
     * from other processors: (1) implementing the ping-pong mechanism (2)
     * control of the sending of message (3) collecting of acks
     * <p>
     * Note: the method may be invoked concurrently by multiple threads.
     */
    @Override
    public void onReceive(final IPMessage msg) {
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
