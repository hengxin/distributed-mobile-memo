package io.github.hengxin.distributed_mobile_memo.sharedmemory.atomicity;


import android.support.annotation.NonNull;
import android.util.Log;

import io.github.hengxin.distributed_mobile_memo.group.GroupConfig;

/**
 * @author hengxin
 * @date Jun 27, 2014
 * @description Factory for creating atomicity register clients
 * according to the algorithm in which they are participating
 */
// TODO: 16-3-10 eliminate the "enum" singleton pattern
public enum AtomicityRegisterClientFactory {
    INSTANCE;

    private static final String TAG = AtomicityRegisterClientFactory.class.getName();
    private int alg_type = -1;

    // TODO: 16-3-10 using "enum"
    public static final int NO_SUCH_ATOMICITY = -1;
    public static final int SWMR_ATOMICITY = 0;
    public static final int SWMR_2ATOMICITY = 1;
    public static final int MWMR_ATOMICITY = 2;
    public static final int EVENTUAL_CONSISTENCY = 4;

//    public enum Algorithm { SWMR_ATOMICITY, SWMR_2ATOMICITY, MWMR_ATOMICITY, SWMR_EVENTUAL };

    private AbstractAtomicityRegisterClient atomicity_register_client = null;

    /**
     * @param alg_type type of algorithm to run
     */
    public void setAtomicityRegisterClient(int alg_type) {
        this.alg_type = alg_type;
    }

    /**
     * Create client of appropriate type (according to {@link #alg_type}) and return it.
     * @return a concrete instance of {@link AbstractAtomicityRegisterClient}
     */
    @NonNull
    public AbstractAtomicityRegisterClient getAtomicityRegisterClient() throws NoSuchAtomicAlgorithmSupported {
        if (this.atomicity_register_client == null) {
            int replica_num = GroupConfig.INSTANCE.getGroupSize();
            int majority;
            Log.d(TAG, "Replica number is: " + replica_num);

            switch (alg_type) {
                case AtomicityRegisterClientFactory.SWMR_ATOMICITY:
                    majority = replica_num / 2 + 1;
                    this.atomicity_register_client = new SWMRAtomicityRegisterClient(majority, majority);
                    break;
                case AtomicityRegisterClientFactory.SWMR_2ATOMICITY:
                    majority = replica_num / 2 + 1;
                    this.atomicity_register_client = new SWMR2AtomicityRegisterClient(majority, majority);
                    break;
                case AtomicityRegisterClientFactory.MWMR_ATOMICITY:
                    majority = replica_num / 2 + 1;
                    this.atomicity_register_client = new MWMRAtomicityRegisterClient(majority, majority);
                    break;
                default:
                    throw new NoSuchAtomicAlgorithmSupported("No such atomicity algorithm " + alg_type);
            }
        }

        return this.atomicity_register_client;
    }

    /**
     * An exception indicates that no such an algorithm is supported.
     *
     * @author hengxin
     * @date Jun 27, 2014
     */
    public class NoSuchAtomicAlgorithmSupported extends Exception {

        private static final long serialVersionUID = -4107923527979819074L;

        public NoSuchAtomicAlgorithmSupported(String msg) {
            super(msg);
        }

    }
}
