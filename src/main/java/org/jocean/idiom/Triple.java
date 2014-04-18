/**
 * 
 */
package org.jocean.idiom;

import java.io.Serializable;

/**
 * @author isdom
 *
 */
public final class Triple<FIRST, SECOND, THIRD> implements Serializable {

	/**
	 * Triple's serialVersionUID
	 */
	private static final long serialVersionUID = 8479803002911944953L;


	public final FIRST first;

	public final SECOND second;

	public final THIRD 	third;
	
	private Triple(FIRST first, SECOND second, THIRD third) {

		this.first = first;

		this.second = second;
		
		this.third = third;
	}
	
	public FIRST getFirst() {
		return first;
	}

	public SECOND getSecond() {
		return second;
	}

	public THIRD getThird() {
		return third;
	}
	
	public static <FIRST, SECOND, THIRD> Triple<FIRST, SECOND, THIRD> of(FIRST first,
			SECOND second, THIRD third) {

		return new Triple<FIRST, SECOND, THIRD>(first, second, third);

	}
	
	

	@Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((first == null) ? 0 : first.hashCode());
        result = prime * result + ((second == null) ? 0 : second.hashCode());
        result = prime * result + ((third == null) ? 0 : third.hashCode());
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
        Triple other = (Triple) obj;
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
        if (third == null) {
            if (other.third != null)
                return false;
        } else if (!third.equals(other.third))
            return false;
        return true;
    }

    @Override
	public String toString() {

		return String.format("Triple[%s,%s,%s]", first, second, third);

	}

}
