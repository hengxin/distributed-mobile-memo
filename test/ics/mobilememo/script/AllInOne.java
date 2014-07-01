package ics.mobilememo.script;

/**
 * process the execution-related tasks all in one,
 * consisting of:
 * (1) collect
 * (2) sync
 * (3) combine 
 * (4) remove
 * (5) uninstall 
 * 
 * @author hengxin
 * @date Jul 1, 2014
 */
public class AllInOne
{
	public static void main(String[] args)
	{
		// (1) collect
		String destination_directory = new ExecutionCollector().collect();
		
		// (2) sync.
		new ExecutionTimeSynchronizer(destination_directory).sync();
		
		// (3) combine
		new SyncedExecutionCombiner(destination_directory);
		
		// (4) remove
		new ExecutionsRemover().remove();
		
		// (5) uninstall
		new APKUninstaller().uninstall();
	}
}
