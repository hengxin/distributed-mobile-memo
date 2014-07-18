package ics.mobilememo.service.timingservice.message;

/**
 * Request for time from Android device to PC:
 * please give me your current system time
 * 
 * @author hengxin
 * @date Jul 15, 2014
 */
public class RequestTimeMsg extends Message
{
	private static final long serialVersionUID = 3783088713292474780L;

	public RequestTimeMsg()
	{
		super(Message.REQUEST_TIME_MSG);
	}
	
	@Override
	public String toString()
	{
		return "REQUEST_TIME_MSG";
	}
}
