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

package uiso.util;

import uiso.Tile;
import uiso.UIsoEngine;
import uiso.interfaces.ISimulationLogic;

public class SimpleSimulationLogic implements ISimulationLogic {

	@Override
	public boolean canSetTileZ(UIsoEngine uiso_engine, Tile tile, int z) {
		return true;
	}

	@Override
	public void informTileZUpdate(UIsoEngine uiso_engine, Tile tile, int old_z) {
	}

	@Override
	public void informTileSlopeUpdate(UIsoEngine uiso_engine, Tile tile, int old_slope) {
	}

}
