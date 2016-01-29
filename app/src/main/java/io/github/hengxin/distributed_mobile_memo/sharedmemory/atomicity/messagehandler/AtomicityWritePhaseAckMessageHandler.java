/**
 * @author hengxin
 * @date May 7, 2014
 * @description handling with messages of type {@link AtomicityWritePhaseAckMessage}
 */
package io.github.hengxin.distributed_mobile_memo.sharedmemory.atomicity.messagehandler;

import io.github.hengxin.distributed_mobile_memo.sharedmemory.atomicity.message.AtomicityMessage;
import io.github.hengxin.distributed_mobile_memo.sharedmemory.atomicity.message.AtomicityWritePhaseAckMessage;

public class AtomicityWritePhaseAckMessageHandler implements
        IAtomicityMessageHandler {

    /**
     * Handle with the message {@link AtomicityWritePhaseAckMessage}
     * @param atomicityMessage message of type {@link AtomicityWritePhaseAckMessage} to handle with
     */
    @Override
    public void handleAtomicityMessage(AtomicityMessage atomicityMessage) {
        // TODO Auto-generated method stub

    }

}
