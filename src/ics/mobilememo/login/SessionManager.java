/**
 * @author hengxin
 * @date May 11, 2014
 * @description {@link SessionManager} manages user sessions,
 * including login action, login checking, and log-in information storage.
 * 
 * Note: the code is adapted from that in the site: 
 * <url>http://www.androidhive.info/2012/08/android-session-management-using-shared-preferences/</url>
 */
package ics.mobilememo.login;

import ics.mobilememo.R;
import ics.mobilememo.group.member.SystemNode;
import ics.mobilememo.network.wifi.WifiAdmin;

import java.util.HashMap;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SessionManager
{
	private SharedPreferences pref;
	private static final String PREF_FILE_NAME = "MobileMemoPref";
	// Shared pref mode
	private int PRIVATE_MODE = 0;
	// Editor for Shared preferences
	private Editor editor;

	private Context _context;

	private SystemNode system_node = new SystemNode();
	
	// pid [required]
	public static final String KEY_NODE_ID = "pid";
	public static final int DEFAULT_PID = -1;
	
	// name [required]
	public static final String KEY_NODE_NAME = "name";
	
	// ip [required]
	public static final String KEY_NODE_IP = "ip"; 
	
	/**
	 * indicates whether the necessary [required] login information,
	 * including {@link #KEY_NODE_ID}, {@link #KEY_NODE_NAME}, and {@link #KEY_NODE_IP},
	 * is complete or not
	 * Note that the associated value of {@link #IS_LOGIN_INFO_COMPLETE} is <code>true</code>
	 * does not necessarily imply that the user has logged on successfully.
	 * We have to check whether the ip address associated with {@link #KEY_NODE_IP}
	 * is available.
	 * 
	 * @see SessionManager#isLoggedIn()
	 */
	private static final String IS_LOGIN_INFO_COMPLETE = "IsLogInInfoComplete";

	/**
	 * constructor of {@link SessionManager}
	 * @param context {@link Context} in which the {@link SessionManager} works
	 */
	public SessionManager(Context context)
	{
		this._context = context;
		this.pref = this._context.getSharedPreferences(PREF_FILE_NAME, PRIVATE_MODE);
		this.editor = this.pref.edit();
	}

	/**
	 * Create login session:
	 * store login value as <code>true</code>;
	 * store pid, name, and ip information in {@link #pref}.
	 * 
	 * @param pid pid (an integer) associated with {@link #KEY_NODE_ID}
	 * @param name name (a string) associated with {@link #KEY_NODE_NAME}
	 * @param ip ip address (ip format) associated with {@link #KEY_NODE_IP}
	 */
	public void createLoginSession(int pid, String name, String ip)
	{
		editor.putInt(KEY_NODE_ID, pid);
		this.editor.putString(KEY_NODE_NAME, name);
		editor.putString(KEY_NODE_IP, ip);
		
//		editor.putBoolean(IS_LOGIN_INFO_COMPLETE, true);

		editor.commit();
	}

	/**
	 * check user login status:
	 * if false it will redirect the user to login page,
	 * else it won't do anything
	 * */
	public void checkLogin()
	{
		// the user has been logged in successfully
		if (this.isLoggedIn())
		{
			AlertDialog dialog = null;
			AlertDialog.Builder builder = new AlertDialog.Builder(this._context);
			
			builder.setTitle("Login Info")
				.setMessage("You have logged in as: \n " + this.system_node.toString())
				.setIcon(R.drawable.success);

			// continue 
			builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					dialog.dismiss();
				}
			});
			
			// relogin: redirect to the {@link LoginActivity}
			builder.setNegativeButton(R.string.login_info_relogin, new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					SessionManager.this.redirect2Login();
				}
			});
			
			dialog = builder.create();
		}
		else
			this.redirect2Login();
	}

	/**
	 * Get stored session data
	 * */
	public HashMap<String, String> getUserDetails()
	{
		HashMap<String, String> user = new HashMap<String, String>();
		// user name
		user.put(KEY_NODE_ID, pref.getString(KEY_NODE_ID, null));

		// user email id
		user.put(KEY_NODE_NAME, pref.getString(KEY_NODE_NAME, null));

		// return user
		return user;
	}

	/**
	 * Clear session details
	 * */
	public void logoutUser()
	{
		// Clearing all data from Shared Preferences
		editor.clear();
		editor.commit();

		// After logout redirect user to Loing Activity
		Intent i = new Intent(_context, LoginActivity.class);
		// Closing all the Activities
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		// Add new Flag to start new Activity
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		// Staring Login Activity
		_context.startActivity(i);
	}

	/**
	 * redirect the user to the login screen
	 */
	private void redirect2Login()
	{
		Intent intent = new Intent(_context, LoginActivity.class);

		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		this._context.startActivity(intent);
	}
	
	/**
	 * check whether the user is logged on or not
	 * It checks not only the required fields like pid, name, and ip,
	 * but also whether the ip address associated with {@link #KEY_NODE_IP}
	 * is available or not.
	 * 
	 * @return <code>true</code>, if the user has been logged on; 
	 * 	<code>false</code>, otherwise.
	 */
	public boolean isLoggedIn()
	{
		/**
		 * to check whether the login information is complete or not
		 */
		if (! this.isLoginInfoComplete())
			return false;
		/**
		 * to check whether the ip address is available 
		 */
		return new WifiAdmin(this._context).isAvailable(this.system_node.getNodeIp());
	}
	
	/**
	 * to check whether the login information is complete:
	 * it should include pid, name, and ip address
	 * during checking, it will also fill the {@link #system_node} field
	 * 
	 * @return <code>true</code> if the login information is complete;
	 * 	<code>false</code>, otherwise
	 */
	private boolean isLoginInfoComplete()
	{
		int node_id = this.pref.getInt(KEY_NODE_ID, SystemNode.NODE_ID_DEFAULT);
		if (node_id == SystemNode.NODE_ID_DEFAULT) 
			return false;
		this.system_node.setNodeId(node_id);

		String node_name = this.pref.getString(KEY_NODE_NAME, SystemNode.NODE_NAME_DEFAULT);
		if (node_name.equals(SystemNode.NODE_NAME_DEFAULT))
			return false;
		this.system_node.setNodeName(node_name);
		
		String node_ip = this.pref.getString(KEY_NODE_IP, SystemNode.NODE_IP_DEFAULT);
		if (node_ip.equals(SystemNode.NODE_IP_DEFAULT))
			return false;
		this.system_node.setNodeIP(node_ip);
		
		return true;
	}
}
