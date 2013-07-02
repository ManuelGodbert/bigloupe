package org.bigloupe.web.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.atmosphere.cpr.ApplicationConfig;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.cpr.BroadcasterFactory;
import org.atmosphere.cpr.FrameworkConfig;
import org.atmosphere.cpr.HeaderConfig;
import org.atmosphere.websocket.WebSocketEventListenerAdapter;
import org.bigloupe.web.view.NoOpView;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;


/**
 * Simple example with Atmosphere 
 * 		GET /pubsub/{topic}.html : subscribe to {topic} notification
 * 		POST /pubsub/{topic}.html : post new Data
 * AtmosphereHandler PubSub
 */
@Controller
@RequestMapping("/pubsub")
public class PubSubController {
	
	public PubSubController() {
	}

	/**
	 * This method takes a request to subscribe to the topic
	 * 
	 * @param request
	 * @return ModelAndView
	 */
	@RequestMapping(value = "{topic}", method = RequestMethod.GET)
	public ModelAndView subscribe(HttpServletRequest request) throws Exception {
		AtmosphereResource resource = (AtmosphereResource) request
				.getAttribute(FrameworkConfig.ATMOSPHERE_RESOURCE);

		this.doGet(resource, request, resource.getResponse());

		// A NoOpView is returned to tell Spring Dispatcher framework not to render anything
		// since it is all Atmosphere-related code
		ModelAndView mv = new ModelAndView(new NoOpView());
		return mv;
	}

	/**
	 * Takes a request to post data and broadcasts it to everyone else.
	 * 
	 * @param request
	 * @return String
	 */
	@RequestMapping(value = "{topic}", method = RequestMethod.POST)
	public ModelAndView broadcastMessage(HttpServletRequest request)
			throws Exception {
		
		this.doPost(request);

		// A NoOpView is returned to tell Spring Dispatcher framework not to render anything
		// since it is all Atmosphere-related code
		ModelAndView mv = new ModelAndView(new NoOpView());
		return mv;
	}

	private void doGet(
			AtmosphereResource r,
			HttpServletRequest req, HttpServletResponse res) {
		
		// Log all events on the console, including WebSocket events.
		r.addEventListener(new WebSocketEventListenerAdapter());

		res.setContentType("text/html;charset=ISO-8859-1");
		String requestURI = req.getRequestURI();
		// Truncate /pubsub & .html
		requestURI = requestURI.substring("/pubsub/".length(), requestURI.length() - ".html".length());
		Broadcaster b = lookupBroadcaster(requestURI);
		r.setBroadcaster(b);

		String header = req.getHeader(HeaderConfig.X_ATMOSPHERE_TRANSPORT);
		if (HeaderConfig.LONG_POLLING_TRANSPORT.equalsIgnoreCase(header)) {
			req.setAttribute(ApplicationConfig.RESUME_ON_BROADCAST,
					Boolean.TRUE);
			r.suspend(-1, false);
		} else {
			r.suspend(-1);
		}
	}

	private void doPost(HttpServletRequest req) throws IOException {
		Broadcaster b = lookupBroadcaster(req.getPathInfo());
		String message = req.getReader().readLine();

		if (message != null) {
			b.broadcast(message);
		}
	}

	/**
	 * Retrieve the {@link Broadcaster} based on the request's path info.
	 * 
	 * @param pathInfo
	 * @return the {@link Broadcaster} based on the request's path info.
	 */
    Broadcaster lookupBroadcaster(String pathInfo) {
        if (pathInfo == null) {
            return BroadcasterFactory.getDefault().lookup("/", true);
        } else {
            String[] decodedPath = pathInfo.split("/");
            return BroadcasterFactory.getDefault().lookup(
                    decodedPath[decodedPath.length - 1], true);
        }
    }

}
