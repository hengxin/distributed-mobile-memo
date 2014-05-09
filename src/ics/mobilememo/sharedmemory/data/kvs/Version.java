/**
 * @author hengxin
 * @creation 2013-8-9
 * @file Version.java
 *
 * @description
 */
package ics.mobilememo.sharedmemory.data.kvs;

import java.io.Serializable;


/**
 * @author hengxin
 * @description versioning mechanism, whereby each stored value
 * 	is associated with a logical timestamp
 */
public class Version implements Comparable<Version>, Serializable
{
	private static final long serialVersionUID = 5115680262452013498L;
	public static final Version NULL_VERSION = new Version(-1,-1);

	private int seqno;	// sequence no.
	private int pid;		// pid

	/**
	 * @param seqno sequence no
	 * @param id pid
	 */
	public Version(int seqno, int id)
	{
		this.seqno = seqno;
		this.pid = id;
	}

//	/**
//	 * @return {@link #seqno}: the sequence no.
//	 */
//	public int getSeqno()
//	{
//		return this.seqno;
//	}

//	/**
//	 * @return {@link #id}: the pid
//	 */
//	public int getId()
//	{
//		return this.id;
//	}

	/**
	 * increment the sequence no and set the pid
	 * @param pid pid to set
	 * @return the new Version (the old version is unchanged)
	 */
	public Version increment(int pid)
	{
		// make sure that pid has been set
		assert pid >= 0;
		
		return new Version(this.seqno + 1, pid);
	}

	/**
	 * we compare two versions according to the lexicographic order
	 * @param version another Version to be compared with
	 * @return 1 if this Version is larger;
	 * 		  -1 if another Version is larger;
	 * 		   0 if they are equal.
	 */
	@Override
	public int compareTo(Version version)
	{
		if (this.seqno > version.seqno)
			return 1;
		if (this.seqno < version.seqno)
			return -1;
		if (this.pid > version.pid)
			return 1;
		if (this.pid < version.pid)
			return -1;

		return 0;
	}

	/**
	 * String format of the Version representation:
	 * Version : (seqno, id)
	 */
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Version : (").append(this.seqno).append(',').append(this.pid).append(')');
		return sb.toString();
	}

	/**
	 * are the two Version (s) equal
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (! (obj instanceof Version))
			return false;

		Version ver = (Version) obj;
		return this.seqno == ver.seqno && this.pid == ver.pid;
	}
}
