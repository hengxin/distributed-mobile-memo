/**
 * @author hengxin
 * @date Jun 27, 2014
 * @description Implementing atomicity register supporting multiple writers and multiple readers (MWMR).
 */
package io.github.hengxin.distributed_mobile_memo.sharedmemory.atomicity;

import java.util.Map;

import io.github.hengxin.distributed_mobile_memo.sharedmemory.atomicity.message.AtomicityMessage;
import io.github.hengxin.distributed_mobile_memo.sharedmemory.data.kvs.Key;
import io.github.hengxin.distributed_mobile_memo.sharedmemory.data.kvs.Version;
import io.github.hengxin.distributed_mobile_memo.sharedmemory.data.kvs.VersionValue;

public class MWMRAtomicityRegisterClient extends
        AbstractAtomicityRegisterClient {
    // Log for class {@link MWMRAtomicityRegisterClient}
    private final static String TAG = MWMRAtomicityRegisterClient.class.getName();

    public MWMRAtomicityRegisterClient(final int read_quorum_size, final int write_quorum_size) {
        super(read_quorum_size, write_quorum_size);
    }


    /**
     * {@link #get(Key)} method supporting MWMR:
     * it consists of three phases: read_phase, local computation, and write_phase
     */
    @Override
    public VersionValue get(Key key) {
//		Log.d(TAG, "Client issues a GET request ...");

        this.op_cnt++;

//		Log.d(TAG, "Begin to get value associated with Key = " + key.toString());

        // read phase: contact a quorum of the server replicas for the latest value and version
        Map<String, AtomicityMessage> read_phase_acks = this.readPhase(key);

        // local computation: extract the latest VersionValue (value and its version)
        VersionValue max_vval = this.extractMaxVValFromAcks(read_phase_acks);

        // write phase: write-back the VersionValue into a (write) quorum of the server replicas
        this.writePhase(key, max_vval);

        // return the latest VersionValue
        return max_vval;
    }

    /**
     * {@link #put(Key, String)} method supporting MWMR:
     * it consists of three phases: read_phase, local computation, and write_phase
     */
    @Override
    public VersionValue put(Key key, String val) {
//		Log.d(TAG, "Client issues a PUT request ...");

        this.op_cnt++;

        // read phase: contact a quorum of the server replicas for the latest value and version
        Map<String, AtomicityMessage> read_phase_acks = this.readPhase(key);

        // local computation: extract the latest VersionValue; increment the version; construct the new VersionValue to write
        VersionValue max_vval = this.extractMaxVValFromAcks(read_phase_acks);

        Version max_version = max_vval.getVersion();
        VersionValue new_vval = new VersionValue(this.getNextVersion(max_version), val);

        // write phase: write-back the VersionValue into a quorum of the server replicas
        this.writePhase(key, new_vval);

        return new_vval;
    }

}
