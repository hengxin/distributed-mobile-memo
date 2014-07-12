package ics.mobilememo.benchmark.ui;

import ics.mobilememo.R;
import ics.mobilememo.benchmark.executor.Executor;
import ics.mobilememo.benchmark.workload.PoissonWorkloadGenerator;
import ics.mobilememo.benchmark.workload.Request;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

/**
 * Configure the benchmark workloads and run them
 * @author hengxin
 * @date Jun 02, Jun 28, 2014
 */
public class BenchmarkFragment extends Fragment // implements OnItemSelectedListener 
{
	private static final String TAG = BenchmarkFragment.class.getName();
	
	// as a reader or a writer
	private RadioGroup radio_grp_rw = null;
	
	private EditText etxt_request_number = null;
	private EditText etxt_rate = null;
	private EditText etxt_key_range = null;
	private EditText etxt_value_range = null;
	private Button btn_run_benchmark = null;
	
	// TextView to show whether the execution has been generated or not
	private TextView txt_exec_ready = null;
//	private Button btn_exec_sync = null;
	
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
		this.btn_run_benchmark = (Button) view.findViewById(R.id.btn_run);
		
		/**
		 * default values for test
		 */
		this.etxt_request_number.setText("1000");
		this.etxt_rate.setText("10");
		this.etxt_key_range.setText("1");
		this.etxt_value_range.setText("5");
		
		this.txt_exec_ready = (TextView) view.findViewById(R.id.txt_exec_ready);
//		this.btn_exec_sync = (Button) view.findViewById(R.id.btn_exec_sync);
//		this.btn_exec_sync.setEnabled(false);
		
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
    	this.btn_run_benchmark.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				txt_exec_ready.setText(R.string.txt_exec_not_ready);
				btn_run_benchmark.setEnabled(false);
				
				// collect the configurations of this benchmark
				
				int role = BenchmarkFragment.this.getRoleChosen(v);
				int total_requests = Integer.parseInt(BenchmarkFragment.this.etxt_request_number.getText().toString());
				int rate = Integer.parseInt(BenchmarkFragment.this.etxt_rate.getText().toString());
				int key_range = Integer.parseInt(BenchmarkFragment.this.etxt_key_range.getText().toString());
				int value_range = Integer.parseInt(BenchmarkFragment.this.etxt_value_range.getText().toString());
				
				// establish the queue of requests between {@link PoissonWorkloadGenerator} and {@link Executor}
				BlockingQueue<Request> request_queue = new LinkedBlockingDeque<Request>();
				
				// start executor {@link Executor}
				Thread executor_thread = new Thread(new Executor(request_queue, total_requests));
				executor_thread.start();
				
				// block the {@link Executor} for a while
				try
				{
					Thread.sleep((int )(Math.random() * 100 + 1));
				} catch (InterruptedException ie)
				{
					ie.printStackTrace();
				}
				
				// start workload {@link PoissonWorkload}
				Thread workload_thread = new Thread(new PoissonWorkloadGenerator(request_queue, role, total_requests, rate, key_range, value_range));
				workload_thread.start();
				
				// wait for the completion of this benchmark 
				try
				{
					workload_thread.join();
					executor_thread.join();
				} catch (InterruptedException ie)
				{
					ie.printStackTrace();
				}
				
				// another benchmark can be configured and run
				btn_run_benchmark.setEnabled(true);
				
				// the pre-processing can now be performed on the generated execution
				txt_exec_ready.setText(R.string.txt_exec_ready);
//				btn_exec_sync.setEnabled(true);
			}
		});
    	
//    	/**
//    	 * pre-processing the execution of benchmark for further use:
//    	 * adjust the timestamps (i.e., start_time, finish_time) of operations
//    	 * according to the offset of the system time of device to the prescribed "perfect time" (e.g., of a PC)
//    	 */
//    	this.btn_exec_sync.setOnClickListener(new OnClickListener()
//		{
//			@Override
//			public void onClick(View v)
//			{
//				btn_exec_sync.setEnabled(false);
//				
//				// (1) get the time offset
//				long offset = getTimeDiff();
//				
//				// (2) sync. the execution
//				new ExecutionLogHandler(ConfigureLog4J.INSTANCE.getFileName()).sync(offset);
//				
//				Toast.makeText(getActivity(), "Sync. is finished.", Toast.LENGTH_SHORT).show();
//			}
//		});
    }
    
//    /**
//     * retrieve "time diff value" from the sync_time.txt file 
//     * @return time diff value in millisecond
//     */
//    private long getTimeDiff()
//    {
//		String sync_time_file_name = Environment.getExternalStorageDirectory() + File.separator + "sync_time.txt";
//		BufferedReader br = null;
//		long diff = 0;
//		
//		try
//		{
//			br = new BufferedReader(new FileReader(sync_time_file_name));
//			// the first line reads like "diff 1000"
//			String diff_line = br.readLine();
//			diff = Integer.parseInt(diff_line.substring(5));
//		} catch (FileNotFoundException fnfe)
//		{
//			fnfe.printStackTrace();
//		} catch (IOException ioe)
//		{
//			ioe.printStackTrace();
//		} finally
//		{
//			try
//			{
//				br.close();
//			} catch (IOException ioe)
//			{
//				ioe.printStackTrace();
//			}
//		}
//		
//		return diff;
//    }
    
    /**
     * @param v view
     * @return the role ( {@value Request#WRITE_TYPE} or {@value Request#READ_TYPE} ) chosen
     */
    private int getRoleChosen(View v)
    {
    	int roleId = this.radio_grp_rw.getCheckedRadioButtonId();
		
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
