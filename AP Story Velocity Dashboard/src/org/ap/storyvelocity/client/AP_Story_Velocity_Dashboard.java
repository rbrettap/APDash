package org.ap.storyvelocity.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Random;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;

import java.util.Date;

import org.ap.storyvelocity.shared.StoryDetailClient;

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
		private TextBox newSymbolTextBox;
		private Button addButton;
		private Button feedIngesterButton;
		private Label lastUpdatedLabel;
		private Label lastFetchedLabel;
		private ArrayList <String> stories = new ArrayList<String>();  
		private static final int REFRESH_INTERVAL = 60000;
		private Image image;
		private Label lblStoryWatcher;
		
	    private LoginInfo loginInfo = null;
		private VerticalPanel loginPanel = new VerticalPanel();
		private Label loginLabel = new Label(
		      "Please sign in to your Google Account to access the AP Dashboard application.");
	    private Anchor signInLink = new Anchor("Sign In");
	    private Anchor signOutLink = new Anchor("Sign Out");
	    private final StoryServiceAsync storyService = GWT.create(StoryService.class);
	    
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
				mainPanel.setSize("440px", "290px");
				{
					image = new Image("aplogo.png");
					mainPanel.add(image);
				}
				{
					lblStoryWatcher = new Label("Story Velocity Dashboard");
					lblStoryWatcher.setStyleName("gwt-Label-StockWatcher");
					
					mainPanel.add(lblStoryWatcher);
				}
				
				feedIngesterPanel = new HorizontalPanel();
				feedIngesterPanel.addStyleName("addPanel");
				mainPanel.add(feedIngesterPanel);				
				{
					feedIngesterButton = new Button("Get Realtime Data");
					feedIngesterButton.setStyleName("gwt-Button-Add");
					feedIngesterButton.addClickHandler(new ClickHandler() {
						public void onClick(ClickEvent event) {
							fetchRealTimeAnalytics();
						}
					});
					feedIngesterButton.setText("Start Fetching Realtime Data");
					feedIngesterPanel.add(feedIngesterButton);
					lastFetchedLabel = new Label("  Last fetch was at: ");
					feedIngesterPanel.add(lastFetchedLabel);
				}
				{
					storyFlexTable = new FlexTable();
					//Add these lines
					storyFlexTable.setText(0, 0, "Story");
					storyFlexTable.setText(0, 1, "Publish_Date");
					storyFlexTable.setText(0, 2, "TimeInApp");
					storyFlexTable.setText(0, 3, "Total PageViews");
					storyFlexTable.setText(0, 4, "Velocity");
					storyFlexTable.setText(0, 5, "PVs_Last_15_mins");
					storyFlexTable.setText(0, 6, "Trend");
					storyFlexTable.setText(0, 7, "Active");
					
					// Add styles to elements in the stock list table.
					storyFlexTable.setCellPadding(6);
				    storyFlexTable.getRowFormatter().addStyleName(0, "watchListHeader");
				    storyFlexTable.addStyleName("watchList");
				    storyFlexTable.getCellFormatter().addStyleName(0, 1, "watchListNumericColumn");
				    storyFlexTable.getCellFormatter().addStyleName(0, 2, "watchListNumericColumn");
				    storyFlexTable.getCellFormatter().addStyleName(0, 3, "watchListNumericColumn");
				    storyFlexTable.getCellFormatter().addStyleName(0, 4, "watchListNumericColumn");
				    storyFlexTable.getCellFormatter().addStyleName(0, 5, "watchListNumericColumn");
				    storyFlexTable.getCellFormatter().addStyleName(0, 6, "watchListNumericColumn");
				    storyFlexTable.getCellFormatter().addStyleName(0, 7, "watchListRemoveColumn");
				    
					mainPanel.add(storyFlexTable);
				}
				{
					addPanel = new HorizontalPanel();
					addPanel.addStyleName("addPanel");
					mainPanel.add(addPanel);
					{
						newSymbolTextBox = new TextBox();
						newSymbolTextBox.addKeyPressHandler(new KeyPressHandler() {
							public void onKeyPress(KeyPressEvent event) {
								if (event.getCharCode() == KeyCodes.KEY_ENTER){
									addStoryName();
								}
							}
						});
						newSymbolTextBox.setFocus(true);
						addPanel.add(newSymbolTextBox);
					}
					{
						addButton = new Button("New button");
						addButton.setStyleName("gwt-Button-Add");
						addButton.addClickHandler(new ClickHandler() {
							public void onClick(ClickEvent event) {
								addStoryName();
							}
						});
						addButton.setText("Add");
						addPanel.add(addButton);
					}
				}
				{
					lastUpdatedLabel = new Label("New label");
					mainPanel.add(lastUpdatedLabel);
				}
			}
			
			// add default stories
			//addStoryToDatabase("STORY1");
			//String[] storyIds = new String[]{"STORY1", "STORY2", "STORY3", "STORY4", "STORY5", "STORY6",
			//	"STORY7", "STORY8", "STORY9", "STORY10"};
			//addStories(storyIds);
			
            /*
			addStory("STORY1");			
			addStory("STORY2");
			addStory("STORY3");
			addStory("STORY4");
			addStory("STORY5");
			addStory("STORY6");
			addStory("STORY7");
			addStory("STORY8");
			addStory("STORY9");
			addStory("STORY10");
			*/
			
			/*
			addDefaultStories("STORY1");
			addDefaultStories("STORY2");
			addDefaultStories("STORY3");
			addDefaultStories("STORY4");
			addDefaultStories("STORY5");
			addDefaultStories("STORY6");
			addDefaultStories("STORY7");
			addDefaultStories("STORY8");
			addDefaultStories("STORY9");
			addDefaultStories("STORY10");
			*/
			getStoryDetailsByBulk(10);
			
			// setup timer to refresh list automatically
			Timer refreshTimer = new Timer() {
				public void run()
				{
					refreshWatchListAfter5Mins();
					//refreshWatchList();
				}
			};
			refreshTimer.scheduleRepeating(REFRESH_INTERVAL);

			
		}
		
		private void refreshWatchListAfter5Mins() {
	
			//StoryDetailClient[] pVes = new StoryDetailClient[stories.size()];
			//getStoryDetails("STORY1");
			//updateTable(pVes);

			
		}
		
		
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
				String timeInApp = "15 mins";
				int trendfifteenmins = Random.nextInt(500);
				
				//double change = pageViews * MAX_PRICE_CHANGE
				//		* (Random.nextDouble() * 2.0 - 1.0);

				pVes[i] = new StoryDetailClient((String) stories.get(i), today.getTime(), timeInApp, pageViews, velocity, trendfifteenmins, active);
			}

			updateTable(pVes);

			
		}
		
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
			lastFetchedLabel.setText("Last update : " + updatedDate);
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
					+ DateTimeFormat.getMediumDateTimeFormat().format(new Date()));
		}

		private void updateTable(StoryDetailClient storyPageView, int rowCount) {
			// make sure the stock is still in our watch list
			/*  fix this
			 * if (!stories.contains(storyPageView.getStoryId()))
			{
				return;
			}
			*/

			int row = rowCount + 1;
			
			storyFlexTable.setText(row, 0, storyPageView.getStoryId());
			storyFlexTable.setWidget(row, 2, new Label());
			storyFlexTable.getCellFormatter().addStyleName(row, 1, "watchListNumericColumn");
			storyFlexTable.getCellFormatter().addStyleName(row, 2, "watchListNumericColumn");
			storyFlexTable.getCellFormatter().addStyleName(row, 3, "watchListNumericColumn");
			storyFlexTable.getCellFormatter().addStyleName(row, 4, "watchListNumericColumn");
			storyFlexTable.getCellFormatter().addStyleName(row, 5, "watchListNumericColumn");
			storyFlexTable.getCellFormatter().addStyleName(row, 6, "watchListNumericColumn");
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
			
			Date d = new Date(storyPageView.getPubDate());
			DateTimeFormat sdf = DateTimeFormat.getFormat("MMMM dd, yyyy HH:mm aa");
			String pubDate = sdf.format(d);
			
			String timeInApp = storyPageView.getTimeInApp();
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
		private void addStoryName() {
			final String symbol = newSymbolTextBox.getText().toUpperCase().trim();
		    newSymbolTextBox.setFocus(true);

		    // Stock code must be between 1 and 10 chars that are numbers, letters, or dots.
		    if (!symbol.matches("^[0-9A-Z\\.]{1,10}$")) {
		      Window.alert("'" + symbol + "' is not a valid story name.");
		      newSymbolTextBox.selectAll();
		      return;
		    }

		    newSymbolTextBox.setText("");

		 // don't add the story if it's already in the watch list
		    if (stories.contains(symbol))
		        return;

		    // add the stock to the list
		    int row = storyFlexTable.getRowCount();
		    stories.add(symbol);
		    storyFlexTable.setText(row, 0, symbol);
		    storyFlexTable.setWidget(row, 2, new Label());
		    storyFlexTable.getCellFormatter().addStyleName(row, 1, "watchListNumericColumn");
		    storyFlexTable.getCellFormatter().addStyleName(row, 2, "watchListNumericColumn");
		    storyFlexTable.getCellFormatter().addStyleName(row, 3, "watchListNumericColumn");
		    storyFlexTable.getCellFormatter().addStyleName(row, 4, "watchListNumericColumn");
		    storyFlexTable.getCellFormatter().addStyleName(row, 5, "watchListNumericColumn");
		    storyFlexTable.getCellFormatter().addStyleName(row, 6, "watchListNumericColumn");
		    storyFlexTable.getCellFormatter().addStyleName(row, 7, "watchListRemoveColumn");
		    
		    // add button to remove this stock from the list
		    Button removeStock = new Button("x");
		    removeStock.addStyleDependentName("remove");
		    removeStock.addClickHandler(new ClickHandler() {
		    public void onClick(ClickEvent event) {					
		        int removedIndex = stories.indexOf(symbol);
		        stories.remove(removedIndex);
		        storyFlexTable.removeRow(removedIndex + 1);
		    }
		    });
		    storyFlexTable.setWidget(row, 7, removeStock);	
			
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
		    storyFlexTable.getCellFormatter().addStyleName(row, 7, "watchListRemoveColumn");
		    
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
	

	private void getStoryDetailsByBulk(final int numResults) {
			
		storyService.getStoryDetailsInBulk(numResults, new AsyncCallback<List<StoryDetailClient>>() {
		
		public void onFailure(Throwable error) {
		    	  
		}
		public void onSuccess(List<StoryDetailClient> result) {
		       
			StoryDetailClient[] pVes = new StoryDetailClient[result.size()];
			
			for (int i = 0; i < result.size(); i++)
			{
				pVes[i] = (StoryDetailClient)result.get(i);
				updateTable(pVes[i], i);
			}
			
		}

		 });
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
		
		
	}



