/*
 * Copyright 2012, 2015 Luis Henrique O. Rios
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

public class Point {
	/* Public: */
	public int x, y, z;

	public Point() {
	}

	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Point(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * Copies the data from {@code p} to this instance
	 * 
	 * @param p
	 *           the instance from where the data will be copied
	 */
	public void copyFrom(Point p) {
		this.x = p.x;
		this.y = p.y;
		this.z = p.z;
	}

	@Override
	public String toString() {
		return "[" + this.x + "," + this.y + "," + this.z + "]";
	}

	@Override
	public Object clone() {
		Point o = new Point();
		o.copyFrom(this);
		return o;
	}
}
