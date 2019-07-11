package io.github.hengxin.distributed_mobile_memo.benchmark.executor;

import org.apache.log4j.Logger;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import io.github.hengxin.distributed_mobile_memo.benchmark.workload.PoissonWorkloadGenerator;
import io.github.hengxin.distributed_mobile_memo.benchmark.workload.Request;
import io.github.hengxin.distributed_mobile_memo.benchmark.workload.RequestRecord;
import io.github.hengxin.distributed_mobile_memo.logging.ConfigureLog4J;
import io.github.hengxin.distributed_mobile_memo.service.timingservice.TimingService;
import io.github.hengxin.distributed_mobile_memo.sharedmemory.atomicity.AbstractAtomicityRegisterClient;
import io.github.hengxin.distributed_mobile_memo.sharedmemory.atomicity.AtomicityRegisterClientFactory;
import io.github.hengxin.distributed_mobile_memo.sharedmemory.data.kvs.Key;
import io.github.hengxin.distributed_mobile_memo.sharedmemory.data.kvs.VersionValue;

/**
 * @author hengxin
 * @description executor responsible for issuing the requests from workload benchmarks
 * @date 2014-04-24
 */
public class Executor implements Runnable {
    private static final String TAG = Executor.class.getName();

    /**
     * use "android-logging-log4j"
     * <a href>https://code.google.com/p/android-logging-log4j/</a>
     */
    private final Logger log4android = Logger.getLogger(Executor.class);

    // number of requests to execute
    private int request_number = -1;
    private BlockingQueue<Request> request_queue = new LinkedBlockingDeque<>();

    AbstractAtomicityRegisterClient client = null;

    /**
     * Using the producer-consumer synchronization mechanism.
     *
     * @param request_queue  {@link #request_queue}: queue of {@link RequestRecord}s
     *                       between producer {@link PoissonWorkloadGenerator} and consumer {@link Executor}
     * @param request_number {@link #request_number}: number of requests to execute
     */
    public Executor(BlockingQueue<Request> request_queue, int request_number) {
        ConfigureLog4J.INSTANCE.configure();

        this.request_queue = request_queue;
        this.request_number = request_number;

        try {
            this.client = AtomicityRegisterClientFactory.INSTANCE.getAtomicityRegisterClient();
        } catch (AtomicityRegisterClientFactory.NoSuchAtomicAlgorithmSupportedException nsaas) {
            nsaas.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Issue the request and record statistical information.
     *
     * @param request request to issue
     */
    private void issue(Request request) {
        int type = request.getType();
        Key key = request.getKey();
        int val = request.getValue();
        VersionValue vvalue;

        long invocation_time = TimingService.INSTANCE.pollingTime();
        if (type == Request.WRITE_TYPE)    // it is W[0]
            vvalue = client.put(key, val);
        else // it is R[1]
            vvalue = client.get(key);
        long response_time = TimingService.INSTANCE.pollingTime();

        log4android.debug(new RequestRecord(type, invocation_time, response_time, key, vvalue)
                .toCompactedString());
    }

    @Override
    public void run() {
        int index = 0;
        while (index < this.request_number) {
            try {
                this.issue(request_queue.take());
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
            index++;
        }

        /**
         * Shut down the logger and let all the buffered logs get flushed
         * See http://stackoverflow.com/a/3078377/1833118
         *
         * @author hengxin
         * @date Jul 15, 2014
         */
        ConfigureLog4J.INSTANCE.shutdown();
    }
}
