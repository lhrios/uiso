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

public class UIsoConstants {
	/* Public: */

	//@formatter:off
	/* These constants define the position of a point relative to the following four-sided polygon. 
	 *     
	 *                  \    /
	 *                   \  /
	 *                    N
	 *           \       / \      / 
	 *            \    /     \   /
	 *             W /         \ E
	 *             / \ polygon / \
	 *           /     \     /    \
	 *         /         \ /
	 *                    S
	 *                  /  \
	 *                 /    \ 
	 */
	//@formatter:on
	public final static int INSIDE_POLYGON = 0;
	public final static int ABOVE_WN_LINE = 1;
	public final static int ABOVE_NE_LINE = 2;
	public final static int BELOW_SW_LINE = 4;
	public final static int BELOW_ES_LINE = 8;

	/* Package: */
}
