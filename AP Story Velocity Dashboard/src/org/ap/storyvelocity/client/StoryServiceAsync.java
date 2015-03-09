package org.ap.storyvelocity.client;

import java.util.List;

import org.ap.storyvelocity.shared.StoryDetailClient;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface StoryServiceAsync {
	  public void addStoryDetail(String storyName, AsyncCallback<Void> async);
	  public void addStory(String storyName, AsyncCallback<Void> async);
	  public void addStoryDetails(String[] storyNames, AsyncCallback<Void> async);
	  public void removeStory(String storyName, AsyncCallback<Void> async);
	  public void getStories(AsyncCallback<List<StoryDetailClient>> async);
	  public void getStoryDetailsInBulk(int numResults, int sorttype, long lastFetchedTime, AsyncCallback<List<StoryDetailClient>> async);
	  public void getUpdatedStoryDetailsInBulk(String[] storyNames, long lastFetchedTime, AsyncCallback<List<StoryDetailClient>> async);	

	  public void fetchRealTimeAnalytics(AsyncCallback<String> async);
	  public void getCronJobSettings(AsyncCallback<String> async) throws NotLoggedInException;
	  
}
