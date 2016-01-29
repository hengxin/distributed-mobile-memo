package io.github.hengxin.distributed_mobile_memo.utility.filesys;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Utility functions for manipulation on file system.
 *
 * @author hengxin
 * @date Aug 11, 2014
 */
public class FileSysUtil {
    /**
     * @param directory specified directory
     * @return sub-directories of a specified directory
     * They are relative paths, instead of the absolute ones.
     */
    public static String[] getSubDirectories(String directory) {
        File file = new File(directory);

        String[] sub_directories = file.list(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                return new File(current, name).isDirectory();
            }
        });

        return sub_directories;
    }
}
