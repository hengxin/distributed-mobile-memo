/**
 * @author hengxin
 * @date May 9, 2014
 * @description {@link WifiAdmin} handles with the issues related to Wi-Fi networking,
 * 	including establishing, maintaining, retrieving Wi-Fi connection information.
 */
package ics.mobilememo.network.wifi;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteOrder;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.nfc.Tag;
import android.util.Log;

public class WifiAdmin
{
	private static final String TAG = WifiAdmin.class.getName();
	
	private final Context context;
	private final WifiManager wifi_manager;
	private WifiInfo wifi_info;
//	private final ConnectivityManager connectivity_manager;

	/**
	 * initialize 
	 * @param context {@link Context}
	 */
	public WifiAdmin(Context context)
	{
		this.context = context;
		this.wifi_manager = (WifiManager) this.context.getSystemService(Context.WIFI_SERVICE);
		
		Log.d(TAG, "The wifi manager now is " + this.wifi_manager);
	}
	
	/**
	 * enable Wi-Fi
	 */
	public void enableWiFi()
	{
		if (!this.wifi_manager.isWifiEnabled())
			this.wifi_manager.setWifiEnabled(true);
	}
	
	/**
	 * @return the ip address (in standard String format) in Wi-Fi network
	 * 	<code>null</code> if no Wi-Fi connection has been established.
	 */
	public String getIP()
	{
		if (wifi_manager != null)
		{
			this.wifi_info = this.wifi_manager.getConnectionInfo();
			Log.d(TAG, "The wifi info is now " + this.wifi_info);
			int ipAddress = this.wifi_info.getIpAddress();
			Log.d(TAG, "The Integer format of ip address is " + ipAddress);
			return this.formatIP(ipAddress);
		}
		
		return null;
	}
	
	/**
	 * to check whether an ip address is available now
	 * @param ip ip address to check
	 * @return <code>true</code> if the ip address is available;
	 * 	<code>false</code>, otherwise. 
	 */
	public boolean isAvailable(String ip)
	{
		String wifi_ip = this.getIP();
		Log.d(TAG, "The wifi ip is now " + wifi_ip);
		return wifi_ip.equals(ip);
	}
	
	/**
	 * convert ip address in Integer format to the one in String format
	 * @param ipAddress ip address in Integer format
	 * @return ip address in String format
	 * 
	 * license: http://stackoverflow.com/q/16730711/1833118
	 */
	private String formatIP(int ipAddress)
	{
		// convert little-endian to big-endianif if needed
	    if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) 
	        ipAddress = Integer.reverseBytes(ipAddress);

	    byte[] ipByteArray = BigInteger.valueOf(ipAddress).toByteArray();

	    String ipAddressString;
	    try 
	    {
	        ipAddressString = InetAddress.getByAddress(ipByteArray).getHostAddress();
	    } catch (UnknownHostException ex) 
	    {
	        Log.e("WIFIIP", "Unable to get host address.");
	        ipAddressString = null;
	    }

	    Log.d(TAG, "The String format of the ip address is " + ipAddressString);
	    return ipAddressString;
	}
}
