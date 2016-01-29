package io.github.hengxin.distributed_mobile_memo.pc.analysis.script;

import io.github.hengxin.distributed_mobile_memo.utility.filesys.FilesCombiner;

/**
 * Combine the delay files of separate replicas into a single one.
 *
 * @author hengxin
 * @date Aug 11, 2014
 */
public class ExecutionDelayCombiner {
    private final String execution_directory;

    // delay files of separate replicas
    private final String single_delay_file_name = "execution_delay.txt";

    // delay file to store all the delay data
    private final String allinone_delay_file_name = "delay.txt";

    /**
     * Constructor of {@link ExecutionDelayCombiner}
     *
     * @param execution_directory directory for execution
     */
    public ExecutionDelayCombiner(String execution_directory) {
        this.execution_directory = execution_directory;
    }

    /**
     * Combine the delay files of separate replicas into a single one
     *
     * @return the (absolute) path of the all-in-one delay file
     */
    public String combine() {
        System.out.println("Combine delays in this directory: " + this.execution_directory);
        String allinone_delay_file_path = new FilesCombiner(this.execution_directory, this.single_delay_file_name, this.allinone_delay_file_name).combine();
        System.out.println("Delay combination Finished.");

        return allinone_delay_file_path;
    }

    /**
     * Test of {@link ExecutionDelayCombiner}
     *
     * @param args
     */
    public static void main(String[] args) {
        new ExecutionDelayCombiner("C:\\Users\\ics-ant\\Desktop\\executions\\allinonetest").combine();
    }
}
