/**
 * 
 */
package org.jocean.idiom;


/**
 * @author isdom
 *
 */
public class Features {
    public static <E extends Enum<E>> boolean isEnabled(final int features, final E feature) {
        return (features & getMask(feature)) != 0;
    }

    public static <E extends Enum<E>> int config(
    		int features, final E feature, final boolean state) {
        if (state) {
            features |= getMask(feature);
        } else {
            features &= ~getMask(feature);
        }

        return features;
    }
    
    @SafeVarargs
	public static <E extends Enum<E>> int featuresAsInt(final E... features) {
        int featuresAsInt = 0;
        for ( E feature : features) {
            featuresAsInt = config(featuresAsInt, feature, true);
        }
        
        return featuresAsInt;
    }

    public static <E extends Enum<E>> int getMask(final E feature) {
        return (1 << feature.ordinal());
    }
}
