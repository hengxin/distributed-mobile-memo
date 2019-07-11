/**
 * @author hengxin
 * @date May 28, 2014
 * @description Generate {@link Request} according to its type (Write[0], Read[1])
 */
package io.github.hengxin.distributed_mobile_memo.benchmark.workload;

import java.util.Random;

import io.github.hengxin.distributed_mobile_memo.sharedmemory.data.kvs.Key;


public enum RequestFactory {
    INSTANCE;

    private static final Random rand = new Random();

    /**
     * Generate a {@link Request} according to the type (W[0], R[1])
     * @param type type of the {@link Request} to generate
     * @param key_range range of keys
     * @param value_range range of values
     * @return {@link Request} generated
     * @throws RequestTypeNotDefinedException
     */
    public Request generateRequest(int type, int key_range, int value_range) throws RequestTypeNotDefinedException {
        Key key = new Key(rand.nextInt(key_range));

        switch (type) {
            case Request.READ_TYPE:
                return new ReadRequest(key);

            case Request.WRITE_TYPE:
                return new WriteRequest(key, rand.nextInt(value_range));

            default:
                throw new RequestTypeNotDefinedException("Not such request type: " + type);
        }

    }
}