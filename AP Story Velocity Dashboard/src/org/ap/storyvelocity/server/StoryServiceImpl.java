package org.ap.storyvelocity.server;

import org.ap.storyvelocity.client.NotLoggedInException;
import org.ap.storyvelocity.client.StoryService;

import com.google.gwt.user.client.Random;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

import org.ap.storyvelocity.client.NotLoggedInException;
import org.ap.storyvelocity.shared.StoryDetailClient;
import org.ap.storyvelocity.client.StoryService;
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
	     javax.jdo.Transaction tx = pm.currentTransaction();
		
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
	
			
			String timeInApp = "15 mins";
			int trendfifteenmins = 25;
			
			org.ap.storyvelocity.server.StoryDetail e = new org.ap.storyvelocity.server.StoryDetail(storyId, today.getTime(), timeInApp, trendfifteenmins, active);

	        PageView pv = new PageView(today, pageViews);
			PageView pv1 = new PageView(yesterday, pageViews1);
			e.setKey(key);
			ArrayList<PageView> pageViewSets = new ArrayList<PageView>();
			pageViewSets.add(pv);
			pageViewSets.add(pv1);
			e.pageViewSets = pageViewSets;
			
			
			tx.begin();
		    pm.makePersistent(e);
		    tx.commit();
		    
		} finally {
			if (tx.isActive()) {
				tx.rollback();
		    }
			pm.close();
		}
	}
	
	
	@Override
	public void addStoryDetails(String[] storyNames) throws NotLoggedInException {
		checkLoggedIn();
		 
		PersistenceManager pm = PMF.get().getPersistenceManager();
	     javax.jdo.Transaction tx = pm.currentTransaction();
		
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
	
			
			String timeInApp = "15 mins";
			int trendfifteenmins = 25;
			
			org.ap.storyvelocity.server.StoryDetail e = new org.ap.storyvelocity.server.StoryDetail(storyNames[i], today.getTime(), timeInApp, trendfifteenmins, active);

	        PageView pv = new PageView(today, pageViews);
			PageView pv1 = new PageView(yesterday, pageViews1);
			e.setKey(key);
			ArrayList<PageView> pageViewSets = new ArrayList<PageView>();
			pageViewSets.add(pv);
			pageViewSets.add(pv1);
			e.pageViewSets = pageViewSets;
			
			
			  tx.begin();
		      pm.makePersistent(e);
		      tx.commit();
			}
		    
		} finally {
			if (tx.isActive()) {
				tx.rollback();
		    }
			pm.close();
		}
	}
	
	@Override
	public void addStory(String storyId) throws NotLoggedInException {
		checkLoggedIn();
		PersistenceManager pm = PMF.get().getPersistenceManager();
		 javax.jdo.Transaction tx = pm.currentTransaction();
		try {
			tx.begin();	
		  pm.makePersistent(new Story(getUser(), storyId));
		  tx.commit();
		} finally {
	        if (tx.isActive()) {
	            tx.rollback();
	        }
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
					String timeInApp = timeDifference + " mins";
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
	public List<StoryDetailClient> getStoryDetailsInBulk(int numResults) {
		 PersistenceManager pm = PMF.get().getPersistenceManager();
		 org.ap.storyvelocity.server.StoryDetail storyDetailResult = null;
		 org.ap.storyvelocity.shared.StoryDetailClient storyDetailClient = null;
		 List<StoryDetailClient> sdclientlist = new ArrayList<StoryDetailClient>();

	      try {
		        
	    	  //Key key = KeyFactory.createKey(StoryDetail.class.getSimpleName(), storyId);
			  //org.ap.storyvelocity.server.StoryDetail e = pm.getObjectById(org.ap.storyvelocity.server.StoryDetail.class, key);
	    	  Query q = pm.newQuery(StoryDetail.class);
	    	  q.setOrdering("pubDate desc");
	    	  q.setRange(0, 10);
	    	  List<StoryDetail> results = (List<StoryDetail>) q.execute();
	    	  
	    	
	    	  if (!results.isEmpty()) {
	    		    for (StoryDetail sd : results) {
	    		    	 // should only be one or none....
	    		       Key key = KeyFactory.createKey(StoryDetail.class.getSimpleName(), sd.getStoryId());
	    			   sd = pm.getObjectById(org.ap.storyvelocity.server.StoryDetail.class, key);

	    			   List<PageView> pageViewSets = sd.getPageViewSets();
	    	    		  
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
						String timeInApp = timeDifference + " mins";
						sd.setTimeInApp(timeInApp);

						sd.setActive("YES");
   	
						// clear this out before sending on the wire...
					    storyDetailClient = new StoryDetailClient(sd.getStoryId(), sd.getPubDate(), 
					    sd.getTimeInApp(), totalPageViews, sd.getVelocity(), sd.getTrendFifteenMins(), 
					    sd.getActive());
					    
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


	@Override
	public void removeStory(String storyId) throws NotLoggedInException {
	    checkLoggedIn();
	    PersistenceManager pm = PMF.get().getPersistenceManager();
	    javax.jdo.Transaction tx = pm.currentTransaction();
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
		
		StoryAnalyticsAPI storyAnaltyicsAPI = StoryAnalyticsAPI.getInstance();
		storyAnaltyicsAPI.getRealtimeQuery();
		addGADetailsToServer();

		return null;
	}
	
	public boolean addGADetailsToServer() throws NotLoggedInException {
		checkLoggedIn();
		 
		PersistenceManager pm = PMF.get().getPersistenceManager();
	     javax.jdo.Transaction tx = pm.currentTransaction();
		
		try {
			
			for (String keyEntryMap: StoryAnalyticsAPI.getInstance().gaDataMap.keySet())
			{
			   Key key = KeyFactory.createKey(StoryDetail.class.getSimpleName(), keyEntryMap);
			   
			   String active = "YES";
			   GAStory gaStory = (GAStory)StoryAnalyticsAPI.getInstance().gaDataMap.get(keyEntryMap);
			   String ga_storyName = gaStory.getGAStoryName();
			   Date ga_retrievaldate = gaStory.getGARetrievalDate();
			   int ga_pageview = gaStory.getGAPageviews();
			   String timeInApp = "";
			   int trendfifteenmins = 0;
			   
			   org.ap.storyvelocity.server.StoryDetail sdetail =  getStoryDetailById(ga_storyName);
			   
			   if (sdetail == null)
			   {
				   sdetail = new org.ap.storyvelocity.server.StoryDetail(ga_storyName, ga_retrievaldate.getTime(), 
						   timeInApp, trendfifteenmins, active);
				   
			        PageView pv = new PageView(ga_retrievaldate, ga_pageview);
			        sdetail.setKey(key);
					ArrayList<PageView> pageViewSets = new ArrayList<PageView>();
					pageViewSets.add(pv);
					sdetail.pageViewSets = pageViewSets;
					sdetail.setVelocity(ga_pageview);
					
			   }
			   else
			   {
				   // need to do persistent calculations here....
				   //timeInApp = "15 mins";
				   //trendfifteenmins = 25;
				   int velocity = 0;

				   sdetail.setKey(key);
				   PageView pv = new PageView(ga_retrievaldate, ga_pageview);
				   sdetail.pageViewSets.add(pv);
				   
				   for (int i = 0; i < sdetail.pageViewSets.size(); i++)
				   {
					  PageView pvi =  (PageView)sdetail.pageViewSets.get(i);
					  velocity += pvi.getPageviews();
				   }
				   sdetail.setVelocity(velocity);
				   
			   }
						
			  tx.begin();
		      pm.makePersistent(sdetail);
		      tx.commit();
		      
		      // after it's done should clear hashmap and flag inProgress ....
		      
			}
		    
		} finally {
			if (tx.isActive()) {
				tx.rollback();
		    }
			pm.close();
		}
		return true;
	}
	
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
			  
	      } finally {
	          pm.close();
	      }
		  return storyDetailResult;	      
	}

}
