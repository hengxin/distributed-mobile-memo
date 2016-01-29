package io.github.hengxin.distributed_mobile_memo.service.timingservice.message;

/**
 * @author hengxin
 * @description message for sync time
 * type: Message.SYNC_TIME_MSG
 * payload: time in millisecond (long Class type)
 * @date Jun 21, 2014
 */
public class ResponseTimeMsg extends Message {
    private static final long serialVersionUID = 1863422101310415237L;

    /**
     * constructor of {@link ResponseTimeMsg}
     *
     * @param time actual payload of {@link ResponseTimeMsg}: time in millisecond
     */
    public ResponseTimeMsg(long time) {
        super(Message.SYNC_TIME_MSG);
        super.payload = time;
    }

    /**
     * @return actual payload of {@link ResponseTimeMsg}: time in millisecond
     */
    public long getHostPCTime() {
        return (long) super.payload;
    }

    /**
     * "RESPONSE_TIME_MSG: " + time
     */
    @Override
    public String toString() {
        return "RESPONSE_TIME_MSG: " + payload;
    }
}
