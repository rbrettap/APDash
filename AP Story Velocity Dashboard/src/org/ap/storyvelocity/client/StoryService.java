package org.ap.storyvelocity.client;

import org.ap.storyvelocity.server.StoryDetail;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client-side stub for the RPC service.
 */
@RemoteServiceRelativePath("story")
public interface StoryService extends RemoteService {
	 public void addStoryDetail(String storyName) throws NotLoggedInException;
	 public void addStory(String storyName) throws NotLoggedInException;
	 public void removeStory(String storyName) throws NotLoggedInException;
	 public String[] getStories() throws NotLoggedInException;
}
