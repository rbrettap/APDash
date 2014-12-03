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
public class StoryIngestion
{
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;
	
	@Persistent
	private int storycount;
	@Persistent
	private Date storyingestiondate;
	@Persistent
	private int action;

	public StoryIngestion()
	{
	}

	public StoryIngestion(Date storyingestiondate, int storycount, int action)
	{
		this.storyingestiondate = storyingestiondate;
		this.storycount = storycount;
		this.action = action;
	}

	public Key getKey() {
        return key;
    }

	public void setKey(Key key) {
        this.key = key;
    }
	

	public Date getStoryIngestionDate() {
		return storyingestiondate;
	}

	public void setStoryIngestionDate(Date storyingestiondate) {
		this.storyingestiondate = storyingestiondate;
	}

	public int getStoryCount() {
		return storycount;
	}

	public void setStoryCount(int storycount) {
		this.storycount = storycount;
	}

	public int getAction() {
		return action;
	}

	public void setAction(int action) {
		this.action = action;
	}

}