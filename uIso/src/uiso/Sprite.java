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
 * Represents a sprite that will be used to draw a {@link SpriteObject} (that can have an arbitrary number of sprites). A sprite has an 2D anchor point, an 3D bounding
 * box with offsets and an image. The anchor is the point that is drawn as (0,0) when the image is drawn. The bounding box is used to sort the sprites (in order to create
 * the 3D illusion) at the moment they will be drawn. It needs to wrap the volume of the object sprite it is representing. As the bounding box is defined inside the
 * engine simulation, it employs the virtual coordinate system. This class also implements {@link Cloneable} interface (this could not be done explicit because some ME
 * versions does not include it).
 * 
 * <pre>
 * References: 
 * - Bounding box idea: OpenTTD isometric engine.
 * </pre>
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

	public int getBoundingBoxW() {
		return this.bounding_box_w;
	}

	public int getBoundingBoxL() {
		return this.bounding_box_l;
	}

	public int getBoundingBoxH() {
		return this.bounding_box_h;
	}

	public int getBoundingBoxOffsetX() {
		return this.bounding_box_offset_x;
	}

	public int getBoundingBoxOffsetY() {
		return this.bounding_box_offset_y;
	}

	public int getBoundingBoxOffsetZ() {
		return this.bounding_box_offset_z;
	}

	public void setBoundingBoxW(int w) {
		this.bounding_box_w = w;
	}

	public void setBoundingBoxH(int h) {
		this.bounding_box_h = h;
	}

	public void setBoundingBoxL(int l) {
		this.bounding_box_l = l;
	}

	public void setBoundingBoxOffsetX(int bounding_box_offset_x) {
		this.bounding_box_offset_x = bounding_box_offset_x;
	}

	public void setBoundingBoxOffsetY(int bounding_box_offset_y) {
		this.bounding_box_offset_y = bounding_box_offset_y;
	}

	public void setBoundingBoxOffsetZ(int bounding_box_offset_z) {
		this.bounding_box_offset_z = bounding_box_offset_z;
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
		o.bounding_box_w = this.bounding_box_w;
		o.bounding_box_h = this.bounding_box_h;
		o.bounding_box_l = this.bounding_box_l;
		o.bounding_box_offset_x = this.bounding_box_offset_x;
		o.bounding_box_offset_y = this.bounding_box_offset_y;
		o.bounding_box_offset_z = this.bounding_box_offset_z;

		return o;
	}

	/* Package: */
	UIsoImage image;
	int anchor_x, anchor_y, bounding_box_w, bounding_box_h, bounding_box_l, bounding_box_offset_x, bounding_box_offset_y, bounding_box_offset_z;
}
