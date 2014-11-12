package org.ap.storyvelocity.client;

import java.util.Date;

public class PageView
{
	private int pageviews;
	private Date pageViewDate;
    private StoryDetailClient storyDetail;

	public PageView()
	{
	}

	public PageView(Date pageViewDate, int pageviews)
	{
		this.pageViewDate = pageViewDate;
		this.pageviews = pageviews;
	}

	public StoryDetailClient getStoryDetail() {
        return storyDetail;
    }

	public void setStoryDetail(StoryDetailClient storyDetail) {
        this.storyDetail = storyDetail;
    }
	

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