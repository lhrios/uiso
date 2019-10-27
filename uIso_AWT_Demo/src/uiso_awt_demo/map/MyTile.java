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

package uiso_awt_demo.map;

import uiso.Tile;
import uiso_awt_demo.object.Common;

public class MyTile extends Tile {
	/* Public: */

	public MyTile() {
		this.setPassability(true);
	}

	public int getSpriteIndex() {
		return (super.getUserData() & 0x00000018) >> 3;
	}

	public void setSpriteIndex(int sprite_index) {
		assert (sprite_index <= 0x3);
		super.setUserData((super.getUserData() & 0xFFFFFFE7) | (sprite_index << 3));
		assert (this.getSpriteIndex() == sprite_index);
	}

	public void setTileType(TileType tile_type) {
		int ordinal = tile_type.ordinal();
		assert (ordinal <= 0x7);
		super.setUserData((super.getUserData() & 0xFFFFFFF8) | ordinal);
		assert (this.getTileType() == tile_type);
	}

	public TileType getTileType() {
		int ordinal = (super.getUserData() & 0x00000007);
		return TileType.values()[ordinal];
	}

	public int getKey() {
		return Common.computeKey(this.getTileType(), this.getSpriteIndex());
	}

	public boolean isPassable() {
		return (super.getUserData() & 0x00000020) != 0;
	}

	public void setPassability(boolean passability) {
		super.setUserData((super.getUserData() & 0xFFFFFFDF) | (passability ? 0x20 : 0));
		assert (this.isPassable() == passability);
	}

	@Override
	public String toString() {
		return String.format("%s %b", super.toString(), this.isPassable());
	}
}
