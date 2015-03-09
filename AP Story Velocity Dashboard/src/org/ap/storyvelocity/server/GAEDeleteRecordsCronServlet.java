package org.ap.storyvelocity.server;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.*;


@SuppressWarnings("serial")
public class GAEDeleteRecordsCronServlet extends HttpServlet {
private static final Logger _logger = Logger.getLogger(GAEDeleteRecordsCronServlet.class.getName());

public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

try {

	_logger.info("Cron has been executed");
	 StoryServiceImpl serverImpl = new StoryServiceImpl();
	 serverImpl.removeRecordsFromServer();
	 serverImpl.removeStoryIngestionRecords();

 }
 catch (Exception ex) {

	_logger.info(ex.toString());
 }
}

@Override
public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
  doGet(req, resp);
 }
}