/**
 * @author hengxin
 * @creation 2013-8-9
 * @file Version.java
 * @description
 */
package io.github.hengxin.distributed_mobile_memo.sharedmemory.data.kvs;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;

import java.io.Serializable;
import java.util.Iterator;

/**
 * @author hengxin
 * @description versioning mechanism, whereby each stored value
 * 	is associated with a logical timestamp
 */
public class Version implements Comparable<Version>, Serializable {
    private static final long serialVersionUID = 5115680262452013498L;

    /**
     * Reserved version: RESERVED_VERSION = (-1,-1)
     */
    public static final Version RESERVED_VERSION = new Version(-1, -1);

    private int seqno;
    private final int pid;

    public Version(int seqno, int id) {
        this.seqno = seqno;
        this.pid = id;
    }

    /**
     * @throws NumberFormatException
     */
    public static Version parse(String ver_str) {
        Iterator<String> version_iter = Splitter.on(CharMatcher.anyOf("(,)"))
                .trimResults()
                .omitEmptyStrings()
                .split(ver_str)
                .iterator();
        return new Version(Integer.parseInt(version_iter.next()),
                Integer.parseInt(version_iter.next()));
    }

    public int getSeqno() {
        return this.seqno;
    }

    /**
     * Increment the {@link #seqno}.
     * @param pid pid to set
     * @return the new incremented Version (leaving the old {@link Version} unchanged)
     *
     * TODO: check the method calls
     */
    public Version increment(int pid) {
        // make sure that pid has been set
        assert pid >= 0;

        return new Version(this.seqno + 1, pid);
    }

    /**
     * Compare two versions according to the lexicographic order.
     * @param version another Version to be compared with
     * @return 1 if this Version is larger;
     * 		  -1 if another Version is larger;
     * 		   0 if they are equal.
     */
    @Override
    public int compareTo(Version version) {
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

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (!(obj instanceof Version))
            return false;

        Version that = (Version) obj;
        return this.seqno == that.seqno && this.pid == that.pid;
    }

    /**
     * String format of {@link Version}: (seqno, id).
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(20);

        sb.append('(')
                .append(seqno).append(',').append(pid)
                .append(')');

        return sb.toString();
    }

}
