package org.bigloupe.web.service.common;

import java.io.File;
import java.io.Reader;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.analysis.KeywordTokenizer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.ReusableAnalyzerBase;
import org.apache.lucene.analysis.ReusableAnalyzerBase.TokenStreamComponents;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.ngram.EdgeNGramTokenFilter;
import org.apache.lucene.analysis.ngram.EdgeNGramTokenFilter.Side;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.vectorhighlight.FastVectorHighlighter;
import org.apache.lucene.search.vectorhighlight.FieldQuery;
import org.apache.lucene.store.NIOFSDirectory;
import org.apache.lucene.util.Version;
import org.bigloupe.web.BigLoupeConfiguration;
import org.bigloupe.web.dto.ResultSearch;
import org.bigloupe.web.listener.SessionListener;
import org.bigloupe.web.service.hadoop.StatusesIndexService;
import org.bigloupe.web.util.lucene.AutoCompletionAnalyzer;
import org.bigloupe.web.util.lucene.LowercaseAnalyzer;
import org.bigloupe.web.util.lucene.LuceneUtil;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockServletContext;


public class StatusesIndexServiceTest {

	StatusesIndexService statusesIndexService;

	@Before
	public void setUp() throws Exception {
		statusesIndexService = new StatusesIndexService();
		BigLoupeConfiguration configuration = new BigLoupeConfiguration();
		statusesIndexService.setConfiguration(configuration);
		statusesIndexService.afterPropertiesSet();

		FileSystem fs = FileSystem.get(new Configuration());

		SessionListener sessionListener = new SessionListener();
		Map<HttpSession, String> sessions = sessionListener.getSessions();
		MockHttpSession session = new MockHttpSession();
		sessions.put(session, "127.0.0.1");
		session.setAttribute(BigLoupeConfiguration.FILESYSTEM, fs);

	}

	@After
	public void tearDown() throws Exception {
	}


	public class SearchAutoCompleteAnalyzer extends ReusableAnalyzerBase {

		@Override
		protected TokenStreamComponents createComponents(String fieldName,
				Reader reader) {
			KeywordTokenizer tokenizer = new KeywordTokenizer(reader);
			return new TokenStreamComponents(tokenizer,
					new EdgeNGramTokenFilter(tokenizer, Side.FRONT, 1, 20));
		}

	}

	@Test
	public void test() throws Exception {
		System.out.println("Start indexing");
		statusesIndexService.scanFileSystem();
		System.out.println("End indexing");

		// Search
		//Analyzer analyzer = new KeywordAnalyzer();
		Analyzer analyzer = new LowercaseAnalyzer();
		//Analyzer analyzer = new SearchAutoCompleteAnalyzer();
		//Analyzer analyzer = new AutoCompletionAnalyzer();
		
		QueryParser queryParser = new QueryParser(Version.LUCENE_35, "highlightedpath",
				analyzer);
		Query q = queryParser.parse("Jetty");
		int hitsPerPage = 1000;

        FastVectorHighlighter highlighter = LuceneUtil.makeHighlighter();
        FieldQuery fieldQuery = highlighter.getFieldQuery(q);
        
		IndexReader reader = IndexReader.open(new NIOFSDirectory(new File(
				"d:/indexes/_0")));
		IndexSearcher searcher = new IndexSearcher(reader);
		TopScoreDocCollector collector = TopScoreDocCollector.create(
				hitsPerPage, true);
		searcher.search(q, collector);
		ScoreDoc[] hits = collector.topDocs().scoreDocs;
		for (int i = 0; i < hits.length; ++i) {
			int docId = hits[i].doc;
			
			org.apache.lucene.document.Document d = searcher.doc(docId);
			System.out.println("Items found for file : ");
			System.out.println("Path : " + d.get("path"));
			System.out.println("HighlightedPath : " +  highlighter.getBestFragment(fieldQuery,
					reader, docId, "highlightedpath", 10000));
		}
	}

}
