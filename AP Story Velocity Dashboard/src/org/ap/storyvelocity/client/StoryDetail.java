package org.ap.storyvelocity.client;

import java.util.Date;

public class StoryDetail
{
	private String storyId;
	private Date pubDate;
	private String timeInApp;
	private int pageviews;
	private int velocity; 
	private int trendfifteenmins;
	private String active;

	public StoryDetail()
	{
	}

	public StoryDetail(String storyId, Date pubDate, String timeInApp, int pageviews, int velocity, int trendfifteenmins, String active)
	{
		this.storyId = storyId;
		this.pubDate = pubDate;
		this.timeInApp = timeInApp;
		this.pageviews = pageviews;
		this.velocity = velocity;
		this.trendfifteenmins = trendfifteenmins;
		this.active = active;
	}

	public String getStoryId()
	{
		return this.storyId;
	}

	public void setStoryId(String storyId)
	{
		this.storyId = storyId;
	}

	public Date getPubDate() {
		return pubDate;
	}

	public void setPubDate(Date pubDate) {
		this.pubDate = pubDate;
	}

	public String getTimeInApp() {
		return timeInApp;
	}

	public void setTimeInApp(String timeInApp) {
		this.timeInApp = timeInApp;
	}

	public int getPageviews() {
		return pageviews;
	}

	public void setPageviews(int pageviews) {
		this.pageviews = pageviews;
	}

	public int getVelocity() {
		return velocity;
	}

	public void setVelocity(int velocity) {
		this.velocity = velocity;
	}

	public int getTrendFifteenMins() {
		return trendfifteenmins;
	}

	public void setTrendFifteenMins(int trendfifteenmins) {
		this.trendfifteenmins = trendfifteenmins;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}
}
