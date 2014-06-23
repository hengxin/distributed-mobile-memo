package ics.mobilememo.benchmark.ui;

import ics.mobilememo.R;
import ics.mobilememo.benchmark.executor.Executor;
import ics.mobilememo.benchmark.workload.PoissonWorkloadGenerator;
import ics.mobilememo.benchmark.workload.Request;
import ics.mobilememo.execution.ExecutionLogHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import log4android.ConfigureLog4J;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;

public class BenchmarkFragment extends Fragment
{
	private static final String TAG = BenchmarkFragment.class.getName();
	
	private RadioGroup radio_grp_rw = null;
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
	

    private void addButtonListener(final View view)
    {
        /**
         * click the "Run Benchmark" button:
         * (1) configure a benchmark
         * (2) run the benchmark
         */
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
    	
    	/**
    	 * pre-processing the execution of benchmark for further use:
    	 * adjust the timestamps (i.e., start_time, finish_time) of operations
    	 * according to the offset of the system time of device to the prescribed "perfect time" (e.g., of a PC)
    	 */
    	view.findViewById(R.id.btn_exec_sync).setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// (1) get the time offset
				long offset = getTimeDiff();
				
				// (2) sync. the execution
				new ExecutionLogHandler(ConfigureLog4J.INSTANCE.getFileName()).sync(offset);
			}
		});
    }
    
    /**
     * retrieve "time diff value" from the sync_time.txt file 
     * @return time diff value in millisecond
     */
    private long getTimeDiff()
    {
		String sync_time_file_name = Environment.getExternalStorageDirectory() + File.separator + "sync_time.txt";
		BufferedReader br = null;
		long diff = 0;
		
		try
		{
			br = new BufferedReader(new FileReader(sync_time_file_name));
			// the first line reads like "diff 1000"
			String diff_line = br.readLine();
			diff = Integer.parseInt(diff_line.substring(5));
		} catch (FileNotFoundException fnfe)
		{
			fnfe.printStackTrace();
		} catch (IOException ioe)
		{
			ioe.printStackTrace();
		} finally
		{
			try
			{
				br.close();
			} catch (IOException ioe)
			{
				ioe.printStackTrace();
			}
		}
		
		return diff;
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
