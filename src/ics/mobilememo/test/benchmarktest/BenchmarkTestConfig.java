/**
 * @author hengxin
 * @date May 7, 2014
 * @description configuration for benchmark test
 */
package ics.mobilememo.test.benchmarktest;

// Enum singleton
public enum BenchmarkTestConfig
{
	INSTANCE;
	
	private boolean isBenchmarkEnabled = false;
	
	public boolean isBenchmarkEnabled()
	{
		return this.isBenchmarkEnabled;
	}
	
	public void enableBenchmark()
	{
		this.isBenchmarkEnabled = true;
	}
	
	public void disableBenchmark()
	{
		this.isBenchmarkEnabled = false;
	}
}
