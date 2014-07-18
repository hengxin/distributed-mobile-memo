package ics.mobilememo.service.timingservice;

import ics.mobilememo.service.timingservice.message.AuthMsg;
import ics.mobilememo.service.timingservice.message.Message;
import ics.mobilememo.service.timingservice.message.RequestTimeMsg;
import ics.mobilememo.service.timingservice.message.ResponseTimeMsg;
import ics.mobilememo.utility.socket.SocketUtil;

import java.net.Socket;

/**
 * Time polling service provider.
 * @author hengxin
 * @date Jul 18, 2014
 */
public enum TimingService
{
	INSTANCE;
	
	/**
	 * Socket connecting Android device and PC host
	 */
	private Socket host_socket = null;
	
	public void setHostSocket(final Socket host_socket)
	{
		this.host_socket = host_socket;
	}
	
	/**
	 * Receive {@link AuthMsg} from PC; Enable the time-polling functionality.
	 */
	public void receiveAuthMsg()
	{
		SocketUtil.INSTANCE.receiveMsg(host_socket);
	}
	
	/**
	 * Wait for and receive {@link ResponseTimeMsg} from PC.
	 * @param host_socket the message is sent via this specified socket
	 * @return {@link ResponseTimeMsg} from PC
	 */
	public ResponseTimeMsg receiveResponseTimeMsgInNewThread()
	{
		Message msg = SocketUtil.INSTANCE.receiveMsgInNewThread(host_socket);
		assert msg.getType() == Message.RESPONSE_TIME_MSG;
		return (ResponseTimeMsg) msg;
	}
	
	/**
	 * Poll system time of PC
	 * @return system time of PC
	 */
	public long pollingTime()
	{
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
