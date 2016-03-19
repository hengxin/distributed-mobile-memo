package io.github.hengxin.distributed_mobile_memo.sharedmemory.eventual;

import io.github.hengxin.distributed_mobile_memo.group.GroupConfig;
import io.github.hengxin.distributed_mobile_memo.quorum.QuorumSystem;
import io.github.hengxin.distributed_mobile_memo.sharedmemory.atomicity.SWMR2AtomicityRegisterClient;

/**
 * {@link EventualConsistencyClient} represents client that runs the RWN (i.e., R + W <= N) protocol
 * for eventual consistency.
 *
 * <p>The RWN protocol is exactly the same with that of emulating 2-atomic, single-writer registers,
 * except that they have different quorum configurations. For eventual consistency, W = \lceil N / 2 \ceil
 * (in Java, it is (N + 1) / 2) and R = N - W. For (2)-atomicity, R = W = \lfloor N / 2 \rfloor + 1.</p>
 *
 * Created by hengxin on 16-3-10.
 */
public class EventualConsistencyClient extends SWMR2AtomicityRegisterClient {
    private static final String TAG = EventualConsistencyClient.class.getName();

    @Override
    public QuorumSystem configQuorumSystem() {
        int replica_size = GroupConfig.INSTANCE.getGroupSize();
        int write_quorum_size = (replica_size + 1) / 2;
        int read_quorum_size = replica_size - write_quorum_size;

        return new QuorumSystem(replica_size, read_quorum_size, write_quorum_size);
    }
}
