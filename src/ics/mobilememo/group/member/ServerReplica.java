/**
 * @author hengxin
 * @date May 9, 2014
 * @description {@link ServerReplica} represents a data replica as a server
 */
package ics.mobilememo.group.member;

import java.io.Serializable;

public class ServerReplica implements Serializable
{
	private static final long serialVersionUID = 2642974560070519463L;

	/**
	 * ip address
	 */
	private final String ip;
	/**
	 * name of the {@link ServerReplica}
	 */
	private final String name;

	/**
	 * constructor of {@link ServerReplica} with only {@link #ip}
	 * the unspecified name is set to "DUMMY"
	 * @param ip {@link #ip}: ip address
	 */
	public ServerReplica(String ip)
	{
		this.ip = ip;
		this.name = "DUMMY";
	}

	/**
	 * constructor of {@link ServerReplica} with both {@link #ip} and {@link #name}
	 * @param ip
	 * @param name
	 */
	public ServerReplica(String ip, String name)
	{
		this.ip = ip;
		this.name = name;
	}

	/**
	 * @return {@link #ip}: ip address of the {@link ServerReplica}
	 */
	public String getIp()
	{
		return ip;
	}

	/**
	 * @return {@link #name}: name of the {@link ServerReplica}
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * string format: {@link #name} @ {@link #ip}
	 */
	@Override
	public String toString()
	{
		return name + '@' + ip;
	}
}
