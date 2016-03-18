package io.github.hengxin.distributed_mobile_memo.utility.filesys;

import java.io.File;

/**
 * Utility methods for file manipulations.
 * Created by hengxin on 16-3-16.
 */
public class FileUtil {
    /**
     * Create {@link File} object for {@code path}; directories are created if necessary.
     * @param path  path of file
     * @return  {@link File} object
     */
    public static File create(String path) {
        File file = new File(path);
        file.getParentFile().mkdirs();
        return file;
    }
}
