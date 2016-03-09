package io.github.hengxin.distributed_mobile_memo.pc.analysis.script;

import android.annotation.TargetApi;
import android.os.Build;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import io.github.hengxin.distributed_mobile_memo.benchmark.workload.RequestRecord;
import io.github.hengxin.distributed_mobile_memo.pc.analysis.execution.ExecutionLogHandler;

/**
 * Extracting "delay" data from "execution" file which consists of lines of "type \t delay".
 * @author hengxin
 * @date Jul 5, 2014
 */
public class ExecutionDelayExtractor {

    /**
     * Extract delay data from execution files in some specified directory.
     * The directory {@param exec_dir} is assumed to be structured as follows:
     *
     * exec_dir
     *   - sub_dir
     *     - exec_file
     *   - sub_dir
     *     - exec_file
     *   - ...
     *     - ...
     *
     * The resulting structure will be:
     *
     * exec_dir
     *   - sub_dir
     *     - exec_file
     *     - delay_file
     *   - sub_dir
     *     - exec_file
     *     - delay_file
     *   - ...
     *     - ...
     *     - ...
     *
     * @param exec_dir  directory whose subdirectories contain execution files
     * @param exec_file execution file from which delay data is extracted
     * @param delay_file delay file for storing extracted delay data
     * @throws IOException  file/directory-related IO exceptions
     */
    public void extract(final String exec_dir, final String exec_file, final String delay_file) throws IOException {
        for (File sub_dir : new File(exec_dir).listFiles()) {
            if (sub_dir.isDirectory())  // TODO using listFiles(FileFilter)
                for (File file : sub_dir.listFiles())
                    if (file.getName().equals(exec_file))   // TODO using listFiles(FilenameFilter)
                        this.extract(file, new File(sub_dir, delay_file));
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)  // for the try-with-resources statement
    private void extract(final File exec_file, final File delay_file) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(delay_file))) {
            ExecutionLogHandler exe_handler = new ExecutionLogHandler(exec_file.getAbsolutePath());

            for (RequestRecord rr : exe_handler.loadRequestRecords()) {
                bw.write(rr.getType() + "\t" + rr.getDelay());
                bw.newLine();
            }
        }
    }

}
