/**
 * 
 */
package org.jocean.idiom;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author isdom
 *
 */
public class PropertyPlaceholderHelper {
    
    private static final Logger LOG = 
            LoggerFactory.getLogger(PropertyPlaceholderHelper.class);

    private static final Map<String, String> wellKnownSimplePrefixes = 
            new HashMap<String, String>(4);

    static {
        wellKnownSimplePrefixes.put("}", "{");
        wellKnownSimplePrefixes.put("]", "[");
        wellKnownSimplePrefixes.put(")", "(");
    }
    
    private final String placeholderPrefix;

    private final String placeholderSuffix;

    private final String simplePrefix;

    private final String valueSeparator;

    private final boolean ignoreUnresolvablePlaceholders;

    private final Visitor2<String, String>  _recordResolvedPlaceholder;

    /**
     * Creates a new <code>PropertyPlaceholderHelper</code> that uses the supplied prefix and suffix.
     * Unresolvable placeholders are ignored.
     * @param placeholderPrefix the prefix that denotes the start of a placeholder.
     * @param placeholderSuffix the suffix that denotes the end of a placeholder.
     */
      public PropertyPlaceholderHelper(final String placeholderPrefix, final String placeholderSuffix) {
          this(placeholderPrefix, placeholderSuffix, null, true, null);
      }

    /**
     * Creates a new <code>PropertyPlaceholderHelper</code> that uses the supplied prefix and suffix.
     * @param placeholderPrefix the prefix that denotes the start of a placeholder
     * @param placeholderSuffix the suffix that denotes the end of a placeholder
     * @param valueSeparator the separating character between the placeholder variable
     * and the associated default value, if any
     * @param ignoreUnresolvablePlaceholders indicates whether unresolvable placeholders should be ignored
     * (<code>true</code>) or cause an exception (<code>false</code>).
     */
    public PropertyPlaceholderHelper(
            final String placeholderPrefix, final String placeholderSuffix,
            final String valueSeparator, final boolean ignoreUnresolvablePlaceholders,
            final Visitor2<String, String> recordResolvedPlaceholder) {

        _assertNotNull(placeholderPrefix, "placeholderPrefix must not be null");
        _assertNotNull(placeholderSuffix, "placeholderSuffix must not be null");
        this.placeholderPrefix = placeholderPrefix;
        this.placeholderSuffix = placeholderSuffix;
        String simplePrefixForSuffix = wellKnownSimplePrefixes.get(this.placeholderSuffix);
        if (simplePrefixForSuffix != null && this.placeholderPrefix.endsWith(simplePrefixForSuffix)) {
            this.simplePrefix = simplePrefixForSuffix;
        }
        else {
            this.simplePrefix = this.placeholderPrefix;
        }
        this.valueSeparator = valueSeparator;
        this.ignoreUnresolvablePlaceholders = ignoreUnresolvablePlaceholders;
        this._recordResolvedPlaceholder = recordResolvedPlaceholder;
    }


    /**
     * Replaces all placeholders of format <code>${name}</code> with the corresponding property
     * from the supplied {@link Properties}.
     * @param value the value containing the placeholders to be replaced.
     * @param properties the <code>Properties</code> to use for replacement.
     * @return the supplied value with placeholders replaced inline.
     */
    public String replacePlaceholders(final Object resolveContext, final String value, final Properties properties,
            final Set<String> visitedPlaceholders) {
        _assertNotNull(properties, "Argument 'properties' must not be null.");
        return replacePlaceholders(resolveContext, value, new PlaceholderResolver() {
            public String resolvePlaceholder(final Object resolveContext, final String placeholderName) {
                return properties.getProperty(placeholderName);
            }
        }, visitedPlaceholders);
    }

    /**
     * Replaces all placeholders of format <code>${name}</code> with the value returned from the supplied
     * {@link PlaceholderResolver}.
     * @param value the value containing the placeholders to be replaced.
     * @param placeholderResolver the <code>PlaceholderResolver</code> to use for replacement.
     * @return the supplied value with placeholders replaced inline.
     */
    public String replacePlaceholders(
            final Object resolveContext, 
            final String value, 
            final PlaceholderResolver placeholderResolver, 
            final Set<String> visitedPlaceholders) {
        _assertNotNull(value, "Argument 'value' must not be null.");
        return parseStringValue(resolveContext, value, placeholderResolver, 
                null != visitedPlaceholders ? visitedPlaceholders : new HashSet<String>());
    }

    protected String parseStringValue(
            final Object resolveContext,
            final String strVal, 
            final PlaceholderResolver placeholderResolver, 
            final Set<String> visitedPlaceholders) {

        if ( LOG.isDebugEnabled() ) {
            LOG.debug("in parseStringValue for: {}", strVal);
        }
        
        StringBuilder buf = new StringBuilder(strVal);

        int startIndex = strVal.indexOf(this.placeholderPrefix);
        while (startIndex != -1) {
            int endIndex = findPlaceholderEndIndex(buf, startIndex);
            if (endIndex != -1) {
                String placeholder = buf.substring(startIndex + this.placeholderPrefix.length(), endIndex);
                if (!visitedPlaceholders.add(placeholder)) {
                    throw new IllegalArgumentException(
                            "Circular placeholder reference '" + placeholder + "' in property definitions");
                }
                // Recursive invocation, parsing placeholders contained in the placeholder key.
                placeholder = parseStringValue(resolveContext, placeholder, placeholderResolver, visitedPlaceholders);

                if ( LOG.isDebugEnabled() ) {
                    LOG.debug("found placeholder: {}", placeholder);
                }
                // Now obtain the value for the fully resolved key...
                String propVal = placeholderResolver.resolvePlaceholder(resolveContext, placeholder);
                if (propVal == null && this.valueSeparator != null) {
                    int separatorIndex = placeholder.indexOf(this.valueSeparator);
                    if (separatorIndex != -1) {
                        String actualPlaceholder = placeholder.substring(0, separatorIndex);
                        String defaultValue = placeholder.substring(separatorIndex + this.valueSeparator.length());
                        propVal = placeholderResolver.resolvePlaceholder(resolveContext, actualPlaceholder);
                        if (propVal == null) {
                            propVal = defaultValue;
                        }
                    }
                }
                if (propVal != null) {
                    // Recursive invocation, parsing placeholders contained in the
                    // previously resolved placeholder value.
                    propVal = parseStringValue(resolveContext, propVal, placeholderResolver, visitedPlaceholders);
                    buf.replace(startIndex, endIndex + this.placeholderSuffix.length(), propVal);
                    recordResolvedPlaceholder(placeholder, propVal);
                    if (LOG.isTraceEnabled()) {
                        LOG.trace("Resolved placeholder '" + placeholder + "'");
                    }
                    startIndex = buf.indexOf(this.placeholderPrefix, startIndex + propVal.length());
                }
                else if (this.ignoreUnresolvablePlaceholders) {
                    recordResolvedPlaceholder(placeholder, "");
                    // Proceed with unprocessed value.
                    startIndex = buf.indexOf(this.placeholderPrefix, endIndex + this.placeholderSuffix.length());
                }
                else {
                    throw new IllegalArgumentException("Could not resolve placeholder '" +
                            placeholder + "'" + " in string value [" + strVal + "]");
                }

                visitedPlaceholders.remove(placeholder);
            }
            else {
                startIndex = -1;
            }
        }

        return buf.toString();
    }

    private int findPlaceholderEndIndex(final CharSequence buf, final int startIndex) {
        int index = startIndex + this.placeholderPrefix.length();
        int withinNestedPlaceholder = 0;
        while (index < buf.length()) {
            if (_substringMatch(buf, index, this.placeholderSuffix)) {
                if (withinNestedPlaceholder > 0) {
                    withinNestedPlaceholder--;
                    index = index + this.placeholderSuffix.length();
                }
                else {
                    return index;
                }
            }
            else if (_substringMatch(buf, index, this.simplePrefix)) {
                withinNestedPlaceholder++;
                index = index + this.simplePrefix.length();
            }
            else {
                index++;
            }
        }
        return -1;
    }

    /**
     * Strategy interface used to resolve replacement values for placeholders contained in Strings.
     * @see PropertyPlaceholderHelper
     */
    public static interface PlaceholderResolver {

        /**
         * Resolves the supplied placeholder name into the replacement value.
         * @param placeholderName the name of the placeholder to resolve
         * @return the replacement value or {@code null} if no replacement is to be made
         */
        String resolvePlaceholder(final Object resolveContext, final String placeholderName);
    }
    
    private static boolean _substringMatch(final CharSequence str, int index, final CharSequence substring) {
        for (int j = 0; j < substring.length(); j++) {
            int i = index + j;
            if (i >= str.length() || str.charAt(i) != substring.charAt(j)) {
                return false;
            }
        }
        return true;
    }

    public static void _assertNotNull(final Object object, final String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }
    
    private void recordResolvedPlaceholder(final String placeholder, final String propVal) {
        if ( null != this._recordResolvedPlaceholder ) {
            try {
                this._recordResolvedPlaceholder.visit(placeholder, propVal);
            }
            catch (Exception e) {
                LOG.warn("exception when recordResolvedPlaceholder for placeholder({}) with propVal({}), detail:{}",
                        placeholder, propVal, ExceptionUtils.exception2detail(e));
            }
        }
//        if ( !this._resolvedPlaceholders.containsKey(placeholder) ) {
//            this._resolvedPlaceholders.put(placeholder, propVal);
//        }
//        else {
//            final Object data = this._resolvedPlaceholders.get(placeholder);
//            if ( data instanceof String ) {
//                if ( data.equals(propVal) ) {
//                    //  just ignore
//                    if ( LOG.isDebugEnabled() ) {
//                        LOG.debug("found the same propVal {} for placeholder {}, just ignore", propVal, placeholder);
//                    }
//                }
//                else {
//                    this._resolvedPlaceholders.put(placeholder, new String[]{propVal, data.toString()});
//                }
//            }
//            else {
//                // suppose string arrays
//                final String[] arrays = (String[])data;
//                if ( Arrays.asList(arrays).contains(propVal) ) {
//                    //  just ignore
//                    if ( LOG.isDebugEnabled() ) {
//                        LOG.debug("found the same propVal {} for placeholder {}, just ignore", propVal, placeholder);
//                    }
//                }
//                else {
//                     final String[] newArrays = Arrays.copyOf(arrays, arrays.length + 1);
//                     newArrays[arrays.length] = propVal;
//                    this._resolvedPlaceholders.put(placeholder, newArrays);
//                }
//            }
//        }
    }
    
//    private final Map<String, Object>   _resolvedPlaceholders = new HashMap<String, Object>();
}
