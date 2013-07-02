package org.bigloupe.web.monitor.server;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bigloupe.web.monitor.service.DAGNode;
import org.bigloupe.web.monitor.service.DAGTransformer;
import org.bigloupe.web.monitor.service.StatsReadService;
import org.bigloupe.web.monitor.service.WorkflowEvent;
import org.bigloupe.web.monitor.service.impl.SugiyamaLayoutTransformer;
import org.bigloupe.web.monitor.util.JSONUtil;
import org.eclipse.jetty.server.AbstractHttpConnection;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;


/**
 * Handler for the API data responses.
 *
 * @author billg
 */
public class APIHandler extends AbstractHandler {
  private static final String QUERY_PARAM_WORKFLOW_ID = "workflowId";
  private static final String QUERY_PARAM_SINCE = "sinceId";

  private static final String MIME_TYPE_HTML = "text/html";
  private static final String MIME_TYPE_JSON = "application/json";

  private StatsReadService statsReadService;

  public APIHandler(StatsReadService statsReadService) {
    this.statsReadService = statsReadService;
  }

  
  @Override
  public void handle(String target,Request requestJetty,
                     HttpServletRequest request,
                     HttpServletResponse response
                     ) throws IOException, ServletException {

    if (target.endsWith("/dag")) {
      response.setContentType(MIME_TYPE_JSON);
      response.setStatus(HttpServletResponse.SC_OK);

      Collection<DAGNode> nodes =
        statsReadService.getDagNodeNameMap(request.getParameter(QUERY_PARAM_WORKFLOW_ID)).values();

      // add the x, y coordinates
      DAGTransformer dagTransformer = new SugiyamaLayoutTransformer(true);
      dagTransformer.transform(nodes);

      sendJson(request, response, nodes.toArray(new DAGNode[nodes.size()]));
    } else if (target.endsWith("/events")) {
      response.setContentType(MIME_TYPE_JSON);
      response.setStatus(HttpServletResponse.SC_OK);
      Integer sinceId = request.getParameter(QUERY_PARAM_SINCE) != null ?
              Integer.getInteger(request.getParameter(QUERY_PARAM_SINCE)) : -1;

      Collection<WorkflowEvent> events =
        statsReadService.getEventsSinceId(request.getParameter(QUERY_PARAM_WORKFLOW_ID), sinceId);

      sendJson(request, response, events.toArray(new WorkflowEvent[events.size()]));
    }
    else if (target.endsWith(".html")) {
      response.setContentType(MIME_TYPE_HTML);
      // this is because the next handler will be picked up here and it doesn't seem to
      // handle html well. This is jank.
    }
  }

  private static void sendJson(HttpServletRequest request,
                               HttpServletResponse response, Object object) throws IOException {
    JSONUtil.writeJson(response.getWriter(), object);
    response.getWriter().close();
    setHandled(request);
  }

  private static void setHandled(HttpServletRequest request) {
    Request base_request = (request instanceof Request) ?
        (Request)request : AbstractHttpConnection.getCurrentConnection().getRequest();
    base_request.setHandled(true);
  }
}
