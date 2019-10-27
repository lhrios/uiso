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

package uiso_awt_demo.object;

import java.util.Arrays;
import java.util.Map;

import uiso.Sprite;
import uiso.UIsoEngine;
import uiso_awt_demo.simulation.SimulationConstants;

public class Wall extends MySpriteObject<Wall.WallType> {
	/* Public: */
	public static enum WallType {

		//@formatter:off
		X(SimulationConstants.TILE_VIRTUAL_SIZE, 8, Alignment.CENTER_ALIGNMENT, Alignment.CENTER_ALIGNMENT), 
		CROSS(SimulationConstants.TILE_VIRTUAL_SIZE, SimulationConstants.TILE_VIRTUAL_SIZE, Alignment.CENTER_ALIGNMENT, Alignment.CENTER_ALIGNMENT), 
		Y(8, SimulationConstants.TILE_VIRTUAL_SIZE, Alignment.CENTER_ALIGNMENT, Alignment.CENTER_ALIGNMENT),  
		X_GATE_BEFORE(8, 8, Alignment.LEFT_ALIGNMENT, Alignment.CENTER_ALIGNMENT), 
		X_GATE_AFTER(8, 8, Alignment.RIGHT_ALIGNMENT, Alignment.CENTER_ALIGNMENT), 
		Y_GATE_BEFORE(8, 8, Alignment.CENTER_ALIGNMENT, Alignment.LEFT_ALIGNMENT), 
		Y_GATE_AFTER(8, 8, Alignment.CENTER_ALIGNMENT, Alignment.RIGHT_ALIGNMENT);
		//@formatter:on

		private WallType(int w, int h, Alignment x_alignment, Alignment y_alignment) {
			this.w = w;
			this.h = h;
			this.x_alignment = x_alignment;
			this.y_alignment = y_alignment;
		}

		public int getTilePositionOffsetX() {
			int offset = 0;
			switch (this.x_alignment) {
				case CENTER_ALIGNMENT:
					offset = (SimulationConstants.TILE_VIRTUAL_SIZE - this.w) / 2 + this.w - 1;
				break;
				case LEFT_ALIGNMENT:
					offset = SimulationConstants.TILE_VIRTUAL_SIZE - 1;
				break;
				case RIGHT_ALIGNMENT:
					offset = this.w;
				break;
				default:
					assert (false);
				break;
			}
			assert (0 <= offset && offset < SimulationConstants.TILE_VIRTUAL_SIZE);
			return offset;
		}

		public int getTilePositionOffsetY() {
			int offset = 0;
			switch (this.y_alignment) {
				case CENTER_ALIGNMENT:
					offset = (SimulationConstants.TILE_VIRTUAL_SIZE - this.h) / 2 + this.h - 1;
				break;
				case LEFT_ALIGNMENT:
					offset = SimulationConstants.TILE_VIRTUAL_SIZE - 1;
				break;
				case RIGHT_ALIGNMENT:
					offset = this.h;
				break;
				default:
					assert (false);
				break;
			}
			assert (0 <= offset && offset < SimulationConstants.TILE_VIRTUAL_SIZE);
			return offset;
		}

		/* Private: */
		private int w, h;
		private Alignment x_alignment, y_alignment;
	}

	public static Map<Integer, Sprite> createSprites() {
		Map<Integer, Sprite> sprites = Common.createSpritesFromImage("wall.png", 64, 157, Arrays.asList(WallType.values()), 1);
		return sprites;
	}

	public Wall() {
		this.setObjectType(ObjectType.WALL);
	}

	@Override
	public void update(UIsoEngine uiso_engine, int tick) {
	}

	@Override
	public WallType getEnumFromOrdinal(int ordinal) {
		return WallType.values()[ordinal];
	}
}
