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
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import org.ap.storyvelocity.client.NotLoggedInException;
import org.ap.storyvelocity.client.StoryDetailClient;
import org.ap.storyvelocity.client.StoryService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server-side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class StoryServiceImpl extends RemoteServiceServlet implements
StoryService {
	
	private static final Logger LOG = Logger.getLogger(StoryServiceImpl.class.getName());
	private static final PersistenceManagerFactory PMF =
	      JDOHelper.getPersistenceManagerFactory("transactions-optional");
	
	@Override
	public void addStoryDetail(String storyId) throws NotLoggedInException {
		checkLoggedIn();
		PersistenceManager pm = getPersistenceManager();
		pm.currentTransaction().begin();
		
		try {

			Key key = KeyFactory.createKey(PageView.class.getSimpleName(), storyId);
			
			int pageViews = 100;
			int pageViews1 = 150;
			int velocity = 500;
			String active = "YES";
			Date today = new Date();
			Calendar cal = Calendar.getInstance();
			today = cal.getTime();
			
			cal.add(Calendar.DAY_OF_YEAR, -1);
			Date yesterday = cal.getTime();
	
			
			String timeInApp = "15 mins";
			int trendfifteenmins = 25;
			
			org.ap.storyvelocity.server.StoryDetail e = new org.ap.storyvelocity.server.StoryDetail(storyId, today, timeInApp, trendfifteenmins, active);

	        PageView pv = new PageView(yesterday, pageViews);
			PageView pv1 = new PageView(today, pageViews1);
			e.getPageViewSets().add(pv);
			e.getPageViewSets().add(pv1);
			
			pv.setKey(key);
			pv1.setKey(key);
			
		    pm.makePersistent(e);
		    pm.currentTransaction().commit();
		    
		} finally {
			if (pm.currentTransaction().isActive()) {
		        pm.currentTransaction().rollback();
		    }
		}
	}
	
	@Override
	public void addStory(String storyId) throws NotLoggedInException {
		checkLoggedIn();
		PersistenceManager pm = getPersistenceManager();
		try {
		  pm.makePersistent(new Story(getUser(), storyId));
		} finally {
		      pm.close();
		}
	}

	@Override
	public void removeStory(String storyId) throws NotLoggedInException {
	    checkLoggedIn();
	    PersistenceManager pm = getPersistenceManager();
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
	    PersistenceManager pm = getPersistenceManager();
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

	private PersistenceManager getPersistenceManager() {
		return PMF.getPersistenceManager();
	}


}
