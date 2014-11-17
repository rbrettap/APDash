package org.ap.storyvelocity.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jdo.annotations.Element;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.Extension;
import javax.jdo.annotations.Order;
import javax.jdo.annotations.Persistent;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.datanucleus.annotations.Unowned;


@PersistenceCapable
public class StoryDetail
{
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;
	
	@Persistent
	private String storyId;
	@Persistent
	private Date pubDate;
	@Persistent
	private String timeInApp;
	@NotPersistent
	private int velocity;
	@Persistent
	private int trendfifteenmins;
	@Persistent
	private String active;
	
	 @Persistent(mappedBy = "storyDetail")
	public List<PageView> pageViewSets;

	public StoryDetail()
	{
	}

	public StoryDetail(String storyId, Date pubDate, String timeInApp, int trendfifteenmins, String active)
	{
		this.storyId = storyId;
		this.pubDate = pubDate;
		this.timeInApp = timeInApp;
		this.trendfifteenmins = trendfifteenmins;
		this.active = active;
	}

	public Key getKey() {
        return key;
    }
	
	public Key getId() {
		return key;
    }

	public void setKey(Key key) {
        this.key = key;
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

	public List<PageView> getPageViewSets() {
		return pageViewSets;
	}

	public void addPageViewSet(PageView pageView) {
		this.pageViewSets.add(pageView);
	}

	
	public void setPageViewSets(List<PageView> pageViewSets) {
		this.pageViewSets = pageViewSets;
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
