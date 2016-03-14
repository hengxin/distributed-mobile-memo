/**
 * @author hengxin
 * @date 2014-04-22
 * @description record for each request: type (read/write), start time, finish time,
 * delay (finish time - start time), and value (with version number)
 */
package io.github.hengxin.distributed_mobile_memo.benchmark.workload;

import io.github.hengxin.distributed_mobile_memo.sharedmemory.data.kvs.Key;
import io.github.hengxin.distributed_mobile_memo.sharedmemory.data.kvs.Version;
import io.github.hengxin.distributed_mobile_memo.sharedmemory.data.kvs.VersionValue;

public class RequestRecord extends Request implements Comparable<RequestRecord> {
    //	private VersionValue vvalue = null;
    private Version version = null;

    private long start_time = 0;
    private long finish_time = 0;
    // delay = finish_time - start_time
    private long delay = 0;

    /**
     * constructor of {@link RequestRecord}
     *
     * @param type {@value Request#WRITE_TYPE} or {@value Request#READ_TYPE}
     * @param start start time
     * @param finish finish time
     * @param vvalue value (with version number)
     *
     * delay = finish - start will be calculated and recorded
     */
    public RequestRecord(int type, long start, long finish, Key key, VersionValue vvalue) {
        super(key);

        super.type = type;

        this.start_time = start;
        this.finish_time = finish;
        this.delay = this.finish_time - this.start_time;

        this.version = vvalue.getVersion();
        super.val = vvalue.getValue();
    }

    /**
     * constructor of {@link RequestRecord}
     *
     * @param type {@value Request#WRITE_TYPE} or {@value Request#READ_TYPE}
     * @param start start time
     * @param finish finish time
     * @param delay delay
     * @param version {@link Version}
     * @param val value
     */
    public RequestRecord(int type, long start, long finish, long delay, Key key, Version version, String val) {
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

    public void setStartTime(long start_time) {
        this.start_time = start_time;
    }

    public long getFinishTime() {
        return this.finish_time;
    }

    public void setFinishTime(long finish_time) {
        this.finish_time = finish_time;
    }

    public long getDelay() {
        return this.delay;
    }

    /**
     * @return String of RequestRecord: type \t start_time \t finish_time \t delay \t key \t vvalue
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Type: ").append(this.type).append('\t')
                .append("Start: ").append(this.start_time).append('\t')
                .append("Finish: ").append(this.finish_time).append('\t')
                .append("Delay: ").append(this.delay).append('\t')
                .append(this.key).append('\t')
                .append(this.version).append('\t')
                .append("Value: ").append(super.val);

        return sb.toString();
    }

    /**
     * compare two {@link RequestRecord}s as intervals
     * to determine which is the preceding one.
     * return:
     * 1 if this {@link RequestRecord} precedes the other one (@parma rr)
     * -1 if the one specified by @param rr precedes this one
     * 0 if the two {@link RequestRecord}s are concurrent
     *
     * @param rr {@link RequestRecord} to compare with
     * @return
     * 1 if this {@link RequestRecord} precedes the other one (@parma rr)
     * -1 if the one specified by @param rr precedes this one
     * 0 if the two {@link RequestRecord}s are concurrent
     */
    public int intervalCompareTo(RequestRecord rr) {
        if (this.finish_time < rr.start_time)
            return 1;

        if (rr.finish_time < this.start_time)
            return -1;

        return 0;
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
     * Check whether this {@link RequestRecord} reads from another one {@code rr}.
     * It first checks whether this one is of {@link Request#READ_TYPE} and {@code rr} {@link Request#WRITE_TYPE}.
     * @param rr a {@link RequestRecord}
     * @return  {@code true} if this {@link RequestRecord} reads from {@code rr}; {@code false}, otherwise.
     */
    public boolean readFrom(RequestRecord rr) {
        if (! this.isRead() || rr.isRead())
            return false;
        return this.version.equals(rr.version);
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
