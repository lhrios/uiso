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

import java.awt.Rectangle;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import uiso.UIsoEngine;
import uiso_awt_demo.map.MyTile;
import uiso_awt_demo.map.TileType;
import uiso_awt_demo.object.Wall.WallType;
import uiso_awt_demo.simulation.SimulationConstants;
import uiso_awt_demo.util.TerraformUtils;

public class CastleBuilder {

	public CastleBuilder(Rectangle rectangle, Alignment alignment_x, Alignment alignment_y, String[] blue_prints) {
		this.blue_prints = blue_prints;
		this.w = blue_prints.length;
		this.h = blue_prints[0].length();

		switch (alignment_x) {
			case CENTER_ALIGNMENT:
				this.base_x = (rectangle.width - this.w) / 2 + rectangle.x;
			break;
			case LEFT_ALIGNMENT:
				this.base_x = rectangle.x + (rectangle.width - 1) - (this.w - 1);
			break;
			case RIGHT_ALIGNMENT:
				this.base_x = rectangle.x;
			break;
		}

		switch (alignment_y) {
			case CENTER_ALIGNMENT:
				this.base_y = (rectangle.height - this.h) / 2 + rectangle.y;
			break;
			case LEFT_ALIGNMENT:
			/* TODO: implement. */
			break;
			case RIGHT_ALIGNMENT:
			/* TODO: implement. */
			break;
		}

		/* Sanity check: */
		for (int x = 0; x < this.w; x++) {
			assert (this.h == blue_prints[x].length());
		}

	}

	public void build(UIsoEngine uiso_engine) {
		for (int x = 0; x < this.w; x++) {
			for (int y = 0; y < this.h; y++) {
				char c = this.blue_prints[x].charAt(y);
				MyTile tile = (MyTile) uiso_engine.getTile(x + this.base_x, y + this.base_y);

				this.createWalls(x, y, c, uiso_engine, tile);
			}
		}

		this.especial_positions = Collections.unmodifiableMap(this.especial_positions);
	}

	public Map<Character, MyTile> getEspecialTilesMap() {
		return this.especial_positions;
	}

	/* Private: */
	private int w, h;
	private int base_x, base_y;
	private String[] blue_prints;
	private Map<Character, MyTile> especial_positions = new HashMap<Character, MyTile>();

	private void createWall(int x, int y, WallType wall_type, UIsoEngine uiso_engine) {
		Wall wall = new Wall();

		wall.setX((x + this.base_x) * SimulationConstants.TILE_VIRTUAL_SIZE + wall_type.getTilePositionOffsetX());
		wall.setY((y + this.base_y) * SimulationConstants.TILE_VIRTUAL_SIZE + wall_type.getTilePositionOffsetY());
		wall.setEnum(wall_type);
		uiso_engine.insertObject(wall);
	}

	private void createWalls(int x, int y, char c, UIsoEngine uiso_engine, MyTile tile) {
		switch (c) {
			case 'G':
				if (this.safeGetChar(x + 1, y) != ' ' && this.safeGetChar(x - 1, y) != ' ') {
					this.createWall(x, y, WallType.X_GATE_BEFORE, uiso_engine);
					this.createWall(x, y, WallType.X_GATE_AFTER, uiso_engine);
				} else {
					this.createWall(x, y, WallType.Y_GATE_BEFORE, uiso_engine);
					this.createWall(x, y, WallType.Y_GATE_AFTER, uiso_engine);
				}
			break;
			case '+':
				this.createWall(x, y, WallType.CROSS, uiso_engine);
				tile.setPassability(false);
			break;
			case '|':
				this.createWall(x, y, WallType.X, uiso_engine);
				tile.setPassability(false);
			break;
			case '-':
				this.createWall(x, y, WallType.Y, uiso_engine);
				tile.setPassability(false);
			break;
			default:
				if (c != 'O' && Character.isLetter(c)) {
					this.especial_positions.put(c, tile);
				}
			break;
		}

		if (c != 'O') {
			TerraformUtils.setAllTileCornersZ(0, uiso_engine, tile);
			this.adjustTile(x, y, c, uiso_engine, tile);
		}
	}

	private void adjustTile(int x, int y, char c, UIsoEngine uiso_engine, MyTile tile) {
		if (c == ' ' || c == '+') {
			tile.setTileType(TileType.CASTLE_FLOOR_FULL);
		} else {
			boolean neighbour_on_ne = this.safeGetChar(x + 1, y) != 'O';
			boolean neighbour_on_sw = this.safeGetChar(x - 1, y) != 'O';
			boolean neighbour_on_nw = this.safeGetChar(x, y + 1) != 'O';
			boolean neighbour_on_se = this.safeGetChar(x, y - 1) != 'O';

			if (neighbour_on_ne && neighbour_on_sw && neighbour_on_nw && neighbour_on_se) {
				tile.setTileType(TileType.CASTLE_FLOOR_FULL);
			} else if (!neighbour_on_ne) {
				tile.setTileType(TileType.CASTLE_FLOOR_NE);
			} else if (!neighbour_on_sw) {
				tile.setTileType(TileType.CASTLE_FLOOR_SW);
			} else if (!neighbour_on_nw) {
				tile.setTileType(TileType.CASTLE_FLOOR_NW);
			} else {
				tile.setTileType(TileType.CASTLE_FLOOR_SE);
			}
		}
	}

	private char safeGetChar(int x, int y) {
		if (0 <= x && x < this.w && 0 <= y && y < this.h) {
			return this.blue_prints[x].charAt(y);
		}
		return 'O'; /* Outside. */
	}
}
