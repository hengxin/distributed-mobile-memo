package io.github.hengxin.distributed_mobile_memo.utility.filesys;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Combine interrelated files in different sub-directories into one file.
 *
 * @author hengxin
 * @date Aug 11, 2014
 */
public class FilesCombiner {
    // specified directory to work with
    private final String dir;
    // which files to be combined
    private final String single_file_name;
    // which file to store the combined result
    private final String allinone_file_name;

    /**
     * Constructor of {@link FilesCombiner}
     *
     * @param dir                specified directory to work with
     * @param single_file_name   which files to be combined
     * @param allinone_file_name which file to store the combined result
     */
    public FilesCombiner(String dir, String single_file_name, String allinone_file_name) {
        this.dir = dir;
        this.single_file_name = single_file_name;
        this.allinone_file_name = allinone_file_name;
    }

    /**
     * Combine interrelated files in different sub-directories into one file.
     *
     * @return the (absolute) path of the result file
     */
    public String combine() {
        BufferedWriter bw = null;
        String allinone_file_path = dir + "\\" + this.allinone_file_name;

        try {
            bw = new BufferedWriter(new FileWriter(allinone_file_path));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        BufferedReader br = null;
        String raw_rr_line = null;

        // check each sub-directory
        for (String sub_directory : FileSysUtil.getSubDirectories(this.dir)) {
            File[] files = new File(dir + "\\" + sub_directory).listFiles();

            for (File file : files) {
                // find the file to be combined
                if (file.getName().equals(single_file_name)) {
                    // read its contents and store them into the result file
                    try {
                        br = new BufferedReader(new FileReader(file.getAbsoluteFile()));
                        while ((raw_rr_line = br.readLine()) != null) {
                            bw.write(raw_rr_line + "\n");
                        }
                    } catch (FileNotFoundException fnfe) {
                        fnfe.printStackTrace();
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }
            }
        }

        try {
            br.close();
            bw.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return allinone_file_path;
    }
}
