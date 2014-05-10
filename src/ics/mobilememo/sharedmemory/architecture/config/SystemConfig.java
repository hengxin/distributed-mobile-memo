/**
 * @author hengxin
 * @date May 8, 2014
 * @description configuration parameters for client/server: network communication and pid 
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
	 * my pid
	 * it should be unique in the whole system
	 * now, this requirement is enforced manually and in a centralized way
	 *   // TODO: to implement this requirement automatically and in a distributed way
	 */
	private int pid = -1;
	
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

	/**
	 * @return {@link #pid}: id of the process as a client or server
	 */
	public int getPid()
	{
		return pid;
	}
	
	/**
	 * set {@link #pid}
	 * @param pid pid to set
	 */
	public void setPid(int pid)
	{
		this.pid = pid;
	}
}
