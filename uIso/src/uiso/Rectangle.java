/*
 * Copyright 2015 Luis Henrique O. Rios
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

public class Rectangle {
	/* Public: */
	public int w, h;

	/**
	 * Copies the data from {@code r} to this instance
	 * 
	 * @param p
	 *           the instance from where the data will be copied
	 */
	public void copyFrom(Rectangle r) {
		this.w = r.w;
		this.h = r.h;
	}

	@Override
	public String toString() {
		return "[" + this.w + "," + this.h + "]";
	}

	@Override
	public Object clone() {
		Rectangle r = new Rectangle();
		r.copyFrom(this);
		return r;
	}
}
