/**
 * @author hengxin
 * @date May 10, 2014
 * @description pop the {@link JoinGroupDialog} to add a new server replica
 * this dialog is attached to {@link GroupFragment} and the latter implements the
 * {@link IJoinGroupListener} interface to retrieve the information collected in the dialog
 * @see GroupFragment
 * @see IJoinGroupListener
 */
package io.github.hengxin.distributed_mobile_memo.group;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import io.github.hengxin.distributed_mobile_memo.R;
import io.github.hengxin.distributed_mobile_memo.group.member.SystemNode;

public class JoinGroupDialog extends DialogFragment {
    /**
     * ip of the replica which joins the group
     */
    private String replica_ip = null;
    /**
     * name of the replica which joins the group
     */
//	private String replica_name = null;

    private EditText etxt_replica_ip = null;
//	private EditText etxt_replica_name = null;

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
        View view = inflater.inflate(R.layout.layout_join_group_dialog, null);
        this.etxt_replica_ip = (EditText) view.findViewById(R.id.etxt_replica_ip);
//        this.etxt_replica_name = (EditText) view.findViewById(R.id.etxt_replica_name);

        builder.setTitle(R.string.join_group_dialog_title);
        builder.setView(view)
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // simply close the dialog
                                JoinGroupDialog.this.getDialog().dismiss();
                            }
                        })
                        // add action buttons
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            /**
                             * collecting information and invoke {@link GroupFragment}
                             * which has implemented
                             */
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                /**
                                 * collecting information from input box:
                                 * #key_str and #val_str
                                 * and construct the {@link Key} from #key_str since it is fixed
                                 */
                                JoinGroupDialog.this.replica_ip = JoinGroupDialog.this.etxt_replica_ip.getText().toString();
//								JoinGroupDialog.this.replica_name = JoinGroupDialog.this.etxt_replica_name.getText().toString();

                                // close the dialog
                                JoinGroupDialog.this.dismiss();

                                // notify the listener (here, it is {@link GroupFragment})
                                ((IJoinGroupListener) JoinGroupDialog.this.getTargetFragment()).onJoinGroup(
                                        new SystemNode(JoinGroupDialog.this.replica_ip /*, JoinGroupDialog.this.replica_name */));
                            }
                        });
        return builder.create();
    }

    /**
     * @author hengxin
     * @date May 10, 2014
     * @description the object which cares about the "add new server replica" action
     *  should implement this {@link IJoinGroupListener} interface
     *
     * @see GroupFragment
     */
    public interface IJoinGroupListener {
        public void onJoinGroup(SystemNode replica);
    }
}
