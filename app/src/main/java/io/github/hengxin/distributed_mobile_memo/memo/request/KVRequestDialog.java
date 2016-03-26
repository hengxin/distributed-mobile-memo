/**
 * @author hengxin
 * @date May 9, 2014
 * @description {@link KVRequestDialog} is used to collect necessary information
 * for issuing requests on server replicas.
 * The requests includes put, get, and remove.
 * {@link KVRequestDialog} is a generic one which can be instantiated as
 * {@link KVPutRequestDialog}, {@link KVGetRequestDialog}, and {@link KVRemoveRequestDialog}.
 * @see KVPutRequestDialog
 * @see KVGetRequestDialog
 * @see KVRemoveRequestDialog
 */
package io.github.hengxin.distributed_mobile_memo.memo.request;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import io.github.hengxin.distributed_mobile_memo.R;
import io.github.hengxin.distributed_mobile_memo.memo.MemoFragment;
import io.github.hengxin.distributed_mobile_memo.sharedmemory.data.kvs.Key;
import io.github.hengxin.distributed_mobile_memo.sharedmemory.data.kvs.VersionValue;

public abstract class KVRequestDialog extends DialogFragment {
    private static final String TAG = KVRequestDialog.class.getName();

    protected String key_str = null;
    protected String val_str = null;

    protected Key request_key = null;
    protected VersionValue vval_return = null;

    /**
     * {@link #val_str} is not necessary for either {@link KVGetRequestDialog} or
     * {@link KVRemoveRequestDialog} while it is required for {@link KVPutRequestDialog}.
     *
     * @see KVPutRequestDialog
     * @see KVGetRequestDialog
     * @see KVRemoveRequestDialog
     */
    protected boolean is_val_required = false;

    protected EditText etxt_key = null;
    protected EditText etxt_val = null;

    /**
     * resource id of the title of dialog
     */
    protected int dialog_title_id = -1;

    /**
     * create dialog by reusing the {@link AlertDialog}
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        /**
         *  inflate and set the layout for the dialog
         *  pass <code>null</code> as the parent view because its going in the dialog layout
         */
        View view = inflater.inflate(R.layout.layout_kv_dialog, null);
        this.etxt_key = (EditText) view.findViewById(R.id.etxt_kv_request_dialog_key);
        this.etxt_val = (EditText) view.findViewById(R.id.etxt_kv_request_dialog_value);
        if (!this.is_val_required)
            this.etxt_val.setEnabled(false);

        builder.setTitle(this.dialog_title_id);
        builder.setView(view)
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // simply close the dialog
                                KVRequestDialog.this.getDialog().dismiss();
                            }
                        })
                        // add action buttons
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            /**
                             * perform the corresponding request
                             */
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                /**
                                 * collecting information from input box:
                                 * #key_str and #val_str
                                 * and construct the {@link Key} from #key_str since it is fixed
                                 */
                                KVRequestDialog.this.key_str = KVRequestDialog.this.etxt_key.getText().toString();
                                if (KVRequestDialog.this.is_val_required)
                                    KVRequestDialog.this.val_str = KVRequestDialog.this.etxt_val.getText().toString();
                                KVRequestDialog.this.request_key = Key.parse(KVRequestDialog.this.key_str);

                                // invoke appropriate action for each request
                                VersionValue vval = KVRequestDialog.this.onRequestPerformed();

                                // close the dialog
                                KVRequestDialog.this.dismiss();

                                // notify the listener {@link MemoFragment}
                                ((IRequestResultListener) KVRequestDialog.this.getTargetFragment()).onRequestRusultReturned(request_key, vval);
                            }
                        });
        return builder.create();
    }

    /**
     * upon clicking the OK button, perform the corresponding request
     * @return {@link VersionValue} as the result of a request
     *
     * @see KVPutRequestDialog
     * @see KVGetRequestDialog
     * @see KVRemoveRequestDialog
     */
    public abstract VersionValue onRequestPerformed();

    /**
     * @author hengxin
     * @date May 10, 2014
     * @description Any class (such as {@link MemoFragment}) which wants
     * to do something upon the request result should implement
     * this interface {@link IRequestResultListener}.
     *
     * @see {@link MemoFragment}
     */
    public interface IRequestResultListener {
        /**
         * implement this method to get and handle with the request result
         * @param key {@link Key} to return
         * @param vval {@link VersionValue} to return
         */
        public void onRequestRusultReturned(Key key, VersionValue vval);
    }
}
