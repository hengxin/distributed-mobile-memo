/**
 * @author hengxin
 * @date Jun 27, 2014
 * @description Implementing atomic register supporting single writer and multiple readers (SWMR).
 */
package io.github.hengxin.distributed_mobile_memo.sharedmemory.atomicity;

import java.util.Map;

import io.github.hengxin.distributed_mobile_memo.login.SessionManager;
import io.github.hengxin.distributed_mobile_memo.sharedmemory.atomicity.message.AtomicityMessage;
import io.github.hengxin.distributed_mobile_memo.sharedmemory.data.kvs.Key;
import io.github.hengxin.distributed_mobile_memo.sharedmemory.data.kvs.Version;
import io.github.hengxin.distributed_mobile_memo.sharedmemory.data.kvs.VersionValue;

/**
 * Client for atomic SWMR registers.
 * @author hengxin
 */
public class SWMRAtomicityRegisterClient extends AbstractAtomicityRegisterClient {

    private static final String TAG = SWMRAtomicityRegisterClient.class.getName();

    /**
     * @author added by hengxin
     * @date Jun 26, 2014
     * @description for single writer (SWMR), maintaining its monotonically increasing version;
     * In SWMR case, it is not necessary for the writer to query a majority of server replicas
     * to get the latest versioned value till that time. It only needs to access its cached version.
     * <p>
     * Being accessed by the only writer, it is not necessary to synchronize.
     * <p>
     * It is initialized to (-1, THIS.PID)
     */
    private Version cached_version = new Version(-1, new SessionManager().getNodeId());

    /**
     * increment the {@link #cached_version} and return the new {@link Version}
     *
     * @return a new {@link Version} after increment
     */
    private Version incrementAndGetCachedVersion() {
        this.cached_version = cached_version.increment(new SessionManager().getNodeId());
        return this.cached_version;
    }

    /*
     * @see ics.mobilememo.sharedmemory.atomicity.AbstractAtomicityRegisterClient#get(ics.mobilememo.sharedmemory.data.kvs.Key)
     *
     * Get method supporting only one writer.
     * It is the same as that in {@link MWMRAtomicityRegisterClient} supporting multiple writers.
     * Namely it consists of three phases: read_phase, local computation, and write_phase
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

        // write phase: write-back the VersionValue into a (write) quorum of the server replicas
        this.writePhase(key, max_vval);

        // return the latest VersionValue
        return max_vval;
    }

    /*
     * @see ics.mobilememo.sharedmemory.atomicity.AbstractAtomicityRegisterClient#put(ics.mobilememo.sharedmemory.data.kvs.Key, java.lang.String)
     *
     * Put method supporting only one writer.
     * In this case, it is not necessary for this writer to obtain the latest
     * version (of a particular key) through the read_phrase (@see AbstractAtomicityRegisterClient#read_phase()).
     * It uses its locally cached version ( # ).
     *
     * @see also MWMRAtomicityRegisterClient#put(Key, String)
     */
    @Override
    public VersionValue put(Key key, String val) {
//		Log.d(TAG, TAG + " issues a PUT request ...");

        this.op_cnt++;

        // get the version to use
        Version max_version = this.incrementAndGetCachedVersion();

        // construct the {@link VersionValue} to put
        VersionValue new_vval = new VersionValue(this.getNextVersion(max_version), val);

        // write phase: write-back the VersionValue into a (write) quorum of the server replicas
        this.writePhase(key, new_vval);

        return new_vval;
    }

}
