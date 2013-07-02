package org.bigloupe.web.controller;

import static org.elasticsearch.index.query.QueryBuilders.matchAllQuery;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.bigloupe.web.BigLoupeConfiguration;
import org.bigloupe.web.dto.ResultSearch;
import org.elasticsearch.action.count.CountResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.indices.IndexMissingException;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * Suggestion with Elastic Search engine
 * 
 * @author bigloupe
 * 
 */
@Controller
@RequestMapping("/search")
public class ElasticSearchSuggestionController extends
		AbstractElasticSearchController {
	private static Logger logger = LoggerFactory
			.getLogger(ElasticSearchSuggestionController.class);

	/**
	 * Query elastic search for autosuggestion
	 */
	@RequestMapping("/suggest")
	@ResponseBody
	protected List<ResultSearch> suggest(HttpServletRequest req)
			throws ServletException, IOException {

		try {
			return elasticSearchQuery(req);
		} catch (IndexMissingException ime) {
			List<ResultSearch> results = new ArrayList<ResultSearch>(1);
			results.add(new ResultSearch(ime.getMessage()));
			return results;		
		} catch (Exception ex) {
			ex.printStackTrace();
			List<ResultSearch> results = new ArrayList<ResultSearch>(1);
			results.add(new ResultSearch("Error searching in ElasticSearch"));
			return results;
		}
	}

	/**
	 * Query elastic search to count number of indexed elements
	 */
	@RequestMapping("/count")
	@ResponseBody
	protected List<ResultSearch> count(HttpServletRequest req)
			throws ServletException, IOException {
		try {
			return numberOfIndexedElements();
		} catch (Exception ex) {
			ex.printStackTrace();
			List<ResultSearch> results = new ArrayList<ResultSearch>(1);
			results.add(new ResultSearch("Error searching in ElasticSearch"));
			return results;
		}
	}

	/**
	 * Describe fields for an object
	 */
	@RequestMapping("/describeFields")
	@ResponseBody
	protected List<ResultSearch> describeFields(HttpServletRequest req)
			throws ServletException, IOException {
		try {
			SearchResponse searchResponse = getClient()
					.prepareSearch(req.getParameter("obj"))
					.setSearchType(SearchType.QUERY_AND_FETCH).setQuery(
							QueryBuilders.matchAllQuery()).setFrom(0)
					.setSize(1).setExplain(true).execute()
					.actionGet();
			List<ResultSearch> results = new ArrayList<ResultSearch>(1);
			SearchHits hits = searchResponse.getHits();
			SearchHit[] hitsArray = hits.getHits();
			if (hitsArray.length > 0) {
				SearchHit searchHit = hitsArray[0];
				Map<String, Object> map = searchHit.sourceAsMap();
				Set<String> keySet = map.keySet();
				for (String key : keySet) {
					results.add(new ResultSearch(key));
				}
				Collections.sort(results);
				return results;
			} else {
				results.add(new ResultSearch("No value"));
			}
			return results;
		} catch (Exception ex) {
			ex.printStackTrace();
			List<ResultSearch> results = new ArrayList<ResultSearch>(1);
			results.add(new ResultSearch("Error searching in ElasticSearch"));
			return results;
		}
	}

	/**
	 * Return facets : aggregated data based on a search query
	 */
	@RequestMapping("/facets")
	@ResponseBody
	protected List<ResultSearch> getFacets(HttpServletRequest req)
			throws ServletException, IOException {
//		try {
//			SearchResponse searchResponse = getClient()
//					.prepareSearch(req.getParameter("obj"))
//					.setSearchType(SearchType.QUERY_AND_FETCH).setQuery(
//							QueryBuilders.matchAllQuery()).setFrom(0)
//					.setSize(1).setExplain(true).execute()
//					.actionGet();
//			List<ResultSearch> results = new ArrayList<ResultSearch>(1);
//			SearchHits hits = searchResponse.getHits();
//			SearchHit[] hitsArray = hits.getHits();
//			if (hitsArray.length > 0) {
//				SearchHit searchHit = hitsArray[0];
//				Map<String, Object> map = searchHit.sourceAsMap();
//				Set<String> keySet = map.keySet();
//				for (String key : keySet) {
//					results.add(new ResultSearch(key));
//				}
//				Collections.sort(results);
//				return results;
//			} else {
//				results.add(new ResultSearch("No value"));
//			}
//			return results;
//		} catch (Exception ex) {
//			ex.printStackTrace();
//			List<ResultSearch> results = new ArrayList<ResultSearch>(1);
//			results.add(new ResultSearch("Error searching in ElasticSearch"));
//			return results;
//		}
		return null;
	}

	
	/**
	 * Query to search indices in ElasticSearch
	 * 
	 * @param req
	 * @param resp
	 * @throws Exception
	 */
	private List<ResultSearch> elasticSearchQuery(HttpServletRequest req)
			throws Exception {

		Long start, end;
		start = System.currentTimeMillis();
		String structureSelect = req.getParameter("structureSelect");
		QueryBuilder termQueryBuilder;
		if (structureSelect == null)
			termQueryBuilder = QueryBuilders.queryString(req.getParameter("searchInput"));
		else
			termQueryBuilder = QueryBuilders.termQuery(structureSelect,
					req.getParameter("searchInput"));
		
		SearchResponse searchResponse = getClient()
				.prepareSearch(req.getParameter("obj"))
				.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
				.setQuery(termQueryBuilder).setFrom(0)
				.setSize(60).setExplain(true).execute().actionGet();
		end = System.currentTimeMillis();

		long size = (searchResponse.getHits().getTotalHits() > 60 ? 60
				: searchResponse.getHits().getTotalHits());
		List<ResultSearch> results = new ArrayList<ResultSearch>((int) size);
		SearchHits hits = searchResponse.getHits();
		SearchHit[] hitsArray = hits.getHits();
		for (int i = 0; i < hitsArray.length; i++) {
			SearchHit searchHit = hitsArray[i];
			ResultSearch result = new ResultSearch(
					(String) searchHit.sourceAsString());
			results.add(result);
		}
		logger.debug("Completed writing to Stream");
		logger.debug(String.format("Query time: %s ms", (end - start)));
		return results;

	}

	/**
	 * Number of indexed element
	 * 
	 * @param resp
	 * @throws IOException
	 */
	private List<ResultSearch> numberOfIndexedElements() throws Exception {

		CountResponse response = getClient().prepareCount()
				.setQuery(matchAllQuery()).execute().actionGet();

		List<ResultSearch> results = new ArrayList<ResultSearch>(1);
		results.add(new ResultSearch(String.format(
				"Searching against %s objects", response.count())));
		return results;
	}
}
