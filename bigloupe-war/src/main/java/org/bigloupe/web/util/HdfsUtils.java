package org.bigloupe.web.util;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathFilter;

import com.google.common.base.Preconditions;

public class HdfsUtils {

	public static class RegexExcludePathFilter implements PathFilter {
		private final String regex;

		public RegexExcludePathFilter(String regex) {
			this.regex = Preconditions.checkNotNull(regex);
		}

		public boolean accept(Path path) {
			return !path.toString().matches(regex);
		}
	}

	public static final PathFilter HIDDEN_FILE_FILTER = new PathFilter() {
		@Override
		public boolean accept(Path p) {
			String name = p.getName();
			return !(name.startsWith(".") || name.startsWith("_") || name.contains("part"));
		}
	};
}
