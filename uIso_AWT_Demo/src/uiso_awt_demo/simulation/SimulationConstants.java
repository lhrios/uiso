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

package uiso_awt_demo.simulation;

import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;

import uiso.UIsoConfiguration;
import uiso_awt_demo.map.MyTileFactory;

public class SimulationConstants {

	public static final int CANVAS_W = 900;
	public static final int CANVAS_H = 600;

	public static final int UISO_ENGINE_VIEWPORT_BORDER_WIDTH = 5;
	public static final int UISO_ENGINE_VIEWPORT_DELTA_W = 50;
	public static final int UISO_ENGINE_VIEWPORT_DELTA_H = 35;

	public static final int MAP_W = 100;
	public static final int MAP_H = 100;

	public static final int TILE_VIRTUAL_SIZE = 32;

	public static final int VIEWPORT_MOVE_DELTA = 40;

	public static final Font DEFAULT_FONT = new Font("monospaced", Font.BOLD | Font.ITALIC, 20);
	public static final Color DEFAULT_TEXT_COLOR = Color.WHITE;
	public static final Color CLEAR_COLOR = Color.BLACK;
	public static final Color LINE_COLOR = Color.MAGENTA;
	public static final Color INFO_TEXT_COLOR = Color.CYAN;
	public static final Color FPS_TEXT_COLOR = Color.WHITE;
	public static final Font INFO_TEXT_FONT = new Font("monospaced", Font.BOLD, 25);

	public static final float FRAME_TIME = 1000.f / 30.f; /* mi1liseconds. */

	public static final int MIN_SLEEP_TIME = 5; /* mi1liseconds. */

	public static final int MOUNTAINS_CIRCLE_RADIOUS = 5;
	public static final int MOUNTAINS_INTERVAL = 3;
	public static final int MOUNTAINS_MIN_Z = 4;

	public static final UIsoConfiguration UISO_CONFIGURATION;

	public static final Rectangle EDITABLE_AREA = new Rectangle(0, 0, 20, 100);
	public static final Rectangle EMPTY_AREA = new Rectangle(20, 0, 20, 100);
	public static final Rectangle CASTLE_LAND_AREA = new Rectangle(40, 0, 60, 100);

	//@formatter:off
	public final static String[] CASTLE_BLUE_PRINTS = {
		"OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO+----+-----+----+OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO",
		"OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO|    |  M  |    |OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO",
		"OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO|    |     |    |OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO",
		"OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO|    +--G--+    |OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO",
		"OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO|               |OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO",
		"OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO|               |OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO",		
		"OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO|               |OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO",
		"OOOOOOOOOOOOOOOOOOOOOOOOOOO+---------+    +--G--+    +---------+OOOOOOOOOOOOOOOOOOOOOOOOOO",
		"OOOOOOOOOOOOOOOOOOOOOOOOOOO|         G    |     |    G         |OOOOOOOOOOOOOOOOOOOOOOOOOO",		
		"OOOOOOOOOOOOOOOOOOOOOOOOOOO|         |    |     |    |         |OOOOOOOOOOOOOOOOOOOOOOOOOO",
		"OOOOOOOOOOOOOOOOOOOOOOOOOOO|         +----+-----+----+         |OOOOOOOOOOOOOOOOOOOOOOOOOO",		
		"OOOOOOOOOOOOOOOOOOOOOOOOOOO|         |               |         |OOOOOOOOOOOOOOOOOOOOOOOOOO",
		"OOOOOOOOOOOOOOOOOOOOOOOOOOO|         |               |         |OOOOOOOOOOOOOOOOOOOOOOOOOO",		
		"OOOOOOOOOOOOOOOOOOOOOOOOOOO|         G               G         |OOOOOOOOOOOOOOOOOOOOOOOOOO",
		"OOOOOOOOOOOOOOOOOOOOOOOOOOO|         |               |         |OOOOOOOOOOOOOOOOOOOOOOOOOO",		
		"OOOOOOOOOOOOOOOOOOOOOOOOOOO|         |               |         |OOOOOOOOOOOOOOOOOOOOOOOOOO",
		"+-----+--------------------+---G-----+----+--G--+----+-----G---+-------------------+-----+",
		"|     |                                   |     |                                  |     |",
		"|     G                                   |     |                                  G     |",
		"|     |                                   |     |                                  |     |",
		"+-----+-------G-------+-------------------+     +------------------+-------G-------+-----+",
		"OOOOOO|               |                   |     |                  |               |OOOOOO",
		"OOOOOO|               |                   +--G--+                  |               |OOOOOO",
		"OOOOOO|               |                   |     |                  |               |OOOOOO",         
		"OOOOOO|               |                   |     |                  |               |OOOOOO",
		"OOOOOO|               |                   |     |                  |               |OOOOOO",
		"OOOOOO|               |                   |     |                  |               |OOOOOO",
		"OOOOOO|               |                   |     |                  |               |OOOOOO",
		"OOOOOO+--------+------+---------+         |     |         +--------+-----+---------+OOOOOO",
		"OOOOOOOOOOOOOOO|                |         |     |         |              |OOOOOOOOOOOOOOOO",
		"OOOOOOOOOOOOOOO|                |         +--G--+         |              |OOOOOOOOOOOOOOOO",         
		"OOOOOOOOOOOOOOO|                G         |     |         G              |OOOOOOOOOOOOOOOO",
		"OOOOOOOOOOOOOOO|                |         G     G         |              |OOOOOOOOOOOOOOOO",         
		"OOOOOOOOOOOOOOO|                |         |     |         |              |OOOOOOOOOOOOOOOO",
		"OOOOOOOOOOOOOOO|                +---------+     +---------+              |OOOOOOOOOOOOOOOO",         
		"+--------------+                |OOOOOOOOO|     |OOOOOOOOO|              +---------------+",
		"|              |                |OOOOOOOOO|     |OOOOOOOOO|              |               |",         
		"|              |                |OOOOOOOOO|     |OOOOOOOOO|              |               |",		
		"|              G                |OOOOOOOOO|     |OOOOOOOOO|              G               |",         
		"|              |                |OOOOOOOOO+--G--+OOOOOOOOO|              |               |",
		"|              |                |OOOOOOOOOOOOOOOOOOOOOOOOO|              |               |",
		"|              |                |OOOOOOOOOOOOOOOOOOOOOOOOO|              |               |",
		"+--------------+----------------+OOOOOOOOOOOOOOOOOOOOOOOOO+--------------+---------------+",
		"OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO",
		"OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO",
		"OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO",
		"OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO",		
		"OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO",
		"OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO"
	};
	//@formatter:on

	static {
		UISO_CONFIGURATION = new UIsoConfiguration();
		UISO_CONFIGURATION.use_dirty_rectangle = false;
		UISO_CONFIGURATION.max_objects_in_the_scene = 100;
		UISO_CONFIGURATION.debug = false;
		UISO_CONFIGURATION.tile_h = 64;
		UISO_CONFIGURATION.tile_w = 128;
		UISO_CONFIGURATION.w = SimulationConstants.MAP_W;
		UISO_CONFIGURATION.h = SimulationConstants.MAP_H;
		UISO_CONFIGURATION.tile_max_z = 10;
		UISO_CONFIGURATION.tile_factory = new MyTileFactory();
	}
}
