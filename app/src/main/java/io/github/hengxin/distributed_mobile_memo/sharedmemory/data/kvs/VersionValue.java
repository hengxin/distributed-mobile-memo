/**
 * @author hengxin
 * @creation 2013-8-9
 * @file Value.java
 * @description
 */
package io.github.hengxin.distributed_mobile_memo.sharedmemory.data.kvs;

import java.io.Serializable;

/**
 * @author hengxin
 * @description value with its version;
 * 	Here, value is in String type for simplicity.
 */
public class VersionValue implements Comparable<VersionValue>, Serializable {
    private static final String TAG = VersionValue.class.getName();

    private static final long serialVersionUID = -6258082960233438012L;

    /**
     * Reserved versioned value: RESERVED_VERSIONVALUE = (RESERVED_VERSION = (-1,-1), {@link #RESERVED_VALUE} )
     */
    private static final String RESERVED_VALUE = "RESERVED_VALUE";
    public static final VersionValue RESERVED_VERSIONVALUE = new VersionValue(Version.RESERVED_VERSION, RESERVED_VALUE);

    private Version ver = null;
    private String val = null;

    /**
     * @param ver Version
     * @param val val in type String
     */
    public VersionValue(Version ver, String val) {
        this.ver = ver;
        this.val = val;
    }

    /**
     * @return {@link #ver}: the version
     */
    public Version getVersion() {
        return this.ver;
    }

    /**
     * @return {@link #val}: the value associated with the version;
     * it is a String NOW.
     */
    public String getValue() {
        return this.val;
    }

    /**
     * Note that: when you call the method, you should guarantee that the two
     * VersionValue (s) are with the same Key.
     * compare two VersionValue (s) according to their Version (s)
     * @see Version#compareTo(Version)
     *
     * @param vval another VersionValue to be compared with
     * @return 1 (>0) if this VersionValue is larger;
     * 		  -1 (<0) if another VersionValue is larger;
     * 		   0 if they are equal.
     */
    @Override
    public int compareTo(VersionValue vval) {
        return this.ver.compareTo(vval.ver);
    }

    /**
     * Get the max of two VersionValue according to their versions;
     * Note that: the caller is responsible for ensuring that
     * 	the keys of the two VersionValue are equal.
     * @param vval1 the first {@link VersionValue}
     * @param vval2 the second {@link VersionValue}
     * @return the max {@link VersionValue}
     */
    public static VersionValue max(VersionValue vval1, VersionValue vval2) {
        if (vval1.compareTo(vval2) >= 0)
            return vval1;
        return vval2;
    }

    /**
     * get the max VersionValue according to the {@link #compareTo(VersionValue)} method
     * @param vvals array of VersionValue (s)
     * @return the max VersionValue which has the largest Version
     *
     * TODO: two or more maxs???
     */
    public static VersionValue max(VersionValue[] vvals) {
        VersionValue max_vval = vvals[0];

        for (int i = 1; i < vvals.length; i++)
            if (vvals[i].compareTo(max_vval) > 0)
                max_vval = vvals[i];
        return max_vval;
    }

    /**
     * String format of the VersionValue representation:
     * 	Value : {@link #val} [Version : (seqno, id)]
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Value : ").append(this.val).append(" [").append(this.ver.toString()).append(']');
        return sb.toString();
    }
}
