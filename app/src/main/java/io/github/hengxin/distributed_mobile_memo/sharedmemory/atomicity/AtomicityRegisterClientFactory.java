package io.github.hengxin.distributed_mobile_memo.sharedmemory.atomicity;


/**
 * @author hengxin
 * @date Jun 27, 2014
 * @description Factory for creating atomicity register clients
 * according to the algorithm in which they are participating
 */
public enum AtomicityRegisterClientFactory {
    INSTANCE;

    public static final int NO_SUCH_ATOMICITY = -1;
    public static final int SWMR_ATOMICITY = 0;
    public static final int SWMR_2ATOMICITY = 1;
    public static final int MWMR_ATOMICITY = 2;

    private AbstractAtomicityRegisterClient atomicity_register_client = null;

    /**
     * instantiate appropriate {@link AbstractAtomicityRegisterClient} according to the type of the algorithm to run
     *
     * @param alg_type type of the algorithm to run
     * @throws NoSuchAtomicAlgorithmSupported exception indicating that no such atomicity algorithm is defined
     */
    public void setAtomicityRegisterClient(int alg_type) throws NoSuchAtomicAlgorithmSupported {
        switch (alg_type) {
            case AtomicityRegisterClientFactory.SWMR_ATOMICITY:
                this.atomicity_register_client = SWMRAtomicityRegisterClient.INSTANCE();
                break;
            case AtomicityRegisterClientFactory.SWMR_2ATOMICITY:
                this.atomicity_register_client = SWMR2AtomicityRegisterClient.INSTANCE();
                break;
            case AtomicityRegisterClientFactory.MWMR_ATOMICITY:
                this.atomicity_register_client = MWMRAtomicityRegisterClient.INSTANCE();
                break;
            default:
                throw new NoSuchAtomicAlgorithmSupported("No such atomicity algorithm " + alg_type);
        }
    }

    /**
     * @return {@link #atomicity_register_client}
     */
    public AbstractAtomicityRegisterClient getAtomicityRegisterClient() {
        return this.atomicity_register_client;
    }

    /**
     * An exception indicates that no such atomic algorithm is supported.
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
