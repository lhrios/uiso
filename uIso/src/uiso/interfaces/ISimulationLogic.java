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

package uiso.interfaces;

import uiso.Tile;
import uiso.UIsoEngine;

/**
 * With this class the user can be informed about some changes in the map and also restrict some operations. Note that while inside these methods is not valid to change
 * the engine state.
 * 
 * @author luis
 */
public interface ISimulationLogic {
	/* Public: */
	boolean canSetTileZ(UIsoEngine uiso_engine, Tile tile, int z);

	/**
	 * Informs user that a {@link Tile} z-coordinate has been changed.
	 * 
	 * @param uiso_engine
	 *           the {@link UIsoEngine} that owns the affected {@link Tile}
	 * @param tile
	 *           the affected {@link Tile}
	 * @param old_z
	 *           the previous z-coordinate value
	 */
	void informTileZUpdate(UIsoEngine uiso_engine, Tile tile, int old_z);

	/**
	 * Informs user that a {@link Tile} slope has been changed.
	 * 
	 * @param uiso_engine
	 *           the {@link UIsoEngine} that owns the affected {@link Tile}
	 * @param tile
	 *           the affected {@link Tile}
	 * @param old_slope
	 *           the previous {@link Tile} slope
	 */
	void informTileSlopeUpdate(UIsoEngine uiso_engine, Tile tile, int old_slope);
}
