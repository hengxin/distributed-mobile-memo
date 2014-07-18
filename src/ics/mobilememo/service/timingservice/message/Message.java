package ics.mobilememo.service.timingservice.message;

import java.io.Serializable;

/**
 * @description Basic messages in communication; 
 * Each message has its type indicated by an Integer and payload of arbitrary class. 
 * 
 * @author hengxin
 * @date Jun 21, 2014
 */
public abstract class Message implements Serializable
{
	private static final long serialVersionUID = -3130699534237155171L;

	public static final int SYNC_TIME_MSG = 0;
	
	// from PC to Android device: I authorize your time polling
	public static final int AUTHORIZATION_MSG = 1;
	// from Android device to PC: Please give me your current time
	public static final int REQUEST_TIME_MSG = 2;
	// from PC to Android device: Here is my current time (when I accept your request)
	public static final int RESPONSE_TIME_MSG = 4;
	
	// type of message
	protected final int type;
	// actual payload of message
	protected Object payload = null;
	
	/**
	 * constructor of {@link Message}
	 * @param type type of this message ( {@link #type} )
	 */
	public Message(int type)
	{
		this.type = type;
	}
	
	/**
	 * constructor of {@link Message}
	 * @param type type of this message ( {@link #type} )
	 * @param payload actual payload of this message ( {@link #payload} )
	 */
	public Message(int type, Object payload)
	{
		this.type = type;
		this.payload = payload;
	}
	
	/**
	 * @return {@link #type}: type of the message
	 */
	public int getType()
	{
		return this.type;
	}
	
	/**
	 * @return {@link #payload}: actual payload of the message
	 */
	public Object getPayload()
	{
		return this.payload;
	}
}
