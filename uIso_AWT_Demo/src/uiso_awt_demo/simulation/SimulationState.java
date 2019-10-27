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

package uiso_awt_demo.simulation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import uiso.Point;
import uiso.StringObject;
import uiso.Tile;
import uiso.UIsoEngine;
import uiso_awt_demo.map.MyTile;
import uiso_awt_demo.object.Alignment;
import uiso_awt_demo.object.AnimatedString;
import uiso_awt_demo.object.CastleBuilder;
import uiso_awt_demo.object.Minotaur;
import uiso_awt_demo.util.RandomUtils;

class SimulationState {
	/* Public: */

	public SimulationState(UIsoEngine uiso_engine) {

		/* Random generates scenario tile heights. */
		for (int y = SimulationConstants.EDITABLE_AREA.y; y <= SimulationConstants.EDITABLE_AREA.y + SimulationConstants.EDITABLE_AREA.height; y += 4) {
			for (int x = SimulationConstants.EDITABLE_AREA.x; x <= SimulationConstants.EDITABLE_AREA.y + SimulationConstants.EDITABLE_AREA.width; x += 4) {
				int z = this.random.nextInt(SimulationConstants.UISO_CONFIGURATION.tile_max_z + 1);
				Tile tile = uiso_engine.getTile(x, y);
				if (uiso_engine.canSetTileZ(tile, z))
					uiso_engine.setTileZ(tile, z);
			}
		}

		/* Generate the mountains behind the castle. */
		{
			Point p = new Point();

			for (int y = SimulationConstants.CASTLE_LAND_AREA.y; y <= SimulationConstants.CASTLE_LAND_AREA.height; y++) {
				int x_center =
						(int) Math.round(Math.sin((y + SimulationConstants.CASTLE_LAND_AREA.height * 2.5f) / (SimulationConstants.CASTLE_LAND_AREA.height * 2f) * Math.PI)
								* SimulationConstants.CASTLE_LAND_AREA.width + SimulationConstants.CASTLE_LAND_AREA.x + SimulationConstants.CASTLE_LAND_AREA.width);

				if (y % SimulationConstants.MOUNTAINS_INTERVAL == 0) {
					int z =
							SimulationConstants.MOUNTAINS_MIN_Z
									+ this.random.nextInt(SimulationConstants.UISO_CONFIGURATION.tile_max_z - SimulationConstants.MOUNTAINS_MIN_Z + 1);

					RandomUtils.randomPointInsideCircle(x_center, y, SimulationConstants.MOUNTAINS_CIRCLE_RADIOUS, this.random, p);
					p.x = UIsoEngine.clamp(0, SimulationConstants.MAP_W, p.x);
					p.y = UIsoEngine.clamp(0, SimulationConstants.MAP_H, p.y);
					uiso_engine.setTileZ(uiso_engine.getTile(p), z);
				}

			}

			/* Make some tiles, behind the mountains, invisible. */
			List<Tile> tiles = new ArrayList<Tile>();
			for (int y = SimulationConstants.CASTLE_LAND_AREA.y; y <= SimulationConstants.CASTLE_LAND_AREA.height; y++) {
				int x_center =
						(int) Math.round(Math.sin((y + SimulationConstants.CASTLE_LAND_AREA.height * 2.5f) / (SimulationConstants.CASTLE_LAND_AREA.height * 2f) * Math.PI)
								* SimulationConstants.CASTLE_LAND_AREA.width + SimulationConstants.CASTLE_LAND_AREA.x + SimulationConstants.CASTLE_LAND_AREA.width);

				for (int x = SimulationConstants.CASTLE_LAND_AREA.x; x <= x_center
						&& x <= SimulationConstants.CASTLE_LAND_AREA.x + SimulationConstants.CASTLE_LAND_AREA.width; x++) {
					Tile tile = uiso_engine.getTile(x, y);
					if (tile.getSlope() == Tile.FLAT && uiso_engine.getTileZ(tile) == 0) {
						tiles.add(tile);
					} else {
						break;
					}
				}

				if (tiles.size() > 0) {
					Tile last_tile_after_mountains = tiles.get(tiles.size() - 1);
					Tile first_mountain_tile = uiso_engine.getTile(uiso_engine.getTileX(last_tile_after_mountains) + 1, y);
					/* Is it before a mountain ? */
					if (first_mountain_tile.getSlope() != Tile.FLAT) {
						for (Tile tile : tiles) {
							tile.setVisibility(false);
						}
					}
				}

				tiles.clear();
			}
		}

		StringObject string_object = new StringObject();
		string_object.setString("Editable area\nUse left and right mouse buttons to change terrain height");
		string_object.setX((SimulationConstants.EDITABLE_AREA.x + SimulationConstants.EDITABLE_AREA.width) * SimulationConstants.TILE_VIRTUAL_SIZE);
		string_object.setY((SimulationConstants.EDITABLE_AREA.y + SimulationConstants.EDITABLE_AREA.height) / 2 * SimulationConstants.TILE_VIRTUAL_SIZE);
		string_object.setColor(SimulationConstants.INFO_TEXT_COLOR);
		string_object.setFont(SimulationConstants.INFO_TEXT_FONT);
		uiso_engine.insertObject(string_object);

		CastleBuilder castle_builder =
				new CastleBuilder(SimulationConstants.CASTLE_LAND_AREA, Alignment.LEFT_ALIGNMENT, Alignment.CENTER_ALIGNMENT, SimulationConstants.CASTLE_BLUEPRINT);
		castle_builder.build(uiso_engine);
		MyTile minotaur_initial_tile = castle_builder.getEspecialTilesMap().get('M');

		this.minotaur = new Minotaur();
		this.minotaur.setX(uiso_engine.getTileX(minotaur_initial_tile) * SimulationConstants.TILE_VIRTUAL_SIZE + Minotaur.TILE_OFFSET_X);
		this.minotaur.setY(uiso_engine.getTileY(minotaur_initial_tile) * SimulationConstants.TILE_VIRTUAL_SIZE + Minotaur.TILE_OFFSET_Y);
		uiso_engine.insertObject(this.minotaur);

		MyTile castle_entrance_tile = castle_builder.getEspecialTilesMap().get('E');
		this.castle_entrance = new AnimatedString("Castle Entrance");
		this.castle_entrance.setFont(SimulationConstants.ANIMATED_TEXT_FONT);
		this.castle_entrance.setColor(SimulationConstants.ANIMATED_TEXT_COLOR);
		this.castle_entrance.setX(uiso_engine.getTileX(castle_entrance_tile) * SimulationConstants.TILE_VIRTUAL_SIZE);
		this.castle_entrance.setY(uiso_engine.getTileY(castle_entrance_tile) * SimulationConstants.TILE_VIRTUAL_SIZE);
		uiso_engine.insertObject(this.castle_entrance);

		uiso_engine.scrollToTile(minotaur_initial_tile);
	}

	public AnimatedString castle_entrance;
	public Minotaur minotaur;
	public Random random = new Random();
	public int tick;
}