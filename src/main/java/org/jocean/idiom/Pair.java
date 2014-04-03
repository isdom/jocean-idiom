/**
 * 
 */
package org.jocean.idiom;

import java.io.Serializable;

/**
 * @author hp
 *
 */
public final class Pair<FIRST, SECOND> implements Serializable {

	/**
	 * Pair's serialVersionUID
	 */
	private static final long serialVersionUID = -3560542815329489993L;

	public final FIRST first;

	public final SECOND second;

	private Pair(FIRST first, SECOND second) {

		this.first = first;

		this.second = second;

	}
	
	public FIRST getFirst() {
		return first;
	}

	public SECOND getSecond() {
		return second;
	}

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((first == null) ? 0 : first.hashCode());
		result = prime * result + ((second == null) ? 0 : second.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		@SuppressWarnings("rawtypes")
        Pair other = (Pair) obj;
		if (first == null) {
			if (other.first != null)
				return false;
		} else if (!first.equals(other.first))
			return false;
		if (second == null) {
			if (other.second != null)
				return false;
		} else if (!second.equals(other.second))
			return false;
		return true;
	}

	public static <FIRST, SECOND> Pair<FIRST, SECOND> of(FIRST first,
			SECOND second) {

		return new Pair<FIRST, SECOND>(first, second);

	}

	@Override
	public String toString() {

		return String.format("Pair[%s,%s]", first, second);

	}

}
