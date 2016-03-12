package io.github.hengxin.distributed_mobile_memo.pc.analysis.quantification.quantifyrwn;

import android.annotation.TargetApi;
import android.os.Build;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import io.github.hengxin.distributed_mobile_memo.benchmark.workload.RequestRecord;

/**
 * {@link StalenessViolationMap} maintains a map mapping each staleness level to a set of {@link RequestRecord}s.
 * Created by hengxin on 16-3-12.
 */
public class StalenessViolationMap {
    private Multimap<Integer, RequestRecord> violation_map = ArrayListMultimap.create();

    /**
     * Put a pair of (staleness, {@link RequestRecord}) into this violation map.
     * @param k staleness level
     * @param rr {@link RequestRecord}
     */
    public void put(int k, RequestRecord rr) {
        this.violation_map.put(k, rr);
    }

    /**
     * Write this {@link #violation_map} into file.
     * The first line is the total number of (read) operations involved in this {@link #violation_map}.
     * The pairs of (staleness, list of {@link RequestRecord}s) in {@link #violation_map} are followed,
     * stored in different String formats according to {@code isSummary}.
     * If {@code isSummary} is {@code true}, then for each pair of (staleness, list of {@link RequestRecord}s)
     * only a line "staleness=size,proportion" is written; else, the list of {@link RequestRecord}s are followed,
     * one in each line.
     *
     * @param path  path of file
     * @param isSummary if the option is {@code true}, then for each pair of (staleness, list of {@link RequestRecord}s)
     *                  it only writes a line "staleness=size,proportion"; else, the list of {@link RequestRecord}s
     *                  are followed, one in each line.
     * @throws IOException  thrown if file not found
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void write2File(String path, boolean isSummary) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(path))) {
            int size = this.violation_map.size();
            bw.write(String.valueOf(size));
            bw.newLine();

            Map<Integer, Collection<RequestRecord>> violation_k_rr_map = this.violation_map.asMap();
            Collection<RequestRecord> rr_col;

            for (Map.Entry<Integer, Collection<RequestRecord>> violation_k_rr_entry : violation_k_rr_map.entrySet()) {
                rr_col = violation_k_rr_entry.getValue();
                bw.write(violation_k_rr_entry.getKey().toString());
                bw.write('=');
                bw.write(String.valueOf(rr_col.size()));
                bw.write(',');
                bw.write(String.valueOf(rr_col.size() * 1.0 / size));
                bw.newLine();

                if (! isSummary)
                    for (RequestRecord rr : rr_col) {
                        bw.write(rr.toString());
                        bw.newLine();
                    }
            }
        }
    }

}
