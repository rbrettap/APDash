package org.ap.storyvelocity.client;

import java.util.List;

import org.ap.storyvelocity.shared.StoryDetailClient;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client-side stub for the RPC service.
 */
@RemoteServiceRelativePath("story")
public interface StoryService extends RemoteService {
	 public void addStoryDetail(String storyName) throws NotLoggedInException;
	 public void addStory(String storyName) throws NotLoggedInException;
	 public void addStoryDetails(String[] storyNames) throws NotLoggedInException;
	 public void removeStory(String storyName) throws NotLoggedInException;
	 public List<StoryDetailClient> getStories() throws NotLoggedInException;
	 public StoryDetailClient getStoryDetails(String storyId) throws NotLoggedInException;
	 public List<StoryDetailClient> getStoryDetailsInBulk(int numResults, int sorttype, long lastFetchedTime);	 
	 public String fetchRealTimeAnalytics() throws NotLoggedInException;
}
