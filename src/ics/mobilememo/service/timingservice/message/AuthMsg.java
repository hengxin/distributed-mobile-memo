package ics.mobilememo.service.timingservice.message;

/**
 * Authorization message from PC to Android device:
 * I authorize your time polling.
 * 
 * @author hengxin
 * @date Jul 15, 2014
 */
public class AuthMsg extends Message
{
	private static final long serialVersionUID = -6941902841278433115L;

	public AuthMsg()
	{
		super(Message.AUTHORIZATION_MSG);
	}
	
	@Override
	public String toString()
	{
		return "AUTHORIZATION";
	}
}
