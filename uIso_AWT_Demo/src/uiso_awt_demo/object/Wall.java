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

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import uiso.Sprite;
import uiso_awt_demo.simulation.SimulationConstants;

public class Wall extends MySpriteObject<Wall.WallType> {
	/* Public: */
	public static enum WallType {
		//@formatter:off
		X(SimulationConstants.TILE_VIRTUAL_SIZE, 8, 96, Alignment.CENTER_ALIGNMENT, Alignment.CENTER_ALIGNMENT), 
		CROSS(SimulationConstants.TILE_VIRTUAL_SIZE, SimulationConstants.TILE_VIRTUAL_SIZE, 96, Alignment.CENTER_ALIGNMENT, Alignment.CENTER_ALIGNMENT), 
		Y(8, SimulationConstants.TILE_VIRTUAL_SIZE, 96, Alignment.CENTER_ALIGNMENT, Alignment.CENTER_ALIGNMENT),  
		X_GATE_BEFORE(8, 8, 96, Alignment.LEFT_ALIGNMENT, Alignment.CENTER_ALIGNMENT), 
		X_GATE_AFTER(8, 8, 96, Alignment.RIGHT_ALIGNMENT, Alignment.CENTER_ALIGNMENT), 
		Y_GATE_BEFORE(8, 8, 96, Alignment.CENTER_ALIGNMENT, Alignment.LEFT_ALIGNMENT), 
		Y_GATE_AFTER(8, 8, 96, Alignment.CENTER_ALIGNMENT, Alignment.RIGHT_ALIGNMENT);
		//@formatter:on

		private WallType(int w, int h, int l, Alignment x_alignment, Alignment y_alignment) {
			this.w = w;
			this.h = h;
			this.l = l;
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

		public void setBoundingBox(Sprite sprite) {
			sprite.setBoundingBoxOffsetZ(0);
			sprite.setBoundingBoxL(this.l);
			sprite.setBoundingBoxOffsetX(-2);
			sprite.setBoundingBoxOffsetY(-2);
			sprite.setBoundingBoxH(this.h - 4);
			sprite.setBoundingBoxW(this.w - 4);
		}

		private int w, h, l;
		private Alignment x_alignment, y_alignment;
	}

	public static Map<Integer, Sprite> createSprites() {
		Map<Integer, Sprite> sprites = Common.createSpritesFromImage("wall.png", 125, 157, Arrays.asList(WallType.values()), 1);

		Iterator<Sprite> i = sprites.values().iterator();
		WallType.X.setBoundingBox(i.next());
		WallType.CROSS.setBoundingBox(i.next());
		WallType.Y.setBoundingBox(i.next());
		WallType.X_GATE_AFTER.setBoundingBox(i.next());
		WallType.X_GATE_BEFORE.setBoundingBox(i.next());
		WallType.Y_GATE_AFTER.setBoundingBox(i.next());
		WallType.Y_GATE_BEFORE.setBoundingBox(i.next());

		return sprites;
	}

	public Wall() {
		this.setObjectType(ObjectType.WALL);
	}

	@Override
	public void update(int tick) {
	}

	@Override
	public WallType getEnumFromOrdinal(int ordinal) {
		return WallType.values()[ordinal];
	}
}
