package org.ap.storyvelocity.server;

import org.ap.storyvelocity.client.NotLoggedInException;
import org.ap.storyvelocity.client.StoryService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.ap.storyvelocity.server.PMF;

import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import org.ap.storyvelocity.shared.StoryDetailClient;
import org.ap.storyvelocity.shared.Util;
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
	
	// should use this to get the latest updates to a story detail....
	@Override
	public StoryDetailClient getStoryDetails(String storyId) throws NotLoggedInException {
	    //checkLoggedIn();
		 PersistenceManager pm = PMF.get().getPersistenceManager();
		 org.ap.storyvelocity.shared.StoryDetailClient storyDetailClient = null;

	      try {
		        
	    	  Key key = KeyFactory.createKey(StoryDetail.class.getSimpleName(), storyId);
			  org.ap.storyvelocity.server.StoryDetail e = pm.getObjectById(org.ap.storyvelocity.server.StoryDetail.class, key);
	    	      if (e == null)
	    	    	  return null;
			  
	    		  // should only be one or none....
	    	      storyDetailClient =  getDetailsForStoryId(e);
	


	      } finally {
	          pm.close();
	      }
		  return storyDetailClient;
	      
	}
 
	
	@Override
	public List<StoryDetailClient> getStoryDetailsInBulk(int numResults, int sorttype, long lastFetchedTime) {
		
		// first check to see if the lastFetchedTime is within the time interval....
		 String sortOrderString = "velocity desc";
		 sortOrderString = StoryUtil.getSortOrderString(sorttype);
		 StoryUtil.storyDetailMapFetch =  StoryUtil.getStoryDetailMapFetch(sorttype);

		
		 PersistenceManager pm = PMF.get().getPersistenceManager();
		 List<StoryDetailClient> sdclientlist = new ArrayList<StoryDetailClient>();
		 sdclientlist.clear();
		 
  	     String keyString = "";
  	     Date mappingDate = new Date(0);
   	     Query q = null;
  	     
			if (sorttype == 0)
			{
				keyString = "velocitydesc" + numResults;
				mappingDate = StoryUtil.storyDetailMapFetch;
				
  	    	    q = pm.newQuery(StoryDetail.class, "active == 'Y'");
	    	    q.setOrdering("velocity desc");  
	    	    q.setRange(0, numResults);
			}
			else if (sorttype == 1)
			{
				keyString = "pubDatedesc" + numResults;
				mappingDate = StoryUtil.pdstoryDetailMapFetchDesc;
				
  	    	    q = pm.newQuery(StoryDetail.class, "active == 'Y'");
	    	    q.setOrdering("pubDate desc");  
	    	    q.setRange(0, numResults);

			}
			else if (sorttype == 2)
			{
				keyString = "totalPageViewsdesc" + numResults;
				mappingDate = StoryUtil.tpvstoryDetailMapFetchDesc;
				
  	    	    q = pm.newQuery(StoryDetail.class, "active == 'Y'");
	    	    q.setOrdering("totalPageViews desc");
	    	    q.setRange(0, numResults);

			}
			else if (sorttype == 3)
			{
				keyString = "velocityasc" + numResults;
				mappingDate = StoryUtil.vstoryDetailMapFetchAsc;
				
  	    	    q = pm.newQuery(StoryDetail.class, "active == 'Y'");
	    	    q.setOrdering("velocity asc");  
	    	    q.setRange(0, numResults);
			}
			else if (sorttype == 4)
			{
				keyString = "pubDateasc" + numResults;
				mappingDate = StoryUtil.pdstoryDetailMapFetchAsc;				
  	    	    q = pm.newQuery(StoryDetail.class, "active == 'Y'");
	    	    q.setOrdering("pubDate  asc");  
	    	    q.setRange(0, numResults);
				
			}
			else if (sorttype == 5)
			{
				keyString = "totalPageViewsasc" + numResults;
				mappingDate = StoryUtil.tpvstoryDetailMapFetchAsc;				
  	    	    q = pm.newQuery(StoryDetail.class, "active == 'Y'");
	    	    q.setOrdering("totalPageViews asc");  
	    	    q.setRange(0, numResults);
			}
			else if (sorttype == 6)
			{
				keyString = "pvlast15desc" + numResults;
				//mappingDate = StoryUtil.pvlast15DetailMapFetchDesc;
				// commented these out to always get latest results...
				q = pm.newQuery(StoryDetail.class, "active == 'Y'");
  	    	    q.setOrdering("lastUpdatedDate desc"); 
  	    	    //q.setOrdering("trendfifteenmins desc")  // not necessary since we are doing this 
  	    	    // sort on the client side.;
  	    	    q.setFilter("active == 'Y'");
	    	    q.setRange(0, numResults*2);

			}
			else if (sorttype == 7)
			{
				keyString = "pvlast15asc" + numResults;
				//mappingDate = StoryUtil.pvlast15DetailMapFetchAsc;
				// commented these out to always get latest results...
  	    	    q = pm.newQuery(StoryDetail.class, "active == 'Y'");
  	    	    q.setOrdering("lastUpdatedDate desc"); 
	    	    //q.setOrdering("trendfifteenmins asc");
	    	    q.setRange(0, numResults*2);

			}
			else if (sorttype == 8)
			{
				keyString = "lastupdateddesc" + numResults;
				mappingDate = StoryUtil.storyLastUpdatedMapFetchDesc;
				
  	    	    q = pm.newQuery(StoryDetail.class, "active == 'Y'");
	    	    q.setOrdering("lastUpdatedDate desc");  
	    	    q.setRange(0, numResults);

			}
			else if (sorttype == 9)
			{
				keyString = "lastupdatedasc" + numResults;
				mappingDate = StoryUtil.storyLastUpdatedMapFetchAsc;
				
  	    	    q = pm.newQuery(StoryDetail.class, "active == 'Y'");
	    	    q.setOrdering("lastUpdatedDate asc");  
	    	    q.setRange(0, numResults);

			}
			else if (sorttype == 10)
			{
				keyString = "timeinappdesc" + numResults;
				//mappingDate = StoryUtil.timeInAppMapFetchDesc;
				
  	    	    q = pm.newQuery(StoryDetail.class, "active == 'Y'");
  	    	    q.setOrdering("pubDate desc"); 
	    	    q.setRange(0, numResults);

			}
			else if (sorttype == 11)
			{
				keyString = "timeinappasc" + numResults;
				//mappingDate = StoryUtil.timeInAppMapFetchAsc;
  	    	    q = pm.newQuery(StoryDetail.class, "active == 'Y'");
  	    	    q.setOrdering("pubDate asc"); 
	    	    q.setRange(0, numResults);
			}
  	     

	      try {

	    	  //Key key = KeyFactory.createKey(StoryDetail.class.getSimpleName(), storyId);
			  //org.ap.storyvelocity.server.StoryDetail e = pm.getObjectById(org.ap.storyvelocity.server.StoryDetail.class, key);
	    	  List<StoryDetail> results = null;
	    	  
	    	  // storymap must contain the cache results or 
	    	  // had the cache results and be older than 2 minutes....
	    	  
	    	  if (!StoryUtil.filteredStoryDetailMap.containsKey(keyString) || 
	    			  ( StoryUtil.filteredStoryDetailMap.containsKey(keyString) && !StoryUtil.isDateYounger(mappingDate, 1)))
	    	  {
	    			// do the query here because the map is empty....
		    	    results = (List<StoryDetail>) q.execute();
		    	   
		    	    if (results.size() > numResults)
		    	    {
		    	    	results = results.subList(0, numResults);
		    	    }

	    		  
	    			if (sorttype == 0)
	    			{
	    				if ( StoryUtil.filteredStoryDetailMap.containsKey(keyString))
	    					 StoryUtil.filteredStoryDetailMap.remove(keyString);
	    				
	    				 StoryUtil.filteredStoryDetailMap.put(keyString, results);
	    				 StoryUtil.vstoryDetailMapFetchDesc = new Date();
	    				
	    			}
	    			else if (sorttype == 1)
	    			{
	    				if ( StoryUtil.filteredStoryDetailMap.containsKey(keyString))
	    					 StoryUtil.filteredStoryDetailMap.remove(keyString);
	    				 StoryUtil.filteredStoryDetailMap.put(keyString, results);
	    				 StoryUtil.pdstoryDetailMapFetchDesc = new Date();
	    			}
	    			else if (sorttype == 2)
	    			{
	    				if ( StoryUtil.filteredStoryDetailMap.containsKey(keyString))
	    					 StoryUtil.filteredStoryDetailMap.remove(keyString);
	    				 StoryUtil.filteredStoryDetailMap.put(keyString, results);
	    				 StoryUtil.tpvstoryDetailMapFetchDesc = new Date();
	    			}
	    			else if (sorttype == 3)
	    			{
	    				if ( StoryUtil.filteredStoryDetailMap.containsKey(keyString))
	    					 StoryUtil.filteredStoryDetailMap.remove(keyString);
	    				
	    				 StoryUtil.filteredStoryDetailMap.put(keyString, results);
	    				 StoryUtil.vstoryDetailMapFetchAsc = new Date();
	    			}
	    			else if (sorttype == 4)
	    			{
	    				if ( StoryUtil.filteredStoryDetailMap.containsKey(keyString))
	    					 StoryUtil.filteredStoryDetailMap.remove(keyString);
	    				
	    				 StoryUtil.filteredStoryDetailMap.put(keyString, results);
	    				 StoryUtil.pdstoryDetailMapFetchAsc = new Date();
	    			}
	    			else if (sorttype == 5)
	    			{
	    				if ( StoryUtil.filteredStoryDetailMap.containsKey(keyString))
	    					 StoryUtil.filteredStoryDetailMap.remove(keyString);
	    				
	    				 StoryUtil.filteredStoryDetailMap.put(keyString, results);
	    				 StoryUtil.tpvstoryDetailMapFetchAsc = new Date();
	    			}
	    			else if (sorttype == 6)
	    			{
	    				if ( StoryUtil.filteredStoryDetailMap.containsKey(keyString))
	    					 StoryUtil.filteredStoryDetailMap.remove(keyString);
	    				
	    				 StoryUtil.filteredStoryDetailMap.put(keyString, results);
	    				 StoryUtil.pvlast15DetailMapFetchDesc = new Date();
	    			}
	    			else if (sorttype == 7)
	    			{
	    				if ( StoryUtil.filteredStoryDetailMap.containsKey(keyString))
	    					 StoryUtil.filteredStoryDetailMap.remove(keyString);
	    				
	    				 StoryUtil.filteredStoryDetailMap.put(keyString, results);
	    				 StoryUtil.pvlast15DetailMapFetchAsc = new Date();
	    			}
	    			else if (sorttype == 8)
	    			{
	    				if ( StoryUtil.filteredStoryDetailMap.containsKey(keyString))
	    					 StoryUtil.filteredStoryDetailMap.remove(keyString);
	    				
	    				 StoryUtil.filteredStoryDetailMap.put(keyString, results);
	    				 StoryUtil.storyLastUpdatedMapFetchDesc = new Date();
	    			}
	    			else if (sorttype == 9)
	    			{
	    				if ( StoryUtil.filteredStoryDetailMap.containsKey(keyString))
	    					 StoryUtil.filteredStoryDetailMap.remove(keyString);
	    				
	    				 StoryUtil.filteredStoryDetailMap.put(keyString, results);
	    				 StoryUtil.storyLastUpdatedMapFetchAsc = new Date();
	    			}
	    			else if (sorttype == 10)
	    			{
	    				if ( StoryUtil.filteredStoryDetailMap.containsKey(keyString))
	    					 StoryUtil.filteredStoryDetailMap.remove(keyString);
	    				
	    				 StoryUtil.filteredStoryDetailMap.put(keyString, results);
	    				 StoryUtil.timeInAppMapFetchDesc = new Date();
	    			}
	    			else if (sorttype == 11)
	    			{
	    				if ( StoryUtil.filteredStoryDetailMap.containsKey(keyString))
	    					 StoryUtil.filteredStoryDetailMap.remove(keyString);
	    				
	    				 StoryUtil.filteredStoryDetailMap.put(keyString, results);
	    				 StoryUtil.timeInAppMapFetchAsc = new Date();
	    			}
	    	  }
	    	  else
	    	  {
	    			if ( StoryUtil.filteredStoryDetailMap.containsKey(keyString))
	    			   results = (List<StoryDetail>)( StoryUtil.filteredStoryDetailMap.get(keyString));
	    	  }

	    	     if (!results.isEmpty()) {
	    		    for (StoryDetail sd : results) {
	    		    	 // should only be one or none....
	    		    	
	    		      StoryDetailClient sdc = getDetailsForStoryId(sd);
	    		      if (sdc != null)
 				        sdclientlist.add(sdc);
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
	
	
	private StoryDetailClient getDetailsForStoryId(StoryDetail sd) {
		
		 org.ap.storyvelocity.shared.StoryDetailClient storyDetailClient = null;

		 PersistenceManager pm = PMF.get().getPersistenceManager();
		
   	       // should only be one or none....
	       Key key = KeyFactory.createKey(StoryDetail.class.getSimpleName(), sd.getStoryId());
		   sd = pm.getObjectById(org.ap.storyvelocity.server.StoryDetail.class, key);
		   
		   // so first check to see if the current story detail is active, otherwise don't grab it....
		   if (sd.getActive().equals("N"))
		   {
			   // don't even bother with the rest....
			   return null;
		   }

		   List<PageView> pageViewSets = sd.getPageViewSets();
		   
		   if (pageViewSets == null)
		   {
			   return null;   // there's an error here - continue on....
		   }
		  
		   int totalPageViews = 0;
		   int totalPageViewsLast15 = 0;
		   
		   // need to calculate timeInApp
		   Date pubDate = new Date();
		   Date lastUpdatedDate = new Date();
		   
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
				else
				{
					   if (pv.getPageViewDate().before(pubDate))
					   {
						   pubDate = pv.getPageViewDate();
					   }
				 }

				
				totalPageViews = totalPageViews + pv.getPageviews();
				
				int timeRelativeToPubDate = (int)StoryUtil.getTimeInAppRelativeToPageViewTime(pubDate, pv.getPageViewDate());
				sb.append(timeRelativeToPubDate+",");	   						
				sb.append(pv.getPageviews()+"\n");
				
				
				/* this is for velocity in chart....
				int timeRelativeToPubDate = (int)StoryUtil.getTimeInAppRelativeToPageViewTime(pubDate, pv.getPageViewDate());
				int relativeVelocity =  StoryUtil.calculateSingularVelocity(totalPageViews, timeRelativeToPubDate);
				
				// basically velocity is much closer if time relative to pub date is not at time 0.
				if (timeRelativeToPubDate > 2)
				{
				  sb.append(timeRelativeToPubDate+",");	   						
				  //sb.append(pv.getPageviews()+",");	   						
				  sb.append(relativeVelocity+"\n");
				}
				*/
				
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
				if (StoryUtil.isDateYounger(pv.getPageViewDate(), 15))
						totalPageViewsLast15 = totalPageViewsLast15 + pv.getPageviews();
				
				// set the last updated date to the last pageViewDate....
				lastUpdatedDate = pv.getPageViewDate();		
			}
			
			
			double timeDifference = StoryUtil.getTimeInApp(pubDate);	   					
			int timeInApp = (int)timeDifference;
			sd.setTimeInApp(timeInApp);
			
			
			
			int velocity =  StoryUtil.calculateVelocity(pageViewSets, timeDifference);
			sd.setVelocity(velocity);
			sd.setTrendFifteenMins(totalPageViewsLast15);
			sd.setTotalPageViews(totalPageViews);
			sd.setPubDate(pubDate.getTime());
			sd.setLastUpdatedDate(lastUpdatedDate.getTime());
			
			// determine if story is actually older than 36 hours otherwise set to inactive and stop processing on it.....
			if (timeInApp >  StoryUtil.MAX_AGE_OF_STORY)
			{
				sd.setActive("N");
				return null;
			}
			sd.setActive("Y");

			// clear this out before sending on the wire...
		    storyDetailClient = new StoryDetailClient(sd.getStoryId(), sd.getPubDate(), sd.getLastUpdatedDate(),
		    sd.getTimeInApp(), totalPageViews, sd.getVelocity(), sd.getTrendFifteenMins(), 
		    sd.getActive());
		    storyDetailClient.pageViewTrend = sb.toString(); // remove the last comma....
		
		
		    return storyDetailClient;
	}


	@Override
	public List<StoryDetailClient> getUpdatedStoryDetailsInBulk(String [] storyNames, long lastFetchedTime) {
		
		ArrayList updatedStoryList = new ArrayList();
		
		
		// first check to see if the lastFetchedTime is greater than the last ingest date. If so, then we have nothing
		// to update and can just return....
		if (lastFetchedTime > GlobalVariableStorage.lastIngestDate.getTime() && GlobalVariableStorage.lastIngestDate.getTime() > 0)
		{
			return null;
		}
	
		
		 org.ap.storyvelocity.shared.StoryDetailClient storyDetailClient = null;
		 List<StoryDetailClient> sdclientlist = new ArrayList<StoryDetailClient>();
		 PersistenceManager pm = PMF.get().getPersistenceManager();
		 
		
		
		// first do a check to see if any client stories have been updated recently so we need to 
		// update those for certain........
		for (String story: storyNames) {
			
			if (GlobalVariableStorage.gaStoryMap.containsKey(story))
			{
				updatedStoryList.add(story);
			}
			
		    try {
			        
	 			// do the query here because the map is empty....
	         	Query q = pm.newQuery(StoryDetail.class, "storyId == storyIdParam AND active == 'Y'");
		     	q.declareParameters("String storyIdParam");
	    	    q.setRange(0, 1);
			   	List<StoryDetail> results = (List<StoryDetail>) q.execute(story);
			    // so for each updated story we need to get those story details....  
			   	
	    	     if (!results.isEmpty()) {
	    		    for (StoryDetail sd : results) {
	    		    	 // should only be one or none....
	    		    	
	    		      StoryDetailClient sdc = getDetailsForStoryId(sd);
	    		      if (sdc != null)
				        sdclientlist.add(sdc);
	    		    }
	    		  } else {
	    		    // Handle "no results" case
	       	    	  continue;
	       	   	
	    		  }
							    
			  }
			  finally {
			    	  
			  }
			
			
		}
		  return sdclientlist;
	}


	// not used....
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
	    }	
	}

	
	// not used
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
		
		int numResults = 15;
		
		StoryAnalyticsAPI storyAnaltyicsAPI = StoryAnalyticsAPI.getInstance();
		storyAnaltyicsAPI.getRealtimeQuery(numResults);
		addGADetailsToServer();

		return null;
	}
	
	
	@Override
	public String getCronJobSettings() throws NotLoggedInException {
		
		// use this method to fetch the last cron job run and to update the db to make sure
		// the cron job frequency is run correctly....


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
			   
			   // clear the temporary gastory map for each ingestion.....
			   GlobalVariableStorage.get().gaStoryMap.clear();
			   
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
					
					double timeDifference = StoryUtil.getTimeInApp(ga_retrievaldate);
					int velocity =  StoryUtil.calculateVelocity(pageViewSets, timeDifference);
					sdetail.setVelocity(velocity);
					
					GlobalVariableStorage.get().gaStoryMap.put(ga_storyName, sdetail);
					
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
					  PageView pvi =  (PageView)pageViewSets.get(i);
 
					  if (i == 0)
   					  {
   							pubDate =  pvi.getPageViewDate();
   					  }
					  else
					  {
						   if (pvi.getPageViewDate().before(pubDate))
						   {
							   pubDate = pvi.getPageViewDate();
						   }
					  }
					  
					  totalPageViews = totalPageViews + pvi.getPageviews();
					  
					  // look at the pageviewDate to see whether it's within the last 15...
					  if (StoryUtil.isDateYounger(pv.getPageViewDate(), 15))
					  {
						  trendfifteenmins = trendfifteenmins + pvi.getPageviews();
					  }
					  
				   }
				   sdetail.setLastUpdatedDate(ga_retrievaldate.getTime());
				   sdetail.setPageViewSets(pageViewSets);
				   sdetail.setPubDate(pubDate.getTime());
				   
				   // after calculation here, make sure that we set the active flag to yes/no depending on age....
				   if (StoryUtil.isDateYounger(pv.getPageViewDate(),  StoryUtil.MAX_AGE_OF_STORY))
				   {
					  sdetail.setActive("Y");
				   
				   }
				   else
				   {
					  sdetail.setActive("N");
				   }
				   
				   sdetail.setTotalPageViews(totalPageViews);
				   sdetail.setTrendFifteenMins(trendfifteenmins);
					
				   double timeDifference = StoryUtil.getTimeInAppRelativeToPageViewTime(pubDate, ga_retrievaldate);
					// do we calculate the last velocity or total velocity of all pageview sets???
				   velocity =  StoryUtil.calculateSingularVelocity(totalPageViews, timeDifference);
				   sdetail.setVelocity(velocity);
			   }
			   
			  // need to store old AP records here in db.
			   GlobalVariableStorage.get().gaStoryMap.put(ga_storyName, sdetail);
			   
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
			
			// set the last ingest date in global variable storage....
			GlobalVariableStorage.lastIngestDate = new Date();
			
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
