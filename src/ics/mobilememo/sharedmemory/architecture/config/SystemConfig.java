/**
 * @author hengxin
 * @date May 8, 2014
 * @description store the environment variables for network communication
 * 	which are global to the whole system
 */
package ics.mobilememo.sharedmemory.architecture.config;


public enum SystemConfig
{
	INSTANCE;
	
	/**
	 * default global port for communication: 1234
	 */
	public static final int NETWORK_PORT = 1234;
	/**
	 * timeout for connection
	 */
	public static final int TIMEOUT = 5000;
	
	/**
	 * my ip address
	 */
	private String ip = null;
	
	/**
	 * @return {@link #ip}: my ip address
	 */
	public String getIP()
	{
		return this.ip;
	}
	
	/**
	 * set my ip address {@link #ip}
	 * @param ip ip address to be used
	 */
	public void setIP(String ip)
	{
		this.ip = ip;
	}
}
