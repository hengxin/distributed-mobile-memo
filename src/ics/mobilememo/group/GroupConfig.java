/**
 * @author hengxin
 * @date May 9, 2014
 * @description {@link GroupConfig} is responsible for establishing and maintaining
 *  the membership information about {@link ServerReplica}s (not about clients).
 */
package ics.mobilememo.group;

import ics.mobilememo.group.member.ServerReplica;

import java.util.ArrayList;
import java.util.List;

public enum GroupConfig
{
	INSTANCE;
	
	/**
	 * maintain a list of {@link ServerReplica}s
	 */
	private List<ServerReplica> replica_list = new ArrayList<>();
	
	/**
	 * @return the size of the group of {@link ServerReplica}s
	 */
	public int getGroupSize()
	{
		return this.replica_list.size();
	}
	
	/**
	 * @return a list of {@link ServerReplica}s in the group
	 */
	public List<ServerReplica> getGroupMembers()
	{
		return this.replica_list;
	}
	
	/**
	 * add a new {@link ServerReplica} into this group
	 * @param replica {@link ServerReplica} to be added
	 */
	public void addReplica(ServerReplica replica)
	{
		this.replica_list.add(replica);
	}
}
