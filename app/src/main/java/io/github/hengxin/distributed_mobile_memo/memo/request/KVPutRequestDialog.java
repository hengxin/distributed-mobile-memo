/**
 * @author hengxin
 * @date May 9, 2014
 * @description {@link KVPutRequestDialog} is for the "put" request on server replica.
 */
package io.github.hengxin.distributed_mobile_memo.memo.request;

import android.widget.Toast;

import io.github.hengxin.distributed_mobile_memo.MobileMemoActivity;
import io.github.hengxin.distributed_mobile_memo.R;
import io.github.hengxin.distributed_mobile_memo.sharedmemory.atomicity.AbstractAtomicityRegisterClient;
import io.github.hengxin.distributed_mobile_memo.sharedmemory.atomicity.AtomicityRegisterClientFactory;
import io.github.hengxin.distributed_mobile_memo.sharedmemory.data.kvs.VersionValue;

public class KVPutRequestDialog extends KVRequestDialog {

    /**
     * constructor of {@link KVPutRequestDialog}:
     * set its title;
     * the value input box is required
     *
     * @see KVRequestDialog#is_val_required
     */
    public KVPutRequestDialog() {
        super.dialog_title_id = R.string.kv_put_dialog_title;
        super.is_val_required = true;
    }

    /**
     * Perform the "PUT" request.
     */
    @Override
    public VersionValue onRequestPerformed() {
        Toast.makeText(MobileMemoActivity.MOBILEMEMO_ACTIVITY, "Put " + super.request_key.toString() + '[' + val_str + ']', Toast.LENGTH_SHORT).show();

        AbstractAtomicityRegisterClient client = null;
        try {
            client = AtomicityRegisterClientFactory.INSTANCE.getAtomicityRegisterClient();
        } catch (AtomicityRegisterClientFactory.NoSuchAtomicAlgorithmSupportedException nsaas) {
            nsaas.printStackTrace();
            System.exit(1);
        }

        return client.put(super.request_key, val_str);
    }

}
