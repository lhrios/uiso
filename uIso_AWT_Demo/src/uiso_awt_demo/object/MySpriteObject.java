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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import uiso.Sprite;
import uiso.SpriteObject;
import uiso_awt_demo.simulation.SimulationConstants;

public abstract class MySpriteObject<E extends Enum<E>> extends SpriteObject {

	/* Public: */
	public static Map<ObjectType, Map<Integer, Sprite>> sprites = new HashMap<ObjectType, Map<Integer, Sprite>>();

	public static void init() {
		sprites.put(ObjectType.WALL, Wall.createSprites());
		sprites.put(ObjectType.TERRAFORM_ICON, TerraformIcon.createSprites());
		sprites.put(ObjectType.MINOTAUR, Minotaur.createSprites());
	}

	public static void setBoundingBox(Collection<Sprite> sprites, int offset_x, int offset_y, int offset_z, int w, int h, int l) {
		for (Sprite s : sprites) {
			setBoundingBox(s, offset_x, offset_y, offset_z, w, h, l);
		}
	}

	public static void setBoundingBox(Sprite s, int offset_x, int offset_y, int offset_z, int w, int h, int l) {
		s.setBoundingBoxOffsetX(offset_x);
		s.setBoundingBoxOffsetY(offset_y);
		s.setBoundingBoxOffsetZ(offset_z);

		s.setBoundingBoxW(w);
		s.setBoundingBoxH(h);
		s.setBoundingBoxL(l);
	}

	public abstract void update(int tick);

	public void setObjectType(ObjectType objectType) {
		int ordinal = objectType.ordinal();
		assert (ordinal <= 0x3F);
		super.setUserData((super.getUserData() & 0x00FFFFFF) | (ordinal << 24));
		assert (this.getObjectType() == objectType);
	}

	public ObjectType getObjectType() {
		int ordinal = (super.getUserData() & 0x3F000000) >>> 24;
		return ObjectType.values()[ordinal];
	}

	public abstract E getEnumFromOrdinal(int ordinal);

	public void setEnum(E e) {
		int ordinal = e.ordinal();
		assert (ordinal <= 0xF);
		super.setUserData((super.getUserData() & 0xFF0FFFFF) | (ordinal << 20));
		assert (this.getEnum() == e);
	}

	public E getEnum() {
		int ordinal = (super.getUserData() & 0x00F00000) >>> 20;
		return this.getEnumFromOrdinal(ordinal);
	}

	public int getSpriteIndex() {
		return (super.getUserData() & 0x000FF000) >>> 12;
	}

	public void setSpriteIndex(int spriteIndex) {
		assert (spriteIndex <= 0xFF);
		super.setUserData((super.getUserData() & 0xFFF00FFF) | (spriteIndex << 12));
		assert (this.getSpriteIndex() == spriteIndex);
	}

	public int getKey() {
		return Common.computeKey(this.getEnum(), this.getSpriteIndex());
	}

	public int getTileX() {
		return this.getX() / SimulationConstants.TILE_VIRTUAL_SIZE;
	}

	public int getTileY() {
		return this.getY() / SimulationConstants.TILE_VIRTUAL_SIZE;
	}
}
