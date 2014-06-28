/**
 * @author hengxin
 * @date May 12, 2014
 * @description {@link LoginActivity} shows and handles with the login activity of the users.
 * 
 *  // TODO: to enhance its functionality by 
 *  using an asynchronous login task to authenticate the user
 *  and showing a progress spinner
 *  you can find such an example in the automatically-generated code for login activities by eclipse
 */
package ics.mobilememo.login;

import ics.mobilememo.MobileMemoActivity;
import ics.mobilememo.R;
import ics.mobilememo.network.wifi.WifiAdmin;
import ics.mobilememo.sharedmemory.architecture.communication.MessagingService;
import ics.mobilememo.sharedmemory.atomicity.AtomicityRegisterClientFactory;
import ics.mobilememo.sharedmemory.atomicity.AtomicityRegisterClientFactory.NoSuchAtomicAlgorithmSupported;
import android.R.integer;
import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends Activity // implements OnItemSelectedListener
{
	private final static String TAG = LoginActivity.class.getName();
	
	// UI references for login
	private EditText etxt_node_id;
	private EditText etxt_node_name;
	private EditText etxt_node_ip;
	private Spinner spinner_algs = null;
	
	private SessionManager session;

//	private int alg_type = -1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		this.session = new SessionManager();
		
		/**
		 * set up the login form
		 */
		// node id: this should be unique globally.
		this.etxt_node_id = (EditText) findViewById(R.id.etxt_node__id);
		// node name: it is not necessary to be unique globally
		this.etxt_node_name = (EditText) findViewById(R.id.etxt_node_name);
		// ip address of node: it must be available
		this.etxt_node_ip = (EditText) findViewById(R.id.etxt_node_ip);
		// fill the ip address if possible
		this.etxt_node_ip.setText(new WifiAdmin(MobileMemoActivity.MOBILEMEMO_ACTIVITY).getIP());
		// set adapter for Spinner
		this.spinner_algs = (Spinner) findViewById(R.id.spinner_algs);
		ArrayAdapter<CharSequence> algs_adapter = ArrayAdapter.createFromResource(this,
		        R.array.spinner_algs_array, android.R.layout.simple_spinner_item);
		algs_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		this.spinner_algs.setAdapter(algs_adapter);
		
		// click the "Sign in" button
		findViewById(R.id.btn_sign_in).setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View view)
					{
						LoginActivity.this.attemptLogin();
					}
				});
		
		// TODO: click the "Exit System" button
		findViewById(R.id.btn_exit).setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View arg0)
			{
				
			}
		});
	}

	/**
	 * attempts to log in the system
	 * If there are form errors (missing fields, unavailable ip, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	private void attemptLogin()
	{
		// values for node identifier, node name, and node ip at the time of the login attempt.
		int node_id = -1;
		String node_name;
		String node_ip; 
		int alg_type = -1;
		
		// Reset errors.
		this.etxt_node_id.setError(null);
		this.etxt_node_name.setError(null);
		this.etxt_node_ip.setError(null);
		
		boolean cancel = false;
		View focusView = null;

		// check node_id
		String node_id_str = this.etxt_node_id.getText().toString();
		if (TextUtils.isEmpty(node_id_str))
		{
			this.etxt_node_name.setError(getString(R.string.error_field_required));
			focusView = etxt_node_name;
			cancel = true;
		}
		else 
			node_id = Integer.parseInt(node_id_str);
		
		// check node_name
		node_name = this.etxt_node_name.getText().toString();
		if (TextUtils.isEmpty(node_name))
		{
			this.etxt_node_name.setError(getString(R.string.error_field_required));
			focusView = etxt_node_name;
			cancel = true;
		}
		
		// check node_ip
		node_ip = this.etxt_node_ip.getText().toString();
		if (TextUtils.isEmpty(node_ip))
		{
			this.etxt_node_ip.setError(getString(R.string.error_field_required));
			focusView = this.etxt_node_ip;
			cancel = true;
		} else if (! new WifiAdmin(getApplicationContext()).isIPAvailable(node_ip))	// check whether the ip address is available or not
		{
			this.etxt_node_ip.setError(getString(R.string.error_unavailable_ip));
			focusView = etxt_node_ip;
			cancel = true;
		}

		// TODO: check algorithm type
		alg_type = this.getAlgType();
		if (alg_type == AtomicityRegisterClientFactory.NO_SUCH_ATOMICITY)
		{
			focusView = this.spinner_algs;
			cancel = true;
		}
		
		// an error here. don't attempt to login
		if (cancel)
			focusView.requestFocus();
		else	// TODO: show a progress spinner and kick off a background task to perform the user login attempt
		{
			Log.d(TAG, "To Create Login Session");
			
			this.session.createLoginSession(node_id, node_name, node_ip, alg_type);

			/**
			 * @author hengxin
			 * @date May 15, 2014
			 * 
			 * Warning: don't use Intent here to start {@link MobileMemoActivity}
			 * it has already been in the Activity stack
			 */
            finish();
            
    		// start to listen to connections; ready to be a server
    		MessagingService.INSTANCE.new ServerTask().execute(node_ip);
    		Log.d(TAG, "Start as a server");
    		// ready to be a client
    		try
			{
				AtomicityRegisterClientFactory.INSTANCE.setAtomicityRegisterClient(alg_type);
			} catch (NoSuchAtomicAlgorithmSupported nsaase)
			{
				nsaase.printStackTrace();
			}
    		
		}
	}
	
    /**
     * the following two methods are from the interface @link AdapterView.OnItemSelectedListener
     */
    
	/**
	 * get the type of the chosen algorithm
	 */
	public int getAlgType()
	{
		String spinner_alg = this.spinner_algs.getSelectedItem().toString();
		int alg_type;
		System.out.println("The chosen algorithm is: " + spinner_alg);
		if (spinner_alg.equals("SWMR_ATOMICITY"))
			alg_type  = AtomicityRegisterClientFactory.SWMR_ATOMICITY;
		else if (spinner_alg.equals("SWMR_2ATOMICITY"))
			alg_type = AtomicityRegisterClientFactory.SWMR_2ATOMICITY;
		else 
			alg_type = AtomicityRegisterClientFactory.MWMR_ATOMICITY;
		
		return alg_type;
	}
	
//    /**
//     * get the selected algorithm 
//     */
//	@Override
//	public void onItemSelected(AdapterView<?> parent, View view, int position,
//			long id)
//	{
//		String spinner_alg = parent.getItemAtPosition(position).toString();
//		System.out.println("The chosen algorithm is: " + spinner_alg);
//		if (spinner_alg.equals("SWMR_ATOMICITY"))
//			this.alg_type  = AtomicityRegisterClientFactory.SWMR_ATOMICITY;
//		else if (spinner_alg.equals("SWMR_2ATOMICITY"))
//			this.alg_type = AtomicityRegisterClientFactory.SWMR_2ATOMICITY;
//		else 
//			this.alg_type = AtomicityRegisterClientFactory.MWMR_ATOMICITY;
//	}
//
//	/**
//	 * set default algorithm to run
//	 */
//	@Override
//	public void onNothingSelected(AdapterView<?> parent)
//	{
//		this.alg_type = AtomicityRegisterClientFactory.SWMR_ATOMICITY;
//	}
}
