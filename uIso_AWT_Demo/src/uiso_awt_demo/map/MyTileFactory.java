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

package uiso_awt_demo.map;

import uiso.Tile;
import uiso.interfaces.ITileFactory;
import uiso_awt_demo.simulation.SimulationConstants;

public class MyTileFactory implements ITileFactory {
	/* Public: */

	@Override
	public Tile buildNewTile(int tile_x, int tile_y) {
		MyTile tile = new MyTile();

		if (SimulationConstants.CASTLE_LAND_AREA.contains(tile_x, tile_y)) {
			tile.setTileType(TileType.GRASS);
		} else if (SimulationConstants.EDITABLE_AREA.contains(tile_x, tile_y)) {
			tile.setTileType(TileType.BARE_GROUND);
		} else if (SimulationConstants.EMPTY_AREA.contains(tile_x, tile_y)) {
			tile.setVisibility(false);
		}

		return tile;
	}
}
