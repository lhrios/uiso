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

/**
 * Represents a sprite that will be used to draw a {@link SpriteObject} (that can have an arbitrary number of sprites). A sprite has an 2D anchor point and an image. The
 * anchor is the point that is drawn as (0,0) when the image is drawn. This class also implements {@link Cloneable} interface (this could not be done explicit because
 * some ME versions does not include it).
 * 
 * @author luis
 */
public class Sprite {
	/* Public: */
	public int getAnchorX() {
		return this.anchor_x;
	}

	public int getAnchorY() {
		return this.anchor_y;
	}

	public void setAnchorX(int anchor_x) {
		this.anchor_x = anchor_x;
	}

	public void setAnchorY(int anchor_y) {
		this.anchor_y = anchor_y;
	}

	public UIsoImage getImage() {
		return this.image;
	}

	public void setImage(UIsoImage image) {
		this.image = image;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		Sprite o = (Sprite) super.clone();

		o.image = this.image;
		o.anchor_x = this.anchor_x;
		o.anchor_y = this.anchor_y;

		return o;
	}

	/* Package: */
	UIsoImage image;
	int anchor_x, anchor_y;
}
