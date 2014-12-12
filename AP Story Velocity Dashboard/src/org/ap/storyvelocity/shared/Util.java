package org.ap.storyvelocity.shared;

public class Util {
	
	public static String convertTimeToHoursMins(int timeInMins) {

		String timeString = "1 min";
		
		if (timeInMins > 0 && timeInMins < 60)
		{
			timeString = timeInMins + " mins";
		}
		else if (timeInMins > 60)
		{
			int hours = timeInMins / 60;
			int mins = timeInMins % 60;
			
			timeString = hours + " hours " + mins + " mins";
		}
		
		
		return timeString;		
	}

}
