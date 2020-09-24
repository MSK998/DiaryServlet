package testing;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TestMain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		long millis = System.currentTimeMillis();
		
		System.out.println(humanTime(millis));
		}
	
	
	public static String humanTime(long millis) {
		String time;
		DateFormat format = new SimpleDateFormat("dd/MM/yyyy 'at' HH:mm");
		
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(millis);
		
		Date date = cal.getTime();
		
		time = format.format(date);
		
		return time;
	}
}
