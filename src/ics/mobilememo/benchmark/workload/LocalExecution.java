/**
 * @author hengxin
 * @date 2014-04-22
 * @description a local execution consists of a sequence of @see RequestRecord 
 */
package ics.mobilememo.benchmark.workload;

import java.util.ArrayList;
import java.util.List;

public class LocalExecution
{
  private List<RequestRecord> exe = new ArrayList<RequestRecord>();
  
  public void addRequestRecord(RequestRecord rr)
  {
	  this.exe.add(rr);
  }
  
}
