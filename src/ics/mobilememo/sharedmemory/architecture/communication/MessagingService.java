/**
 * @author hengxin
 * @creation 2013-8-26; 2014-05-08
 * @file MessagingService.java
 *
 * @description basic message-passing mechanism: establish and listen to connections, send and receive messages
 *   it also dispatches the received messages of type {@link IPMessage} to appropriate handlers.
 */
package ics.mobilememo.sharedmemory.architecture.communication;

import ics.mobilememo.sharedmemory.architecture.config.NetworkConfig;
import ics.mobilememo.sharedmemory.atomicity.message.AtomicityMessage;
import ics.mobilememo.sharedmemory.atomicity.message.AtomicityMessagingService;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import android.os.AsyncTask;
import android.util.Log;

/**
 * Singleton pattern with Java Enum which is simple and thread-safe
 */
public enum MessagingService implements IReceiver
{
	INSTANCE;

	private static final String TAG = MessagingService.class.getName();

	private static final Executor exec = Executors.newCachedThreadPool();

	/**
	 * server socket on which the server replica is listening to and accept messages
	 */
	private ServerSocket server_socket = null;
	
	/**
	 * send the message to the designated receiver and return immediately
	 * without waiting for response
	 *
	 * @param receiver_ip
	 *            ip of the designated receiver
	 * @param msg
	 *            message of type {@link IPMessage} to send
	 */
	public void sendOneWay(final String receiver_ip, final IPMessage msg)
	{
		Log.d(TAG, "Send to " + receiver_ip);
		
		Runnable send_task = new Runnable()
		{
			@Override
			public void run()
			{
				InetSocketAddress socket_address = new InetSocketAddress(
						receiver_ip, NetworkConfig.NETWORK_PORT);
				Socket socket = new Socket();
				try
				{
					socket.connect(socket_address, NetworkConfig.TIMEOUT);
					ObjectOutputStream oos = new ObjectOutputStream(
							socket.getOutputStream());
					oos.writeObject(msg);
					oos.flush();
					Log.i(TAG, "Sending message: " + msg.toString());
				} catch (SocketTimeoutException stoe)
				{
					Log.i(TAG, stoe.getMessage());
				} catch (IOException ioe)
				{
					Log.e(TAG, ioe.getMessage());
					ioe.printStackTrace();
				} finally
				{
					try
					{
						socket.close();
					} catch (IOException ioe)
					{
						Log.e(TAG, ioe.getMessage());
						ioe.printStackTrace();
					}
				}

			}
		};

		exec.execute(send_task);
	}

	/**
	 * start as a server to listen to socket connection requests
	 *
	 * @param server_ip ip address of server
	 */
	public void start2Listen(String server_ip)
	{
		/**
		 * @author added by hengxin
		 * @date Jul 1, 2014
		 */
//		ServerSocket server_socket = null;
		if (this.server_socket != null)
			return;
		
		try
		{
			server_socket = new ServerSocket();
			server_socket.bind(new InetSocketAddress(server_ip,
					NetworkConfig.NETWORK_PORT));

			while (true)
			{
				final Socket connection = server_socket.accept();
				Runnable receive_task = new Runnable()
				{
					@Override
					public void run()
					{
						try
						{
							ObjectInputStream ois = new ObjectInputStream(connection.getInputStream());
							IPMessage msg = (IPMessage) ois.readObject();
							Log.i(TAG, "Receiving message: " + msg.toString());
							MessagingService.this.onReceive(msg);
						} catch (StreamCorruptedException sce)
						{
							Log.e(TAG, sce.getMessage());
							sce.printStackTrace();
						} catch (IOException ioe)
						{
							Log.e(TAG, ioe.getMessage());
							ioe.printStackTrace();
						} catch (ClassNotFoundException cnfe)
						{
							Log.e(TAG, cnfe.getMessage());
							cnfe.printStackTrace();
						}
					}
				};
				exec.execute(receive_task);
			}
		} catch (IOException ioe)
		{
			Log.e(TAG, ioe.getMessage());
		} finally
		{
			try
			{
				server_socket.close();
			} catch (IOException ioe)
			{
				Log.e(TAG, ioe.getMessage());
				ioe.printStackTrace();
			}
		}
	}

	/**
	 * upon receiving messages of type {@link IPMessage}, 
	 * {@link MessagingService} dispatches them
	 * to appropriate handlers according to their concrete sub-types. 
	 *
	 * @param msg received message of type {@link IPMessage}
	 */
	@Override
	public void onReceive(IPMessage msg)
	{
		if (msg instanceof AtomicityMessage)
			AtomicityMessagingService.INSTANCE.onReceive(msg);
		else // TODO: other messages
			return;
	}
	

	/**
	 * exit the messaging service: close the server socket
	 */
	public void exit()
	{
		if (this.server_socket != null && ! this.server_socket.isClosed())
		{
			try
			{
				this.server_socket.close();
			} catch (IOException ioe)
			{
				ioe.printStackTrace();
			}
		}
	}
	
	/**
	 * establish socket to listen to requests in an asynchronous manner
	 * 
	 * It extends {@link AsyncTask} with String parameter "ip address".
	 */
	public class ServerTask extends AsyncTask<String, Void, Void>
	{
		/**
		 * run as a server in the background
		 * 
		 * @param String format of ip address
		 */
		@Override
		protected Void doInBackground(String... params)
		{
			MessagingService.INSTANCE.start2Listen(params[0]);
			return null;
		}
	}
}
