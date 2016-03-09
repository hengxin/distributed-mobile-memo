package io.github.hengxin.distributed_mobile_memo.utility.filesys;

import android.annotation.TargetApi;
import android.os.Build;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Combine several individual files into one.
 * @author hengxin
 * @date Aug 11, 2014
 */
public class FilesCombiner {

    /**
     * Combine several individual files into one.
     * The directory {@param exec_dir} is assumed to be structured as follows:
     * (in our case, individual_file can be "exec_file" or "delay_file")
     *
     * exec_dir
     *   - sub_dir
     *     - individual_file
     *   - sub_dir
     *     - individual_file
     *   - ...
     *     - ...
     *
     * The resulting structure will be:
     *
     * exec_dir
     *   - allinone_file
     *   - sub_dir
     *     - individual_file
     *   - sub_dir
     *     - individual_file
     *   - ...
     *     - ...
     *
     * @param exec_dir    directory that contains the files to be combined
     * @param individual_file   file to be combined
     * @param allinone_file file to store the combined result
     * @return the (absolute) path of the result file
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)  // for the try-with-resources statements
    public String combine(final String exec_dir, final String individual_file, final String allinone_file)
            throws IOException {
        String allinone_file_path = exec_dir + File.separator + allinone_file;

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(allinone_file_path))) {

            String line = null;

            for (File sub_dir : new File(exec_dir).listFiles()) {
                if (sub_dir.isDirectory())  // TODO using listFiles(FileFilter)
                    for (File file : sub_dir.listFiles()) {
                        if (file.getName().equals(individual_file)) // TODO using listFiles(FilenameFilter)
                            // TODO using Files.copy in Java 7+
                            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                                while ((line = br.readLine()) != null) {
                                    bw.write(line);
                                    bw.newLine();
                                }
                            }
                    }
            }
        }

        return allinone_file_path;
    }
}
