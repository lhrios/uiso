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

package uiso;

import uiso.exceptions.InvalidConfigurationException;
import uiso.interfaces.IDrawer;
import uiso.interfaces.ISimulationLogic;
import uiso.interfaces.ITileFactory;
import uiso.interfaces.IUIsoObjectComparator;

/**
 * This class contains the configurations of the engine. It implements {@link Clonable} interface.
 * 
 * @author luis
 */
public class UIsoConfiguration {
	/* Public: */
	public boolean debug, use_dirty_rectangle;
	/** Viewport configuration: */
	public int viewport_w, viewport_h;
	/** Scene configuration: */
	public int max_objects_in_the_scene = 50, max_string_objects_in_the_scene = 10, max_sprites_per_tile = 2;
	/** Tile configuration: */
	public int tile_h, tile_w, slope_height;
	/** Map configuration: */
	public int w, h, tile_max_z;
	public IDrawer drawer;
	public ISimulationLogic simulation_logic;
	public ITileFactory tile_factory;
	public IUIsoObjectComparator sprite_object_comparator, string_object_comparator;

	@Override
	public java.lang.Object clone() throws CloneNotSupportedException {
		UIsoConfiguration o = (UIsoConfiguration) super.clone();
		o.debug = this.debug;
		o.use_dirty_rectangle = this.use_dirty_rectangle;

		o.max_sprites_per_tile = this.max_sprites_per_tile;
		o.viewport_w = this.viewport_w;
		o.viewport_h = this.viewport_h;
		o.max_objects_in_the_scene = this.max_objects_in_the_scene;
		o.max_string_objects_in_the_scene = this.max_string_objects_in_the_scene;

		o.tile_h = this.tile_h;
		o.tile_w = this.tile_w;
		o.slope_height = this.slope_height;

		o.w = this.w;
		o.h = this.h;
		o.tile_max_z = this.tile_max_z;

		o.drawer = this.drawer;
		o.simulation_logic = this.simulation_logic;
		o.tile_factory = this.tile_factory;

		o.sprite_object_comparator = this.sprite_object_comparator;
		o.string_object_comparator = this.string_object_comparator;

		return o;
	}

	/* Package: */
	void Validate() throws InvalidConfigurationException {
		if (this.tile_w <= 8 || this.tile_h <= 4 || this.tile_w % 4 != 0 || (this.tile_w >> 1) != this.tile_h)
			throw new InvalidConfigurationException("The tile dimensions are not valid.");

		if (0 > this.tile_max_z)
			throw new InvalidConfigurationException("The maximum z value of the tiles are not valid.");
		if (this.tile_max_z > 15)
			throw new InvalidConfigurationException("The maximum z value of the tiles is too big.");

		if (this.w <= 0 || this.h <= 0)
			throw new InvalidConfigurationException("The map dimensions are not valid.");
		if (this.w + (this.tile_max_z << 1) + 1 > 256 || this.h + (this.tile_max_z << 1) + 1 > 256)
			throw new InvalidConfigurationException("The map dimensions are too big.");
		if (this.slope_height <= 0)
			throw new InvalidConfigurationException("Tile slope heigth is invalid.");

		if (this.max_objects_in_the_scene < 0)
			throw new InvalidConfigurationException("The maximum number of objects in a scene is invalid.");
		if (this.max_string_objects_in_the_scene < 0)
			throw new InvalidConfigurationException("The maximum number of string objects in a scene is invalid.");

		if (this.drawer == null)
			throw new InvalidConfigurationException("No IDrawer object has been informed.");
		if (this.simulation_logic == null)
			throw new InvalidConfigurationException("No ISimulationLogic object has been informed.");
		if (this.tile_factory == null)
			throw new InvalidConfigurationException("No ITileFactory object has been informed.");
		if (this.viewport_w < 8 || this.viewport_h < 8)
			throw new InvalidConfigurationException("The viewport dimensions are invalid.");

		if (this.sprite_object_comparator == null)
			throw new InvalidConfigurationException("The IUIsoObjectComparator to sort SpriteSceneObjects is invalid.");
		if (this.string_object_comparator == null)
			throw new InvalidConfigurationException("The IUIsoObjectComparator to sort StringSceneObjects is invalid.");
	}
}
