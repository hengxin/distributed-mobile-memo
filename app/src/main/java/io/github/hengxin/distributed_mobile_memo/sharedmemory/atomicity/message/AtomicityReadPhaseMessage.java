/**
 * @author hengxin
 * @date May 7, 2014
 * @description {@link AtomicityReadPhaseMessage} is sent from client to server,
 * querying the newest value with a specified key.
 * {@see AtomicityReadPhaseAckMessage}
 */
package io.github.hengxin.distributed_mobile_memo.sharedmemory.atomicity.message;

import io.github.hengxin.distributed_mobile_memo.sharedmemory.data.kvs.Key;
import io.github.hengxin.distributed_mobile_memo.sharedmemory.data.kvs.VersionValue;

public class AtomicityReadPhaseMessage extends AtomicityMessage {
    private static final long serialVersionUID = 1444946327284724651L;

    /**
     * constructor: {@link AtomicityReadPhaseMessage} with a specified {@link Key};
     * the {@link AtomicityReadPhaseMessage#vval} field is useless
     * and is set to be {@link VersionValue#RESERVED_VERSIONVALUE}
     *
     * @param ip IPMessage
     * @param cnt @see IPMessage#cnt
     * @param key {@link Key}  to query from @see AtomicityMessage#key
     */
    public AtomicityReadPhaseMessage(String ip, int cnt, Key key) {
        super(ip, cnt, key, VersionValue.RESERVED_VERSIONVALUE);
    }

    @Override
    public String toString() {
        return "[READ_PHASE]: " + super.toString();
    }
}
