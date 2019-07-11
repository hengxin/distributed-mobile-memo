package io.github.hengxin.distributed_mobile_memo.pc.analysis.script;

import io.github.hengxin.distributed_mobile_memo.pc.PCConstants;
import io.github.hengxin.distributed_mobile_memo.pc.adb.ADBExecutor;

/**
 * Collect all the execution files from individual phones.
 * @author hengxin
 * @date Jul 1, 2014
 */
public class ExecutionCollector {
    private static final long serialVersionUID = 7715320944749649435L;

    private String adb_path;

    /**
     * Constructor of {@link ExecutionCollector}.
     * @param adb_path  path of adb
     */
    public ExecutionCollector(String adb_path) {
        this.adb_path = adb_path;
    }

    /**
     * Collect all the execution files from individual phones.
     * @param from_dir  directory from which executions are collected
     * @param to_dir    directory into which collected executions are stored
     */
    public void collect(String from_dir, String to_dir) {
        ADBExecutor adb = new ADBExecutor(this.adb_path);

        // "adb -s [device] forward tcp: tcp: "
        adb.execAdbOnlineDevicesPortForward();
        // copy execution-related files from mobile phones to computer
        adb.copyAll(from_dir, to_dir);
    }

    public static void main(String[] args) {
        if (args.length != 2)
            throw new IllegalArgumentException("Arguments: <adb_path> <pc_path>");

        new ExecutionCollector(args[0])
                .collect(PCConstants.MEMO_IN_SDCARD_DIR, args[1]);
    }
}