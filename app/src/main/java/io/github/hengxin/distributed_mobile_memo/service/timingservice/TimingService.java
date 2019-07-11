package io.github.hengxin.distributed_mobile_memo.service.timingservice;

import java.net.Socket;

import io.github.hengxin.distributed_mobile_memo.service.timingservice.message.AuthMsg;
import io.github.hengxin.distributed_mobile_memo.service.timingservice.message.Message;
import io.github.hengxin.distributed_mobile_memo.service.timingservice.message.RequestTimeMsg;
import io.github.hengxin.distributed_mobile_memo.service.timingservice.message.ResponseTimeMsg;
import io.github.hengxin.distributed_mobile_memo.utility.socket.SocketUtil;

/**
 * Time polling service provider.
 *
 * @author hengxin
 * @date Jul 18, 2014
 */
public enum TimingService {
    INSTANCE;

    /**
     * Socket connecting Android device and PC host
     */
    private Socket host_socket = null;

    public void setHostSocket(final Socket host_socket) {
        this.host_socket = host_socket;
    }

    /**
     * Receive {@link AuthMsg} from PC; Enable the time-polling functionality.
     */
    public void receiveAuthMsg() {
        SocketUtil.INSTANCE.receiveMsg(host_socket);
    }

    /**
     * Wait for and receive {@link ResponseTimeMsg} from PC.
     * @return {@link ResponseTimeMsg} from PC
     */
    public ResponseTimeMsg receiveResponseTimeMsgInNewThread() {
        Message msg = SocketUtil.INSTANCE.receiveMsgInNewThread(host_socket);
        assert msg.getType() == Message.RESPONSE_TIME_MSG;
        return (ResponseTimeMsg) msg;
    }

    /**
     * Poll system time of PC
     *
     * @return system time of PC
     */
    public long pollingTime() {
        /**
         * Send {@link RequestTimeMsg} to PC in a new thread.
         * You cannot use network connection on the Main UI thread.
         * Otherwise you will get {@link NetworkOnMainThreadException}
         */
        SocketUtil.INSTANCE.sendMsgInNewThread(new RequestTimeMsg(), host_socket);

        ResponseTimeMsg responseTimeMsg = this.receiveResponseTimeMsgInNewThread();
        return responseTimeMsg.getHostPCTime();
    }
}
