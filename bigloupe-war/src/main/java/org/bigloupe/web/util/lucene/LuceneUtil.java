package org.bigloupe.web.util.lucene;

import org.apache.lucene.search.vectorhighlight.FastVectorHighlighter;
import org.apache.lucene.search.vectorhighlight.FragListBuilder;
import org.apache.lucene.search.vectorhighlight.FragmentsBuilder;
import org.apache.lucene.search.vectorhighlight.SimpleFragListBuilder;
import org.apache.lucene.search.vectorhighlight.SimpleFragmentsBuilder;

public class LuceneUtil {
	public static final String FIELD_NORMAL = "normal";
    public static final String[] PRE_TAGS = new String[]{"<strong>"};
    public static final String[] POST_TAGS = new String[]{"</strong>"};
    
    public static FastVectorHighlighter makeHighlighter() {
        FragListBuilder fragListBuilder = new SimpleFragListBuilder(200);
        FragmentsBuilder fragmentBuilder = new SimpleFragmentsBuilder(PRE_TAGS, POST_TAGS);
        return new FastVectorHighlighter(true, true, fragListBuilder, fragmentBuilder);
    }
}
