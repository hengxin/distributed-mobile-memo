/**
 * @author hengxin
 * @date Jun 27, 2014
 * @description Implementation of SWMR (ALMOST) atomic register supporting single writer and multiple readers (SWMR).
 * The term "ALMOST" here refers to the fact that it is NOT atomic actually.
 * However, it is 2-atomicity with read operations always getting values with bounded (<= 2) staleness.
 * (Moreover, the probability of reading a stale value can be computed.)
 */

package io.github.hengxin.distributed_mobile_memo.sharedmemory.atomicity;

import java.util.Map;

import io.github.hengxin.distributed_mobile_memo.sharedmemory.atomicity.message.AtomicityMessage;
import io.github.hengxin.distributed_mobile_memo.sharedmemory.data.kvs.Key;
import io.github.hengxin.distributed_mobile_memo.sharedmemory.data.kvs.VersionValue;

/**
 * Implementation of SWMR (ALMOST) atomic register supporting single writer and multiple readers (SWMR).
 * The term "ALMOST" here refers to the fact that it is NOT atomic actually.
 * However, it is 2-atomicity with read operations always getting values with bounded (<= 2) staleness.
 * (Moreover, the probability of reading a stale value can be computed.)
 *
 * @author hengxin
 * @date Jun 27, 2014
 */
public class SWMR2AtomicityRegisterClient extends SWMRAtomicityRegisterClient {
    private static final String TAG = SWMR2AtomicityRegisterClient.class.getName();

    /*
     * @see ics.mobilememo.sharedmemory.atomicity.AbstractAtomicityRegisterClient#get(ics.mobilememo.sharedmemory.data.kvs.Key)
     *
     * Get method of {@link SWMR2AtomicityRegisterClient}.
     * It consists of only the read_phase and local computation.
     * Compared with that in \link SWMRAtomicityRegisterClient and \link MWMRAtomicityRegisterClient,
     * it does not need the write_phase, reducing operation latency while preserving almost atomicity.
     */
    @Override
    public VersionValue get(Key key) {
//		Log.d(TAG, TAG + " issues a GET request ...");

        this.op_cnt++;

//		Log.d(TAG, "Begin to get value associated with Key = " + key.toString());

        // read phase: contact a (read) quorum of the server replicas for the latest value and version
        Map<String, AtomicityMessage> read_phase_acks = this.readPhase(key);

        // local computation: extract the latest VersionValue (value and its version)
        VersionValue max_vval = this.extractMaxVValFromAcks(read_phase_acks);

        // return the latest VersionValue
        return max_vval;
    }

}
