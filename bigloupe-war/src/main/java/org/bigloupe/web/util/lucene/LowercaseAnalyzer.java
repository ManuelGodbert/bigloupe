package org.bigloupe.web.util.lucene;

import java.io.Reader;

import org.apache.lucene.analysis.KeywordTokenizer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.ReusableAnalyzerBase;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.util.Version;

/**
 * Analyzer to use for searching
 * ignore uper/lower case
 * 
 * @author bigloupe
 *
 */
public final class LowercaseAnalyzer extends ReusableAnalyzerBase {

	@Override
	protected TokenStreamComponents createComponents(final String fieldName,
			final Reader reader) {
		final Tokenizer source = new KeywordTokenizer(reader);
		return new TokenStreamComponents(source, new LowerCaseFilter(
				Version.LUCENE_35, source));
	}
}
