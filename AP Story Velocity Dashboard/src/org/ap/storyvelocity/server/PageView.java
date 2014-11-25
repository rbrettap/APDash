package org.ap.storyvelocity.server;

import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;


@PersistenceCapable
public class PageView
{
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;
	
	@Persistent
	private int pageviews;
	@Persistent
	private Date pageViewDate;
	//@Persistent
    //private StoryDetail storyDetail;
	@Persistent
	private String storyDetailId;

	public PageView()
	{
	}

	public PageView(Date pageViewDate, int pageviews)
	{
		this.pageViewDate = pageViewDate;
		this.pageviews = pageviews;
	}

	public Key getKey() {
        return key;
    }

	public void setKey(Key key) {
        this.key = key;
    }
	
	public String getStoryDetailId() {
		return storyDetailId;
	}

	public void setStoryDetailId(String storyDetailId) {
		this.storyDetailId = storyDetailId;
	}

	
	
	
	/*  should be for owned example
	public StoryDetail getStoryDetail() {
        return storyDetail;
    }

	public void setStoryDetail(StoryDetail storyDetail) {
        this.storyDetail = storyDetail;
    }
	*/

	public Date getPageViewDate() {
		return pageViewDate;
	}

	public void setPageViewDate(Date pageViewDate) {
		this.pageViewDate = pageViewDate;
	}

	public int getPageviews() {
		return pageviews;
	}

	public void setPageviews(int pageviews) {
		this.pageviews = pageviews;
	}
}