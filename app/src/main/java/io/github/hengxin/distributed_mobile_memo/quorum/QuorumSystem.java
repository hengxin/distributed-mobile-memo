package io.github.hengxin.distributed_mobile_memo.quorum;

import com.google.common.base.MoreObjects;

/**
 * {@link QuorumSystem} encapsulates {@link #read_quorum_size} and {@link #write_quorum_size},
 * and their minimum counterparts {@link #read_quorum_min_size} and {@link #write_quorum_min_size}.
 *
 * Created by hengxin on 16-3-13.
 */
public class QuorumSystem {
    private int replica_size;

    private int read_quorum_size;
    private int read_quorum_min_size = 1;

    private int write_quorum_size;
    private int write_quorum_min_size = 1;

    /**
     * @param replica_size total number of replicas
     * @return  a majority quorum system of which
     *  {@link #read_quorum_size} = {@link #write_quorum_size} = {@code replica_size} / 2 + 1
     */
    public static QuorumSystem createMajorityQuorumSystem(int replica_size) {
        int majority = replica_size / 2 + 1;

        QuorumSystem majority_quorum_system = new QuorumSystem(replica_size, majority, majority);
        majority_quorum_system.read_quorum_min_size = majority;
        majority_quorum_system.write_quorum_min_size = majority;
//        majority_quorum_system.setReadQuorumMinSize(majority);
//        majority_quorum_system.setWriteQuorumMinSize(majority);

        return majority_quorum_system;
    }

    public QuorumSystem(int replica_size, int read_quorum_size, int write_quorum_size) {
        this.replica_size = replica_size;
        this.read_quorum_size = read_quorum_size;
        this.write_quorum_size = write_quorum_size;
    }

    public QuorumSystem copy() {
        QuorumSystem quorum_system = new QuorumSystem(this.replica_size, this.read_quorum_size, this.write_quorum_size);
        quorum_system.read_quorum_min_size = this.read_quorum_min_size;
        quorum_system.write_quorum_min_size = this.write_quorum_min_size;
        return quorum_system;
    }

    public void setReadQuorumSize(int read_quorum_size) {
        this.read_quorum_size = read_quorum_size;
    }

    public void setReadQuorumMinSize(int read_quorum_min_size) {
        this.read_quorum_min_size = read_quorum_min_size;
    }

    public void setWriteQuorumMinSize(int write_quorum_min_size) {
        this.write_quorum_min_size = write_quorum_min_size;
    }

    public void setWriteQuorumSize(int write_quorum_size) {
        this.write_quorum_size = write_quorum_size;
    }

    public int getReplicaSize() {
        return replica_size;
    }

    public int getReadQuorumSize() {
        return read_quorum_size;
    }

    public int getReadQuorumMinSize() {
        return read_quorum_min_size;
    }

    public int getWriteQuorumSize() {
        return write_quorum_size;
    }

    public int getWriteQuorumMinSize() {
        return write_quorum_min_size;
    }

    /**
     * @return  String format of this {@link QuorumSystem}, including its
     * {@link #replica_size}, {@link #read_quorum_size}, {@link #read_quorum_min_size},
     * {@link #write_quorum_size}, and {@link #write_quorum_min_size}.
     */
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(QuorumSystem.class.getName())
                .add("Replica size", replica_size)
                .add("Read quorum size", read_quorum_size)
                .add("Minimum read quorum size", read_quorum_min_size)
                .add("Write quorum size", write_quorum_size)
                .add("Minimum write quorum size", write_quorum_min_size)
                .toString();
    }
}
