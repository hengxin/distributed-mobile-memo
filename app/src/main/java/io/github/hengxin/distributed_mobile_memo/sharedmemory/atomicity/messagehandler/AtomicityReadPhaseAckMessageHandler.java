/**
 * @author hengxin
 * @date May 7, 2014
 * @description handling with messages of type {@link AtomicityReadPhaseAckMessage}
 */
package io.github.hengxin.distributed_mobile_memo.sharedmemory.atomicity.messagehandler;

import io.github.hengxin.distributed_mobile_memo.sharedmemory.atomicity.message.AtomicityMessage;
import io.github.hengxin.distributed_mobile_memo.sharedmemory.atomicity.message.AtomicityReadPhaseAckMessage;

public class AtomicityReadPhaseAckMessageHandler implements IAtomicityMessageHandler {
    /**
     * Handle with the message {@link AtomicityReadPhaseAckMessage}
     * @param atomicityMessage message of type {@link AtomicityReadPhaseAckMessage} to handle with
     */
    @Override
    public void handleAtomicityMessage(AtomicityMessage atomicityMessage) {
        // TODO:
    }
}
