/**
 * @author hengxin
 * @date May 7, 2014
 * @description handling with messages of type {@link AtomicityReadPhaseMessage}
 */
package io.github.hengxin.distributed_mobile_memo.sharedmemory.atomicity.messagehandler;

import io.github.hengxin.distributed_mobile_memo.sharedmemory.atomicity.message.AtomicityMessage;
import io.github.hengxin.distributed_mobile_memo.sharedmemory.atomicity.message.AtomicityReadPhaseMessage;

public class AtomicityReadPhaseMessageHandler implements IAtomicityMessageHandler {
    /**
     * handle with the message {@link AtomicityReadPhaseMessage}
     * it is invoked by @see AtomicityRegisterServer
     * @param atomicityMessage message of type {@link AtomicityReadPhaseMessage} to handle with
     */
    @Override
    public void handleAtomicityMessage(AtomicityMessage atomicityMessage) {
        // TODO Auto-generated method stub

    }
}
