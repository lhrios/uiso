/*
 * Copyright 2012 Luis Henrique O. Rios
 *
 * This file is part of uIsometric Engine.
 *
 * uIsometric Engine is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uIsometric Engine is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with uIsometric Engine.  If not, see <http://www.gnu.org/licenses/>.
 */

package uiso;

/**
 * Represents an object in a 3D space. The objects can be part of at most 4 double linked lists (one for each corner). Each objects grid cell has a double linked list
 * whose elements are the objects inside it.
 * 
 * @author luis
 */

/*            N
 *   +-----------------+
 *   |                 |
 * W |                 | E
 *   |                 |
 *   +-----------------+
 *            S
 */
public abstract class UIsoObject extends LinkedListElement {
	/* Public: */
	public UIsoObject() {
		this.setVisibility(true);
	}

	public int getX() {
		boolean sign = (this.data1 & 0x80000000) != 0;
		return ((this.data1 & 0x7FFF0000) >>> 16) * (sign ? -1 : 1);
	}

	public int getY() {
		boolean sign = (this.data1 & 0x00008000) != 0;
		return (this.data1 & 0x00007FFF) * (sign ? -1 : 1);
	}

	public int getZ() {
		boolean sign = (this.data3 & 0x00008000) != 0;
		return (this.data3 & 0x00007FFF) * (sign ? -1 : 1);
	}

	/**
	 * Each {@link UIsoObject} has 30 bits that can be used freely to store data.
	 * 
	 * @return the user data stored in this {@link UIsoObject}
	 */
	public int getUserData() {
		return (this.data2 & 0x3FFFFFFF);
	}

	public void setX(int x) {
		boolean sign = false;
		if (x < 0) {
			sign = true;
			x = -x;
		}
		this.data1 = ((sign ? 1 : 0) << 31) | ((x << 16) & 0x7FFF0000) | (this.data1 & 0x0000FFFF);
	}

	public void setY(int y) {
		boolean sign = false;
		if (y < 0) {
			sign = true;
			y = -y;
		}
		this.data1 = ((sign ? 1 : 0) << 15) | (y & 0x00007FFF) | (this.data1 & 0xFFFF0000);
	}

	public void setZ(int z) {
		boolean sign = false;
		if (z < 0) {
			sign = true;
			z = -z;
		}
		this.data3 = ((sign ? 1 : 0) << 15) | (z & 0x00007FFF) | (this.data3 & 0xFFFF0000);
	}

	/**
	 * Each {@link UIsoObject} has 30 bits that can be used freely to store data.
	 * 
	 * @param user_data
	 *           the user data to be stored in this {@link UIsoObject}. It uses only the 30 least significative bits.
	 */
	public void setUserData(int user_data) {
		this.data2 = (user_data & 0x3FFFFFFF) | (this.data2 & 0xC0000000);
	}

	@Override
	public String toString() {
		return "[" + this.getX() + "," + this.getY() + "," + this.getZ() + "] : " + this.getUserData();
	}

	public boolean isVisible() {
		return (this.data2 & 0x40000000) != 0;
	}

	public void setVisibility(boolean visibility) {
		if (visibility)
			this.data2 = (1 << 30) | (this.data2 & 0xBFFFFFFF);
		else
			this.data2 &= 0xBFFFFFFF;
	}

	/* Package: */
	final static int NW_VERTEX = 0;
	final static int NE_VERTEX = 1;
	final static int ES_VERTEX = 2;
	final static int WS_VERTEX = 3;
	final static int INVALID_VERTEX = 4;

	LinkedListElement nw_foward, nw_backward, ne_foward, ne_backward, es_foward, es_backward, ws_foward, ws_backward;

	LinkedListElement getPreviousElement(int vertex) {
		switch (vertex) {
			case NW_VERTEX:
				return this.nw_backward;
			case NE_VERTEX:
				return this.ne_backward;
			case ES_VERTEX:
				return this.es_backward;
			case WS_VERTEX:
				return this.ws_backward;
			default:
				assert (false);
			break;
		}
		return null;
	}

	LinkedListElement getNextElement(int vertex) {
		switch (vertex) {
			case NW_VERTEX:
				return this.nw_foward;
			case NE_VERTEX:
				return this.ne_foward;
			case ES_VERTEX:
				return this.es_foward;
			case WS_VERTEX:
				return this.ws_foward;
			default:
				assert (false);
			break;
		}
		return null;
	}

	int getVertexFromPreviousElement(LinkedListElement previous) {
		if (previous == this.nw_backward)
			return NW_VERTEX;
		if (previous == this.ne_backward)
			return NE_VERTEX;
		if (previous == this.es_backward)
			return ES_VERTEX;
		if (previous == this.ws_backward)
			return WS_VERTEX;
		assert (false);
		return INVALID_VERTEX;
	}

	void setValueToNextVertexField(LinkedListElement next, int vertex) {
		switch (vertex) {
			case NW_VERTEX:
				this.nw_foward = next;
			break;
			case NE_VERTEX:
				this.ne_foward = next;
			break;
			case ES_VERTEX:
				this.es_foward = next;
			break;
			case WS_VERTEX:
				this.ws_foward = next;
			break;
			case INVALID_VERTEX:
				assert (false);
			break;
		}
	}

	void setValueToPreviousVertexField(LinkedListElement previous, int vertex) {
		switch (vertex) {
			case NW_VERTEX:
				this.nw_backward = previous;
			break;
			case NE_VERTEX:
				this.ne_backward = previous;
			break;
			case ES_VERTEX:
				this.es_backward = previous;
			break;
			case WS_VERTEX:
				this.ws_backward = previous;
			break;
			case INVALID_VERTEX:
				assert (false);
			break;
		}
	}

	void setValueToVertex(LinkedListElement previous, LinkedListElement next, int vertex) {
		this.setValueToNextVertexField(next, vertex);
		this.setValueToPreviousVertexField(previous, vertex);
	}

	void removeObjectFromLinkedList(int vertex) {
		LinkedListElement previous = this.getPreviousElement(vertex);
		uiso.UIsoObject next = (uiso.UIsoObject) this.getNextElement(vertex);

		if (previous != null) {
			if (previous instanceof UIsoObjectsGridCell) {
				((UIsoObjectsGridCell) previous).isometric_engine_object = next;
			} else {
				uiso.UIsoObject previous_object = (uiso.UIsoObject) previous;
				previous_object.setValueToNextVertexField(next, this.getVertexOfPreviousElementThatContinuesTheListInVertex(vertex));
			}
		}

		if (next != null) {
			next.setValueToPreviousVertexField(previous, this.getVertexOfNextElementThatContinuesTheListInVertex(vertex));
			if (previous instanceof uiso.UIsoObject) {
				uiso.UIsoObject previous_object = (uiso.UIsoObject) previous;
				previous_object.setVertexOfNextElementThatContinuesTheListInVertex(this.getVertexOfNextElementThatContinuesTheListInVertex(vertex), this
						.getVertexOfPreviousElementThatContinuesTheListInVertex(vertex));
				next.setVertexOfPreviousElementThatContinuesTheListInVertex(this.getVertexOfPreviousElementThatContinuesTheListInVertex(vertex), this
						.getVertexOfNextElementThatContinuesTheListInVertex(vertex));
			}
		}

		this.setValueToVertex(null, null, vertex);
	}

	void setSelected(boolean selected) {
		if (selected)
			this.data2 = (1 << 31) | (this.data2 & 0x7FFFFFFF);
		else
			this.data2 &= 0x7FFFFFFF;
	}

	boolean isSelected() {
		return (this.data2 & 0x80000000) != 0;
	}

	int getVertexOfPreviousElementThatContinuesTheListInVertex(int vertex) {
		assert (NW_VERTEX <= vertex && vertex <= WS_VERTEX);
		int shift = (vertex << 1) + 24, value;
		value = (this.data3 & (0x3 << shift)) >>> shift;
		assert (NW_VERTEX <= value && value <= WS_VERTEX);
		return value;
	}

	int getVertexOfNextElementThatContinuesTheListInVertex(int vertex) {
		assert (NW_VERTEX <= vertex && vertex <= WS_VERTEX);
		int shift = (vertex << 1) + 16, value;
		value = (this.data3 & (0x3 << shift)) >>> shift;
		assert (NW_VERTEX <= value && value <= WS_VERTEX);
		return value;
	}

	void setVertexOfPreviousElementThatContinuesTheListInVertex(int vertex_value, int vertex) {
		assert (NW_VERTEX <= vertex_value && vertex_value <= WS_VERTEX);
		assert (NW_VERTEX <= vertex && vertex <= WS_VERTEX);
		int shift = (vertex << 1) + 24;
		this.data3 = (this.data3 & (~(0x3 << shift))) | (vertex_value << shift);
	}

	void setVertexOfNextElementThatContinuesTheListInVertex(int vertex_value, int vertex) {
		assert (NW_VERTEX <= vertex_value && vertex_value <= WS_VERTEX);
		assert (NW_VERTEX <= vertex && vertex <= WS_VERTEX);
		int shift = (vertex << 1) + 16;
		this.data3 = (this.data3 & (~(0x3 << shift))) | (vertex_value << shift);
	}

	/* Private: */
	/* Part of the data is stored in some fields to save memory. */
	/* The compiler allocates 4 bytes for byte, boolean, short and int types. */
	/* Data 1: */
	/* [31 ... 31] (1 bit): x sign */
	/* [30 ... 16] (15 bits): x */
	/* [15 ... 15] (1 bit): y sign */
	/* [14 ... 0] (15 bits): y */
	/* Data 2: */
	/* [31 ... 31] (1 bit): selected - used to avoid repetitions during scene drawing */
	/* [30 ... 30] (1 bits): visible */
	/* [29 ... 0] (30 bits): user data */
	/* Data 3: */
	/* [31 ... 30] (2 bits): ws_backward field */
	/* [29 ... 28] (2 bits): es_backward field */
	/* [27 ... 26] (2 bits): ne_backward field */
	/* [25 ... 24] (2 bits): nw_backward field */
	/* [23 ... 22] (2 bits): ws_foward field */
	/* [21 ... 20] (2 bits): es_foward field */
	/* [19 ... 18] (2 bits): ne_foward field */
	/* [17 ... 16] (2 bits): nw_foward field */
	/* [15 ... 15] (1 bit): z sign */
	/* [14 ... 0] (15 bits): z */

	private int data1, data2, data3;
}
