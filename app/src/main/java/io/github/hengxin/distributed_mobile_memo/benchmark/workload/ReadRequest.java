/**
 * @author hengxin
 * @date May 28, 2014
 * @description {@ReadRequest}
 */
package io.github.hengxin.distributed_mobile_memo.benchmark.workload;

import io.github.hengxin.distributed_mobile_memo.sharedmemory.data.kvs.Key;

public class ReadRequest extends Request {

    public ReadRequest(Key key) {
        super(key);
        super.type = Request.READ_TYPE;
    }

}
