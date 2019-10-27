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

package uiso_awt_demo.object;

/* NW         N          NE
 *   +-----------------+
 *   |                 |
 * W |                 | E
 *   |                 |
 *   +-----------------+
 * SW         S          SE          
 */
public enum Direction {
	NW, N, NE, E, SE, S, SW, W;

	public static Direction getDirectionFromDelta(int delta_x, int delta_y) {
		Direction y_direction = null, x_direction = null;
		if (delta_y > 0) {
			y_direction = SE;
		} else if (delta_y < 0) {
			y_direction = NW;
		}

		if (delta_x > 0) {
			x_direction = SW;
		} else if (delta_x < 0) {
			x_direction = NE;
		}

		if (x_direction == null) {
			return y_direction;
		} else if (y_direction == null) {
			return x_direction;
		} else if (y_direction == NW && x_direction == NE) {
			return N;
		} else if (y_direction == NW && x_direction == SW) {
			return W;
		} else if (y_direction == SE && x_direction == NE) {
			return E;
		} else if (y_direction == SE && x_direction == SW) {
			return S;
		}

		return null;
	}
}
