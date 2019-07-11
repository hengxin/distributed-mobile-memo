/**
 * @author hengxin
 * @creation 2013-8-13; 2014-05-08
 * @file AtomicRegisterServer.java
 * @description server part (replica) of the "client/server" architecture;
 * the server replica passively receives messages of type
 * {@link AtomicityReadPhaseMessage} and {@link AtomicityWritePhaseMessage} and responds with
 * messages of type {@link AtomicityReadPhaseAckMessage} and {@link AtomicityWritePhaseAckMessage}, respectively.
 */
package io.github.hengxin.distributed_mobile_memo.sharedmemory.atomicity;

import io.github.hengxin.distributed_mobile_memo.login.SessionManager;
import io.github.hengxin.distributed_mobile_memo.sharedmemory.architecture.communication.IPMessage;
import io.github.hengxin.distributed_mobile_memo.sharedmemory.architecture.communication.MessagingService;
import io.github.hengxin.distributed_mobile_memo.sharedmemory.atomicity.message.AtomicityMessage;
import io.github.hengxin.distributed_mobile_memo.sharedmemory.atomicity.message.AtomicityReadPhaseAckMessage;
import io.github.hengxin.distributed_mobile_memo.sharedmemory.atomicity.message.AtomicityReadPhaseMessage;
import io.github.hengxin.distributed_mobile_memo.sharedmemory.atomicity.message.AtomicityWritePhaseAckMessage;
import io.github.hengxin.distributed_mobile_memo.sharedmemory.atomicity.message.AtomicityWritePhaseMessage;
import io.github.hengxin.distributed_mobile_memo.sharedmemory.atomicity.messagehandler.IAtomicityMessageHandler;
import io.github.hengxin.distributed_mobile_memo.sharedmemory.data.kvs.Key;
import io.github.hengxin.distributed_mobile_memo.sharedmemory.data.kvs.VersionValue;
import io.github.hengxin.distributed_mobile_memo.sharedmemory.data.kvs.kvstore.KVStoreInMemory;

/**
 * Singleton design pattern with Java Enum which is simple and thread-safe
 */
public enum AtomicityRegisterServer implements IAtomicityMessageHandler {
    INSTANCE;    // it is thread-safe

    private static final String TAG = AtomicityRegisterServer.class.getName();

    /**
     * The server replica passively receives messages of type
     * {@link AtomicityReadPhaseMessage} and {@link AtomicityWritePhaseMessage} and responds with
     * messages of type {@link AtomicityReadPhaseAckMessage} and {@link AtomicityWritePhaseAckMessage}, respectively.
     */
    @Override
    public void handleAtomicityMessage(AtomicityMessage atomicityMessage) {
        String from_ip = atomicityMessage.getSenderIP();
        String my_ip = new SessionManager().getNodeIp();
        int cnt = atomicityMessage.getCnt();
        Key key = atomicityMessage.getKey();

        /**
         * responds to the message of type {@link AtomicityReadPhaseMessage}
         * with message of type {@link AtomicityReadAckPhaseMessage},
         * including the {@link Key} and the {@link VersionValue}
         * (it may be {@link RESERVED_VERSIONVALUE}) found
         * in the server replica
         */
        if (atomicityMessage instanceof AtomicityReadPhaseMessage) {
            // TODO: refactor KVStore
            VersionValue vval = KVStoreInMemory.INSTANCE.getVersionValue(key);
            IPMessage atomicity_read_phase_ack_msg = new AtomicityReadPhaseAckMessage(my_ip, cnt, key, vval);
            MessagingService.INSTANCE.sendOneWay(from_ip, atomicity_read_phase_ack_msg);
        } else { // (atomicityMessage instanceof AtomicityWritePhaseMessage)
            VersionValue vval_now = KVStoreInMemory.INSTANCE.getVersionValue(key);
            VersionValue vval_max = VersionValue.max(atomicityMessage.getVersionValue(), vval_now);
            KVStoreInMemory.INSTANCE.put(key, vval_max);
            IPMessage atomicity_write_phase_ack_rmsg = new AtomicityWritePhaseAckMessage(my_ip, cnt);
            MessagingService.INSTANCE.sendOneWay(from_ip, atomicity_write_phase_ack_rmsg);
        }

    }
}
