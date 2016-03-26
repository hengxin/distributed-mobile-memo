package io.github.hengxin.distributed_mobile_memo.sharedmemory.atomicity;

import java.util.Map;

import io.github.hengxin.distributed_mobile_memo.group.GroupConfig;
import io.github.hengxin.distributed_mobile_memo.login.SessionManager;
import io.github.hengxin.distributed_mobile_memo.quorum.QuorumSystem;
import io.github.hengxin.distributed_mobile_memo.sharedmemory.architecture.IRegisterClient;
import io.github.hengxin.distributed_mobile_memo.sharedmemory.atomicity.message.AtomicityMessage;
import io.github.hengxin.distributed_mobile_memo.sharedmemory.atomicity.message.AtomicityMessagingService;
import io.github.hengxin.distributed_mobile_memo.sharedmemory.atomicity.message.AtomicityReadPhaseAckMessage;
import io.github.hengxin.distributed_mobile_memo.sharedmemory.atomicity.message.AtomicityReadPhaseMessage;
import io.github.hengxin.distributed_mobile_memo.sharedmemory.atomicity.message.AtomicityWritePhaseMessage;
import io.github.hengxin.distributed_mobile_memo.sharedmemory.atomicity.messagehandler.Communication;
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

}
