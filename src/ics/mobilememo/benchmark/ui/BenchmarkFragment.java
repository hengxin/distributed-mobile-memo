package ics.mobilememo.benchmark.ui;

import ics.mobilememo.R;
import ics.mobilememo.benchmark.executor.Executor;
import ics.mobilememo.benchmark.workload.PoissonWorkloadGenerator;
import ics.mobilememo.benchmark.workload.Request;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import org.apache.log4j.Logger;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class BenchmarkFragment extends Fragment
{
	private final Logger log4android = Logger.getLogger(Executor.class);
	private static final String TAG = BenchmarkFragment.class.getName();
	
	private RadioGroup radio_grp_rw = null;
	private RadioButton radio_role = null;
	private EditText etxt_request_number = null;
	private EditText etxt_rate = null;
	private EditText etxt_key_range = null;
	private EditText etxt_value_range = null;
	
	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public BenchmarkFragment()
	{
	}

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_benchmark, container, false);

		this.radio_grp_rw = (RadioGroup) view.findViewById(R.id.radio_grp_rw);
		this.etxt_request_number = (EditText) view.findViewById(R.id.etxt_request_number);
		this.etxt_rate = (EditText) view.findViewById(R.id.etxt_rate);
		this.etxt_key_range = (EditText) view.findViewById(R.id.etxt_key_range);
		this.etxt_value_range = (EditText) view.findViewById(R.id.etxt_value_range);
		
		// handle with the click of the "Run the benchmark" button
		this.addButtonListener(view);
		
		return view;
	}
	
    /**
     * run the benchmark
     */
    private void addButtonListener(final View view)
    {
    	view.findViewById(R.id.btn_run).setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				int role = BenchmarkFragment.this.getRoleChosen(v);
				int total_requests = Integer.parseInt(BenchmarkFragment.this.etxt_request_number.getText().toString());
				int rate = Integer.parseInt(BenchmarkFragment.this.etxt_rate.getText().toString());
				int key_range = Integer.parseInt(BenchmarkFragment.this.etxt_key_range.getText().toString());
				int value_range = Integer.parseInt(BenchmarkFragment.this.etxt_value_range.getText().toString());
				
				// establish the queue of requests between {@link PoissonWorkloadGenerator} and {@link Executor}
				BlockingQueue<Request> request_queue = new LinkedBlockingDeque<Request>();
				
				// start executor {@link Executor}
				(new Thread(new Executor(request_queue))).start();
				// block the {@link Executor} for a while
				try
				{
					Thread.sleep(5000);
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				// start workload {@link PoissonWorkload}
				(new Thread(new PoissonWorkloadGenerator(request_queue, role, total_requests, rate, key_range, value_range))).start();
			}
		});
    }
    
    /**
     * @param v view
     * @return the role ( {@value Request#WRITE_TYPE} or {@value Request#READ_TYPE} ) chosen
     */
    private int getRoleChosen(View v)
    {
    	int roleId = this.radio_grp_rw.getCheckedRadioButtonId();
		Log.d(TAG, "Role: " + roleId);
		
		switch (roleId)
		{
			case R.id.radio_reader:
				return Request.READ_TYPE;

			case R.id.radio_writer:
				return Request.WRITE_TYPE;
				
			default:
				return -1;
		}
    }
}
