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

import uiso.interfaces.ITileFactory;

class UIsoMap {
	/* Package: */
	Tile tiles[][];

	UIsoMap(int w, int h, int tile_max_z, ITileFactory tile_factory) {
		assert (0 < w && w + (tile_max_z << 1) + 1 <= 256 && 0 < h && h + (tile_max_z << 1) + 1 <= 256);

		this.tiles = new Tile[h + (tile_max_z << 1) + 1][w + (tile_max_z << 1) + 1];
		for (int y = 0; y <= (tile_max_z << 1) + h; y++) {
			for (int x = 0; x <= (tile_max_z << 1) + w; x++) {
				Tile tile;

				if (x > w + tile_max_z || y > h + tile_max_z || x < tile_max_z || y < tile_max_z) {
					tile = new Tile();
					tile.setVisibility(false);
				} else if (x == w + tile_max_z || y == h + tile_max_z) {
					tile = tile_factory.buildNewTile(x - tile_max_z, y - tile_max_z);
					tile.setVisibility(false);
				} else {
					tile = tile_factory.buildNewTile(x - tile_max_z, y - tile_max_z);
				}

				tile.setX(x);
				tile.setY(y);
				tile.setZ(0);

				this.tiles[y][x] = tile;
			}
		}
	}
}
