/**
 * @author hengxin
 * @date May 9, 2014
 * @description {@link SystemNode} represents a physical node in the system.
 *  It has an identifier, a name, and an available ip address.
 */
package ics.mobilememo.group.member;

import ics.mobilememo.sharedmemory.atomicity.AtomicityRegisterClientFactory;

import java.io.Serializable;

public class SystemNode implements Serializable
{
	private static final long serialVersionUID = 2642974560070519463L;

	/**
	 * identifier of the {@link SystemNode}
	 * it is an integer value
	 * 
	 * its default value is -1
	 */
	private int node_id;
	public static int NODE_ID_DEFAULT = -1;
	
	/**
	 * name of the {@link SystemNode}
	 * its default value is <code>null</code>
	 */
	private String node_name;
	public static String NODE_NAME_DEFAULT = null;
	
	/**
	 * ip address of the {@link SystemNode}
	 * its default value is <code>null</code>
	 */
	private String node_ip;
	public static String NODE_IP_DEFAULT = null;

	/**
	 * algorithm type (to run) of the {@link SystemNode}
	 * its default value is @link AtomicityRegisterClientFactory#NO_SUCH_ATOMICITY
	 */
	private int alg_type;
	public static int ALG_TYPE_DEFAULT = AtomicityRegisterClientFactory.NO_SUCH_ATOMICITY;
	
	/**
	 * default constructor of {@link SystemNode}
	 * set all the fields to their default values
	 */
	public SystemNode()
	{
		this.node_id = SystemNode.NODE_ID_DEFAULT;
		this.node_name = SystemNode.NODE_NAME_DEFAULT;
		this.node_ip = SystemNode.NODE_IP_DEFAULT;
		this.alg_type = SystemNode.ALG_TYPE_DEFAULT;
	}

	/**
	 * constructor of {@link SystemNode} with only {@link #node_ip} set
	 * @param ip ip address of {@link SystemNode}
	 */
	public SystemNode(String ip)
	{
		this.node_id = SystemNode.NODE_ID_DEFAULT;
		this.node_name = SystemNode.NODE_NAME_DEFAULT;
		this.node_ip = ip;
	}
	
	/**
	 * constructor of {@link SystemNode} with {@link #node_id} and {@link #node_ip}
	 * the unspecified name is set to "DUMMY"
	 * 
	 * @param node_id {@link #node_id}: identifier of the system node
	 * @param ip {@link #node_ip}: ip address of the system node
	 */
	public SystemNode(int node_id, String ip)
	{
		this.node_id = node_id;
		this.node_ip = ip;
		this.node_name = "DUMMY";
	}

	/**
	 * constructor of {@link SystemNode} with {@link #node_name} and {@link #node_ip}
	 * 
	 * @param name {@link #node_name} of the system node
	 * @param ip {@link #node_ip} of the system node
	 */
	public SystemNode(String name, String ip)
	{
		this.node_name = name;
		this.node_ip = ip;
	}
	
	/**
	 * constructor of {@link SystemNode} with {@link #node_id}, {@link #node_name}, and {@link #node_ip}
	 * 
	 * @param node_id {@link #node_id}: identifier of the system node
	 * @param name {@link #node_name}: name of the system node 
	 * @param ip {@link #node_ip}: ip address of the system node
	 * @param alg_type {@link #alg_type}: type of the algorithm to run
	 */
	public SystemNode(int node_id, String name, String ip, int alg_type)
	{
		this.node_id = node_id;
		this.node_ip = ip;
		this.node_name = name;
		this.alg_type = alg_type;
	}

	/**
	 * @return {@link #node_id}: identifier of the {@link SystemNode}
	 */
	public int getNodeId()
	{
		return this.node_id;
	}

	/**
	 * set the field {@link #node_id}
	 * @param node_id identifier of the {@link SystemNode} to set
	 */
	public void setNodeId(int node_id)
	{
		this.node_id = node_id;
	}
	
	/**
	 * @return {@link #node_name}: name of the {@link SystemNode}
	 */
	public String getNodeName()
	{
		return node_name;
	}
	
	/**
	 * set the field {@link #node_name}
	 * @param node_name name of the {@link SystemNode} to set
	 */
	public void setNodeName(String node_name)
	{
		this.node_name = node_name;
	}
	
	/**
	 * @return {@link #node_ip}: ip address of the {@link SystemNode}
	 */
	public String getNodeIp()
	{
		return node_ip;
	}

	/**
	 * set the field {@link #node_ip}
	 * @param node_ip ip address of the {@link SystemNode}
	 */
	public void setNodeIP(String node_ip)
	{
		this.node_ip = node_ip;
	}
	
	/**
	 * @return {@link #alg_type}: type of the algorithm to run
	 */
	public int getAlgType()
	{
		return this.alg_type;
	}
	
	/**
	 * set the type of the algorithm to run
	 * @param alg_type type of algorithm. @see AtomicityRegisterClientFactory
	 */
	public void setAlgType(int alg_type)
	{
		this.alg_type = alg_type;
	}
	
	/**
	 * string format: {@link #node_name}[{@link #node_id}]@{@link #node_ip}
	 */
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append(this.node_name).append('[').append(this.node_id).append(']')
			.append('@').append(this.node_ip);
		
		return sb.toString();
	}
}
