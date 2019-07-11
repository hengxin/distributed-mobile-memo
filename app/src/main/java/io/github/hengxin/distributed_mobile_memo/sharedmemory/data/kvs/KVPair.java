/**
 * @author hengxin
 * @creation 2013-8-28
 * @file KVPair.java
 * @description
 */
package io.github.hengxin.distributed_mobile_memo.sharedmemory.data.kvs;

/**
 * @author hengxin
 * @date 2013-8-28
 * @description single key-value pair
 */
public class KVPair {
    private Key key;
    private VersionValue vval;

    public KVPair(Key key, VersionValue vval) {
        this.key = key;
        this.vval = vval;
    }

    public Key getKey() {
        return this.key;
    }

    public VersionValue getVVal() {
        return this.vval;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Key : ").append(this.key).append(';').append(this.vval.toString());
        return sb.toString();
    }
}
