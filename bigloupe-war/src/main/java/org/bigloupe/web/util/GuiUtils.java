package org.bigloupe.web.util;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.joda.time.Period;
import org.joda.time.ReadablePeriod;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

/**
 * Utilities available from within velocity. An instance of this class is added
 * to every request
 * 
 * @author jkreps
 * 
 */
public class GuiUtils {

    private static final long ONE_KB = 1024;
    private static final long ONE_MB = 1024 * ONE_KB;
    private static final long ONE_GB = 1024 * ONE_MB;
    private static final long ONE_TB = 1024 * ONE_GB;
    private static final Pattern NEWLINE_PATTERN = Pattern.compile("[\r\n]");

    private static final PeriodFormatter PEROD_FORMAT = new PeriodFormatterBuilder().printZeroNever()
                                                                                    .appendDays()
                                                                                    .appendSuffix(" day",
                                                                                                  " days")
                                                                                    .appendSeparator(", ")
                                                                                    .printZeroNever()
                                                                                    .appendHours()
                                                                                    .appendSuffix(" hour",
                                                                                                  " hours")
                                                                                    .appendSeparator(", ")
                                                                                    .printZeroAlways()
                                                                                    .appendMinutes()
                                                                                    .appendSuffix(" minute",
                                                                                                  " minutes")
                                                                                    .toFormatter();

    
    
    public static DateTime now() {
    	return new DateTime();
    }
    
    public static String getShortTimeZone() {
    	return DateTimeZone.getDefault().getShortName(new DateTime().getMillis());
    }
    
    public static String formatDate(DateTime date) {
        return formatDate(date, "MM-dd-yyyy");
    }

    public String formatDateTime(DateTime date) {
        return formatDate(date, "MM-dd-yyyy HH:mm:ss");
    }

    public static String formatDateTimeAndZone(DateTime date) {
        return formatDate(date, "MM-dd-yyyy HH:mm:ss z");
    }
    
    public static String formatDate(DateTime date, String format) {
        DateTimeFormatter f = DateTimeFormat.forPattern(format);
        return f.print(date);
    }

    public static Duration duration(DateTime a, DateTime b) {
        return new Duration(a, b);
    }

    public static int durationInMinutes(DateTime a, DateTime b) {
        return duration(a, b).toPeriod().toStandardMinutes().getMinutes();
    }

    public static Period period(DateTime a, DateTime b) {
        return duration(a, b).toPeriod();
    }

    public static String formatPeriod(ReadablePeriod period) {
        return PEROD_FORMAT.print(period);
    }

    public String getClassBaseName(Class<?> c) {
        String name = c.getName();
        int last = name.lastIndexOf('.');
        if(last >= 0)
            return name.substring(last + 1, name.length());
        else
            return name;
    }

    public String repeat(String s, int n) {
        StringBuffer b = new StringBuffer(n * s.length());
        for(int i = 0; i < n; i++)
            b.append(s);
        return b.toString();
    }

    public String formatPercent(double d, int fracDigits) {
        NumberFormat format = NumberFormat.getPercentInstance();
        format.setMaximumFractionDigits(fracDigits);
        return format.format(d);
    }

    public boolean isNull(Object o) {
        return o == null;
    }

    public <T> List<T> sorted(Collection<T> c, Comparator<T> comparator) {
        List<T> l = new ArrayList<T>(c);
        Collections.sort(l, comparator);
        return l;
    }

    public static <T> List<T> reversed(Collection<T> c) {
        List<T> l = new ArrayList<T>(c);
        Collections.reverse(l);
        return l;
    }

    public <T extends Comparable<? super T>> List<T> sorted(Collection<T> c) {
        List<T> l = new ArrayList<T>(c);
        Collections.sort(l);
        return l;
    }

    public Set<Object> getEmptySet() {
        return new HashSet<Object>();
    }

    public <T> void add(Collection<T> c, T t) {
        c.add(t);
    }

    public String newlineToBr(String in) {
        return NEWLINE_PATTERN.matcher(in).replaceAll("<br/>");
    }

    public DateTime getNow() {
        return new DateTime();
    }

    public String elipsify(String s, int length) {
        if(s == null)
            return "";
        else if(s.length() <= length)
            return s;
        else
            return s.substring(0, Math.min(s.length(), length - 3)) + "...";
    }

    public String displayBytes(long sizeBytes) {
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(2);
        if(sizeBytes >= ONE_TB)
            return nf.format(sizeBytes / (double) ONE_TB) + " tb";
        else if(sizeBytes >= ONE_GB)
            return nf.format(sizeBytes / (double) ONE_GB) + " gb";
        else if(sizeBytes >= ONE_MB)
            return nf.format(sizeBytes / (double) ONE_MB) + " mb";
        else if(sizeBytes >= ONE_KB)
            return nf.format(sizeBytes / (double) ONE_KB) + " kb";
        else
            return sizeBytes + " B";
    }

    public List<Integer> seq(int max) {
        List<Integer> l = new ArrayList<Integer>();
        for(int i = 0; i < max; i++)
            l.add(i);
        return l;
    }

    public Integer add(Integer n, Integer m) {
    	return n + m;
    }
    
    public Integer sub(Integer n, Integer m) {
    	return n - m;
    }
    
    public Integer div(Integer n, Integer m) {
    	return n / m;
    }
    
    public Integer mul(Integer n, Integer m) {
    	return n * m;
    }
    
    public Integer mod(Integer n, Integer m) {
    	return n % m;
    }
    
    public Integer max(Integer n, Integer m) {
    	return Math.max(n, m);
    }
}