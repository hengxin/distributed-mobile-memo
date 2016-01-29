/**
 * @author hengxin
 * @date Jun 21, 2014
 * @description pc host side:
 *   (1) port forwarding
 *   (2)
 */
package ics.mobilememo.service.timingservice.pc;

import ics.mobilememo.script.ADBExecutor;
import ics.mobilememo.service.timingservice.message.AuthMsg;
import ics.mobilememo.service.timingservice.message.Message;
import ics.mobilememo.service.timingservice.message.RequestTimeMsg;
import ics.mobilememo.service.timingservice.message.ResponseTimeMsg;
import ics.mobilememo.utility.socket.SocketUtil;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class PCHost
{
	private Executor exec = Executors.newFixedThreadPool(5);
	
	private static long cnt = 0;
	
	/**
	 * pairs of (device, hostport)
	 */
	private final Map<String, Integer> device_hostport_map;
	private final Map<String, Socket> device_hostsocket_map = new HashMap<>();
	
	/**
	 * constructor of {@link PCHost}:
	 * establish socket connections for each device
	 * 
	 * @param device_hostport_map {@link #device_hostport_map}: map of (device, port)
	 */
	public PCHost(Map<String, Integer> device_hostport_map)
	{
		this.device_hostport_map = device_hostport_map;
		this.createDeviceHostConnection();
	}
	
	/**
	 * Establish connections for each device on a specified port on the host PC
	 * and store them in the {@link #device_hostsocket_map} for further use
	 */
	private void createDeviceHostConnection()
	{
		String device = null;
		int host_port = -1;
		
		for(Map.Entry<String, Integer> device_hostport : this.device_hostport_map.entrySet())
		{
			device = device_hostport.getKey();
			host_port = device_hostport.getValue();
			
			// create socket for each "device" on the "host_port" of host PC
			Socket device_hostsocket = null;
			try
			{
				device_hostsocket = new Socket("localhost", host_port);
			} catch (UnknownHostException uhe)
			{
				uhe.printStackTrace();
			} catch (IOException ioe)
			{
				ioe.printStackTrace();
			}
			
			// store the sockets created for each "device"
			this.device_hostsocket_map.put(device, device_hostsocket);
		}
	}
	
	/**
	 * Send {@link AuthMsg} to an Android device on the other side of a specified socket
	 * @param host_socket send message via this specified socket
	 */
	private void sendAuthMsg(final Socket host_socket)
	{
		SocketUtil.INSTANCE.sendMsg(new AuthMsg(), host_socket);
	}
	
	/**
	 * Wait for {@link RequestTimeMsg} from Android device
	 * @param host_socket waiting on this specified socket
	 * @return {@link RequestTimeMsg} received
	 */
	private Message waitForRequestTimeMsg(final Socket host_socket)
	{
		Message msg = SocketUtil.INSTANCE.receiveMsg(host_socket);
		assert msg.getType() == Message.REQUEST_TIME_MSG;
		return (RequestTimeMsg) msg;
	}
	
	/**
	 * Send {@link ResponseTimeMsg} to an Android device on the other side of a specified socket
	 * @param host_socket send message via this specified socket
	 */
	private void sendResponseTimeMsg(final Socket host_socket)
	{
		System.out.println(++cnt);
		SocketUtil.INSTANCE.sendMsg(new ResponseTimeMsg(System.currentTimeMillis()), host_socket);
	}
	
	/**
	 * Send the current system time (denoted by {@link ResponseTimeMsg})
	 * to some device attached to the specified socket in a new thread
	 * @param host_socket the message is sent via this socket
	 */
	private void sendResponseTimeMsgInNewThread(final Socket host_socket)
	{
		SocketUtil.INSTANCE.sendMsgInNewThread(new ResponseTimeMsg(System.currentTimeMillis()), host_socket);
	}
	
	/**
	 * Core method for starting and providing time-polling service
	 */
	public void startTimePollingService()
	{
		Socket host_socket = null;
		for(Map.Entry<String, Socket> device_hostsocket : this.device_hostsocket_map.entrySet())
		{
			host_socket = device_hostsocket.getValue();
			
			/**
			 * Give authorization of polling time to Android devices
			 */
			this.sendAuthMsg(host_socket);
			/**
			 * Alternate: wait for {@link RequestTimeMsg} from Android devices 
			 * and send {@link ResponseTimeMsg} with system time to them
			 */
			exec.execute(new Alternate(host_socket));
		}
	}
	
	/**
	 * Core class for providing time-polling service.
	 * Alternate: wait for {@link RequestTimeMsg} from Android devices 
	 * and send {@link ResponseTimeMsg} with system time to them
	 * 
	 * Multi-threaded Issue:
	 * It assigns a separate thread for each attached Android device.
	 * 
	 * @author hengxin
	 * @date Jul 15, 2014
	 */
	final class Alternate implements Runnable
	{
		final Socket host_socket;
		
		public Alternate(Socket host_socket)
		{
			this.host_socket = host_socket;
		}
		
		@Override
		public void run()
		{
			Message msg = null;
			while (true)
			{
				// wait for {@link RequestTimeMsg} from Android device
				msg = waitForRequestTimeMsg(host_socket);
//				System.out.println("Receiving RequestTimeMsg: " + msg.toString());
				// send {@link ResponseTimeMsg} with current system time to Android device
				sendResponseTimeMsg(host_socket);
			}
		}
	}
	
	/**
	 * close all the host sockets
	 */
	public void shutDown()
	{
		for (Map.Entry<String, Socket> device_hostsocket : this.device_hostsocket_map.entrySet())
		{
			try
			{
				device_hostsocket.getValue().close();
			} catch (IOException ioe)
			{
				ioe.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) throws InterruptedException
	{
		ADBExecutor adb_executor = new ADBExecutor("D:\\AndroidSDK\\platform-tools\\adb.exe ");
		Map<String, Integer> device_hostport_map = adb_executor.execAdbOnlineDevicesPortForward();
		final PCHost host = new PCHost(device_hostport_map);

		host.startTimePollingService();
	}

}
