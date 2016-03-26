/**
 * @author hengxin
 * @date May 7, 2014
 * @description {@link AtomicityWritePhaseMessage} is sent from client to server, asking
 * it to store the value associated with a specified key.
 * {@see AtomicityWritePhaseAckMessage}
 */

package io.github.hengxin.distributed_mobile_memo.sharedmemory.atomicity.message;

import io.github.hengxin.distributed_mobile_memo.sharedmemory.data.kvs.Key;
import io.github.hengxin.distributed_mobile_memo.sharedmemory.data.kvs.VersionValue;

public class AtomicityWritePhaseMessage extends AtomicityMessage {
    private static final long serialVersionUID = -1324534020658757819L;

    public AtomicityWritePhaseMessage(String ip, int cnt,
                                      Key key, VersionValue vval) {
        super(ip, cnt, key, vval);
    }

    @Override
    public String toString() {
        return "[WRITE_PHASE]: " + super.toString();
    }
}
