package org.ap.storyvelocity.server;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.ap.storyvelocity.server.PageView;

public class StoryUtil {
	
	public static HashMap<String, StoryDetail> storyDetailMap = new HashMap<String, StoryDetail>();
	public static HashMap<String, List<StoryDetail>> filteredStoryDetailMap = new HashMap<String, List<StoryDetail>>();

	public static int MAX_AGE_OF_STORY = 1440; // represents 24 hours in minutes....
	public static Date storyDetailMapFetch = new Date(0);
	public static Date vstoryDetailMapFetchDesc = new Date(0);
	public static Date pdstoryDetailMapFetchDesc = new Date(0);
	public static Date tpvstoryDetailMapFetchDesc = new Date(0);
	public static Date vstoryDetailMapFetchAsc = new Date(0);
	public static Date pdstoryDetailMapFetchAsc = new Date(0);
	public static Date tpvstoryDetailMapFetchAsc = new Date(0);
	public static Date pvlast15DetailMapFetchDesc = new Date(0);
	public static Date pvlast15DetailMapFetchAsc = new Date(0);
	public static Date storyLastUpdatedMapFetchDesc = new Date(0);
	public static Date storyLastUpdatedMapFetchAsc = new Date(0);
	public static Date timeInAppMapFetchDesc = new Date(0);
	public static Date timeInAppMapFetchAsc = new Date(0);
	
	public static Date getStoryDetailMapFetch(int sorttype)
	{
		Date storyDetailMapFetch = new Date(0);
		
		if (sorttype == 0)
		{
			storyDetailMapFetch = vstoryDetailMapFetchDesc;
		}
		else if (sorttype == 1)
		{
			storyDetailMapFetch = pdstoryDetailMapFetchDesc;
		}
		else if (sorttype == 2)
		{
			storyDetailMapFetch = tpvstoryDetailMapFetchDesc;
		}
		else if (sorttype == 3)
		{
			storyDetailMapFetch = vstoryDetailMapFetchAsc;
		}
		else if (sorttype == 4)
		{
			storyDetailMapFetch = pdstoryDetailMapFetchAsc;
		}
		else if (sorttype == 5)
		{
			storyDetailMapFetch = tpvstoryDetailMapFetchAsc;
		}
		else if (sorttype == 6)
		{
			storyDetailMapFetch = pvlast15DetailMapFetchDesc;
		}
		else if (sorttype == 7)
		{
			storyDetailMapFetch = pvlast15DetailMapFetchAsc;
		}
		else if (sorttype == 8)
		{
			storyDetailMapFetch = storyLastUpdatedMapFetchDesc;
		}
		else if (sorttype == 9)
		{
			storyDetailMapFetch = storyLastUpdatedMapFetchAsc;
		}
		else if (sorttype == 10)
		{
			storyDetailMapFetch = timeInAppMapFetchDesc;
		}
		else if (sorttype == 11)
		{
			storyDetailMapFetch = timeInAppMapFetchAsc;
		}
		return storyDetailMapFetch;
	}
	
	
	public static String getSortOrderString(int sorttype)
	{
		String sortOrderString = "velocity desc";
		
		if (sorttype == 0)
		{
			sortOrderString = "velocity desc";
		}
		else if (sorttype == 1)
		{
			sortOrderString = "pubDate desc";
		}
		else if (sorttype == 2)
		{
			sortOrderString = "totalPageViews desc";
		}
		else if (sorttype == 3)
		{
			sortOrderString = "velocity asc";
		}
		else if (sorttype == 4)
		{
			sortOrderString = "pubDate asc";
		}
		else if (sorttype == 5)
		{
			sortOrderString = "totalPageViews asc";
		}
		else if (sorttype == 6)
		{
			sortOrderString = "trendfifteenmins desc";
		}
		else if (sorttype == 7)
		{
			sortOrderString = "trendfifteenmins asc";
		}
		else if (sorttype == 8)
		{
			sortOrderString = "lastUpdatedDate desc";
		}
		else if (sorttype == 9)
		{
			sortOrderString = "lastUpdatedDate asc";
		}
		else if (sorttype == 10)
		{
			sortOrderString = "timeInApp desc";
		}
		else if (sorttype == 11)
		{
			sortOrderString = "timeInApp asc";
		}
		return sortOrderString;
	}
		
	
	// calculates whether or not the page view was in the last 15 minutes...
	public static boolean isDateYounger(Date pubDate, int value)
	{
		double timeDifference = 0.0;
		Date today = new Date();
		Calendar cal = Calendar.getInstance();
		today = cal.getTime();
		timeDifference = (double)((today.getTime() - pubDate.getTime())/(1000*60));
		
		if (timeDifference < value)
			return true;
		
		return false;
	}
	
	
	// returns the time in app of the storyId
	//
	// 
	public static double getTimeInAppRelativeToPageViewTime(Date pubDate, Date time2)
	{
		double timeDifference = 0.0;
		timeDifference = (double)((time2.getTime() - pubDate.getTime())/(1000*60));
		
		// make sure time difference is always at least 1min to not skew velocity
		if (timeDifference < 1.0)
			timeDifference = 1.0;
		
		return timeDifference;
	}
	
	// returns the time in app of the storyId
	//
	// 
	public static double getTimeInApp(Date pubDate)
	{
		double timeDifference = 0.0;
		Date today = new Date();
		Calendar cal = Calendar.getInstance();
		today = cal.getTime();
		timeDifference = (double)((today.getTime() - pubDate.getTime())/(1000*60));
		
		// make sure time difference is always at least 1min to not skew velocity
		if (timeDifference < 1.0)
			timeDifference = 1.0;
		
		return timeDifference;
	}
	

		
	

	
	public static int calculateSingularVelocity(int pageView, double timeDifference)
	{
		int velocity = (int) ((pageView / timeDifference) * 60);			
	    return velocity;
	}

	
	public static int calculateVelocity(List<PageView> pageViewSets, double timeDifference)
	{
		   int totalPageViews = 0;
		   

		   Date pubDate = new Date();
				
			for (int ii = 0; ii < pageViewSets.size(); ii++)
			{
				PageView pv = (PageView)pageViewSets.get(ii);
				totalPageViews += pv.getPageviews();
				
				if (pv.getPageViewDate().before(pubDate))
                  pubDate = pv.getPageViewDate();
			}
			
			int velocity = (int) ((totalPageViews / timeDifference) * 60);			
			return velocity;
	}

}
