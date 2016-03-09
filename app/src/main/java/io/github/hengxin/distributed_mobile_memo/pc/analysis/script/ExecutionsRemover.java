package io.github.hengxin.distributed_mobile_memo.pc.analysis.script;

import io.github.hengxin.distributed_mobile_memo.pc.adb.ADBExecutor;

/**
 * Remove specified files/directories from all attached mobile phones.
 *
 * @author hengxin
 * @date Jul 1, 2014
 */
public class ExecutionsRemover {

    private String adb_path;

    /**
     * Constructor of {@link ExecutionsRemover}.
     * @param adb_path  path of adb
     */
    public ExecutionsRemover(String adb_path) {
        this.adb_path = adb_path;
    }

    /**
     * Remove file/directory from phones.
     * @param path  path for file/directory to remove
     */
    public void remove(String path) {
        ADBExecutor adb = new ADBExecutor(this.adb_path);

        // "adb -s [device] forward tcp: tcp: "
        adb.execAdbOnlineDevicesPortForward();
        // remove files/directories from all attached mobile phones
        adb.removeFromAll(path);
    }

    public static void main(String[] args) {
        new ExecutionsRemover(ADBExecutor.DEFAULT_ADB_PATH).remove("/sdcard/single_execution");
    }
}
