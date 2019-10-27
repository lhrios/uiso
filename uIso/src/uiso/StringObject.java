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
 * Represents a {@link String} object that is managed by {@link UIsoEngine}. The string anchor is the center of the rectangle that bounds it.
 * 
 * @author luis
 */
public class StringObject extends UIsoObject {
	/* Public: */
	public void setString(String s) {
		this.s = s;
	}

	public String getString() {
		return this.s;
	}

	public void getString(String s) {
		this.s = s;
	}

	public Object getFont() {
		return this.font;
	}

	public void setFont(Object font) {
		this.font = font;
	}

	public Object getColor() {
		return this.color;
	}

	public void setColor(Object color) {
		this.color = color;
	}

	/* Package: */
	Object color;
	Object font;
	String s;
}
