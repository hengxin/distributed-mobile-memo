/**
 * reader for execution log:
 * parse requests ( {@link RequestRecord} ) stored in log file
 * and create instances for them
 */
package io.github.hengxin.distributed_mobile_memo.pc.analysis.execution;

import android.annotation.TargetApi;
import android.os.Build;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.github.hengxin.distributed_mobile_memo.benchmark.workload.RequestRecord;

public class ExecutionLogHandler {
    private String log_file;

    public ExecutionLogHandler(String file) {
        this.log_file = file;
    }

    /**
     * Parse the {@link RequestRecord}s stored in the file named {@link #log_file},
     * and create instances for them.
     * @return a list of {@link RequestRecord}s
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public List<RequestRecord> loadRequestRecords() throws IOException {
        List<RequestRecord> request_record_list = new ArrayList<>();
        String rr_line;

        try (BufferedReader br = new BufferedReader(new FileReader(this.log_file))) {
            while ((rr_line = br.readLine()) != null) {
                RequestRecord rr = RequestRecord.parse(rr_line);
                if (rr != null)
                    request_record_list.add(rr);
            }
        }

        return request_record_list;
    }

}