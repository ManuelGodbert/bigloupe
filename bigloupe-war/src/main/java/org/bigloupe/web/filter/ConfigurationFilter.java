package org.bigloupe.web.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bigloupe.web.BigLoupeConfiguration;

/**
 * Servlet Filter implementation class ConfigurationFilter
 */
public class ConfigurationFilter implements Filter {

	boolean bigLoupeInitialized = false;

	/**
	 * Default constructor.
	 */
	public ConfigurationFilter() {
	}

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		if (!bigLoupeInitialized) {
			String serverUrl = BigLoupeConfiguration
					.getConfigurationWebBigLoupeServer();
			// Test if configuration already exist
			if (serverUrl == null) {
				serverUrl = BigLoupeConfiguration.readServerUrlConfiguration();
				// If no configuration save a new one
				if (serverUrl == null) {
					serverUrl = request.getScheme() + "://"
							+ request.getServerName() + ":"
							+ request.getServerPort();
					BigLoupeConfiguration.saveServerUrlConfiguration(serverUrl);
					String url = request.getScheme() + "://"
							+ request.getServerName() + ":"
							+ request.getServerPort()
							+ ((HttpServletRequest)request).getSession().getServletContext().getContextPath()
							+ "/init.jsp";

					HttpServletResponse resp = (HttpServletResponse) response;
					resp.reset();
					resp.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
					resp.setHeader("Location", url);

					return;
				} else
					bigLoupeInitialized = true;

			} else
				bigLoupeInitialized = true;
		}

		// pass the request along the filter chain
		chain.doFilter(request, response);
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig filterConfig) throws ServletException {

	}

}
