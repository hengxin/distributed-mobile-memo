package io.github.hengxin.distributed_mobile_memo.pc;

import java.io.File;

/**
 * Constants for configurations, including file paths and apk name.
 * Created by hengxin on 16-3-12.
 */
public class PCConstants {
    public static final String MEMO_IN_SDCARD_DIR = "/sdcard/memo";

    public static final String INDIVIDUAL_EXECUTION_FILE = "execution.txt";
    public static final String INDIVIDUAL_DELAY_FILE = "delay.txt";

    public static final String INDIVIDUAL_EXECUTION_FILE_PATH_IN_SDCARD = MEMO_IN_SDCARD_DIR
            + File.separator + INDIVIDUAL_EXECUTION_FILE;

    public static final String ALLINONE_DIR = "allinone";
    public static final String ALLINONE_EXECUTION_FILE_PATH = ALLINONE_DIR + File.separator + "allinone_execution.txt";
    public static final String ALLINONE_DELAY_FILE_PATH = ALLINONE_DIR + File.separator + "allinone_delay.txt";

    public static final String TWO_ATOMICITY_DIR = "2atomicity";
    public static final String CP_FILE_PATH = TWO_ATOMICITY_DIR + File.separator + "cp.txt";
    public static final String ONI_FILE_PATH = TWO_ATOMICITY_DIR + File.separator + "oni.txt";

    public static final String RWN_DIR = "rwn";
    public static final String STALE_MAP_FILE_PATH = RWN_DIR + File.separator + "staleness.txt";

    public static final String MEMO_APK = "io.github.hengxin.distributed_mobile_memo";
}
