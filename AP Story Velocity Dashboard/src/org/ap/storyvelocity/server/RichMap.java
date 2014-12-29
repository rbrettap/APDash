package org.ap.storyvelocity.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class RichMap extends HashMap {

	  int maxCap = 25; // default value
	  Map mapDateKey = new HashMap();
	  List theDates = new ArrayList();

	  public class MyInfo {
	     Object object = null;
	     Date   stamp = null;
	     public MyInfo(Object theObject) {
	        object = theObject;
	        setStamp();
	     }
	     public Date getStamp() { return stamp; }
	     public Object getObject() { return object; }
	     public void setStamp() { stamp = new Date(); }
	  }

	  public RichMap() {
	     super();
	  }
	  
	  public void clear() {
		  super.clear();
	  }

	  public RichMap(int maxCapacity) {
	     super();
	     maxCap = maxCapacity;
	  }

	  public Object put(Object key, Object value) {
	     if ( size()==maxCap ) {
	       Collections.sort(theDates);
	       Date oldestDate = (Date) theDates.get(0);
	       Object oldKey = mapDateKey.get(oldestDate);
	       remove(oldKey);
	     }
	     MyInfo m = new MyInfo(value);
	     mapDateKey.put(m.getStamp(), key);
	     theDates.add(m.getStamp());
	     return super.put(key, m);
	  }

	  public Object remove(Object key) {
	      MyInfo m = (MyInfo) super.get(key);
	      if (m==null)
	          return null;
	      theDates.remove(m.getStamp());
	      return super.remove(key);
	  }
	 
	  public Object get(Object key) {
	     MyInfo m = (MyInfo) super.get(key);
	     if (m==null)
	         return null;
	     m.setStamp();
	     return m.getObject();
	  }
	 
	  public String toString() {
	     Iterator it = super.keySet().iterator();
	     String txt = "";
	     while (it.hasNext()) {
	         Object key = it.next();
	         MyInfo m = (MyInfo) super.get(key);
	         txt += ("key="+key + " value=" + m.getObject() + " date=" +m.getStamp() + "\r\n");
	     }
	     return txt;
	  }
}