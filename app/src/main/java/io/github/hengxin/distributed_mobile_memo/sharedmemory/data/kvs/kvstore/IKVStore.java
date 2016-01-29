/**
 * @author hengxin
 * @date May 8, 2014
 * @description interface for kvstore supporting put, get, and remove methods.
 * for its concrete classes, you can store the data in memory, in file, or in database.
 */
package io.github.hengxin.distributed_mobile_memo.sharedmemory.data.kvs.kvstore;

import io.github.hengxin.distributed_mobile_memo.sharedmemory.data.kvs.Key;
import io.github.hengxin.distributed_mobile_memo.sharedmemory.data.kvs.VersionValue;

public interface IKVStore {
    /**
     * put {@link Key} + {@link VersionValue} into the kvstore
     * @param key specified {@link Key}
     * @param vval {@link VersionValue} associated with the {@link Key}
     */
    public void put(Key key, VersionValue vval);

    /**
     * return the {@link VersionValue} associated with the specified {@link Key}
     * @param key {@link Key} to query
     * @return {@link VersionValue} associated with the specified {@link Key}
     */
    public VersionValue getVersionValue(Key key);

    /**
     * remove the {@link VersionValue} associated with the specified {@link Key}
     * @param key {@link Key} specified
     */
    public void remove(Key key);
}
