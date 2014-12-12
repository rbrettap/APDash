package org.ap.storyvelocity.server;

import org.ap.storyvelocity.client.NotLoggedInException;
import org.ap.storyvelocity.client.StoryService;

import com.google.gwt.user.client.Random;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.ap.storyvelocity.server.PMF;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import org.ap.storyvelocity.shared.StoryDetailClient;
import org.ap.storyvelocity.server.StoryDetail;
import org.ap.storyvelocity.server.PageView;
import org.ap.storyvelocity.server.Story;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server-side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class StoryServiceImpl extends RemoteServiceServlet implements
StoryService {
	
	private static final Logger LOG = Logger.getLogger(StoryServiceImpl.class.getName());
	public HashMap<String, StoryDetail> storyDetailMap = new HashMap<String, StoryDetail>();
	public HashMap<String, List<StoryDetail>> filteredStoryDetailMap = new HashMap<String, List<StoryDetail>>();
	private static Date storyDetailMapFetch = new Date(0);
	private static Date vstoryDetailMapFetchDesc = new Date(0);
	private static Date pdstoryDetailMapFetchDesc = new Date(0);
	private static Date tpvstoryDetailMapFetchDesc = new Date(0);
	private static Date vstoryDetailMapFetchAsc = new Date(0);
	private static Date pdstoryDetailMapFetchAsc = new Date(0);
	private static Date tpvstoryDetailMapFetchAsc = new Date(0);
	public static int MAX_AGE_OF_STORY = 2160; // represents 36 hours in minutes....
	
	@Override
	public void addStoryDetail(String storyId) throws NotLoggedInException {
		checkLoggedIn();
		 
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		try {

			Key key = KeyFactory.createKey(StoryDetail.class.getSimpleName(), storyId);
			
			int pageViews = 100;
			int pageViews1 = 150;
			int velocity = 500;
			String active = "YES";
			Date today = new Date();
			Calendar cal = Calendar.getInstance();
			today = cal.getTime();
			
			Date yesterday = new Date();
			Calendar cal2 = Calendar.getInstance();
			cal2.add(Calendar.DAY_OF_YEAR, -1);
			yesterday = cal2.getTime();
	
			
			int timeInApp = 15;
			int trendfifteenmins = 25;
			
			org.ap.storyvelocity.server.StoryDetail e = new org.ap.storyvelocity.server.StoryDetail(storyId, today.getTime(), timeInApp, trendfifteenmins, active);

	        PageView pv = new PageView(today, pageViews);
			PageView pv1 = new PageView(yesterday, pageViews1);
			e.setKey(key);
			ArrayList<PageView> pageViewSets = new ArrayList<PageView>();
			pageViewSets.add(pv);
			pageViewSets.add(pv1);
			e.setPageViewSets(pageViewSets);
			
		    pm.makePersistent(e);
		    
		} finally {
			pm.close();
		}
	}
	
	
	@Override
	public void addStoryDetails(String[] storyNames) throws NotLoggedInException {
		checkLoggedIn();
		 
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		try {
			
			for (int i = 0; i < storyNames.length; i++)
			{

			Key key = KeyFactory.createKey(StoryDetail.class.getSimpleName(), storyNames[i]);
			
			int pageViews = 100;
			int pageViews1 = 150;
			int velocity = 500;
			String active = "YES";
			Date today = new Date();
			Calendar cal = Calendar.getInstance();
			today = cal.getTime();
			
			Date yesterday = new Date();
			Calendar cal2 = Calendar.getInstance();
			cal2.add(Calendar.DAY_OF_YEAR, -1);
			yesterday = cal2.getTime();
	
			
			int timeInApp = 15;
			int trendfifteenmins = 25;
			
			org.ap.storyvelocity.server.StoryDetail e = new org.ap.storyvelocity.server.StoryDetail(storyNames[i], today.getTime(), timeInApp, trendfifteenmins, active);

	        PageView pv = new PageView(today, pageViews);
			PageView pv1 = new PageView(yesterday, pageViews1);
			e.setKey(key);
			ArrayList<PageView> pageViewSets = new ArrayList<PageView>();
			pageViewSets.add(pv);
			pageViewSets.add(pv1);
			e.setPageViewSets(pageViewSets);
			
		      pm.makePersistent(e);
			}
		    
		} finally {
			pm.close();
		}
	}
	
	@Override
	public void addStory(String storyId) throws NotLoggedInException {
		checkLoggedIn();
		PersistenceManager pm = PMF.get().getPersistenceManager();
		 //javax.jdo.Transaction tx = pm.currentTransaction();
		try {
			//tx.begin();	
		  pm.makePersistent(new Story(getUser(), storyId));
		  //tx.commit();
		} finally {
	        //if (tx.isActive()) {
	        //    tx.rollback();
	        //}
	        pm.close();
		}
	}
	
	@Override
	public StoryDetailClient getStoryDetails(String storyId) throws NotLoggedInException {
	    //checkLoggedIn();
		 PersistenceManager pm = PMF.get().getPersistenceManager();
		 org.ap.storyvelocity.server.StoryDetail storyDetailResult = null;
		 org.ap.storyvelocity.shared.StoryDetailClient storyDetailClient = null;

	      try {
		        
	    	  Key key = KeyFactory.createKey(StoryDetail.class.getSimpleName(), storyId);
			  org.ap.storyvelocity.server.StoryDetail e = pm.getObjectById(org.ap.storyvelocity.server.StoryDetail.class, key);
	    	  //Query q = pm.newQuery(StoryDetail.class, "storyId == storyIdParam");
	    	  //q.declareParameters("String storyIdParam");
	    	  //List<StoryDetail> results = (List<StoryDetail>) q.execute(storyId);
	    	      if (e == null)
	    	    	  return null;
			  
	    		  // should only be one or none....
	    		  storyDetailResult = (StoryDetail)e;
	    		  List<PageView> pageViewSets = storyDetailResult.getPageViewSets();
	    		  
				   int totalPageViews = 0;
				   
				   // need to calculate timeInApp
				   Date pubDate = new Date();
					
					for (int ii = 0; ii < pageViewSets.size(); ii++)
					{
						PageView pv = (PageView)pageViewSets.get(ii);
						totalPageViews += pv.getPageviews();
						
						if (pv.getPageViewDate().before(pubDate))
                          pubDate = pv.getPageViewDate();
					}
					
					Date today = new Date();
					Calendar cal = Calendar.getInstance();
					today = cal.getTime();
					double timeDifference = (double)((today.getTime() - pubDate.getTime())/(1000*60));
					int timeInApp = (int)timeDifference;
					storyDetailResult.setTimeInApp(timeInApp);

					storyDetailResult.setActive("YES");
					// clear this out before sending on the wire...
					storyDetailClient = new StoryDetailClient(storyDetailResult.getStoryId(), storyDetailResult.getPubDate(), storyDetailResult.getTimeInApp(), 
							totalPageViews, storyDetailResult.getVelocity(), storyDetailResult.getTrendFifteenMins(), storyDetailResult.getActive());


	      } finally {
	          pm.close();
	      }
		  return storyDetailClient;
	      
	}
 
	
	@Override
	public List<StoryDetailClient> getStoryDetailsInBulk(int numResults, int sorttype, long lastFetchedTime) {
		
		// first check to see if the lastFetchedTime is within the time interval....
		String sortOrderString = "velocity desc";
		
		if (sorttype == 0)
		{
			sortOrderString = "velocity desc";
			storyDetailMapFetch = vstoryDetailMapFetchDesc;
		}
		else if (sorttype == 1)
		{
			sortOrderString = "pubDate desc";
			storyDetailMapFetch = pdstoryDetailMapFetchDesc;
		}
		else if (sorttype == 2)
		{
			sortOrderString = "totalPageViews desc";
			storyDetailMapFetch = tpvstoryDetailMapFetchDesc;
		}
		else if (sorttype == 3)
		{
			sortOrderString = "velocity asc";
			storyDetailMapFetch = vstoryDetailMapFetchAsc;
		}
		else if (sorttype == 4)
		{
			sortOrderString = "pubDate asc";
			storyDetailMapFetch = pdstoryDetailMapFetchAsc;
		}
		else if (sorttype == 5)
		{
			sortOrderString = "totalPageViews asc";
			storyDetailMapFetch = tpvstoryDetailMapFetchAsc;
		}
		
		 PersistenceManager pm = PMF.get().getPersistenceManager();
		 org.ap.storyvelocity.server.StoryDetail storyDetailResult = null;
		 org.ap.storyvelocity.shared.StoryDetailClient storyDetailClient = null;
		 List<StoryDetailClient> sdclientlist = new ArrayList<StoryDetailClient>();

	      try {
		        
	    	  //Key key = KeyFactory.createKey(StoryDetail.class.getSimpleName(), storyId);
			  //org.ap.storyvelocity.server.StoryDetail e = pm.getObjectById(org.ap.storyvelocity.server.StoryDetail.class, key);
	    	  List<StoryDetail> results = null;

	    	  // turn off caching for now....
	    	  if (!isDateYounger(storyDetailMapFetch, 1))
	    	  {
	    			// do the query here because the map is empty....
	  	    	    Query q = pm.newQuery(StoryDetail.class, "active == 'Y' || active == 'YES'");
	  	    	    //q.declareParameters("String activeFlagParam");
		    	    q.setOrdering(sortOrderString);
		    	    q.setRange(0, numResults);
		    	    results = (List<StoryDetail>) q.execute();
	    		  
	    			if (sorttype == 0)
	    			{
	    				if (filteredStoryDetailMap.containsKey("velocitydesc"))
	    					filteredStoryDetailMap.remove("velocitydesc");
	    				
	    				filteredStoryDetailMap.put("velocitydesc", results);
	    				vstoryDetailMapFetchDesc = new Date();
	    				
	    			}
	    			else if (sorttype == 1)
	    			{
	    				if (filteredStoryDetailMap.containsKey("pubDatedesc"))
	    					filteredStoryDetailMap.remove("pubDatedesc");
	    				filteredStoryDetailMap.put("pubDatedesc", results);
	    				pdstoryDetailMapFetchDesc = new Date();
	    			}
	    			else if (sorttype == 2)
	    			{
	    				if (filteredStoryDetailMap.containsKey("totalPageViewsdesc"))
	    					filteredStoryDetailMap.remove("totalPageViewsdesc");
	    				filteredStoryDetailMap.put("totalPageViewsdesc", results);
	    				tpvstoryDetailMapFetchDesc = new Date();
	    			}
	    			else if (sorttype == 3)
	    			{
	    				if (filteredStoryDetailMap.containsKey("velocityasc"))
	    					filteredStoryDetailMap.remove("velocityasc");
	    				
	    				filteredStoryDetailMap.put("velocityasc", results);
	    				vstoryDetailMapFetchAsc = new Date();
	    			}
	    			else if (sorttype == 4)
	    			{
	    				if (filteredStoryDetailMap.containsKey("pubDateasc"))
	    					filteredStoryDetailMap.remove("pubDateasc");
	    				
	    				filteredStoryDetailMap.put("pubDateasc", results);
	    				pdstoryDetailMapFetchAsc = new Date();
	    			}
	    			else if (sorttype == 5)
	    			{
	    				if (filteredStoryDetailMap.containsKey("totalPageViewsasc"))
	    					filteredStoryDetailMap.remove("totalPageViewsasc");
	    				
	    				filteredStoryDetailMap.put("totalPageViewsasc", results);
	    				tpvstoryDetailMapFetchAsc = new Date();
	    			}
	    	  }
	    	  else
	    	  {
	    			if (sorttype == 0)
	    			{
	    				if (filteredStoryDetailMap.containsKey("velocitydesc"))
	    					 results = (List<StoryDetail>)(filteredStoryDetailMap.get("velocitydesc"));
	    			}
	    			else if (sorttype == 1)
	    			{
	    				if (filteredStoryDetailMap.containsKey("pubDatedesc"))
	    					results = (List<StoryDetail>)(filteredStoryDetailMap.get("pubDatedesc"));
	    			}
	    			else if (sorttype == 2)
	    			{
	    				if (filteredStoryDetailMap.containsKey("totalPageViewsdesc"))
	    					results = (List<StoryDetail>)(filteredStoryDetailMap.get("totalPageViewsdesc"));
	    			}
	    			else if (sorttype == 3)
	    			{
	    				if (filteredStoryDetailMap.containsKey("velocityasc"))
	    					results = (List<StoryDetail>)(filteredStoryDetailMap.get("velocityasc"));
	    			}
	    			else if (sorttype == 4)
	    			{
	    				if (filteredStoryDetailMap.containsKey("pubDateasc"))
	    					results = (List<StoryDetail>)(filteredStoryDetailMap.get("pubDateasc"));
	    			}
	    			else if (sorttype == 5)
	    			{
	    				if (filteredStoryDetailMap.containsKey("totalPageViewsasc"))
	    					results = (List<StoryDetail>)(filteredStoryDetailMap.get("totalPageViewsasc"));
	    			}
	    	  }
	    	  
	    	
	    	  if (!results.isEmpty()) {
	    		    for (StoryDetail sd : results) {
	    		    	 // should only be one or none....
	    		       Key key = KeyFactory.createKey(StoryDetail.class.getSimpleName(), sd.getStoryId());
	    			   sd = pm.getObjectById(org.ap.storyvelocity.server.StoryDetail.class, key);
	    			   
	    			   // so first check to see if the current story detail is active, otherwise don't grab it....
	    			   if (sd.getActive().equals("N"))
	    			   {
	    				   // don't even bother with the rest....
	    				   continue;
	    			   }

	    			   List<PageView> pageViewSets = sd.getPageViewSets();
	    			   
	    			   if (pageViewSets == null)
	    			   {
	    				   continue;   // there's an error here - continue on....
	    			   }
	    	    		  
	   				   int totalPageViews = 0;
	   				   int totalPageViewsLast15 = 0;
	   				   
	   				   // need to calculate timeInApp
	   				   Date pubDate = new Date();
	   				   
	   				   // build the chart buffer...
	   				   StringBuffer sb = new StringBuffer();
	   				   // first make sure the pageViewSets are ordered...
	   			  
	   				   if (pageViewSets.size() > 0) {
	   			         Collections.sort(pageViewSets, new Comparator<PageView>() {
	   			         @Override
	   			         public int compare(final PageView object1, final PageView object2) {
	   			             return object1.compareTo(object2);
	   			         }
	   			        } );
	   			       }
	   				   
	   				   // set a flag to determine whether or not to remove the first velocity point.....
	   				   boolean moreThanPageViewPerStory = false;
	   				   int pageViewSize = pageViewSets.size();
	   				   
	   				   if (pageViewSize > 1) moreThanPageViewPerStory = true;
	   					
	   					for (int ii = 0; ii < pageViewSize; ii++)
	   					{	   						
	   						PageView pv = (PageView)pageViewSets.get(ii);
	   						
	   					    // make sure that the earliest pageViewDate qualifies as the pub Date
	   						if (ii == 0)
	   						{
	   							pubDate =  pv.getPageViewDate();
	   						}
	   						
	   						totalPageViews += pv.getPageviews();
	   						
	   						int timeRelativeToPubDate = (int)getTimeInAppRelativeToPageViewTime(pubDate, pv.getPageViewDate());
	   						int relativeVelocity = calculateSingularVelocity(totalPageViews, timeRelativeToPubDate);
	   						
	   						// basically velocity is much closer if time relative to pub date is not at time 0.
	   						if (timeRelativeToPubDate > 5)
	   						{
	   						  sb.append(timeRelativeToPubDate+",");	   						
	   						  //sb.append(pv.getPageviews()+",");	   						
	   						  sb.append(relativeVelocity+"\n");
	   						}
	   						
	   						/*
	   						if (moreThanPageViewPerStory == false && ii == 0)
	   						{
	   						  sb.append(timeRelativeToPubDate+",");	   						
	   						  sb.append(pv.getPageviews()+",");	   						
	   						  sb.append(relativeVelocity+"\n");
	   						}
	   						else if (moreThanPageViewPerStory == true && ii > 0)
	   						{
		   					  sb.append(timeRelativeToPubDate+",");	   						
		   					  sb.append(pv.getPageviews()+",");	   						
		   					  sb.append(relativeVelocity+"\n");
	   						}
	   						*/
	   						
	   						// is pageView in the last 15 minutes.....
	   						if (isDateYounger(pv.getPageViewDate(), 15))
	   								totalPageViewsLast15 += pv.getPageviews();
	   								
	   					}
	   					
	   					
	   					double timeDifference = getTimeInApp(pubDate);	   					
	   					int timeInApp = (int)timeDifference;
						sd.setTimeInApp(timeInApp);
						
						
						
						int velocity = calculateVelocity(pageViewSets, timeDifference);
						sd.setVelocity(velocity);
						sd.setTrendFifteenMins(totalPageViewsLast15);
						sd.setTotalPageViews(totalPageViews);
						
						// determine if story is actually older than 36 hours otherwise set to inactive and stop processing on it.....
						if (timeInApp > MAX_AGE_OF_STORY)
						{
							sd.setActive("N");
							continue;
						}
						sd.setActive("Y");
   	
						// clear this out before sending on the wire...
					    storyDetailClient = new StoryDetailClient(sd.getStoryId(), sd.getPubDate(), 
					    sd.getTimeInApp(), totalPageViews, sd.getVelocity(), sd.getTrendFifteenMins(), 
					    sd.getActive());
					    storyDetailClient.pageViewTrend = sb.toString(); // remove the last comma....
					    sdclientlist.add(storyDetailClient);
	    		    }
	    		  } else {
	    		    // Handle "no results" case
	       	    	  return null;
	       	   	
	    		  }
	    	  
	      } finally {
	          pm.close();
	      }
		  return sdclientlist;
	      
	}


	public int calculateSingularVelocity(int pageView, double timeDifference)
	{
		int velocity = (int) ((pageView / timeDifference) * 60);			
	    return velocity;
	}

	
	public int calculateVelocity(List<PageView> pageViewSets, double timeDifference)
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
	
	// returns the time in app of the storyId
	//
	// 
	public double getTimeInAppRelativeToPageViewTime(Date pubDate, Date time2)
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
	public double getTimeInApp(Date pubDate)
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
	
	// calculates whether or not the page view was in the last 15 minutes...
	public boolean isDateYounger(Date pubDate, int value)
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
	
	
	
	@Override
	public void removeStory(String storyId) throws NotLoggedInException {
	    checkLoggedIn();
	    PersistenceManager pm = PMF.get().getPersistenceManager();
	    //javax.jdo.Transaction tx = pm.currentTransaction();
	    try {
	      long deleteCount = 0;
	      Query q = pm.newQuery(Story.class, "user == u");
	      q.declareParameters("com.google.appengine.api.users.User u");
	      List<Story> stories = (List<Story>) q.execute(getUser());
	      for (Story story : stories) {
	        if (storyId.equals(story.getStoryId())) {
	          deleteCount++;
	          pm.deletePersistent(story);
	        }
	      }
	      if (deleteCount != 1) {
	        LOG.log(Level.WARNING, "removeStory deleted "+deleteCount+" Stories");
	      }
	    } finally {
	      pm.close();
	    }	}

	@Override
	public List<StoryDetailClient> getStories() throws NotLoggedInException {
	    checkLoggedIn();
	    PersistenceManager pm = PMF.get().getPersistenceManager();
	    List<StoryDetailClient> storyIds = new ArrayList<StoryDetailClient>();
	    
	    try {
	      Query q = pm.newQuery(Story.class, "user == u");
	      q.setOrdering("pubDate desc");
	      q.declareParameters("com.google.appengine.api.users.User u");
	      q.setOrdering("createDate");
	      List<Story> stories = (List<Story>) q.execute(getUser());
	      for (Story story : stories) {
	    	  
	    	  // do a lookup of the storyDetails here....
	    	  Query q1 = pm.newQuery(StoryDetail.class, "storyId == storyIdParam order by pubDate desc");
	    	  q1.declareParameters("String storyIdParam");
	    	  List<StoryDetailClient> results = (List<StoryDetailClient>) q.execute(story.getStoryId());
	    	  
	    	  for (int i = 0; i < results.size(); i++)
	    	     storyIds.add(results.get(i));
	      }
	    	  

	    } finally {
	      pm.close();
	    }
	    return storyIds;	
    }
	
	private void checkLoggedIn() throws NotLoggedInException {
	   if (getUser() == null) {
		  throw new NotLoggedInException("Not logged in.");
		}
	 }

     private User getUser() {
		UserService userService = UserServiceFactory.getUserService();
		return userService.getCurrentUser();
	}


	@Override
	public String fetchRealTimeAnalytics() throws NotLoggedInException {
		
		int numResults = 10;
		
		StoryAnalyticsAPI storyAnaltyicsAPI = StoryAnalyticsAPI.getInstance();
		storyAnaltyicsAPI.getRealtimeQuery(numResults);
		addGADetailsToServer();

		return null;
	}
	
	// this method is designed to retrieve the real time GA data and store into the 
	// datastore
	public boolean addGADetailsToServer() throws NotLoggedInException {
			
	        int recordsProcessed = 0;
		
			for (String keyEntryMap: StoryAnalyticsAPI.getInstance().gaDataMap.keySet())
			{
    			PersistenceManager pm = PMF.get().getPersistenceManager();
	    	    //javax.jdo.Transaction tx = pm.currentTransaction();

				try {
					
			   Key key = KeyFactory.createKey(StoryDetail.class.getSimpleName(), keyEntryMap);
			   
			   String active = "Y";
			   GAStory gaStory = (GAStory)StoryAnalyticsAPI.getInstance().gaDataMap.get(keyEntryMap);
			   String ga_storyName = gaStory.getGAStoryName();
			   Date ga_retrievaldate = gaStory.getGARetrievalDate();
			   int ga_pageview = gaStory.getGAPageviews();
			   int timeInApp = 1; // time in app can never be zero - it's always at least 1 min.
			   int trendfifteenmins = 0;
			   
			   org.ap.storyvelocity.server.StoryDetail sdetail =  getStoryDetailById(ga_storyName);
			   
			   if (sdetail == null)
			   {
				   sdetail = new org.ap.storyvelocity.server.StoryDetail(ga_storyName, ga_retrievaldate.getTime(), 
						   timeInApp, trendfifteenmins, active);
				   
			        PageView pv = new PageView(ga_retrievaldate, ga_pageview);
			        pv.setStoryDetailId(ga_storyName);
			        sdetail.setKey(key);
					List<PageView> pageViewSets = new ArrayList<PageView>();
					pageViewSets.add(pv);
					sdetail.setPageViewSets(pageViewSets);
					sdetail.setLastUpdatedDate(ga_retrievaldate.getTime());
					sdetail.setPubDate(pv.getPageViewDate().getTime());
					sdetail.setStoryItemId("");
					sdetail.setSlug("");
					sdetail.setAuthor("");
					sdetail.setTotalPageViews(ga_pageview);
					sdetail.setTrendFifteenMins(ga_pageview); // obviously this is within the last 15 minutes so need to set it here....
					
					double timeDifference = getTimeInApp(ga_retrievaldate);
					int velocity = calculateVelocity(pageViewSets, timeDifference);
					sdetail.setVelocity(velocity);
					
			   }
			   else
			   {
				   // need to do persistent calculations here....
				   //timeInApp = "15 mins";
				   //trendfifteenmins = 25;
				   int velocity = 0;
				   int totalPageViews = 0;

				   sdetail.setKey(key);
				   List<PageView> pageViewSets = (List<PageView>) sdetail.getPageViewSets();
				   
				   // sort the pageviewsets ....
   				   if (pageViewSets.size() > 0) {
	   			         Collections.sort(pageViewSets, new Comparator<PageView>() {
	   			         @Override
	   			         public int compare(final PageView object1, final PageView object2) {
	   			             return object1.compareTo(object2);
	   			         }
	   			        } );
	   			   }
   				   
				   PageView pv = new PageView(ga_retrievaldate, ga_pageview);
				   pv.setStoryDetailId(ga_storyName);
				   pageViewSets.add(pv);
				   
				   Date pubDate = new Date();
				   
				   for (int i = 0; i < pageViewSets.size(); i++)
				   {
					  if (i == 0)
   					  {
   							pubDate =  pv.getPageViewDate();
   					  }  
					  PageView pvi =  (PageView)pageViewSets.get(i);
					  totalPageViews += pvi.getPageviews();
					  
					  // look at the pageviewDate to see whether it's within the last 15...
					  if (isDateYounger(pv.getPageViewDate(), 15))
					  {
						  trendfifteenmins += pvi.getPageviews();
					  }
					  
				   }
				   sdetail.setLastUpdatedDate(ga_retrievaldate.getTime());
				   sdetail.setPageViewSets(pageViewSets);
				   sdetail.setPubDate(pubDate.getTime());
				   
				   // after calculation here, make sure that we set the active flag to yes/no depending on age....
				   if (isDateYounger(pv.getPageViewDate(), MAX_AGE_OF_STORY))
				   {
					  sdetail.setActive("Y");
				   
				   }
				   else
				   {
					  sdetail.setActive("N");
				   }
				   
				   sdetail.setTotalPageViews(totalPageViews);
				   sdetail.setTrendFifteenMins(trendfifteenmins);
					
				   double timeDifference = getTimeInAppRelativeToPageViewTime(pubDate, ga_retrievaldate);
					// do we calculate the last velocity or total velocity of all pageview sets???
				   velocity = calculateSingularVelocity(totalPageViews, timeDifference);
				   sdetail.setVelocity(velocity);
			   }
						
			  //tx.begin();
		      pm.makePersistent(sdetail);
		      recordsProcessed++;
		      //tx.commit(); 
		      }
		      catch(Exception ex)
		      {
		         
		         // after it's done should clear hashmap and flag inProgress ....
		    	  
		    	  
				} finally {
					//if (tx.isActive()) {
					//	tx.rollback();
				    //}
					pm.close();
				}
		      
			} // end-for loop
			
			// should make the total number processed..
			updatedStoryIngestionTable(recordsProcessed, 1);
		    

		return true;
	}
	
	
	public void updatedStoryIngestionTable(int numStories, int action) {
		 PersistenceManager pm = PMF.get().getPersistenceManager();

	      try {
		        
	    	  Date pubDate = new Date();
	    	  long pubDateKey = pubDate.getTime();
	    	  StoryIngestion storyingestion = new StoryIngestion(pubDate, numStories, action);
	    	  //Key key = KeyFactory.createKey(StoryIngestion.class.getSimpleName(), pubDateKey);
	    	  pm.makePersistent(storyingestion);
			  
	      } finally {
	          pm.close();
	      }
		  return;	      
	}
	

	// this method is designed to search the storydetail and page view tables for information about a record.....
	// 
	// 
	public StoryDetail getStoryDetailById(String storyId) {
		 PersistenceManager pm = PMF.get().getPersistenceManager();
		 org.ap.storyvelocity.server.StoryDetail storyDetailResult = null;

	      try {
		        
	    	  Key key = KeyFactory.createKey(StoryDetail.class.getSimpleName(), storyId);
	    	  
	    	  Query q = pm.newQuery(StoryDetail.class, "storyId == storyIdParam");
	    	  q.declareParameters("String storyIdParam");
	    	  List<StoryDetail> results = (List<StoryDetail>) q.execute(storyId);
	    	  
	    	  if (results.size() > 0)
	    	  {
	    	    storyDetailResult = pm.getObjectById(org.ap.storyvelocity.server.StoryDetail.class, key);
	    	  }

			  if (storyDetailResult == null)
	    	    return null;
			  
			  
	    	  Query q1 = pm.newQuery(PageView.class, "storyDetailId == storyIdParam");
	    	  q1.declareParameters("String storyIdParam");
	    	  q1.setOrdering("pageViewDate desc");
	    	  List<PageView> pageViewSets = (List<PageView>) q1.execute(storyId);
	    	  
	    	  if (pageViewSets != null && pageViewSets.size() > 0)
	    	  {
	    	    storyDetailResult.setPageViewSets(pageViewSets);
	    	  }

			  
	      } finally {
	          pm.close();
	      }
		  return storyDetailResult;	      
	}
	
	
	public int deleteOldRecordsFromServer()  {
		
		int numRecordsDeleted = 0;
		
		numRecordsDeleted = removeRecordsFromServer();
		
		return numRecordsDeleted;

	}
	
	
	// this method is designed to remove the old pageviews and records from the db
	public int removeRecordsFromServer()  {
			
	        int recordsProcessed = 0;
	        
			Date today = new Date();
			Calendar cal = Calendar.getInstance();
			today = cal.getTime();
			
			Date previous = new Date();
			Calendar cal2 = Calendar.getInstance();
			cal2.setTime(previous);
			int hours = cal2.get(Calendar.HOUR_OF_DAY);
			hours -= 36;
			//hours -= 136;
			cal2.set(Calendar.HOUR_OF_DAY, hours);
			previous = cal2.getTime();
			// previous should be the time 36 hours ago.....
			
			PersistenceManager pm = PMF.get().getPersistenceManager();
			org.ap.storyvelocity.server.StoryDetail storyDetailResult = null;
			List<StoryDetail> storyIds = new ArrayList<StoryDetail>();
			
			int deleteCountLimit = 25;

		      try {
			        
		    	  Query q = pm.newQuery(StoryDetail.class,  "pubDate < pubDateParam");
		    	  q.declareParameters("long pubDateParam");
		    	  List<StoryDetail> results = (List<StoryDetail>) q.execute(previous.getTime());
		    	
		    	    for (StoryDetail sd : results) {
		    	    
		    	       if (recordsProcessed > deleteCountLimit)
		    	       		break;
		    	    	
		    		   Key key = KeyFactory.createKey(StoryDetail.class.getSimpleName(), sd.getStoryId());
		    		   sd.setKey(key);
		    		   sd = pm.getObjectById(org.ap.storyvelocity.server.StoryDetail.class, key);
		    		   
		 	    	 
		    		    Query q1 = pm.newQuery(PageView.class, "storyDetailId == storyIdParam");
			    	    q1.declareParameters("String storyIdParam");
			    	    List<PageView> pageViewSets = (List<PageView>) q1.execute(sd.getStoryId());
			    	    
			    	    for (PageView pv : pageViewSets) {
			    	    	
			    	    	pm.deletePersistent(pv); 
			    	    }
				    	
		    		   pm.deletePersistent(sd); 
		    		   recordsProcessed++;
		    	  }
		      }
		      catch(Exception ex)
		      {
			
		      } finally {
					pm.close();
			  }
		      
			// should make the total number processed..
			updatedStoryIngestionTable(recordsProcessed, -1);
		    

		return recordsProcessed;
	}

}
