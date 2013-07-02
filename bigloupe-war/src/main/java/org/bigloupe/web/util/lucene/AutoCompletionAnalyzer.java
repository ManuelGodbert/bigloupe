package org.bigloupe.web.util.lucene;

import java.io.Reader;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.ngram.EdgeNGramTokenFilter;
import org.apache.lucene.analysis.ngram.EdgeNGramTokenFilter.Side;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.util.Version;

/**
 * Analyzer for indexer only (not for searching)
 *  
 * @author bigloupe
 *
 */
public class AutoCompletionAnalyzer extends Analyzer {

	private static final int MAX_TOKEN_LENGTH = StandardAnalyzer.DEFAULT_MAX_TOKEN_LENGTH;

	private final Set<?> STOP_SET = StopAnalyzer.ENGLISH_STOP_WORDS_SET;

	@Override
	public TokenStream tokenStream(String fieldName, Reader reader) {

		StandardTokenizer tokenStream = new StandardTokenizer(
				Version.LUCENE_35, reader);
		tokenStream.setMaxTokenLength(MAX_TOKEN_LENGTH);

		TokenStream result = new StandardFilter(Version.LUCENE_35,
				tokenStream);
		result = new LowerCaseFilter(Version.LUCENE_35, result);
		result = new StopFilter(Version.LUCENE_35, result, STOP_SET);
		result = new EdgeNGramTokenFilter(result, Side.FRONT, 1, 20);

		return result;
	}
}
