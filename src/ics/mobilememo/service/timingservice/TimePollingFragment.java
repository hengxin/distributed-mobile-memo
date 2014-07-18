package ics.mobilememo.service.timingservice;

import ics.mobilememo.R;
import ics.mobilememo.script.ADBExecutor;
import ics.mobilememo.service.timingservice.message.AuthMsg;
import ics.mobilememo.service.timingservice.message.Message;
import ics.mobilememo.service.timingservice.message.ResponseTimeMsg;
import ics.mobilememo.utility.socket.SocketUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Providing time polling service.
 * The time can be local or remote from a PC.
 * 
 * This is migrated from the "android-usb-computer" project
 * 
 * @author hengxin
 * @date Jul 17, 2014
 */
public class TimePollingFragment extends Fragment implements OnClickListener 
{
	private static final String TAG = TimePollingFragment.class.getName();
	
	private static final Executor exec = Executors.newCachedThreadPool();
	
	/**
	 * ServerSocket on the side of Android device
	 */
	private ServerSocket server_socket = null;
	
	// UI elements 
	private Button btn_start_time_poll = null;
	private Button btn_time_poll = null;
	private TextView txt_timing_log = null;

	/**
	 * default constructor of {@link TimePollingFragment}
	 */
	public TimePollingFragment()
	{
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.fragment_timing, container, false);
		
		// button "Start Polling"
		this.btn_start_time_poll = (Button) v.findViewById(R.id.btn_start_polling);
		this.btn_start_time_poll.setOnClickListener(this);
		
		// button "Time Polling"
		this.btn_time_poll = (Button) v.findViewById(R.id.btn_time_polling);
		this.btn_time_poll.setOnClickListener(this);
		this.btn_time_poll.setEnabled(false);
		
		// text view "Timing Service Log"
		this.txt_timing_log = (TextView) v.findViewById(R.id.txt_timing_log);
		
		return v;
	}

	/**
	 * handle with the click events on buttons.
	 */
	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.btn_start_polling:
				exec.execute(this.TimePollingDaemon);
				Toast.makeText(getActivity(), "Starting polling time", Toast.LENGTH_SHORT).show();
				this.txt_timing_log.setText(getString(R.string.txt_timing_log_start));
				this.btn_start_time_poll.setEnabled(false);
				break;
				
			case R.id.btn_time_polling:
				TimingService.INSTANCE.pollingTime();
				break;
				
			default:
				break;
		}
	}

	// Daemon thread for establishing and maintaining the time polling connection
	private Runnable TimePollingDaemon = new Runnable()
	{
		public void run()
		{
			establishDeviceHostConnection();
		}
	};
	
	/**
	 * Starting as a ServerSocket. 
	 * Listen to client Socket, accept, and store it for further communication.
	 */
	private void establishDeviceHostConnection()
	{
		if (this.server_socket != null)
		{
			Log.d(TAG, "Server socket has been already created. Do not create it again.");
			return;
		}
		
		try
		{
			this.server_socket = new ServerSocket();
			this.server_socket.bind(new InetSocketAddress("localhost", ADBExecutor.ANDROID_PORT));
			
			TimingService.INSTANCE.setHostSocket(server_socket.accept());
			
			// receive (and consume) {@link AuthMsg} from PC and enable the time-polling functionality.
			TimingService.INSTANCE.receiveAuthMsg();
			this.afterPermissionGranted();
		} catch (IOException ioe)
		{
			ioe.printStackTrace();
		}
	}

	/**
	 * Change states of UI elements to indicate that {@link AuthMsg} has been received
	 * and permission of polling time has been granted.
	 */
	private void afterPermissionGranted()
	{
		getActivity().runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				btn_time_poll.setEnabled(true);
				txt_timing_log.setText(getString(R.string.txt_timing_log_polling));
			}
		});
	}
	
	/**
	 * Called when the "Back button" on mobile phone is clicked;
	 * Release the server socket (if it exists) before exiting
	 */
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		
		Toast.makeText(getActivity(), "Close the network socket and exit.", Toast.LENGTH_SHORT).show();
		
		if (this.server_socket != null && ! this.server_socket.isClosed())
		{
			try
			{
				this.server_socket.close();
			} catch (IOException ioe)
			{
				ioe.printStackTrace();
			}
		}
	}
	
}
