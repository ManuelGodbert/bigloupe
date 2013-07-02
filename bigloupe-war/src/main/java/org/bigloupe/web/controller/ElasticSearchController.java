package org.bigloupe.web.controller;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.bigloupe.web.dto.ResponseStatus;
import org.bigloupe.web.dto.ResultSearch;
import org.elasticsearch.ElasticSearchException;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.status.IndexStatus;
import org.elasticsearch.action.admin.indices.status.IndicesStatusRequest;
import org.elasticsearch.action.admin.indices.status.IndicesStatusResponse;
import org.elasticsearch.action.search.SearchPhaseExecutionException;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.AndFilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.indices.IndexMissingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.support.SessionStatus;

/**
 * Search in elastic search
 * 
 * @author bigloupe
 * 
 */
@Controller
@RequestMapping("/search")
public class ElasticSearchController extends AbstractElasticSearchController {
	private static Logger logger = LoggerFactory
			.getLogger(ElasticSearchController.class);

	/**
	 * Handle index page (home page)
	 */
	@RequestMapping("/index")
	public String showIndexPage(
			ModelMap model,
			@ModelAttribute("configurationHadoopKarmaCluster") Map<String, String> hadoopKarmaCluster,
			SessionStatus sessionStatus) {
		model.addAttribute("configuration", configuration);
		getAllIndices(model);
		return "elastic-search";
	}

	/**
	 * Get list of index
	 */
	protected void getAllIndices(ModelMap model) {
		try {
			IndicesStatusRequest request = new IndicesStatusRequest();
			IndicesStatusResponse indicesStatusResponse = getClient().admin()
					.indices().status(request).actionGet();
			Map<String, IndexStatus> indices = indicesStatusResponse
					.getIndices();
			model.addAttribute("indices", indices);
		} catch (ElasticSearchException e) {
			// e.printStackTrace();
		}

	}

	/**
	 * Test availability of a new index (called before index creation from user) 
	 */
	@RequestMapping(value = "/testIndex/{indexName}", method = { RequestMethod.GET })
	@ResponseBody
	protected ResponseStatus testIndex(@PathVariable("indexName") String indexName,
			HttpServletRequest req, ModelMap model) throws ServletException,
			IOException {
		try {

			IndicesExistsRequest request = new IndicesExistsRequest(indexName);;
			IndicesExistsResponse indexExistsResponse = getClient().admin()
					.indices().exists(request).actionGet();
			boolean exist = indexExistsResponse
					.isExists();
			ResponseStatus status = new ResponseStatus(exist);
			return status;
			
		} catch (ElasticSearchException e) {
			return new ResponseStatus(false);
		}
		
	}
	
	/**
	 * Query elastic search for autosuggestion
	 */
	@RequestMapping(value = "/removeIndex/{indexName}", method = { RequestMethod.GET })
	@ResponseBody
	protected ResponseStatus removeIndex(@PathVariable("indexName") String indexName,
			HttpServletRequest req, ModelMap model) throws ServletException,
			IOException {
		try {
			DeleteIndexRequest request = new DeleteIndexRequest(indexName);
			DeleteIndexResponse deleteIndexResponse = getClient().admin()
					.indices().delete(request).actionGet();
			boolean acknowledge = deleteIndexResponse
					.getAcknowledged();
			ResponseStatus status = new ResponseStatus(true);
			status.setMessage("Index " + indexName + " has been removed");
			return status;
			
		} catch (ElasticSearchException e) {
			return new ResponseStatus(false);
		}
	}

	/**
	 * Generic query elastic search for full search
	 */
	@RequestMapping(value = "/result", method = { RequestMethod.POST,
			RequestMethod.GET })
	protected String result(HttpServletRequest req, ModelMap model)
			throws ServletException, IOException {

		try {
			model.addAttribute("configuration", configuration);
			getElasticSearchDocuments(req, model);
			return "forward:/WEB-INF/jsp/search/elastic-search-result.jsp";
		} catch (SearchPhaseExecutionException spe) {
			model.addAttribute("queryError", spe.getMessage());
			return "forward:/WEB-INF/jsp/search/elastic-error.jsp";
		} catch (Exception ex) {
			ex.printStackTrace();
			model.addAttribute("searchException", ex);
			return "forward:/WEB-INF/jsp/search/nothing.jsp";
		}
	}

	private void getElasticSearchDocuments(HttpServletRequest req,
			ModelMap model) throws Exception {

		// A serie represents 18 pages
		String startParam = req.getParameter("page");
		int startDoc = 0;
		if ((startParam != null)
				&& (!startParam.equals("undefined") && (Integer
						.parseInt(startParam) > 0))) {
			startDoc = Integer.parseInt(startParam) * 60;
			model.addAttribute("serie", Integer.parseInt(startParam) / 18);
			model.addAttribute("page", Integer.parseInt(startParam) % 18);
		} else {
			model.addAttribute("serie", "0");
			model.addAttribute("page", "0");
		}

		Long start, end;
		start = System.currentTimeMillis();

		String structureSelect = req.getParameter("structureSelect");
		String searchInput = req.getParameter("searchInput");

		QueryBuilder queryBuilder;

		// Case 1 : Tag
		// Build complex query or simple query
		if (req.getParameter("tags") != null
				&& !req.getParameter("tags").equals("")) {
			String tags = req.getParameter("tags");
			List<NameValuePair> nameValuePairs = URLEncodedUtils.parse(tags,
					Charset.defaultCharset());
			AndFilterBuilder andFilterBuilder = null;
			for (NameValuePair nameValuePair : nameValuePairs) {
				if (andFilterBuilder == null) {
					andFilterBuilder = FilterBuilders.andFilter(FilterBuilders
							.queryFilter(QueryBuilders.fieldQuery(
									nameValuePair.getName(),
									nameValuePair.getValue())));
				} else
					andFilterBuilder.add(FilterBuilders
							.queryFilter(QueryBuilders.fieldQuery(
									nameValuePair.getName(),
									nameValuePair.getValue())));
			}
			// Case 1.1 : Tag & searchInput
			if (!searchInput.equals(""))
				queryBuilder = QueryBuilders.filteredQuery(
						QueryBuilders.fieldQuery(structureSelect, searchInput),
						andFilterBuilder);
			else
				// Case 1.2 : Tag & No searchInput
				queryBuilder = QueryBuilders.filteredQuery(
						QueryBuilders.matchAllQuery(), andFilterBuilder);
		} else {
			// Case 2 : No Tag
			// Case 2.1 : No Tag & searchInput
			if (!searchInput.equals(""))
				queryBuilder = QueryBuilders.queryString(searchInput);
			else {
				// Case 2.2 : No Tag & No searchInput
				queryBuilder = QueryBuilders.matchAllQuery();
			}
		}

		SearchResponse response = getClient()
				.prepareSearch(req.getParameter("obj"))
				.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
				.setQuery(queryBuilder).setFrom(startDoc).setSize(60)
				.setExplain(true).execute().actionGet();

		end = System.currentTimeMillis();

		logger.debug("Completed writing to Stream");
		logger.debug(String.format("Query time: %s ms", (end - start)));

		model.addAttribute("elasticSearchDocuments", response);

	}

}
