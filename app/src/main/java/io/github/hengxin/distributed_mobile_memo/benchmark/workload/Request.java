/**
 * @author hengxin
 * @date 2014-04-24
 * @description each request consists of its type (W[0], R[1]), key, and value (if it is of type W)
 */
package io.github.hengxin.distributed_mobile_memo.benchmark.workload;

import io.github.hengxin.distributed_mobile_memo.sharedmemory.data.kvs.Key;

public abstract class Request {
    public static final int WRITE_TYPE = 0;
    public static final int READ_TYPE = 1;

    protected int type = -1;
    protected Key key = null;
    protected String val = null;

    /**
     * @param key {@link Key} for this {@link Request}
     */
    public Request(Key key) {
        this.key = key;
    }

    /**
     * @return type of the request: {@link #WRITE_TYPE} or {@link #READ_TYPE}
     */
    public int getType() {
        return type;
    }

    /**
     * @return key of the request
     */
    public Key getKey() {
        return key;
    }

    /**
     * if the request is a W[0], get its value to be written
     *
     * @return value to write
     */
    public String getValue() {
        return val;
    }
}	
