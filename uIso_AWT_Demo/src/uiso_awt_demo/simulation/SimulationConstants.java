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

import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import uiso.UIsoConfiguration;
import uiso_awt_demo.map.MyTileFactory;

public class SimulationConstants {

	public static final int CANVAS_W = 900;
	public static final int CANVAS_H = 600;

	public static final int UISO_ENGINE_VIEWPORT_BORDER_WIDTH = 5;
	public static final int UISO_ENGINE_VIEWPORT_DELTA_W = 50;
	public static final int UISO_ENGINE_VIEWPORT_DELTA_H = 35;

	public static final int MAP_W = 120;
	public static final int MAP_H = 100;

	public static final int TILE_VIRTUAL_SIZE = 16;
	public static final int SLOPE_HEIGHT = 8;

	public static final int VIEWPORT_MOVE_DELTA = 40;

	public static final Font DEFAULT_FONT = new Font("monospaced", Font.BOLD | Font.ITALIC, 20);
	public static final Color DEFAULT_TEXT_COLOR = Color.WHITE;
	public static final Color CLEAR_COLOR = Color.BLACK;
	public static final Color LINE_COLOR = Color.MAGENTA;
	public static final Color INFO_TEXT_COLOR = Color.CYAN;
	public static final Color ANIMATED_TEXT_COLOR = Color.YELLOW;
	public static final Color FPS_TEXT_COLOR = Color.WHITE;
	public static final Font INFO_TEXT_FONT = new Font("monospaced", Font.BOLD, 25);
	public static final Font ANIMATED_TEXT_FONT = new Font("serif", Font.BOLD, 25);

	public static final float FRAME_TIME = 1000.f / 30.f; /* mi1liseconds. */

	public static final int MIN_SLEEP_TIME = 5; /* mi1liseconds. */

	public static final int MOUNTAINS_CIRCLE_RADIOUS = 5;
	public static final int MOUNTAINS_INTERVAL = 3;
	public static final int MOUNTAINS_MIN_Z = 4;

	public static final UIsoConfiguration UISO_CONFIGURATION;

	public static final Rectangle EDITABLE_AREA = new Rectangle(0, 0, 20, 100);
	public static final Rectangle EMPTY_AREA = new Rectangle(20, 0, 20, 100);
	public static final Rectangle CASTLE_LAND_AREA = new Rectangle(40, 0, 80, 100);

	public final static String[] CASTLE_BLUEPRINT;

	static {
		UISO_CONFIGURATION = new UIsoConfiguration();
		UISO_CONFIGURATION.use_dirty_rectangle = false;
		UISO_CONFIGURATION.max_objects_in_the_scene = 200;
		UISO_CONFIGURATION.tile_h = 32;
		UISO_CONFIGURATION.tile_w = 64;
		UISO_CONFIGURATION.slope_height = SLOPE_HEIGHT;
		UISO_CONFIGURATION.w = SimulationConstants.MAP_W;
		UISO_CONFIGURATION.h = SimulationConstants.MAP_H;
		UISO_CONFIGURATION.tile_max_z = 15;
		UISO_CONFIGURATION.tile_factory = new MyTileFactory();

		UISO_CONFIGURATION.sprite_object_comparator = new SimulationLogic.MySpriteObjectComparator();
		UISO_CONFIGURATION.string_object_comparator = new SimulationLogic.MyStringObjectComparator();

		/* Loads castle blueprint. */
		BufferedReader bufferedReader = null;
		try {
			try {
				bufferedReader = new BufferedReader(new InputStreamReader(SimulationConstants.class.getClassLoader().getResourceAsStream("castle_blueprint.txt")));
				List<String> lines = new ArrayList<String>();
				String line;
				while (null != (line = bufferedReader.readLine())) {
					lines.add(line);
				}

				CASTLE_BLUEPRINT = new String[lines.size()];
				for (int i = 0; i < lines.size(); i++) {
					CASTLE_BLUEPRINT[i] = lines.get(i);
				}

			} finally {
				if (bufferedReader != null) {
					bufferedReader.close();
				}
			}

		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
}
