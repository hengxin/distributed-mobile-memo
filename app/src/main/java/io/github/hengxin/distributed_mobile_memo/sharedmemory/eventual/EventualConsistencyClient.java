package io.github.hengxin.distributed_mobile_memo.sharedmemory.eventual;

import io.github.hengxin.distributed_mobile_memo.sharedmemory.atomicity.SWMR2AtomicityRegisterClient;

/**
 * {@link EventualConsistencyClient} represents client that runs the RWN (i.e., R + W < N) protocol
 * for eventual consistency.
 *
 * <p>The RWN protocol is exactly the same with that of emulating 2-atomic, single-writer registers,
 * except that they have different quorum configurations. For eventual consistency, W = \lfloor N / 2 \rfloor
 * and R = N - 1 - W. For (2)-atomicity, R = W = \lfloor N / 2 \rfloor + 1.</p>
 *
 * Created by hengxin on 16-3-10.
 */
public class EventualConsistencyClient extends SWMR2AtomicityRegisterClient {
    public EventualConsistencyClient(int read_quorum_size, int write_quorum_size) {
        super(read_quorum_size, write_quorum_size);
    }
}
