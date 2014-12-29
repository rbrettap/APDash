package org.ap.storyvelocity.client;

import com.arcadiacharts.charts.ChartException;
import com.arcadiacharts.charts.linechart.ACLineChart;
import com.arcadiacharts.charts.linechart.ACLineChartBuilder;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLTable.Cell;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Label;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Random;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;

import java.util.Date;

import org.ap.storyvelocity.shared.StoryDetailClient;
import org.ap.storyvelocity.shared.Util;

import com.google.gwt.user.client.ui.Image;



/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class AP_Story_Velocity_Dashboard implements EntryPoint {
	/**
	 * Entry point classes define <code>onModuleLoad()</code>.
	 */
		private VerticalPanel mainPanel;
		private FlexTable storyFlexTable;
		private HorizontalPanel addPanel;
		private HorizontalPanel feedIngesterPanel;
		private HorizontalPanel sortChartPanel;
		private TextBox numStoryTextBox;
		private Button addButton;
		private Button feedIngesterButton;
		private Button fetchButton;
		private ListBox listBox;
		private ListBox sortChartBox;
		private Label lastUpdatedLabel;
		private Label lastFetchedLabel;
		private ArrayList <String> stories = new ArrayList<String>();  
		private static final int REFRESH_INTERVAL = 900000;
		private Image image;
		private Label lblStoryWatcher;
		private Label lblCurrentStoryCount;
		private Label lblCurrentStoryFilter;
		public static StoryDetailClient[] pVes = null;

		
		private static int numStoryCount = 10;
		private static int sortFilterType = 0;
		
	    private LoginInfo loginInfo = null;
		private VerticalPanel loginPanel = new VerticalPanel();
		private Label loginLabel = new Label(
		      "Please sign in to your Google Account to access the AP Dashboard application.");
	    private Anchor signInLink = new Anchor("Sign In");
	    private Anchor signOutLink = new Anchor("Sign Out");
	    private final StoryServiceAsync storyService = GWT.create(StoryService.class);
	    
	    final ProgressBar progressBar = new ProgressBar(20 ,ProgressBar.SHOW_TEXT);
	    public static Timer progressTimer = null;
	    
	    private static boolean refreshInProgress = false;
	    private static HashMap<String, String> storyIdMap = new HashMap();
	    private static Date lastFetchedTime = new Date();
	    
	    public void onModuleLoad() {
	        // Check login status using login service.
	        LoginServiceAsync loginService = GWT.create(LoginService.class);
	        loginService.login(GWT.getHostPageBaseURL(), new AsyncCallback<LoginInfo>() {
	          public void onFailure(Throwable error) {
	        	  loadLogin();
	          }

	          public void onSuccess(LoginInfo result) {
	            loginInfo = result;
	            if(loginInfo.isLoggedIn()) {
	            	storyIdMap.clear();
	            	loadStoryWatcher();
	            } else {
	              loadLogin();
	            }
	          }
	        });
	    }
		
	    private void loadLogin() {
	        // Assemble login panel.
	        signInLink.setHref(loginInfo.getLoginUrl());
	        loginPanel.add(loginLabel);
	        loginPanel.add(signInLink);
	        RootPanel.get().add(loginPanel);
	    }
	    
	    
		public void loadStoryWatcher() {
			
			signOutLink.setHref(loginInfo.getLogoutUrl());
			
			RootPanel rootPanel = RootPanel.get();
			{
				mainPanel = new VerticalPanel();
				mainPanel.add(signOutLink);
			    rootPanel.add(mainPanel, 5, 5);
				mainPanel.setSize("700px", "290px");
				{
					image = new Image("aplogo.png");
					mainPanel.add(image);
				}
				{
					lblStoryWatcher = new Label("Story Velocity Dashboard");
					lblStoryWatcher.setStyleName("gwt-Label-StockWatcher");
					
					lastUpdatedLabel = new Label("XX            ");
					lblCurrentStoryCount = new Label("Current Story Count: "+ numStoryCount);
					lblCurrentStoryCount.setStyleName("gwt-Label-StockWatcher");
					
					lblCurrentStoryFilter = new Label("Current Story Filter: velocity descending");
					lblCurrentStoryFilter.setStyleName("gwt-Label-StockWatcher");
					
					mainPanel.add(lblStoryWatcher);
					mainPanel.add(lastUpdatedLabel);
					mainPanel.add(progressBar);
					mainPanel.add(lblCurrentStoryCount);
					//mainPanel.add(lblCurrentStoryFilter);
				}
				
				addPanel = new HorizontalPanel();
				addPanel.addStyleName("addPanel");
				mainPanel.add(addPanel);
				{

						numStoryTextBox = new TextBox();
						numStoryTextBox.addKeyDownHandler(new NumStoryKeyDownHandler());
						numStoryTextBox.setFocus(true);
						
						addPanel.add(numStoryTextBox);
						
						/*
						listBox = new ListBox();
						listBox.addItem("velocity desc");
						listBox.addItem("pubDate desc");
						listBox.addItem("total pageViews desc");						
						listBox.addItem("velocity asc");
						listBox.addItem("pubDate asc");
						listBox.addItem("total pageViews asc");

					    // Make enough room for all five items (setting this value to 1 turns it
					    // into a drop-down list).
						listBox.setVisibleItemCount(3);
						addPanel.add(listBox);
						addButton = new Button("Update Fetch Filter");
						addButton.setStyleName("gwt-Button-Add");
						addButton.addClickHandler(new FetchFilterClickHandler());
						addButton.setText("Update Story Filter");
						addPanel.add(addButton);
						*/
				}
				
				feedIngesterPanel = new HorizontalPanel();
				feedIngesterPanel.addStyleName("addPanel");
				mainPanel.add(feedIngesterPanel);				
				{

					fetchButton = new Button("FetchByVelocity");
					fetchButton.setStyleName("gwt-Button-Add");
					fetchButton.addClickHandler(new ClickHandler() {
						public void onClick(ClickEvent event) {
							// set the fetch time to the current date
							lastFetchedTime = new Date();
							// initial sort type should be by velocity
							getStoryDetailsByBulk(numStoryCount, sortFilterType, lastFetchedTime.getTime());
						}
					});
					fetchButton.setText("FETCH FROM SERVER");
					feedIngesterPanel.add(fetchButton);
					feedIngesterPanel.add(new InlineHTML("    "));

					feedIngesterButton = new Button("Get Realtime Data");
					feedIngesterButton.setStyleName("gwt-Button-Add");
					feedIngesterButton.addClickHandler(new ClickHandler() {
						public void onClick(ClickEvent event) {
							fetchRealTimeAnalytics();
						}
					});
					feedIngesterButton.setText("Fetch Realtime Data into Server");
					feedIngesterPanel.add(feedIngesterButton);
					feedIngesterPanel.add(new InlineHTML("    "));
					
				}
				
				sortChartPanel = new HorizontalPanel();
				sortChartPanel.addStyleName("addPanel");
				mainPanel.add(sortChartPanel);
				{
					sortChartBox = new ListBox();
					sortChartBox.addItem("velocity desc");
					sortChartBox.addItem("pubDate desc");
					sortChartBox.addItem("total pageViews desc");
					sortChartBox.addItem("pvs in the last 15 mins desc");	
					sortChartBox.addItem("time in app desc");	
					sortChartBox.addItem("velocity asc");
					sortChartBox.addItem("pubDate asc");
					sortChartBox.addItem("total pageViews asc");
					sortChartBox.addItem("pvs in the last 15 mins asc");
					sortChartBox.addItem("time in app asc");

					    // Make enough room for all five items (setting this value to 1 turns it
					    // into a drop-down list).
					sortChartBox.setVisibleItemCount(1);
					sortChartBox.addChangeHandler(new ChangeHandler() {
					      @Override
					      public void onChange(ChangeEvent event) {
					    	 
					    	int selectedIndex = sortChartBox.getSelectedIndex(); 
					    	//Window.alert(selectedIndex+"");
					    	
					    	if (pVes.length < 0)
					    	{
					    		return;
					    	}
					    	// convert pVes to a collection....
					    	ArrayList pveslist = new ArrayList();
					    	for (int i = 0; i < pVes.length; i++)
					    	{
					    		pveslist.add((StoryDetailClient)pVes[i]);
					    	}
					    	
					 		if (selectedIndex == 0)
							{
					 			storyFlexTable.clear();
					 			int x = pveslist.size();
					 			
					 			// sort velocity desc
					 			sortFilterType = 0;
					 			
					 			Comparator<StoryDetailClient> velocityOrder =  new Comparator<StoryDetailClient>() {
					 		        public int compare(StoryDetailClient s1, StoryDetailClient s2) {
					 		            return s2.getVelocity() - s1.getVelocity();
					 		        }
					 		    };
					 		    Collections.sort(pveslist, velocityOrder);
					 		    
					 		    pVes = new StoryDetailClient[x];
					 		    
					 		    for (int i = 0; i < x; i++)
					 		    {
					 		      pVes[i] = (StoryDetailClient)pveslist.get(i);	
					 		    }
					 		   updateTable(pVes);
							}
							else if (selectedIndex == 1)
							{
								storyFlexTable.clear();
								int x = pveslist.size();
					 			// sort pubDate desc
								sortFilterType = 1;
								
					 			Comparator<StoryDetailClient> pubDateOrder =  new Comparator<StoryDetailClient>() {
					 		        public int compare(StoryDetailClient s1, StoryDetailClient s2) {
					 		            return (int)(s2.getPubDate() - s1.getPubDate());
					 		        }
					 		    };
					 		    Collections.sort(pveslist, pubDateOrder);
					 		    
					 		    pVes = new StoryDetailClient[x];
					 		    
					 		    for (int i = 0; i < x; i++)
					 		    {
					 		      pVes[i] = (StoryDetailClient)pveslist.get(i);	
					 		    }
					 		   updateTable(pVes);
							}
							else if (selectedIndex == 2)
							{
								storyFlexTable.clear();
								int x = pveslist.size();
					 			// sort totalPageView desc
								sortFilterType = 2;
								
					 			Comparator<StoryDetailClient> pageViewOrder =  new Comparator<StoryDetailClient>() {
					 		        public int compare(StoryDetailClient s1, StoryDetailClient s2) {
					 		            return (int)(s2.getPageviews() - s1.getPageviews());
					 		        }
					 		    };
					 		    Collections.sort(pveslist, pageViewOrder);
					 		    
					 		    pVes = new StoryDetailClient[x];
					 		    
					 		    for (int i = 0; i < x; i++)
					 		    {
					 		      pVes[i] = (StoryDetailClient)pveslist.get(i);	
					 		    }
					 		   updateTable(pVes);
							}
							else if (selectedIndex == 3)
							{
								storyFlexTable.clear();
								int x = pveslist.size();
					 			Comparator<StoryDetailClient> trend15order =  new Comparator<StoryDetailClient>() {
					 		        public int compare(StoryDetailClient s1, StoryDetailClient s2) {
					 		            return (int)(s2.getTrendFifteenMins() - s1.getTrendFifteenMins());
					 		        }
					 		    };
					 		    Collections.sort(pveslist, trend15order);
					 		    
					 		    pVes = new StoryDetailClient[x];
					 		    
					 		    for (int i = 0; i < x; i++)
					 		    {
					 		      pVes[i] = (StoryDetailClient)pveslist.get(i);	
					 		    }
					 		   updateTable(pVes);
							}
							else if (selectedIndex == 4)
							{
								storyFlexTable.clear();
								int x = pveslist.size();
					 			Comparator<StoryDetailClient> timeInAppOrder =  new Comparator<StoryDetailClient>() {
					 		        public int compare(StoryDetailClient s1, StoryDetailClient s2) {
					 		            return (int)(s2.getTimeInApp() - s1.getTimeInApp());
					 		        }
					 		    };
					 		    Collections.sort(pveslist, timeInAppOrder);
					 		    
					 		    pVes = new StoryDetailClient[x];
					 		    
					 		    for (int i = 0; i < x; i++)
					 		    {
					 		      pVes[i] = (StoryDetailClient)pveslist.get(i);	
					 		    }
					 		   updateTable(pVes);
							}
							else if (selectedIndex == 5)
							{
								storyFlexTable.clear();
								int x = pveslist.size();
					 			// sort velocity asc
								sortFilterType = 3;
								
					 			Comparator<StoryDetailClient> velocityOrder =  new Comparator<StoryDetailClient>() {
					 		        public int compare(StoryDetailClient s1, StoryDetailClient s2) {
					 		            return s1.getVelocity() - s2.getVelocity();
					 		        }
					 		    };
					 		    Collections.sort(pveslist, velocityOrder);
					 		    
					 		    pVes = new StoryDetailClient[x];
					 		    
					 		    for (int i = 0; i < x; i++)
					 		    {
					 		      pVes[i] = (StoryDetailClient)pveslist.get(i);	
					 		    }
					 		   updateTable(pVes);
							}
							else if (selectedIndex == 6)
							{
								storyFlexTable.clear();
								int x = pveslist.size();
					 			// sort pubDate asc
								sortFilterType = 4;
								
					 			Comparator<StoryDetailClient> pubDateOrder =  new Comparator<StoryDetailClient>() {
					 		        public int compare(StoryDetailClient s1, StoryDetailClient s2) {
					 		            return (int)(s1.getPubDate() - s2.getPubDate());
					 		        }
					 		    };
					 		    Collections.sort(pveslist, pubDateOrder);
					 		    
					 		    pVes = new StoryDetailClient[x];
					 		    
					 		    for (int i = 0; i < x; i++)
					 		    {
					 		      pVes[i] = (StoryDetailClient)pveslist.get(i);	
					 		    }
					 		   updateTable(pVes);
							}
							else if (selectedIndex == 7)
							{
								storyFlexTable.clear();
								int x = pveslist.size();
					 			//sort totalPageView asc
								sortFilterType = 5;
								
					 			Comparator<StoryDetailClient> pageViewOrder =  new Comparator<StoryDetailClient>() {
					 		        public int compare(StoryDetailClient s1, StoryDetailClient s2) {
					 		            return (int)(s1.getPageviews() - s2.getPageviews());
					 		        }
					 		    };
					 		    Collections.sort(pveslist, pageViewOrder);
					 		    
					 		    pVes = new StoryDetailClient[x];
					 		    
					 		    for (int i = 0; i < x; i++)
					 		    {
					 		      pVes[i] = (StoryDetailClient)pveslist.get(i);	
					 		    }
					 		   updateTable(pVes);
							}
							else if (selectedIndex == 8)
							{
								storyFlexTable.clear();
								int x = pveslist.size();
								// sort pv15 asc
					 			Comparator<StoryDetailClient> trend15order =  new Comparator<StoryDetailClient>() {
					 		        public int compare(StoryDetailClient s1, StoryDetailClient s2) {
					 		            return (int)(s1.getTrendFifteenMins() - s2.getTrendFifteenMins());
					 		        }
					 		    };
					 		    Collections.sort(pveslist, trend15order);
					 		    
					 		    pVes = new StoryDetailClient[x];
					 		    
					 		    for (int i = 0; i < x; i++)
					 		    {
					 		      pVes[i] = (StoryDetailClient)pveslist.get(i);	
					 		    }
					 		   updateTable(pVes);
							}
							else if (selectedIndex == 9)
							{
								storyFlexTable.clear();
								int x = pveslist.size();
								// sort pv15 asc
					 			Comparator<StoryDetailClient> timeInAppOrder =  new Comparator<StoryDetailClient>() {
					 		        public int compare(StoryDetailClient s1, StoryDetailClient s2) {
					 		            return (int)(s1.getTimeInApp() - s2.getTimeInApp());
					 		        }
					 		    };
					 		    Collections.sort(pveslist, timeInAppOrder);
					 		    
					 		    pVes = new StoryDetailClient[x];
					 		    
					 		    for (int i = 0; i < x; i++)
					 		    {
					 		      pVes[i] = (StoryDetailClient)pveslist.get(i);	
					 		    }
					 		   updateTable(pVes);
							}
					      }
					});
				    sortChartPanel.add(sortChartBox);
				    
				}
				
				{
					storyFlexTable = new FlexTable();
					//Add these lines
					storyFlexTable.setText(0, 0, "Story Title");
					
				    // add button to remove this stock from the list
					/*
					Button pubDateButton = new Button("Pub Date");
					pubDateButton.addStyleName("watchListHeaderButton");
					pubDateButton.addClickHandler(new ClickHandler() {
					    public void onClick(ClickEvent event) {                    
			
					    	Window.alert("test1");
					    	return;
					    }
					   });
					
					storyFlexTable.setWidget(0, 1, pubDateButton);   
					*/
					
					storyFlexTable.setText(0, 1, "Pub Date");
					storyFlexTable.setText(0, 2, "Time In App");
					storyFlexTable.setText(0, 3, "Total PageViews");
					
					/*
				    // add button to remove this stock from the list
					Button totalPageViewsButton = new Button("Total PageViews");
					totalPageViewsButton.addStyleName("watchListHeaderButton");
					totalPageViewsButton.addClickHandler(new ClickHandler() {
					    public void onClick(ClickEvent event) {                    
			
					    	Window.alert("test2");
					    	return;
					    }
					   });
					storyFlexTable.setWidget(0, 3, totalPageViewsButton); 
					*/
					
					/*
					Button velocityButton = new Button("Velocity = Est. PageViews/hr.");
					velocityButton.addStyleName("watchListHeaderButton");
					velocityButton.addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent event) {                    
			
					    	Window.alert("test3");
					    	return;
					    }
					});
					storyFlexTable.setWidget(0, 4, velocityButton); 
					*/
					storyFlexTable.setText(0, 4, "Velocity = Est. PageViews/hr.");
					
					
				    /*
					Button pv15Button = new Button("PVs_Last_15_mins");
					pv15Button.addStyleName("watchListHeaderButton");
					pv15Button.addClickHandler(new ClickHandler() {
					 public void onClick(ClickEvent event) {                    
			
					    	Window.alert("test4");
					    	return;
					    }
					});
					storyFlexTable.setWidget(0, 5, pv15Button); 
					*/
					storyFlexTable.setText(0, 5, "PVs in the last 15 mins");
					storyFlexTable.setText(0, 6, "Chart/Trend");
					//storyFlexTable.setText(0, 7, "Active");
					
					// Add styles to elements in the stock list table.
					storyFlexTable.setCellPadding(6);
				    storyFlexTable.getRowFormatter().addStyleName(0, "watchListHeader");
				    storyFlexTable.addStyleName("watchList");
				    storyFlexTable.getCellFormatter().addStyleName(0, 0, "watchListNumericColumn");
				    storyFlexTable.getCellFormatter().addStyleName(0, 1, "watchListNumericColumn");
				    storyFlexTable.getCellFormatter().addStyleName(0, 2, "watchListNumericColumn");
				    storyFlexTable.getCellFormatter().addStyleName(0, 3, "watchListNumericColumn");
				    storyFlexTable.getCellFormatter().addStyleName(0, 4, "watchListNumericColumn");
				    storyFlexTable.getCellFormatter().addStyleName(0, 5, "watchListNumericColumn");
				    storyFlexTable.getCellFormatter().addStyleName(0, 6, "watchListNumericColumn");
				    //storyFlexTable.getCellFormatter().addStyleName(0, 7, "watchListRemoveColumn");
				    
					mainPanel.add(storyFlexTable);
				}
			}
			
			Date firstFetchTime = new Date();
			// initial sort type should be by velocity
			getStoryDetailsByBulk(numStoryCount, 0, firstFetchTime.getTime());
			
			// setup timer to refresh list automatically
			Timer refreshTimer = new Timer() {
				public void run()
				{
					refreshWatchListAfter15Mins();
					//refreshWatchList();
				}
			};
			refreshTimer.scheduleRepeating(REFRESH_INTERVAL);

			
		}
		
		private void refreshWatchListAfter15Mins() {
	
			Date firstFetchTime = new Date();
			// initial sort type should be by velocity
			refreshInProgress = true;
			getStoryDetailsByBulk(numStoryCount, sortFilterType, firstFetchTime.getTime());
			refreshInProgress = false;
		}
		
		
		private void checkForUpdatedStories() {
			
			Date firstFetchTime = new Date();
			// initial sort type should be by velocity
			if (refreshInProgress == true)
				return;
			
			// todo 
			// fix later rbrb
			//getUpdatedStoryDetailsInBulk(storyNames, lastFetchedTime);	
			
			//getStoryDetailsByBulk(numStoryCount, sortFilterType, firstFetchTime.getTime());
		}
		
		
		/*
		private void refreshWatchList() {
			final int MAX_PRICE = 100; // $100
			final double MAX_PRICE_CHANGE = 0.02; // +/- 2%

			StoryDetailClient[] pVes = new StoryDetailClient[stories.size()];
			for (int i = 0; i < stories.size(); i++)
			{
				int pageViews = Random.nextInt(10000);
				int velocity = Random.nextInt(10000);
				String active = "YES";
				Date today = new Date();
				int timeInApp = 15;
				int trendfifteenmins = Random.nextInt(500);
				
				//double change = pageViews * MAX_PRICE_CHANGE
				//		* (Random.nextDouble() * 2.0 - 1.0);

				pVes[i] = new StoryDetailClient((String) stories.get(i), today.getTime(), timeInApp, pageViews, velocity, trendfifteenmins, active);
			}

			updateTable(pVes);

			
		}
		*/
		
		private void addStory(final String storyId) {
			    storyService.addStoryDetail(storyId, new AsyncCallback<Void>() {
			      public void onFailure(Throwable error) {
			      }
			      public void onSuccess(Void ignore) {
			        //displayStock(symbol);
			      }
			    });
		}
		
		
		private void addStories(final String[] storyIds) {
		    storyService.addStoryDetails(storyIds, new AsyncCallback<Void>() {
		      public void onFailure(Throwable error) {
		      }
		      public void onSuccess(Void ignore) {
		        //displayStock(symbol);
		      }
		    });
	}
		  
		  
		
		private void addStoryToDatabase(String storyName)
		{
			/*
			   stockService.addStock(storyName, new AsyncCallback<Void>() {
				      public void onFailure(Throwable error) {
				      }
				      public void onSuccess(Void ignore) {
				        displayStock(storyName);
				      }
				});
		   */
		}
		
		private void updateFeedPanel(String updatedDate)
		{
			// change the last update timestamp
			//lastFetchedLabel.setText("Last update : " + updatedDate);
		}
		
		
		private void updateTable(StoryDetailClient[] pVes)
		{
			if (pVes == null)
				return;
			
			for (int i = 0; i < pVes.length; i++)
			{
				updateTable(pVes[i], i);
			}
			
			// change the last update timestamp
			lastUpdatedLabel.setText("Last update : "
					+ DateTimeFormat.getFormat("MMMM dd, yyyy HH:mm aa").format(new Date()));
		}

		private void updateTable(StoryDetailClient storyPageView, int rowCount) {
			int row = rowCount + 1;
			
			storyFlexTable.setText(row, 0, storyPageView.getStoryId());
			storyFlexTable.setWidget(row, 2, new Label());
			storyFlexTable.getCellFormatter().addStyleName(row, 0, "watchListNumericColumn");
			storyFlexTable.getCellFormatter().addStyleName(row, 1, "watchListNumericColumn");
			storyFlexTable.getCellFormatter().addStyleName(row, 2, "watchListNumericColumn");
			storyFlexTable.getCellFormatter().addStyleName(row, 3, "watchListNumericColumn");
			storyFlexTable.getCellFormatter().addStyleName(row, 4, "watchListNumericColumn");
			storyFlexTable.getCellFormatter().addStyleName(row, 5, "watchListNumericColumn");
			storyFlexTable.getCellFormatter().addStyleName(row, 6, "watchListNumericColumn");
			
		    HorizontalPanel chartPanel = setupChart(storyPageView);
		    storyFlexTable.setWidget(row, 6, chartPanel);
		    
		    /*
			storyFlexTable.getCellFormatter().addStyleName(row, 7, "watchListRemoveColumn");	
			
		    // add button to remove this stock from the list
		    Button removeStock = new Button("x");
		    removeStock.addStyleDependentName("remove");
		    removeStock.addClickHandler(new ClickHandler() {
		    public void onClick(ClickEvent event) {					
		        //int removedIndex = stories.indexOf(storyPageView.getStoryId());
		        //stories.remove(removedIndex);
		        //storyFlexTable.removeRow(removedIndex + 1);
		    }
		    });
		    storyFlexTable.setWidget(row, 7, removeStock);	
			*/
		    
			Date d = new Date(storyPageView.getPubDate());
			DateTimeFormat sdf = DateTimeFormat.getFormat("MMMM dd, yyyy HH:mm aa");
			String pubDate = sdf.format(d);
			
			
			//int timeInAppInt = Integer.parseInt(storyPageView.getTimeInApp());
			int timeInAppInt = storyPageView.getTimeInApp();
			String timeInApp = Util.convertTimeToHoursMins(timeInAppInt);
			
			// convert to hours/mins....
			
			
			int velocity = storyPageView.getVelocity();
			int trendfifteenmins = storyPageView.getTrendFifteenMins();
			
		    // Format the data in the Price and Change fields.
		    String priceText = NumberFormat.getFormat("#,##0").format(storyPageView.getPageviews());
		    NumberFormat changeFormat = NumberFormat.getFormat("+#,##0.00;-#,##0.00");
		    //String changeText = changeFormat.format(storyPageView.getChange());
		    //String changePercentText = changeFormat.format(storyPageView.getChangePercent());
		    
		    storyFlexTable.setText(row, 1, pubDate);
		    storyFlexTable.setText(row, 2, timeInApp);
		    

		    // Populate the Price and Change fields with new data.
		    storyFlexTable.setText(row, 3, priceText);
		    storyFlexTable.setText(row, 4, velocity+"");
		    storyFlexTable.setText(row, 5, trendfifteenmins+"");
		    //Use the line of code without styling
		    //stocksFlexTable.setText(row, 2, changeText + " (" + changePercentText + "%)");
		    
		    //Use this with styling
		   // Label changeWidget = (Label)storyFlexTable.getWidget(row, 5);
		   // changeWidget.setText(changeText + " (" + changePercentText + "%)");
		    
		 // Change the color of text in the Change field based on its value.
		    String changeStyleName = "noChange";
		    
		    /*
		    if (storyPageView.getChangePercent() < -0.1f) {
		      changeStyleName = "negativeChange";
		    }
		    else if (storyPageView.getChangePercent() > 0.1f) {
		      changeStyleName = "positiveChange";
		    }
		    */

		    //changeWidget.setStyleName(changeStyleName);
		}
		
		
		// this method is to provide a basic routine for setting up charts....
		private HorizontalPanel setupChart(StoryDetailClient storyPageView)
		{
			HorizontalPanel chartPanel = new HorizontalPanel();
			
			//List pageViewSets = storyPageView.getPageviews();
			String data = storyPageView.pageViewTrend;			 
			String  myData = "Time in App, Velocity\n"+data;
			
			ACLineChart chart;

			try {
				   chart =  new ACLineChartBuilder()
				    .setWidth(300)
				    .setHeight(200)
			        .setTitle("Story Velocity = Est.PageViews/Hr")
			        .setData(myData)
			        .build();
				   
				    chartPanel = new HorizontalPanel();
				    chartPanel.add( chart );
				    chart.setDone( true ); 
				    
			} catch (ChartException e) {
			    e.printStackTrace();
			}
			return chartPanel;
		}
		

		private void addDefaultStories(final String storyId) {

		    // add the stock to the list
		    int row = storyFlexTable.getRowCount();
		    stories.add(storyId);
		    storyFlexTable.setText(row, 0, storyId);
		    storyFlexTable.setWidget(row, 2, new Label());
		    storyFlexTable.getCellFormatter().addStyleName(row, 1, "watchListNumericColumn");
		    storyFlexTable.getCellFormatter().addStyleName(row, 2, "watchListNumericColumn");
		    storyFlexTable.getCellFormatter().addStyleName(row, 3, "watchListNumericColumn");
		    storyFlexTable.getCellFormatter().addStyleName(row, 4, "watchListNumericColumn");
		    storyFlexTable.getCellFormatter().addStyleName(row, 5, "watchListNumericColumn");
		    storyFlexTable.getCellFormatter().addStyleName(row, 6, "watchListNumericColumn");
		   /* storyFlexTable.getCellFormatter().addStyleName(row, 7, "watchListRemoveColumn");
		    
		    // add button to remove this stock from the list
		    Button removeStock = new Button("x");
		    removeStock.addStyleDependentName("remove");
		    removeStock.addClickHandler(new ClickHandler() {
		    public void onClick(ClickEvent event) {					
		        int removedIndex = stories.indexOf(storyId);
		        stories.remove(removedIndex);
		        storyFlexTable.removeRow(removedIndex + 1);
		    }
		    });
		    storyFlexTable.setWidget(row, 7, removeStock);	
		    */
		    
		    // now we need to retrieve the story record from the database....
		    getStoryDetails(storyId);
		}
		
		private void getStoryDetails(final String storyId) {
			
			  storyService.getStoryDetails(storyId, new AsyncCallback<StoryDetailClient>() {
		      public void onFailure(Throwable error) {
		    	  
		      }
		      public void onSuccess(StoryDetailClient storyDetail) {
		        
					StoryDetailClient[] pVes = new StoryDetailClient[1];
					
					pVes[0] = storyDetail;
					updateTable(pVes);
		      }
		    });
	}
	

	// the main function to populate the grid table in bulk (10 items)
    // from the database....
	private void getStoryDetailsByBulk(final int numResults, final int sorttype, final long lastFetchedTime) {
		

		startProgressBar();
		storyService.getStoryDetailsInBulk(numResults,  sorttype, lastFetchedTime, new AsyncCallback<List<StoryDetailClient>>() {
		
		public void onFailure(Throwable error) {
			
			stopProgressBar();
		    	  
		}
		public void onSuccess(List<StoryDetailClient> result) {
		       
			pVes = null;
			pVes = new StoryDetailClient[result.size()];

			for (int i = 0; i < result.size(); i++)
			{
				pVes[i] = (StoryDetailClient)result.get(i);
				updateTable(pVes[i], i);
				progressBar.setProgress(100);
				stopProgressBar();
			}
			
			Date lastFetchedDate = new Date();
			// change the last update timestamp
			lastUpdatedLabel.setText("Last updated : "
					+ DateTimeFormat.getFormat("MMMM dd, yyyy HH:mm aa").format(lastFetchedDate));
			
		}

		 });
	}


    private void startProgressBar()
    {
		progressBar.setText("Fetching From Server...");
		
		progressTimer = new Timer() {
		    public void run() {
		      int progress = progressBar.getProgress()+4;
		      if (progress>100) cancel();
		      progressBar.setProgress(progress);
		    }
		};
		progressTimer.scheduleRepeating(1000);
    }
    
    private void stopProgressBar()
    {
    	progressTimer.cancel();
		progressBar.setText("");
		progressBar.setProgress(0);
    }
		
		
     // feed fetcher functions......
		private void fetchRealTimeAnalytics() {
			
			  storyService.fetchRealTimeAnalytics(new AsyncCallback<String>() {
		      public void onFailure(Throwable error) {
		    	  
		      }
		      public void onSuccess(String feedIngestionDate) {
		        
		    	  updateFeedPanel(feedIngestionDate);
		      }
		    });
	}		
		
		
		private void updateFetchFilter() {
			
			int storyCount = Integer.parseInt(numStoryTextBox.getText().toUpperCase().trim());
			numStoryTextBox.setFocus(true);

		    // Stock code must be between 1 and 10 chars that are numbers, letters, or dots.
		    if (storyCount < 0 && storyCount > 15) {
		      Window.alert("'" + storyCount + "' is not a valid story count.");
		      numStoryTextBox.selectAll();
		      return;
		    }

		    numStoryTextBox.setText(" ");
		    numStoryCount = storyCount; // set this to be the global story count....
		    
		    String searchFilter = listBox.getItemText(listBox.getSelectedIndex());
		    
		    
		    
		}




		   /** 
		    * create a custom click handler which will call 
		    * onClick method when button is clicked.
		    */
		   private class FetchFilterClickHandler implements ClickHandler {
		      @Override
		      public void onClick(ClickEvent event) {
		    	 
		    	 sortFilterType = listBox.getSelectedIndex(); 
		    	
		 		if (sortFilterType == 0)
				{
		 			lblCurrentStoryFilter.setText("Current Story Filter: velocity descending");
				}
				else if (sortFilterType == 1)
				{
		 			lblCurrentStoryFilter.setText("Current Story Filter: pubDate descending");
				}
				else if (sortFilterType == 2)
				{
		 			lblCurrentStoryFilter.setText("Current Story Filter: totalPageViews descending");
				}
				else if (sortFilterType == 3)
				{
		 			lblCurrentStoryFilter.setText("Current Story Filter: velocity ascending");
				}
				else if (sortFilterType == 4)
				{
		 			lblCurrentStoryFilter.setText("Current Story Filter: pubDate ascending");
				}
				else if (sortFilterType == 5)
				{
		 			lblCurrentStoryFilter.setText("Current Story Filter: totalPageViews ascending");
				}
				else
				{
					
				}
		      }
		   }
		   
		   
		   /**
		    * create a custom key down handler which will call 
		    * onKeyDown method when a key is down in textbox.
		    */
		   private class NumStoryKeyDownHandler implements KeyDownHandler {
		      @Override
		      public void onKeyDown(KeyDownEvent event) {
		         if(event.getNativeKeyCode() == KeyCodes.KEY_ENTER || event.getNativeKeyCode() == KeyCodes.KEY_TAB){
		        	 
		        	 numStoryCount =  Integer.parseInt(((TextBox)event.getSource()).getValue());
		        	 
		        	 if (numStoryCount > 50 || numStoryCount < 10)
		        	 {
		        		 Window.alert("Please enter a story count > 10 && < 50!");
		        		 numStoryCount = 10; // reset to default
		        	 }
		        	 
		        	 lblCurrentStoryCount.setText("Current Story Count: "+ numStoryCount);
		         }
		      }
		   }
		
			
		
	}



