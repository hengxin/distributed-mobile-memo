/**
 * @author hengxin
 * @date 2014-04-22
 * @description record for each request: type (read/write), start time, finish time,
 * delay (finish time - start time), and value (with version number)
 */
package io.github.hengxin.distributed_mobile_memo.benchmark.workload;

import com.google.common.base.MoreObjects;
import com.google.common.base.Splitter;

import java.util.List;

import io.github.hengxin.distributed_mobile_memo.sharedmemory.data.kvs.Key;
import io.github.hengxin.distributed_mobile_memo.sharedmemory.data.kvs.Version;
import io.github.hengxin.distributed_mobile_memo.sharedmemory.data.kvs.VersionValue;

public class RequestRecord extends Request implements Comparable<RequestRecord> {
    private Version version = null;

    private long start_time = 0;
    private long finish_time = 0;
    private long delay = 0;

    public RequestRecord(int type, long start, long finish, Key key, VersionValue vvalue) {
        this(type, start, finish, finish - start, key, vvalue.getVersion(), vvalue.getValue());
    }

    /**
     * @param type {@value Request#WRITE_TYPE} or {@value Request#READ_TYPE}
     * @param start start time
     * @param finish finish time
     * @param delay delay
     * @param version {@link Version}
     * @param val value
     */
    public RequestRecord(int type, long start, long finish, long delay,
                         Key key, Version version, int val) {
        super(key);

        super.type = type;

        this.start_time = start;
        this.finish_time = finish;
        this.delay = delay;

        this.version = version;
        super.val = val;
    }

    public Version getVersion() {
        return version;
    }

    public long getStartTime() {
        return this.start_time;
    }

    public long getFinishTime() {
        return this.finish_time;
    }

    public long getDelay() {
        return this.delay;
    }

    /**
     * @return String of RequestRecord: type \t start_time \t finish_time \t delay \t key \t vvalue
     */
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("Type", type)
                .add("Start", start_time)
                .add("Finish", finish_time)
                .add("Delay", delay)
                .add("Key", key)
                .add("Version", version)
                .add("Value", val)
                .toString();
    }

    /**
     * @see #parse(String)
     */
    public String toCompactedString() {
        StringBuilder sb = new StringBuilder(50);

        sb.append(type).append(';')
                .append(start_time).append(';')
                .append(finish_time).append(';')
                .append(delay).append(';')
                .append(key).append(';')
                .append(version).append(';')
                .append(val);

        return sb.toString();
    }

    /**
     * @param rr_str  String format of {@link RequestRecord}
     * @return  {@link RequestRecord} parsed from {@code rr_str};
     *  {@code null} if parse fails.
     */
    public static RequestRecord parse(String rr_str) {
        List<String> rr_fields = Splitter.on(';')
                .trimResults()
                .omitEmptyStrings()
                .splitToList(rr_str);

        if (rr_fields.size() != 7)
            return null;

        int type = Integer.parseInt(rr_fields.get(0));
        long start = Long.parseLong(rr_fields.get(1));
        long finish = Long.parseLong(rr_fields.get(2));
        long delay = Long.parseLong(rr_fields.get(3));
        Key key = Key.parse(rr_fields.get(4));
        Version version = Version.parse(rr_fields.get(5));
        int val = Integer.parseInt(rr_fields.get(6));

        return new RequestRecord(type, start, finish, delay, key, version, val);
    }

    /**
     * check whether this {@link RequestRecord} precedes another
     * @param rr {@link RequestRecord}
     * @return true, if this {@link RequestRecord} precedes the one specified by @param rr;
     *   false, otherwise.
     */
    public boolean precedes(RequestRecord rr) {
        return this.finish_time < rr.start_time;
    }

    /**
     * Check whether this {@link RequestRecord} if of READ type.
     * @return {@link true} if it is READ; {@code false}, otherwise (i.e., it is of WRITE type).
     */
    public boolean isRead() {
        return this.type == Request.READ_TYPE;
    }

    /**
     * Does this {@link RequestRecord} start within another {@link RequestRecord} specified by @param rr?
     *
     * @param rr
     * 	another {@link RequestRecord}
     * @return
     * 	True, if this {@link RequestRecord} start within another {@link RequestRecord}
     * 	specified by @param rr;
     *  False, otherwise.
     */
    public boolean startWithin(RequestRecord rr) {
        return rr.getStartTime() <= this.getStartTime() && this.getStartTime() <= rr.finish_time;
    }

    /**
     * Does this {@link RequestRecord} finish within a time interval?
     *
     * @param lo
     * 	low bound of a time interval
     * @param hi
     * 	high bound of a time interval
     * @return
     *  True, if this {@link RequestRecord} finish within a time interval;
     *  False, otherwise.
     */
    public boolean finishWithin(long lo, long hi) {
        return lo <= this.getFinishTime() && this.getFinishTime() <= hi;
    }

    /**
     * @inheritDoc
     * Comparison between this {@link #RequestRecord} with another one
     * according to their {@link #start_time}.
     */
    @Override
    public int compareTo(RequestRecord rr) {
        return (int) (this.start_time - rr.start_time);
    }
}
