package ics.mobilememo.script;

import javax.swing.JFileChooser;
import javax.swing.JPanel;

import android.annotation.SuppressLint;

/**
 * Collect executions from separate mobile phones 
 * attached to computer via USB/ADB
 * 
 * @author hengxin
 * @date Jul 1, 2014
 */
public class ExecutionCollector extends JPanel
{
	private static final long serialVersionUID = 7715320944749649435L;
	
	/**
	 * the source directory containing execution-related files in a single mobile phone
	 * 
	 * Lint Warning: Do not hardcode "/sdcard/"; use Environment.getExternalStorageDirectory().getPath() instead
	 * However, 
	 */
	@SuppressLint("SdCardPath")
	private final String single_execution_directory = "/sdcard/single_execution";
	
	// the (default) destination directory to which the execution files are copied/stored
	private String collected_execution_directory = "C:\\Users\\ics-ant\\Desktop\\executions";
	
	/**
	 * collect all the execution-related files from separate mobile phones 
	 * @return destination directory in which the files collected are stored
	 */
	public String collect()
	{
		ADBExecutor adb_executor = new ADBExecutor("D:\\AndroidSDK\\platform-tools\\adb.exe ");
		
		// "adb -s [device] forward tcp: tcp: "
		adb_executor.execAdbOnlineDevicesPortForward();
		// copy execution-related files from mobile phones to computer
		String dest_directory = this.chooseDestDirectory();
		adb_executor.copyFromAll(this.single_execution_directory, dest_directory);
		
		return dest_directory;
	}
	
	/**
	 * choose the destination directory to which the execution files are copied/stored
	 * via JFileChooser
	 * @return the path of the destination directory chosen
	 */
	private String chooseDestDirectory()
	{
		final JFileChooser fc = new JFileChooser(this.collected_execution_directory);
		
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		int ret_val = fc.showOpenDialog(ExecutionCollector.this);
		if (ret_val == JFileChooser.APPROVE_OPTION)
			this.collected_execution_directory = fc.getSelectedFile().getAbsolutePath();
		
		System.out.println("Collected execution directory is: " + this.collected_execution_directory);
		return this.collected_execution_directory;
	}
	
	public static void main(String[] args)
	{
		new ExecutionCollector().collect();
	}
}
