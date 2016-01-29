/**
 * @author hengxin
 * @date May 7, 2014
 * @description interface for handling with messages of type {@link AtomicityMessage}
 */
package io.github.hengxin.distributed_mobile_memo.sharedmemory.atomicity.messagehandler;

import io.github.hengxin.distributed_mobile_memo.sharedmemory.atomicity.message.AtomicityMessage;

public interface IAtomicityMessageHandler {
    /**
     * handling with messages of type {@link AtomicityMessage}
     *
     * @param atomicityMessage {@link AtomicityMessage} to handle with
     */
    public void handleAtomicityMessage(AtomicityMessage atomicityMessage);
}
