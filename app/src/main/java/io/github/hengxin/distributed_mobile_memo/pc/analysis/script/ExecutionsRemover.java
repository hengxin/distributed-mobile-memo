package io.github.hengxin.distributed_mobile_memo.pc.analysis.script;

import android.annotation.SuppressLint;

import io.github.hengxin.distributed_mobile_memo.pc.adb.ADBExecutor;

/**
 * Remove execution-related files from attached mobile phones
 *
 * @author hengxin
 * @date Jul 1, 2014
 */
public class ExecutionsRemover {
    /**
     * the (default) source directory containing execution-related files in a single mobile phone
     * <p>
     * Lint Warning: Do not hardcode "/sdcard/"; use Environment.getExternalStorageDirectory().getPath() instead
     * However,
     */
    @SuppressLint("SdCardPath")
    private String single_execution_directory = "/sdcard/single_execution";

    /**
     * constructor of {@link ExecutionsRemover}
     * <p>
     * the default file/directory to remove is
     * {@link #single_execution_directory} = "/sdcard/single_execution"
     */
    public ExecutionsRemover() {

    }

    /**
     * constructor of {@link ExecutionsRemover}
     *
     * @param single_execution_directory file/directory to remove
     */
    public ExecutionsRemover(String single_execution_directory) {
        this.single_execution_directory = single_execution_directory;
    }

    /**
     * remove execution-related files from all the attached mobile phones
     */
    public void remove() {
        System.out.println("Remove files in " + this.single_execution_directory);

        ADBExecutor adb_executor = new ADBExecutor("C:\\AndroidSDK\\platform-tools\\adb.exe ");

        // "adb -s [device] forward tcp: tcp: "
        adb_executor.execAdbOnlineDevicesPortForward();
        // remove execution-related files from all the attached mobile phones
        adb_executor.removeFromAll(this.single_execution_directory);
    }

    public static void main(String[] args) {
        new ExecutionsRemover().remove();
    }
}
