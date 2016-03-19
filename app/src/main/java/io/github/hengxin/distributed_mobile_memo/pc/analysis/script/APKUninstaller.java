package io.github.hengxin.distributed_mobile_memo.pc.analysis.script;

import io.github.hengxin.distributed_mobile_memo.pc.PCConstants;
import io.github.hengxin.distributed_mobile_memo.pc.adb.ADBExecutor;

/**
 * APK uninstaller.
 * @author hengxin
 * @date Jul 1, 2014
 */
public class APKUninstaller {

    private String adb_path;

    /**
     * Constructor of {@link APKUninstaller}.
     * @param adb_path  path of adb
     */
    public APKUninstaller(String adb_path) {
        this.adb_path = adb_path;
    }

    /**
     * Uninstall the specified apk.
     * @param apk   apk to be uninstalled
     */
    public void uninstall(String apk) {
        ADBExecutor adb = new ADBExecutor(this.adb_path);

        // "adb -s [device] forward tcp: tcp: "
        adb.execAdbOnlineDevicesPortForward();
        // uninstall apk
        adb.uninstall(apk);
    }

    public static void main(String[] args) {
        new APKUninstaller(ADBExecutor.DEFAULT_ADB_PATH).uninstall(PCConstants.MEMO_APK);
    }
}
