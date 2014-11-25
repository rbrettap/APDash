package org.ap.storyvelocity.server;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.*;


@SuppressWarnings("serial")
public class GAEJCronServlet extends HttpServlet {
private static final Logger _logger = Logger.getLogger(GAEJCronServlet.class.getName());
public void doGet(HttpServletRequest req, HttpServletResponse resp)
throws IOException {

try {

	_logger.info("Cronhas been executed");
	 StoryServiceImpl serverImpl = new StoryServiceImpl();
	 serverImpl.fetchRealTimeAnalytics();

}
catch (Exception ex) {

	_logger.info(ex.toString());
}
}

@Override
public void doPost(HttpServletRequest req, HttpServletResponse resp)
throws ServletException, IOException {
  doGet(req, resp);
}
}