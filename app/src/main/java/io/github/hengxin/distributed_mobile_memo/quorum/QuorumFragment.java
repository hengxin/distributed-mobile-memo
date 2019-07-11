package io.github.hengxin.distributed_mobile_memo.quorum;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import io.github.hengxin.distributed_mobile_memo.R;
import io.github.hengxin.distributed_mobile_memo.sharedmemory.atomicity.AbstractAtomicityRegisterClient;
import io.github.hengxin.distributed_mobile_memo.sharedmemory.atomicity.AtomicityRegisterClientFactory;

/**
 * {@link QuorumFragment} shows UI for adjusting sizes of read/write quorums.
 *
 * @see QuorumFragment
 * @author hengxin
 */
public class QuorumFragment extends Fragment {
    private static final String TAG = QuorumFragment.class.getName();

    private NumberPicker np_read_quorum;
    private NumberPicker np_write_quorum;

    private Button btn_quorum_default;
    private Button btn_quorum_done;

    private TextView txt_quorum_info;

    private QuorumSystem default_quorum_system;
    private QuorumSystem done_quorum_system;

    private AbstractAtomicityRegisterClient client;

    public QuorumFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quorum, container, false);

        this.np_read_quorum = (NumberPicker) view.findViewById(R.id.np_read_quorum);
        this.np_read_quorum.setOnValueChangedListener(np_value_change_listener);
        np_read_quorum.setEnabled(false);

        this.np_write_quorum = (NumberPicker) view.findViewById(R.id.np_write_quorum);
        this.np_write_quorum.setOnValueChangedListener(np_value_change_listener);
        np_write_quorum.setEnabled(false);

        this.btn_quorum_default = (Button) view.findViewById(R.id.btn_quorum_default);
        btn_quorum_default.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                useDefaultQuorumSystem();
            }
        });

        this.btn_quorum_done = (Button) view.findViewById(R.id.btn_quorum_done);
        btn_quorum_done.setEnabled(false);

        this.txt_quorum_info = (TextView) view.findViewById(R.id.txt_quorum_info);

        return view;
    }

    /**
     * Use default quorum system specified by each algorithm/protocol to render {@link #np_read_quorum}
     * and {@link #np_write_quorum} whose values can then be adjusted.
     */
    private void useDefaultQuorumSystem() {
        if (this.default_quorum_system == null)
            try {
                this.client = AtomicityRegisterClientFactory.INSTANCE.getAtomicityRegisterClient();
                this.default_quorum_system = client.initQuorumSystem();
            } catch (AtomicityRegisterClientFactory.NoSuchAtomicAlgorithmSupportedException naase) {
                naase.printStackTrace();
                System.exit(1);
            }

        this.np_read_quorum.setEnabled(true);
        np_read_quorum.setMaxValue(default_quorum_system.getReplicaSize());
        np_read_quorum.setMinValue(default_quorum_system.getReadQuorumMinSize());
        np_read_quorum.setValue(default_quorum_system.getReadQuorumSize());
        np_read_quorum.setWrapSelectorWheel(true);

        this.np_write_quorum.setEnabled(true);
        np_write_quorum.setMaxValue(default_quorum_system.getReplicaSize());
        np_write_quorum.setMinValue(default_quorum_system.getWriteQuorumMinSize());
        np_write_quorum.setValue(default_quorum_system.getWriteQuorumSize());
        np_write_quorum.setWrapSelectorWheel(true);

        client.setQuorumSystem(default_quorum_system);
        this.txt_quorum_info.setText(this.default_quorum_system.toString());

        this.btn_quorum_done.setEnabled(true);
        this.done_quorum_system = this.default_quorum_system.copy();

        this.btn_quorum_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                done_quorum_system.setReadQuorumSize(np_read_quorum.getValue());
                done_quorum_system.setWriteQuorumSize(np_write_quorum.getValue());
                client.setQuorumSystem(done_quorum_system);
                txt_quorum_info.setText(done_quorum_system.toString());
            }
        });
    }

    private NumberPicker.OnValueChangeListener np_value_change_listener =
            new NumberPicker.OnValueChangeListener() {
                @TargetApi(Build.VERSION_CODES.KITKAT)
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    txt_quorum_info.setText("Press button \" Done \" to accept new values." + System.lineSeparator() +
                            "Press \" Use Default \"");
                }
            };
}
