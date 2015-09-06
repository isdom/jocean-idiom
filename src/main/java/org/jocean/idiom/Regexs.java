package org.jocean.idiom;

import java.util.regex.Pattern;

public class Regexs {
    private Regexs() {
        throw new IllegalStateException("No instances!");
    }

    public static Pattern safeCompilePattern(final String regex) {
        return null != regex && !"".equals(regex) ? Pattern.compile(regex) : null;
    }
    
    public static boolean isMatched(final Pattern pattern, final String content) {
        return pattern != null ? pattern.matcher(content).find() : true;
    }
}
