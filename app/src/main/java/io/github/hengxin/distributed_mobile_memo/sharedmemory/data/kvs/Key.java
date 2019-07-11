/**
 * @author hengxin
 * @creation 2013-8-8
 * @file Key.java
 * @description
 */
package io.github.hengxin.distributed_mobile_memo.sharedmemory.data.kvs;

import java.io.Serializable;

import static java.util.Objects.hash;

/**
 * The "Key" part of the "key-value" store; it is just a "int" NOW.
 * @author hengxin
 */
public class Key implements Serializable {
    private static final long serialVersionUID = -1479354097038655441L;

    public static final Key RESERVED_KEY = new Key(Integer.MIN_VALUE);

    private final int key;

    public Key(int key) {
        this.key = key;
    }

    /**
     * @throws NumberFormatException if {@code key} is not an "int".
     */
    public static Key parse(String key_str) {
        return new Key(Integer.parseInt(key_str));
    }

    @Override
    public String toString() {
        return String.valueOf(key);
    }

    @Override
    public int hashCode() {
        return hash(this.key);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof Key))
            return false;

        Key that = (Key) obj;
        return this.key == that.key;
    }
}
