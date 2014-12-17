package org.ap.storyvelocity.shared;

public class Util {
	
	// check to see if string is contained in string array
	public static boolean containedInStringArray(String [] arrayToCheck, String value)
	{
		boolean contains = false;
		
		for (String item: arrayToCheck) {
			if (value.equalsIgnoreCase(item))
			{
				contains = true;
				break;
			}
		}
		return contains;		
	}
	

	
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
