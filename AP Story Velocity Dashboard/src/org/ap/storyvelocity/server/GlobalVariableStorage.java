package org.ap.storyvelocity.server;

import java.util.Date;
import java.util.HashMap;

import org.ap.storyvelocity.server.RichMap;

public class GlobalVariableStorage {
	
    private static GlobalVariableStorage mInstance = null;

    private GlobalVariableStorage() {}

    public static GlobalVariableStorage get() {
    	
    	if (mInstance == null)
    	{
    		mInstance = new GlobalVariableStorage();
    		gaStoryMap.clear();  // reinitialize the storage...
    	}
    	
        return mInstance;
    }
    
    public static Date lastIngestDate = new Date(0);
    public static RichMap gaStoryMap = new RichMap(50); // story 50 of the most recent story ids.....
    
}