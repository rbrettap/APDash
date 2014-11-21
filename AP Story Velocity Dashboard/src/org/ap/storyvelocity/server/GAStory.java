package org.ap.storyvelocity.server;

import java.util.Date;

public class GAStory
{
	private int gaPageViews;
	private Date gaRetrievalDate;
	private String gaStoryName;

	public GAStory()
	{
	}

	public GAStory(String gaStoryName, Date gaRetrievalDate, int gaPageViews)
	{
		this.gaRetrievalDate = gaRetrievalDate;
		this.gaPageViews = gaPageViews;
		this.gaStoryName = gaStoryName;
	}
	
	public String getGAStoryName() {
		return gaStoryName;
	}

	public void setGAStoryName(String gaStoryName) {
		this.gaStoryName = gaStoryName;
	}

	public Date getGARetrievalDate() {
		return gaRetrievalDate;
	}

	public void setGARetrievalDate(Date gaRetrievalDate) {
		this.gaRetrievalDate = gaRetrievalDate;
	}

	public int getGAPageviews() {
		return gaPageViews;
	}

	public void setGAPageviews(int gaPageViews) {
		this.gaPageViews = gaPageViews;
	}
}