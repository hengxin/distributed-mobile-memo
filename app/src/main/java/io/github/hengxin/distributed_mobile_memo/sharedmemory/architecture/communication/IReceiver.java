/**
 * @author hengxin
 * @creation 2013-8-26
 * @file IReceiver.java
 * @description
 */
package io.github.hengxin.distributed_mobile_memo.sharedmemory.architecture.communication;

/**
 * @author hengxin
 * @date 2013-8-26
 * @description handler of the received messages
 */
public interface IReceiver {
    /**
     * handler of the received message
     * @param msg message received
     */
    public void onReceive(IPMessage msg);
}
