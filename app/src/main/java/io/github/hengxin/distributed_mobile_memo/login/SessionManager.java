/**
 * @author hengxin
 * @date May 11, 2014
 * @description {@link SessionManager} manages user sessions,
 * including login action, login checking, and log-in information storage.
 * <p>
 * Note: the code is adapted from that in the site:
 * <url>http://www.androidhive.info/2012/08/android-session-management-using-shared-preferences/</url>
 */
package io.github.hengxin.distributed_mobile_memo.login;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import io.github.hengxin.distributed_mobile_memo.MobileMemoActivity;
import io.github.hengxin.distributed_mobile_memo.R;
import io.github.hengxin.distributed_mobile_memo.group.member.SystemNode;
import io.github.hengxin.distributed_mobile_memo.network.wifi.WifiAdmin;
import io.github.hengxin.distributed_mobile_memo.sharedmemory.architecture.communication.MessagingService;
import io.github.hengxin.distributed_mobile_memo.sharedmemory.atomicity.AtomicityRegisterClientFactory;
import io.github.hengxin.distributed_mobile_memo.sharedmemory.atomicity.AtomicityRegisterClientFactory.NoSuchAtomicAlgorithmSupported;

public class SessionManager {
    private static final String TAG = SessionManager.class.getName();

    private SharedPreferences pref;
    private static final String PREF_FILE_NAME = "MobileMemoPref";
    // Shared pref mode
    private int PRIVATE_MODE = 0;
    // Editor for Shared preferences
    private Editor editor;

    private Context _context = MobileMemoActivity.MOBILEMEMO_ACTIVITY;
    ;

    private SystemNode system_node = new SystemNode();

    // pid [required]
    public static final String KEY_NODE_ID = "PID";

    // name [required]
    public static final String KEY_NODE_NAME = "NAME";

    // ip [required]
    public static final String KEY_NODE_IP = "IP";

    // algorithm type [required]
    public static final String KEY_ALG_TYPE = "ALG_TYPE";

    /**
     * constructor of {@link SessionManager}
     */
    public SessionManager() {
        this.pref = this._context.getSharedPreferences(PREF_FILE_NAME, PRIVATE_MODE);
        this.editor = this.pref.edit();
    }

    /**
     * Create login session:
     * set login value as <code>true</code>;
     * store pid, name, ip, and algorithm type in {@link #pref}.
     *
     * @param pid pid (an integer) associated with {@link #KEY_NODE_ID}
     * @param name name (a string) associated with {@link #KEY_NODE_NAME}
     * @param ip ip address (ip format) associated with {@link #KEY_NODE_IP}
     * @param alg_type algorithm to run ({@link #KEY_ALG_TYPE})
     */
    public void createLoginSession(int pid, String name, String ip, int alg_type) {
        this.editor.putInt(KEY_NODE_ID, pid);
        this.editor.putString(KEY_NODE_NAME, name);
        this.editor.putString(KEY_NODE_IP, ip);
        this.editor.putInt(KEY_ALG_TYPE, alg_type);

        this.editor.commit();
    }

    /**
     * check user login status:
     * if false it will redirect the user to login page,
     * else it won't do anything
     * */
    public void checkLogin() {
        // the user has been logged in successfully
        if (this.isLoggedIn()) {
            Log.d(TAG, "the user has been logged in successfully");

            AlertDialog.Builder builder = new AlertDialog.Builder(this._context);

            builder.setTitle("Login Info")
                    .setMessage("You have logged in as: \n " + this.system_node.toString())
                    .setIcon(R.drawable.success)

                            // confirm the login information and then continue
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                            // start to listen to connections; ready to be a server
                            MessagingService.INSTANCE.new ServerTask().execute(SessionManager.this.system_node.getNodeIp());
                            Log.d(TAG, "Start as a server");
                            // ready to be a client
                            try {
                                AtomicityRegisterClientFactory.INSTANCE.setAtomicityRegisterClient(SessionManager.this.system_node.getAlgType());
                            } catch (NoSuchAtomicAlgorithmSupported nsaase) {
                                nsaase.printStackTrace();
                            }
                        }
                    })

                            // re-login: redirect to the {@link LoginActivity}
                    .setNegativeButton(R.string.login_info_relogin, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SessionManager.this.redirect2Login();
                        }
                    })
                    .show();
        } else {
            Log.d(TAG, "Not logged in yet. Redirect to Login screen");
            this.redirect2Login();
        }

    }

    /**
     * @return the identifier of the logged {@link SystemNode}
     */
    public int getNodeId() {
        return this.pref.getInt(KEY_NODE_ID, SystemNode.NODE_ID_DEFAULT);
    }

    /**
     * @return the name of the logged {@link SystemNode}
     */
    public String getNodeName() {
        return this.pref.getString(KEY_NODE_NAME, SystemNode.NODE_NAME_DEFAULT);
    }

    /**
     * @return the ip address of the logged {@link SystemNode}
     */
    public String getNodeIp() {
        return this.pref.getString(KEY_NODE_IP, SystemNode.NODE_IP_DEFAULT);
    }

    /**
     * Clear session details
     * // TODO: not used now
     */
    public void logoutUser() {
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        _context.startActivity(i);
    }

    /**
     * redirect the user to the login screen
     */
    private void redirect2Login() {
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
    public boolean isLoggedIn() {
        // check whether the login information is complete or not
        if (!this.isLoginInfoComplete())
            return false;
        // check whether the ip address is available
        return new WifiAdmin(this._context).isIPAvailable(this.system_node.getNodeIp());
    }

    /**
     * to check whether the login information is complete:
     * it should include pid, name, and ip address
     * during checking, it will also fill the {@link #system_node} field
     *
     * @return <code>true</code> if the login information is complete;
     * 	<code>false</code>, otherwise
     */
    private boolean isLoginInfoComplete() {
        // check node id
        int node_id = this.pref.getInt(KEY_NODE_ID, SystemNode.NODE_ID_DEFAULT);
        if (node_id == SystemNode.NODE_ID_DEFAULT)
            return false;
        this.system_node.setNodeId(node_id);

        // check node name
        String node_name = this.pref.getString(KEY_NODE_NAME, SystemNode.NODE_NAME_DEFAULT);
        if (node_name.equals(SystemNode.NODE_NAME_DEFAULT))
            return false;
        this.system_node.setNodeName(node_name);

        // check node ip
        String node_ip = this.pref.getString(KEY_NODE_IP, SystemNode.NODE_IP_DEFAULT);
        if (node_ip.equals(SystemNode.NODE_IP_DEFAULT))
            return false;
        this.system_node.setNodeIP(node_ip);

        // check algorithm type
        int alg_type = this.pref.getInt(KEY_ALG_TYPE, SystemNode.ALG_TYPE_DEFAULT);
        System.out.println(alg_type);
        System.out.println(SystemNode.ALG_TYPE_DEFAULT);
        if (alg_type == SystemNode.ALG_TYPE_DEFAULT)
            return false;
        this.system_node.setAlgType(alg_type);

        return true;
    }
}
