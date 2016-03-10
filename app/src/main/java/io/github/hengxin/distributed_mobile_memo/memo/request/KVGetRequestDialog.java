/**
 * @author hengxin
 * @date May 9, 2014
 * @description {@link KVGetRequestDialog} is for the "get" request on server replica.
 */
package io.github.hengxin.distributed_mobile_memo.memo.request;

import io.github.hengxin.distributed_mobile_memo.R;
import io.github.hengxin.distributed_mobile_memo.sharedmemory.atomicity.AbstractAtomicityRegisterClient;
import io.github.hengxin.distributed_mobile_memo.sharedmemory.atomicity.AtomicityRegisterClientFactory;
import io.github.hengxin.distributed_mobile_memo.sharedmemory.data.kvs.VersionValue;

public class KVGetRequestDialog extends KVRequestDialog {
    /**
     * constructor of {@link KVGetRequestDialog}
     * set its title;
     * the value input box is not necessary
     *
     * @see KVRequestDialog#is_val_required
     */
    public KVGetRequestDialog() {
        super.dialog_title_id = R.string.kv_get_dialog_title;
    }

    /**
     * Perform the "GET" request.
     */
    @Override
    public VersionValue onRequestPerformed() {
        AbstractAtomicityRegisterClient client = null;
        try {
            client = AtomicityRegisterClientFactory.INSTANCE.getAtomicityRegisterClient();
        } catch (AtomicityRegisterClientFactory.NoSuchAtomicAlgorithmSupported nsaas) {
            nsaas.printStackTrace();
            System.exit(1);
        }

        return client.get(super.request_key);
    }
}
