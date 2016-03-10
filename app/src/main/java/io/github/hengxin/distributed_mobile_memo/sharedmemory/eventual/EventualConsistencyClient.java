package io.github.hengxin.distributed_mobile_memo.sharedmemory.eventual;

import io.github.hengxin.distributed_mobile_memo.sharedmemory.atomicity.SWMR2AtomicityRegisterClient;

/**
 *
 * Created by hengxin on 16-3-10.
 */
public class EventualConsistencyClient extends SWMR2AtomicityRegisterClient {
    public EventualConsistencyClient(int read_quorum_size, int write_quorum_size) {
        super(read_quorum_size, write_quorum_size);
    }
}
