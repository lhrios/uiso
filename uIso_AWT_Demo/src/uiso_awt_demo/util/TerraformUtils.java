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

package uiso_awt_demo.util;

import uiso.Tile;
import uiso.UIsoEngine;
import uiso_awt_demo.map.MyTile;
import uiso_awt_demo.simulation.SimulationConstants;

public class TerraformUtils {
	public static void addToTileCornersZ(int delta, UIsoEngine uiso_engine, MyTile tile) {
		int tile_x = uiso_engine.getTileX(tile);
		int tile_y = uiso_engine.getTileY(tile);
		int tile_z = uiso_engine.getTileZ(tile);
		int slope_index = tile.getSlopeIndex();

		tile_z += Tile.min_z_difference_relative_to_tile_z[slope_index];

		//@formatter:off
		setTileHeight(uiso_engine, tile
				, tile_z + Tile.corner_n_z_relative_to_min_z[slope_index] + delta);
		setTileHeight(uiso_engine, uiso_engine.getTile(tile_x + 0, tile_y + 1)
				, tile_z + Tile.corner_e_z_relative_to_min_z[slope_index] + delta);
		setTileHeight(uiso_engine, uiso_engine.getTile(tile_x + 1, tile_y + 0)
				, tile_z + Tile.corner_w_z_relative_to_min_z[slope_index] + delta);
		setTileHeight(uiso_engine, uiso_engine.getTile(tile_x + 1, tile_y + 1)
				, tile_z + Tile.corner_s_z_relative_to_min_z[slope_index] + delta);
		//@formatter:on
	}

	public static void setAllTileCornersZ(int tile_z, UIsoEngine uiso_engine, MyTile tile) {
		int tile_x = uiso_engine.getTileX(tile);
		int tile_y = uiso_engine.getTileY(tile);

		setTileHeight(uiso_engine, tile, tile_z);
		setTileHeight(uiso_engine, uiso_engine.getTile(tile_x + 0, tile_y + 1), tile_z);
		setTileHeight(uiso_engine, uiso_engine.getTile(tile_x + 1, tile_y + 0), tile_z);
		setTileHeight(uiso_engine, uiso_engine.getTile(tile_x + 1, tile_y + 1), tile_z);
	}

	public static void setTileHeight(UIsoEngine uiso_engine, Tile tile, int tile_z) {
		if (tile_z >= 0 && tile_z <= SimulationConstants.UISO_CONFIGURATION.tile_max_z) {
			uiso_engine.setTileZ(tile, tile_z);
		}
	}
}
