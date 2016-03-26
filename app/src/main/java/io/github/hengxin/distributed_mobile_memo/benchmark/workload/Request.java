/**
 * @author hengxin
 * @date 2014-04-24
 * @description each request consists of its type (W[0], R[1]), key, and value (if it is of type W)
 */
package io.github.hengxin.distributed_mobile_memo.benchmark.workload;

import io.github.hengxin.distributed_mobile_memo.sharedmemory.data.kvs.Key;

public abstract class Request {
    // TODO: 2016/3/26 using enum for type 
    public static final int WRITE_TYPE = 0;
    public static final int READ_TYPE = 1;

    protected int type = -1;
    protected Key key = null;
    protected int val;

    public Request(Key key) {
        this.key = key;
    }

    /**
     * @return type of the request: {@link #WRITE_TYPE} or {@link #READ_TYPE}
     */
    public int getType() {
        return type;
    }

    public Key getKey() {
        return key;
    }

    public int getValue() {
        return val;
    }
}	
