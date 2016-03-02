package io.github.hengxin.distributed_mobile_memo.pc.analysis.script;

import android.annotation.SuppressLint;

import io.github.hengxin.distributed_mobile_memo.pc.adb.ADBExecutor;

/**
 * Collect executions from separate mobile phones attached to computer via USB/ADB.
 *
 * @author hengxin
 * @date Jul 1, 2014
 */
public class ExecutionCollector {
    private static final long serialVersionUID = 7715320944749649435L;

    /**
     * the source directory containing execution-related files in a single mobile phone
     * <p>
     * Lint Warning: Do not hardcode "/sdcard/"; use Environment.getExternalStorageDirectory().getPath() instead
     */
    @SuppressLint("SdCardPath")
    private static final String SDCARD_EXECUTION_DIR = "/sdcard/single_execution";

    /** default directory in computer to store all the execution files collected from mobile
     * phones */
    public static final String DEFAULT_EXECUTION_DIR = "D:\\GitHub\\MobileMemo-Experiment\\For ONI";

    private String execDir;

    /**
     * Constructor with default directory for storing executions {@value #DEFAULT_EXECUTION_DIR}
     */
    public ExecutionCollector() {
        this(ExecutionCollector.DEFAULT_EXECUTION_DIR);
    }

    /**
     * Constructor with user-specified directory for storing executions.
     * @param execDir
     */
    public ExecutionCollector(String execDir) {
        this.execDir = execDir;
    }

    /**
     * Collect all the execution-related files from separate mobile phones.
     */
    public void collect() {
        ADBExecutor adb_executor = new ADBExecutor("C:\\AndroidSDK\\platform-tools\\adb.exe ");

        // "adb -s [device] forward tcp: tcp: "
        adb_executor.execAdbOnlineDevicesPortForward();
        // copy execution-related files from mobile phones to computer
        adb_executor.copyAll(this.SDCARD_EXECUTION_DIR, this.execDir);
    }

    public String getExecDir() {
        return this.execDir;
    }
}