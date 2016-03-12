package io.github.hengxin.distributed_mobile_memo.pc.analysis.quantification.quantify2atomicity;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.annotation.NonNull;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import io.github.hengxin.distributed_mobile_memo.benchmark.workload.RequestRecord;

/**
 * {@link ONITriple} represents possible concurrency patterns
 * that involves two reads (read_rr and pre_read_rr) and one write (write_rr) such that:
 * <ul>
 *     <li>read_rr starts within write_rr</li>
 *     <li>pre_read_rr precedes read_rr</li>
 *     <li>pre_read_rr finishes within the interval between the start of write_rr and the start of read_rr</li>
 * </ul>
 * and read-write patterns such that:
 * <ul>
 *     <li>read_rr reads from write_rr</li>
 *     <li>read_rr reads a version 1-stale than that by pre_read_rr</li>
 * </ul>
 *
 * Created by hengxin on 16-3-12.
 */
public class ONITriple {
    private RequestRecord read_rr;
    private RequestRecord write_rr;
    private RequestRecord pre_read_rr;

    public ONITriple(RequestRecord read_rr, RequestRecord write_rr, RequestRecord pre_read_rr) {
        this.read_rr = read_rr;
        this.write_rr = write_rr;
        this.pre_read_rr = pre_read_rr;
    }

    /**
     * Check if given operations exhibit a concurrency pattern.
     * @param read_rr
     * @param write_rr
     * @param pre_read_rr
     * @return
     */
    public static boolean isCP(@NonNull RequestRecord read_rr, @NonNull RequestRecord write_rr,
                               @NonNull RequestRecord pre_read_rr) {
        throw new UnsupportedOperationException("Not Implemented Yet!");
    }

    /**
     * Check if given operations exhibit a read-write pattern.
     * @param read_rr
     * @param write_rr
     * @param pre_read_rr
     * @return
     */
    public static boolean isRWP(@NonNull RequestRecord read_rr, @NonNull RequestRecord write_rr,
                                @NonNull RequestRecord pre_read_rr) {
        throw new UnsupportedOperationException("Not Implemented Yet!");
    }

    /**
     * Write a list of {@link ONITriple}s into file.
     * @param oni_triple_list   list of {@link ONITriple}s
     * @param path  path of file
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void write2File(List<ONITriple> oni_triple_list, String path) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path))) {
            int count = 0;
            for (ONITriple oni_triple : oni_triple_list) {
                count++;
                bw.write("NO. " + count);
                bw.newLine();
                bw.write(oni_triple.toString());
            }
        }
    }

    /**
     * @return String form of a concurrency pattern:
     * {@link #read_rr} \newline {@link #write_rr} \newline {@link #pre_read_rr} \newline
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append(read_rr).append(System.lineSeparator())
                .append(write_rr).append(System.lineSeparator())
                .append(pre_read_rr).append(System.lineSeparator());

        return sb.toString();
    }

}
