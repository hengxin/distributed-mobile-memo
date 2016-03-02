package io.github.hengxin.distributed_mobile_memo.pc.analysis.script;

import io.github.hengxin.distributed_mobile_memo.pc.adb.ADBExecutor;

/**
 * Script for uninstalling apks.
 *
 * @author hengxin
 * @date Jul 1, 2014
 */
public class APKUninstaller {
    public APKUninstaller() {

    }

    /**
     * uninstall "ics.android_usb_computer" and "ics.mobilememo"
     */
    public void uninstall() {
        ADBExecutor adb_executor = new ADBExecutor("C:\\AndroidSDK\\platform-tools\\adb.exe ");

        // "adb -s [device] forward tcp: tcp: "
        adb_executor.execAdbOnlineDevicesPortForward();

        // uninstall apk "ics.android_usb_computer"
//		adb_executor.uninstall("ics.android_usb_computer");
        // uninstall apk "ics.mobilememo"
        adb_executor.uninstall("io.github.hengxin.distributed_mobile_memo");
    }

    public static void main(String[] args) {
        new APKUninstaller().uninstall();
    }
}
