package io.github.hengxin.distributed_mobile_memo.pc.analysis.quantification.quantify2atomicity;

import android.annotation.TargetApi;
import android.os.Build;

import org.apache.commons.lang3.time.DurationFormatUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.github.hengxin.distributed_mobile_memo.benchmark.workload.RequestRecord;
import io.github.hengxin.distributed_mobile_memo.pc.PCConstants;
import io.github.hengxin.distributed_mobile_memo.pc.analysis.execution.Execution;
import io.github.hengxin.distributed_mobile_memo.pc.analysis.execution.ExecutionLogHandler;

/**
 * Given an execution satisfying 2-atomicity, the
 * statistics for 2-atomicity including:
 * (1) number of occurrences of "concurrency pattern" (CP)
 * (2) number of occurrences of "old-new inversion" (ONI)
 *
 * @see <a href="https://github.com/hengxin/distributed-mobile-memo/wiki/Offline-Analysis#quantifying-2am-executions
 * -quantifying2atomicity">Quantifying 2-Atomicity @ hengxin/distributed-mobile-memo @GitHub</a>
 */
public class Quantifying2Atomicity {

    private int cp_count = 0;
    private int oni_count = 0;

    private final List<ONITriple> cp_list = new ArrayList<>();
    private final List<ONITriple> oni_list = new ArrayList<>();

    /**
     * Quantifying 2-atomicity in terms of numbers of "concurrency patterns (cp)" and "old-new inversions (oni)".
     * @param path path of the file that contains the execution to quantify
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void quantify(String path) throws IOException {
        Execution exec = new Execution(new ExecutionLogHandler(path).loadRequestRecords());

        // quantifying 2-atomicity for each read operation
        for (RequestRecord read_rr : exec.getReadRequestRecordList()) {
            boolean rr_cp_flag = false;
            boolean rr_oni_flag = false;

            Iterator<RequestRecord> write_rr_iter = exec.getWriteRequestRecordList().iterator();  // in program order

            if (write_rr_iter.hasNext())    // skip the first write
                write_rr_iter.next();

            while (write_rr_iter.hasNext()) {
                RequestRecord write_rr = write_rr_iter.next();

                /**
                 * Condition (1) of concurrency pattern: the read starts during the interval of a write.
                 */
                if (read_rr.startWithin(write_rr)) {
                    for (RequestRecord pre_read_rr : exec.getReadRequestRecordList()) {
                        /**
                         * Condition (2) of concurrency pattern : another read operation
                         */
                        if (pre_read_rr.finishWithin(write_rr.getStartTime(), read_rr.getStartTime())) {
                            if (! rr_cp_flag) {
                                this.cp_count++;
                                rr_cp_flag = true;
                            }
                            cp_list.add(new ONITriple(read_rr, write_rr, pre_read_rr));

                            if (this.isONI(read_rr, write_rr, pre_read_rr)) {
                                if (! rr_oni_flag) {
                                    this.oni_count++;
                                    rr_oni_flag = true;
                                }
                                this.oni_list.add(new ONITriple(read_rr, write_rr, pre_read_rr));
                            }
                        }
                    }
                    break;  // there is only one concurrent write operation
                }
            }
        }
    }

    /**
     * To check whether these three operations constitute an "old-new inversion".
     *
     * @param cur_read a read operation
     * @param write    a write operation
     * @param pre_read a read operation
     * @return <code>True</code> if these three operations constitute an "old-new inversion";
     * <code>False</code>, otherwise.
     */
    private boolean isONI(RequestRecord cur_read, RequestRecord write, RequestRecord pre_read) {
        int cur_read_version = cur_read.getVersion().getSeqno();
        int write_version = write.getVersion().getSeqno();
        int pre_read_version = pre_read.getVersion().getSeqno();

        return (pre_read_version == write_version) &&
                (pre_read_version == cur_read_version + 1);
    }

    public int getCPCount() {
        return this.cp_count;
    }

    public int getONICount() {
        return this.oni_count;
    }

    public List<ONITriple> getCPList() {
        return this.cp_list;
    }

    public List<ONITriple> getONIList() {
        return oni_list;
    }

    /**
     * Test {@link Quantifying2Atomicity}.
     * @param args one argument for path of file that contains the execution to quantify
     * @throws IOException  if the file specified in {@code args} is not found
     * @throws IllegalArgumentException  if the size {@code args} != 1
     */
    public static void main(String[] args) throws IOException {
        if (args.length != 1)
            throw new IllegalArgumentException("Parameter: <path>");
        String path = args[0];

        Quantifying2Atomicity quantifer = new Quantifying2Atomicity();

        System.out.println("Quantifying 2-atomicity ...");
        long start_time = System.currentTimeMillis();
        quantifer.quantify(path);
        long finish_time = System.currentTimeMillis();
        System.out.println("Time: " + DurationFormatUtils.formatDurationHMS(finish_time - start_time));

        System.out.println("The number of \"concurrency patterns\" is: " + quantifer.getCPCount());
        System.out.println("The number of \"old new inversions\" is: " + quantifer.getONICount());

        String parent_path = new File(path).getParent();
        // store concurrency patterns
        String cp_file = parent_path + File.separator + PCConstants.CP_FILE_PATH;
        System.out.println("Store concurrency patterns into file: " + cp_file);
        ONITriple.write2File(quantifer.getCPList(), cp_file);

        // store old-new inversions
        String oni_file = parent_path + File.separator + PCConstants.ONI_FILE_PATH;
        System.out.println("Store oni into file: " + oni_file);
        ONITriple.write2File(quantifer.getONIList(), oni_file);
    }

}