package org.ap.storyvelocity.server;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.analytics.Analytics;
import com.google.api.services.analytics.AnalyticsScopes;
import com.google.api.services.analytics.model.GaData.*;
import com.google.api.services.analytics.model.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.io.File;

public class StoryAnalyticsAPI {
	
	private static final JsonFactory JSON_FACTORY = new JacksonFactory();
	public HashMap<String, GAStory> gaDataMap = new HashMap<String, GAStory>();
	public boolean fetchInProgress = false;

	private static StoryAnalyticsAPI storyAnalyticsAPIInstance;

    private StoryAnalyticsAPI() {}

    public static StoryAnalyticsAPI getInstance() {
    	
    	if (storyAnalyticsAPIInstance == null)
    	{
    		storyAnalyticsAPIInstance = new StoryAnalyticsAPI();
    	}
    	return storyAnalyticsAPIInstance;
    }
	
	
	public void clearHashMap(boolean clearMap)
	{
		if (clearMap)
			gaDataMap.clear();
	}
	
	public Analytics setupFetchRequest()
	{
        HttpTransport httpTransport;
        Analytics analyticsService;
        
		try {
			
			httpTransport = GoogleNetHttpTransport.newTrustedTransport();
	        // This is the .p12 file you got from the google api console by clicking generate new key
	        File analyticsKeyFile = new File("apanalyticstools-9e58570c230f.p12");

	        // This is the service account email address that you can find in the api console
	        String apiEmail = "715175621128-0d7iomhaq20bnhsf6pecv6hi7ne7qeap@developer.gserviceaccount.com";


	        GoogleCredential credential = new GoogleCredential.Builder()
	                .setTransport(httpTransport)
	                .setJsonFactory(JSON_FACTORY)
	                .setServiceAccountId(apiEmail)
	                .setServiceAccountScopes(Arrays.asList(AnalyticsScopes.ANALYTICS_READONLY))
	                .setServiceAccountPrivateKeyFromP12File(analyticsKeyFile).build();

	        analyticsService = new Analytics.Builder(httpTransport, JSON_FACTORY, credential)
	                .setApplicationName("apanalyticstools").build();
			
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		return analyticsService;
	}
	
    public void getRealtimeQuery(int numResults) {

        /**
         * 1. Create and Execute a Real Time Report
         * An application can request real-time data by calling the get method on the Analytics service object.
         * The method requires an ids parameter which specifies from which view (profile) to retrieve data.
         * For example, the following code requests real-time data for view (profile) ID 56789.
         */

        try {
        	
        	if (fetchInProgress == true)
        		return;
        	
        	// need to check to see if there's an existing fetch in progress - otherwise we could potentially do these calls
        	// amillion times....
        	gaDataMap.clear();  // make sure we clear the datamap so that we don't keep doing entries.....

            Analytics analyticsService = setupFetchRequest();

            Analytics.Data.Realtime.Get realtimeRequest = analyticsService.data().realtime()
                    .get("ga:83676735",
                            "rt:totalEvents")
                    .setDimensions("rt:eventCategory,rt:eventAction")
                    .setSort("-rt:totalEvents")
                    .setFilters("rt:eventCategory==Selected Story")
                    .setMaxResults(numResults);
            

            String profileId = getFirstProfileId(analyticsService);
            
            if (profileId == null) {
                System.err.println("No profiles found.");
            } else {

                RealtimeData realtimeData = realtimeRequest.execute();
                retrieveRealtimeReportResults(realtimeData);
                return;
            }

        }
        catch (Exception e) {
            // Catch general parsing network errors.
            e.printStackTrace();
        }


    }

    public void analyticsExample() {
    	
    	if (fetchInProgress == true)
    		return;

        try {

        	Analytics analyticsService = setupFetchRequest();

            String profileId = getFirstProfileId(analyticsService);
            if (profileId == null) {
                System.err.println("No profiles found.");
            } else {
                GaData gaData = executeDataQuery(analyticsService, profileId);
                printGaData(gaData);
            }

        }
        catch(Exception ex)
        {
           ex.printStackTrace();

        }

    }

    /**
     * Returns the first profile id by traversing the Google Analytics Management API. This makes 3
     * queries, first to the accounts collection, then to the web properties collection, and finally
     * to the profiles collection. In each request the first ID of the first entity is retrieved and
     * used in the query for the next collection in the hierarchy.
     *
     * @param analytics the analytics service object used to access the API.
     * @return the profile ID of the user's first account, web property, and profile.
     * @throws IOException if the API encounters an error.
     */
    private static String getFirstProfileId(Analytics analytics) throws IOException {
        String profileId = null;

        // Query accounts collection.
        Accounts accounts = analytics.management().accounts().list().execute();

        if (accounts.getItems().isEmpty()) {
            System.err.println("No accounts found");
        } else {
            String firstAccountId = accounts.getItems().get(0).getId();

            // Query webproperties collection.
            Webproperties webproperties =
                    analytics.management().webproperties().list(firstAccountId).execute();

            if (webproperties.getItems().isEmpty()) {
                System.err.println("No Webproperties found");
            } else {
                String firstWebpropertyId = webproperties.getItems().get(0).getId();

                // Query profiles collection.
                Profiles profiles =
                        analytics.management().profiles().list(firstAccountId, firstWebpropertyId).execute();

                if (profiles.getItems().isEmpty()) {
                    System.err.println("No profiles found");
                } else {
                    profileId = profiles.getItems().get(0).getId();
                }
            }
        }
        return profileId;
    }

    /**
     * Returns the top 25 organic search keywords and traffic source by visits. The Core Reporting API
     * is used to retrieve this data.
     *
     * @param analytics the analytics service object used to access the API.
     * @param profileId the profile ID from which to retrieve data.
     * @return the response from the API.
     * @throws IOException tf an API error occured.
     */
    private static GaData executeDataQuery(Analytics analytics, String profileId) throws IOException {
        return analytics.data().ga().get("ga:" + profileId, // Table Id. ga: + profile id.
                "2014-11-04", // Start date.
                "2014-11-05", // End date.
                "ga:visits") // Metrics.
                .setDimensions("ga:source,ga:keyword")
                .setSort("-ga:visits,ga:source")
                .setFilters("ga:medium==organic")
                .setMaxResults(25)
                .execute();
    }

    /**
     * Prints the output from the Core Reporting API. The profile name is printed along with each
     * column name and all the data in the rows.
     *
     * @param results data returned from the Core Reporting API.
     */
    private static void printGaData(GaData results) {
        System.out.println(
                "printing results for profile: " + results.getProfileInfo().getProfileName());

        if (results.getRows() == null || results.getRows().isEmpty()) {
            System.out.println("No results Found.");
        } else {

            // Print column headers.
            for (ColumnHeaders header : results.getColumnHeaders()) {
                System.out.printf("%30s", header.getName());
            }
            System.out.println();

            // Print actual data.
            for (List<String> row : results.getRows()) {
                for (String column : row) {
                    System.out.printf("%30s", column);
                }
                System.out.println();
            }

            System.out.println();
        }
    }


    // real-time data api methods
    private void printRealtimeReport(RealtimeData realtimeData) {
        System.out.println();
        System.out.println("Response:");
        System.out.println("ID:" + realtimeData.getId());
        System.out.println("realtimeData Kind: " + realtimeData.getKind());
        System.out.println();

        printQueryInfo(realtimeData.getQuery());
        printProfileInfo(realtimeData.getProfileInfo());
        printPaginationInfo(realtimeData);
        printDataTable(realtimeData);
    }
    
    // real-time data api methods
    private void retrieveRealtimeReportResults(RealtimeData realtimeData) {

    	retrieveRealTimeData(realtimeData);
    }

    private void printQueryInfo(RealtimeData.Query query) {
        System.out.println("Query Info:");
        System.out.println("Ids: " + query.getIds());
        System.out.println("Metrics: " + query.getMetrics());
        System.out.println("Dimensions: " + query.getDimensions());
        System.out.println("Sort: " + query.getSort());
        System.out.println("Filters: " + query.getFilters());
        System.out.println("Max results: " + query.getMaxResults());
        System.out.println();
    }

    private void printProfileInfo(RealtimeData.ProfileInfo profileInfo) {
        System.out.println("Info:");
        System.out.println("Account ID:" + profileInfo.getAccountId());
        System.out.println("Web Property ID:" + profileInfo.getWebPropertyId());
        System.out.println("Profile ID:" + profileInfo.getProfileId());
        System.out.println("Profile Name:" + profileInfo.getProfileName());
        System.out.println("Table Id:" + profileInfo.getTableId());
        System.out.println();
    }

    private void printPaginationInfo(RealtimeData realtimeData) {
        System.out.println("Pagination info:");
        System.out.println("Self link: " + realtimeData.getSelfLink());
        System.out.println("Total Results: " + realtimeData.getTotalResults());
        System.out.println();
    }
    
    private void retrieveRealTimeData(RealtimeData realtimeData) {
    	
        if (realtimeData.getTotalResults() > 0) {
            System.out.println("Data Table:");

            for (List<String> row : realtimeData.getRows()) {
            	
            	GAStory ga = new GAStory();
            	
            	for (int i = 0; i < row.size(); i++)
            	{
            		String element = row.get(i);
            		
            		// in this case element should always be selected story....
            		if (i == 0) 
            		{
            			continue;
            		}
            		if (i == 1)
            		{
            			ga.setGAStoryName(element);
            			continue;
            		}
            		if (i == 2)
            		{
            			int count = Integer.parseInt(element);
            			ga.setGAPageviews(count);
            			Date d = new Date();
            			ga.setGARetrievalDate(d);
            		}
            	}
            	gaDataMap.put(ga.getGAStoryName(), ga);
            }
            // this should have a complete map here of results.....
            		
        } else {
            System.out.println("No data");
        }
    }


    private void printDataTable(RealtimeData realtimeData) {
        if (realtimeData.getTotalResults() > 0) {
            System.out.println("Data Table:");
            for (RealtimeData.ColumnHeaders header : realtimeData.getColumnHeaders()) {
                System.out.format("%-32s", header.getName() + '(' + header.getDataType() + ')');
            }
            System.out.println();
            for (List<String> row : realtimeData.getRows()) {
                for (String element : row) {
                    System.out.format("%-32s", element);
                }
                System.out.println();
            }
        } else {
            System.out.println("No data");
        }
    }



}
