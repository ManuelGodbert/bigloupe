/**
 * Created on Mar 16, 2009
 */
package org.bigloupe.web.util.test;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockServletConfig;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.TestContext;

/**
 * Listener that sets up a mock web environment based on spring-web mock test objects.
 * <p>
 * The supported &#064;WebResource fields are:
 * <ul>
 * <li>MockServletContext and ServletContext</li>
 * <li>MockServletConfig and ServletConfig</li>
 * <li>MockHttpSession and HttpSession</li>
 * <li>MockHttpServletRequest, HttpServletRequest and ServletRequest</li>
 * <li>MockHttpServletResponse, HttpServletResponse and ServletResponse</li>
 * </ul>
 * The request, response and session are put into the TestContext for other listeners to pick them up.
 * 
 * @see AbstractWebListener
 * @author Gaëtan Pitteloud
 */
public class WebListener extends AbstractWebListener {

    // log4j static Logger for this class
    static final Logger logger = Logger.getLogger(WebListener.class);

    private static final String SESSION_ATTRIBUTE_NAME = "WebListener.httpSession";
    private static final String REQUEST_ATTRIBUTE_NAME = "WebListener.httpServletRequest";
    private static final String RESPONSE_ATTRIBUTE_NAME = "WebListener.httpServletResponse";

    private MockServletConfig config;
    private MockHttpSession session;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    public static MockHttpServletRequest getRequest(TestContext testContext) {
        return (MockHttpServletRequest) testContext.getAttribute(REQUEST_ATTRIBUTE_NAME);
    }

    public static MockHttpServletResponse getResponse(TestContext testContext) {
        return (MockHttpServletResponse) testContext.getAttribute(RESPONSE_ATTRIBUTE_NAME);
    }

    public static MockHttpSession getHttpSession(TestContext testContext) {
        return (MockHttpSession) testContext.getAttribute(SESSION_ATTRIBUTE_NAME);
    }

    @Override
    protected ServletContext createServletContext(TestContext testContext) throws Exception {
        return new MockServletContext();
    }

    @Override
    protected void createAndStoreMocks(TestContext testContext) {
        // Set up Servlet API Objects
        config = new MockServletConfig(servletContext);
        request = new MockHttpServletRequest(servletContext);
        session = (MockHttpSession) request.getSession(true);
        response = new MockHttpServletResponse();

        // pushed in context for other listeners to use it
        testContext.setAttribute(SESSION_ATTRIBUTE_NAME, session);
        testContext.setAttribute(REQUEST_ATTRIBUTE_NAME, request);
        testContext.setAttribute(RESPONSE_ATTRIBUTE_NAME, response);

        putWebObject(MockServletConfig.class, config);
        putWebObject(ServletConfig.class, config);

        putWebObject(MockHttpSession.class, session);
        putWebObject(HttpSession.class, session);

        putWebObject(MockHttpServletRequest.class, request);
        putWebObject(HttpServletRequest.class, request);
        putWebObject(ServletRequest.class, request);

        putWebObject(MockHttpServletResponse.class, response);
        putWebObject(HttpServletResponse.class, response);
        putWebObject(ServletResponse.class, response);
    }

    @Override
    protected void resetMocks(TestContext testContext) {
        config = null;
        request = null;
        response = null;
        session = null;
    }

}
