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

import uiso.exceptions.InvalidTileCoordinatesException;
import uiso.interfaces.IDrawer;
import uiso.interfaces.ISimulationLogic;
import uiso.util.MathUtils;

/**
 * This is the main uIsometric Engine class. It is used to:
 * <ul>
 * <li>Scroll the viewport.</li>
 * <li>Add/remove an object from simulation.</li>
 * <li>Update an object position.</li>
 * <li>Change tile z-coordinate.</li>
 * <li>Get tile virtual coordinates (x, y and z).</li>
 * <li>Draw the scene.</li>
 * </ul>
 * <p>
 * Note that uIsometric Engine (all classes) is not thread-safe. It is not reentrant also (i.e, its state can not be changed while the control flow is executing one of
 * its methods).
 * <p>
 * Real coordinates axis:
 * 
 * <pre>
 *    |
 * ---+-----------------------------------------> (x-axis)
 *    |
 *    |
 *    |
 *    |
 *   \|/ (y-axis)
 * </pre>
 * Virtual coordinates axis:
 * 
 * <pre>
 * 
 *                  | z-axis
 *                  |
 *                  |
 *                 / \
 *               /     \
 *             / the map \  
 *           / \         / \
 *         /     \     /     \
 * x-axis/         \ /         \y-axis
 *             
 * </pre>
 * 
 * @author luis
 */
public class UIsoEngine {
	/* Public: */
	public static boolean has_clamped, clamped_to_min, clamped_to_max;

	public final static int clamp(int min, int max, int value) {
		if (value < min)
			return min;
		if (value > max)
			return max;
		return value;
	}

	public final static int clampAndNotify(int min, int max, int value) {
		has_clamped = true;
		clamped_to_min = clamped_to_max = false;
		if (value < min) {
			clamped_to_min = true;
			return min;
		}
		if (value > max) {
			clamped_to_max = true;
			return max;
		}
		has_clamped = false;
		return value;
	}

	/**
	 * Tile (returned by {@link #getTileFromRealCoordinates(Point, Point)}) position relative to map polygon. The possible values are combinations of:
	 * {@link UIsoConstants#ABOVE_WN_LINE}, {@link UIsoConstants#BELOW_ES_LINE}, {@link UIsoConstants#ABOVE_NE_LINE}, {@link UIsoConstants#BELOW_SW_LINE} and
	 * {@link UIsoConstants#INSIDE_POLYGON}.
	 * 
	 * @see ObjectsGridManager
	 */
	public int tile_position_relative_map_polygon;

	public UIsoEngine(UIsoConfiguration configuration) {
		configuration.Validate();

		/* Copy and/or transform some configurations. */
		this.debug = configuration.debug;
		this.tile_h = configuration.tile_h;
		this.tile_w = configuration.tile_w;
		this.tile_max_z = configuration.tile_max_z;
		this.slope_height = configuration.slope_height;
		this.virtual_world_tile_size = (configuration.tile_w >> 2);
		this.map = new UIsoMap(this.w = configuration.w, this.h = configuration.h, this.tile_max_z, configuration.tile_factory);
		this.maping_helper = new MapingHelper(this.tile_w, this.tile_h, this.virtual_world_tile_size, this.slope_height);
		this.real_w = this.w + (this.tile_max_z << 1) - 1;
		this.real_h = this.h + (this.tile_max_z << 1) - 1;

		this.affected_tiles = this.tile_max_z > 0 ? new Tile[(this.tile_max_z * this.tile_max_z) << 2] : null;

		this.use_dirty_rectangle_system = configuration.use_dirty_rectangle;
		this.viewport_h = configuration.viewport_h;
		this.viewport_h_half = (this.viewport_h >> 1);
		this.viewport_w = configuration.viewport_w;
		this.viewport_w_half = (this.viewport_w >> 1);
		this.drawer = configuration.drawer;
		this.simulation_logic = configuration.simulation_logic;
		this.real_coordinates = new Point();
		this.virtual_coordinates = new Point();
		this.point = new Point();
		this.viewport_point = new Point();
		this.viewport_center = new Point();
		this.string_bounds = new Rectangle();
		this.sprites = new Sprite[configuration.max_sprites_per_tile + 1];

		this.viewport_center.x = this.tile_max_z * this.virtual_world_tile_size;
		this.viewport_center.y = this.tile_max_z * this.virtual_world_tile_size;
		toRealCoordinates(this.viewport_center, this.real_coordinates);
		this.viewport_offset_x = this.real_coordinates.x - this.viewport_w_half;
		this.viewport_offset_y = this.real_coordinates.y - this.viewport_h_half;

		this.objects_grid_manager = new ObjectsGridManager(this);

		this.scene_objects_manager =
				new SceneObjectsManager(this, configuration.max_objects_in_the_scene, configuration.max_string_objects_in_the_scene, configuration.sprite_object_comparator,
						configuration.string_object_comparator);

		/* Dirty rectangle system. */
		// this.scene_rectangle_manager = new SceneRectangleManager(configuration);
	}

	/**
	 * Tells wherever or not the informed coordinates corresponds to an existing tile.
	 * 
	 * @param x
	 *           the tile x-coordinate
	 * @param y
	 *           the tile y-coordinate
	 * @return {@code true} if {@code x} and {@code y} are coordinates of an existing tile
	 */
	public boolean isValidTileCoordinates(int x, int y) {
		return 0 <= x && x <= this.w && 0 <= y && y <= this.h;
	}

	/**
	 * Causes the engine to draw the current scene based on its current state. Three layers will be drawn (in this order):
	 * <ul>
	 * <li>{@link Tile}'s layer.</li>
	 * <li>{@link SpriteObject}'s layer.</li>
	 * <li>{@link StringObject}'s layer.</li>
	 * </ul>
	 * 
	 */
	public void draw() {
		int min_x, min_y, max_x, max_y;
		Tile tile;

		this.drawer.beginDrawing(this);

		/* Use the dirty rectangle system. */
		if (this.use_dirty_rectangle_system) {
			/* It is not ready yet. */

			/* Without dirty rectangle system. */
		} else {
			this.drawer.setClip(0, 0, this.viewport_w, this.viewport_h);
			this.drawer.clear();

			/* As the mapping has an error of +-2 pixels we need to consider this. */
			this.viewport_point.x = -2;
			this.viewport_point.y = -2;
			tile = this.internalGetTileFromRealCoordinates(this.viewport_point, null);
			min_y = tile.getY();
			if (min_y < this.tile_max_z)
				min_y = this.tile_max_z;

			this.viewport_point.x = this.viewport_w - 1 + 2;
			tile = this.internalGetTileFromRealCoordinates(this.viewport_point, null);
			min_x = tile.getX();
			if (min_x < this.tile_max_z)
				min_x = this.tile_max_z;

			this.viewport_point.y = this.viewport_h - 1 + 2;
			tile = this.internalGetTileFromRealCoordinates(this.viewport_point, null);
			max_y = tile.getY();
			if (max_y >= this.h + this.tile_max_z)
				max_y = (this.h + this.tile_max_z - 1);

			this.viewport_point.x = 0 - 2;
			tile = this.internalGetTileFromRealCoordinates(this.viewport_point, null);
			max_x = tile.getX();
			if (max_x >= this.w + this.tile_max_z)
				max_x = (this.w + this.tile_max_z - 1);

			/* Draw the tiles. */
			for (int y = min_y; y <= max_y; y++) {
				for (int x = min_x; x <= max_x; x++) {
					tile = this.map.tiles[y][x];
					if (tile.isVisible())
						this.drawTile(tile);
				}
			}

			/* Draw the objects. */
			this.drawObjects();

			/* Debug: */
			if (this.debug) {
				this.objects_grid_manager.drawObjectsGrid();
				this.drawCentralCross();
				//@formatter:off
				this.drawer.drawString(0, 0, String.format("[%4d,%4d] [%4d,%4d]\n[%4d,%4d] [%4d,%4d]\n[%4d,%4d]"
						, this.viewport_offset_x, this.viewport_offset_y
						, this.viewport_offset_x + this.viewport_w - 1, this.viewport_offset_y
						, this.viewport_offset_x, this.viewport_offset_y + this.viewport_h - 1
						, this.viewport_offset_x + this.viewport_w - 1, this.viewport_offset_y + this.viewport_h - 1
						, this.viewport_center.x, this.viewport_center.y));
				//@formatter:on
			}
		}

		/* Debug: */
		// {
		// this.virtual_coordinates.x = this.virtual_world_tile_size - 1;
		// this.virtual_coordinates.y = this.h * this.virtual_world_tile_size - 1;
		// this.virtual_coordinates.z = 0;
		// this.drawPoint(this.virtual_coordinates);
		// }

		this.drawer.endDrawing();

		/* Dirty rectangle system. */
		// this.last_viewport_offset_x = this.viewport_offset_x;
		// this.last_viewport_offset_y = this.viewport_offset_y;
	}

	/**
	 * Tells wherever or not a tile z-coordinate can be changed to the informed value. It will consider the tile neighbors constraints and the values returned by
	 * {@link ISimulationLogic#canSetTileZ(UIsoEngine, Tile, int)}.
	 * 
	 * @param tile
	 *           the tile that will have its z-coordinate changed
	 * @param z
	 *           the new z-coordinate value
	 * @return {@code true} if tile z-coordinate can be changed to desired value
	 */
	public boolean canSetTileZ(Tile tile, int z) {
		boolean can_set_z;
		int new_z = clamp(0, this.tile_max_z, z), x = tile.getX(), y = tile.getY();

		this.n_affected_tiles = 0;
		can_set_z = new_z == tile.getZ() ? true : this.internalCanSetTileZ(tile, new_z);
		for (int i = 0; i < Tile.N_NEIGHBORS && can_set_z; i++) {
			int neighbour_x = x + Tile.neighbour_x_offset[i];
			int neighbour_y = y + Tile.neighbour_y_offset[i];

			if (0 <= neighbour_x && neighbour_x <= this.real_w && 0 <= neighbour_y && neighbour_y <= this.real_h) {
				Tile neighbour_tile = this.map.tiles[neighbour_y][neighbour_x];
				int difference = (new_z - neighbour_tile.getZ());
				boolean increasing = difference > 0;

				if (difference >= 2 && increasing) {
					can_set_z = can_set_z && this.recursiveSetTileZ(neighbour_tile, new_z - 1, increasing, Tile.neighbour_direction[i], true);
				} else if (difference <= -2 && !increasing) {
					can_set_z = can_set_z && this.recursiveSetTileZ(neighbour_tile, new_z + 1, increasing, Tile.neighbour_direction[i], true);
				}
			}
		}
		return can_set_z;
	}

	public final int getAbsoluteHeightOfPointInTileSlopeSurface(Tile tile, int x, int y) {
		return this.getRelativeHeightOfPointInSlopeSurface(tile.getSlope(), x, y) + this.getTileMinZ(tile) * this.slope_height;
	}

	/**
	 * Gets the virtual height (z-coordinate) of a point in the slope surface. The returned value is relative to minimum slope height.
	 * 
	 * @param slope
	 *           one of the possible slopes
	 * @param x
	 *           the virtual x-coordinate relative to slope origin
	 * @param y
	 *           the virtual y-coordinate relative to slope origin
	 * @return the height of the point informed relative to minimum slope height
	 * @throws IllegalArgumentException
	 *            if the point informed (using relative coordinates) is not inside slope/tile boundaries
	 */
	public final int getRelativeHeightOfPointInSlopeSurface(int slope, int x, int y) throws IllegalArgumentException {
		if (x < 0 || x >= this.virtual_world_tile_size || y < 0 || y >= this.virtual_world_tile_size)
			throw new IllegalArgumentException("The point [" + x + "," + y + "] is not inside tile.");
		float h = this.slope_height;
		float l = this.virtual_world_tile_size;
		float a = l / h;
		switch (slope) {
			case Tile.FLAT:
				return 0;

			case Tile.NE:
				return MathUtils.round(h - x / a);

			case Tile.SW:
				return MathUtils.round(x / a);

			case Tile.ES:
				return MathUtils.round(y / a);

			case Tile.WN:
				return MathUtils.round(h - y / a);

			case Tile.N:
				return MathUtils.round(x + y >= l ? 0 : (l * h - x * h - h * y) / l);

			case Tile.S:
				return MathUtils.round(x + y <= l ? 0 : (l * h - x * h - h * y) / -l);

			case Tile.W:
				return MathUtils.round(x - y < 0 ? 0 : (h * x - h * y) / l);

			case Tile.E:
				return MathUtils.round(x - y > 0 ? 0 : (h * x - h * y) / -l);

			case Tile.WE:
				return MathUtils.round(x + y >= l ? (h * x + h * y - 2 * h * l) / -l : (h * x + h * y) / l);

			case Tile.NS:
				return MathUtils.round(x - y < 0 ? (l * h - h * y + h * x) / l : (l * h - h * x + h * y) / l);

			case Tile.NES:
				return MathUtils.round(x - y <= 0 ? h : (l * h - h * x + h * y) / l);

			case Tile.SWN:
				return MathUtils.round(x - y >= 0 ? h : (l * h - h * y + h * x) / l);

			case Tile.ESW:
				return MathUtils.round(x + y <= l ? (h * x + h * y) / l : h);

			case Tile.WNE:
				return MathUtils.round(x + y <= l ? h : (h * x + h * y - 2 * h * l) / -l);

			default:
				assert (false);
		}
		return 0;
	}

	/**
	 * Returns the tile under the real coordinates (on the viewport). It is not exactly and has an error of +/- 2 pixels. There are two main reasons: the approximation in
	 * the calculations that does not employ floating point numbers and the usage of equations to represent the polygon that defines tile slope boundaries.
	 * 
	 * @param real_coordinates
	 *           the viewport (2D) point coordinates
	 * @param fine_coordinates
	 *           if not null, will receive the virtual coordinates inside the tile
	 * @return the tile under the viewport (2D) point informed
	 */
	public Tile getTileFromRealCoordinates(Point real_coordinates, Point fine_coordinates) {
		int x, y, clamped_x, clamped_y;
		Tile tile = this.internalGetTileFromRealCoordinates(real_coordinates, fine_coordinates);
		x = tile.getX();
		y = tile.getY();

		clamped_y = clampAndNotify(this.tile_max_z, (this.h + this.tile_max_z - 1), y);
		if (clamped_to_min) {
			this.tile_position_relative_map_polygon |= UIsoConstants.ABOVE_WN_LINE;
		} else if (clamped_to_max) {
			this.tile_position_relative_map_polygon |= UIsoConstants.BELOW_ES_LINE;
		}

		clamped_x = clampAndNotify(this.tile_max_z, (this.w + this.tile_max_z - 1), x);
		if (clamped_to_min) {
			this.tile_position_relative_map_polygon |= UIsoConstants.ABOVE_NE_LINE;
		} else if (clamped_to_max) {
			this.tile_position_relative_map_polygon |= UIsoConstants.BELOW_SW_LINE;
		}

		return this.map.tiles[clamped_y][clamped_x];
	}

	public Tile getTile(Point p) throws InvalidTileCoordinatesException {
		return this.getTile(p.x, p.y);
	}

	public Tile getTile(int x, int y) throws InvalidTileCoordinatesException {
		if (!this.isValidTileCoordinates(x, y))
			throw new InvalidTileCoordinatesException("There is no tile with the following coordinates: [" + x + "," + y + "].");
		return this.map.tiles[y + this.tile_max_z][x + this.tile_max_z];
	}

	public int getTileX(UIsoObject o) {
		int x = o.getX();
		if (x >= 0) {
			return x / this.virtual_world_tile_size;
		} else {
			return (x / this.virtual_world_tile_size) - 1;
		}
	}

	public int getTileY(UIsoObject o) {
		int y = o.getY();
		if (y >= 0) {
			return y / this.virtual_world_tile_size;
		} else {
			return (y / this.virtual_world_tile_size) - 1;
		}
	}

	public int getTileX(Tile tile) {
		return tile.getX() - this.tile_max_z;
	}

	public int getTileY(Tile tile) {
		return tile.getY() - this.tile_max_z;
	}

	public int getTileZ(Tile tile) {
		return tile.getZ();
	}

	public int getTileMinZ(Tile tile) {
		return tile.getZ() + Tile.min_z_difference_relative_to_tile_z[tile.getSlopeIndex()];
	}

	public void getVirtualViewportCenterCoordinates(Point p) {
		this.real_coordinates.x = this.viewport_offset_x + this.viewport_w_half;
		this.real_coordinates.y = this.viewport_offset_y + this.viewport_h_half;
		toVirtualCoordinates(p, this.real_coordinates);
		p.x -= this.tile_max_z * this.virtual_world_tile_size;
		p.y -= this.tile_max_z * this.virtual_world_tile_size;
	}

	public void insertObject(UIsoObject object) {
		this.informObjectMotion(object);
	}

	public void informObjectSizeChange(UIsoObject object) {
		this.removeObject(object);
		this.insertObject(object);
	}

	public void informObjectMotion(UIsoObject object) {
		int nw_x, nw_y, ne_x, ne_y, ws_x, ws_y, es_x, es_y;
		UIsoObjectsGridCell nw_cell, ne_cell, es_cell, ws_cell;

		this.virtual_coordinates.x = object.getX() + this.tile_max_z * this.virtual_world_tile_size;
		this.virtual_coordinates.y = object.getY() + this.tile_max_z * this.virtual_world_tile_size;
		this.virtual_coordinates.z = object.getZ();
		toRealCoordinates(this.virtual_coordinates, this.real_coordinates);

		if (object instanceof SpriteObject) {
			UIsoImage image;
			Sprite sprite;

			this.drawer.getObjectSprite((SpriteObject) object, this.sprites);
			sprite = this.sprites[0];
			if (sprite == null)
				return;

			image = sprite.image;
			if (this.debug)
				this.objects_grid_manager.checkObjectLimits(image.getW(), image.getH());

			ws_x = nw_x = this.real_coordinates.x - sprite.getAnchorX();
			ne_y = nw_y = this.real_coordinates.y - sprite.getAnchorY();
			ne_x = es_x = nw_x + image.getW();
			ws_y = es_y = nw_y + image.getH();
		} else {
			StringObject string_object = (StringObject) object;
			this.drawer.getStringBounds(string_object.getString(), this.string_bounds, string_object.getFont());
			if (this.debug)
				this.objects_grid_manager.checkObjectLimits(this.string_bounds.w, this.string_bounds.h);

			ws_x = nw_x = this.real_coordinates.x - (this.string_bounds.w >> 1);
			ne_y = nw_y = this.real_coordinates.y - (this.string_bounds.h >> 1);
			ne_x = es_x = nw_x + this.string_bounds.w;
			ws_y = es_y = nw_y + this.string_bounds.h;
		}

		nw_cell = this.objects_grid_manager.getObjectsGridCellAndCellCoordinates(nw_x, nw_y, null);
		ne_cell = this.objects_grid_manager.getObjectsGridCellAndCellCoordinates(ne_x, ne_y, null);
		ws_cell = this.objects_grid_manager.getObjectsGridCellAndCellCoordinates(ws_x, ws_y, null);
		es_cell = this.objects_grid_manager.getObjectsGridCellAndCellCoordinates(es_x, es_y, null);

		object.removeObjectFromLinkedList(UIsoObject.NW_VERTEX);
		object.removeObjectFromLinkedList(UIsoObject.NE_VERTEX);
		object.removeObjectFromLinkedList(UIsoObject.WS_VERTEX);
		object.removeObjectFromLinkedList(UIsoObject.ES_VERTEX);

		if (nw_cell != null)
			nw_cell.insertObject(object, UIsoObject.NW_VERTEX);
		if (ne_cell != null && nw_cell != ne_cell)
			ne_cell.insertObject(object, UIsoObject.NE_VERTEX);
		if (es_cell != null && nw_cell != es_cell && ne_cell != es_cell)
			es_cell.insertObject(object, UIsoObject.ES_VERTEX);
		if (ws_cell != null && nw_cell != ws_cell && ne_cell != ws_cell && es_cell != ws_cell)
			ws_cell.insertObject(object, UIsoObject.WS_VERTEX);
	}

	public void removeObject(UIsoObject object) {
		object.removeObjectFromLinkedList(UIsoObject.NW_VERTEX);
		object.removeObjectFromLinkedList(UIsoObject.NE_VERTEX);
		object.removeObjectFromLinkedList(UIsoObject.WS_VERTEX);
		object.removeObjectFromLinkedList(UIsoObject.ES_VERTEX);
	}

	/**
	 * @param delta
	 *           real coordinates delta
	 */
	public void scrollViewportCenterWithRealCoordinatesDelta(Point delta) {
		/* Debug: */
		// int viewport_offset_x_before = this.viewport_offset_x;
		// int viewport_offset_y_before = this.viewport_offset_y;

		boolean has_clamped;
		this.real_coordinates.x = this.viewport_offset_x + this.viewport_w_half;
		this.real_coordinates.y = this.viewport_offset_y + this.viewport_h_half;
		toVirtualCoordinates(this.viewport_center, this.real_coordinates);
		int viewport_center_x_before = this.viewport_center.x;
		int viewport_center_y_before = this.viewport_center.y;

		this.viewport_offset_x += delta.x;
		this.viewport_offset_y += delta.y;
		this.real_coordinates.x += delta.x;
		this.real_coordinates.y += delta.y;
		toVirtualCoordinates(this.viewport_center, this.real_coordinates);

		this.viewport_center.x =
				clampAndNotify((this.tile_max_z * this.virtual_world_tile_size), ((this.tile_max_z + this.w) * this.virtual_world_tile_size) - 1, this.viewport_center.x);
		has_clamped = UIsoEngine.has_clamped;
		this.viewport_center.y =
				clampAndNotify((this.tile_max_z * this.virtual_world_tile_size), ((this.tile_max_z + this.h) * this.virtual_world_tile_size) - 1, this.viewport_center.y);
		has_clamped = has_clamped || UIsoEngine.has_clamped;
		this.viewport_center.z = 0;

		if (has_clamped) {
			toRealCoordinates(this.viewport_center, this.real_coordinates);

			if (this.viewport_center.x == viewport_center_x_before && this.viewport_center.y == viewport_center_y_before) {
				this.viewport_offset_x -= delta.x;
				this.viewport_offset_y -= delta.y;
			} else {
				this.viewport_offset_x = this.real_coordinates.x - this.viewport_w_half;
				this.viewport_offset_y = this.real_coordinates.y - this.viewport_h_half;
			}
		}
		/* Debug: */
		// System.out.printf("%f\n", Math
		// .sqrt(Math.pow(this.viewport_offset_x - viewport_offset_x_before, 2) + Math.pow(this.viewport_offset_y - viewport_offset_y_before, 2)));

		assert this.objects_grid_manager.isViewportPositionValid(this.viewport_offset_x, this.viewport_offset_y);
	}

	/**
	 * @param delta
	 *           virtual coordinates delta
	 */
	public void scrollViewportCenterWithVirtualCoordinatesDelta(Point delta) {
		this.viewport_center.x += delta.x;
		this.viewport_center.y += delta.y;

		this.viewport_center.x =
				clamp((this.tile_max_z * this.virtual_world_tile_size), ((this.tile_max_z + this.w) * this.virtual_world_tile_size) - 1, this.viewport_center.x);
		this.viewport_center.y =
				clamp((this.tile_max_z * this.virtual_world_tile_size), ((this.tile_max_z + this.h) * this.virtual_world_tile_size) - 1, this.viewport_center.y);
		this.viewport_center.z = 0;
		toRealCoordinates(this.viewport_center, this.real_coordinates);

		this.viewport_offset_x = this.real_coordinates.x - this.viewport_w_half;
		this.viewport_offset_y = this.real_coordinates.y - this.viewport_h_half;
		assert this.objects_grid_manager.isViewportPositionValid(this.viewport_offset_x, this.viewport_offset_y);
	}

	public void scrollToVirtualCoordinates(Point coordinates) {
		this.internalScrollToCoordinate(coordinates, true);
	}

	public void scrollToTile(Tile tile) {
		this.viewport_center.x = tile.getX() * this.virtual_world_tile_size + (this.virtual_world_tile_size >> 1);
		this.viewport_center.y = tile.getY() * this.virtual_world_tile_size + (this.virtual_world_tile_size >> 1);
		this.viewport_center.z = this.getAbsoluteHeightOfPointInTileSlopeSurface(tile, (this.virtual_world_tile_size >> 1), (this.virtual_world_tile_size >> 1));
		this.internalScrollToCoordinate(this.viewport_center, false);
	}

	public void setTileZ(Tile tile, int z) {
		int new_z = clamp(0, this.tile_max_z, z), x = tile.getX(), y = tile.getY();

		this.n_affected_tiles = 0;
		if (tile.getZ() != new_z) {
			this.addTileToAffectedList(tile);
			this.internalSetTileZ(tile, new_z);
		}
		for (int i = 0; i < Tile.N_NEIGHBORS; i++) {
			int neighbour_x = x + Tile.neighbour_x_offset[i];
			int neighbour_y = y + Tile.neighbour_y_offset[i];

			if (0 <= neighbour_x && neighbour_x <= this.real_w && 0 <= neighbour_y && neighbour_y <= this.real_h) {
				Tile neighbour_tile = this.map.tiles[neighbour_y][neighbour_x];
				int difference = (new_z - neighbour_tile.getZ());
				boolean increasing = difference > 0;

				if (difference >= 2 && increasing) {
					this.recursiveSetTileZ(neighbour_tile, new_z - 1, increasing, Tile.neighbour_direction[i], false);
				} else if (difference <= -2 && !increasing) {
					this.recursiveSetTileZ(neighbour_tile, new_z + 1, increasing, Tile.neighbour_direction[i], false);
				}
			}
		}

		for (int i = 0; i < this.n_affected_tiles; i++) {
			tile = this.affected_tiles[i];
			for (int j = 3; j <= 5; j++) {
				int neighbour_x = tile.getX() + Tile.neighbour_x_offset[j];
				int neighbour_y = tile.getY() + Tile.neighbour_y_offset[j];
				if (neighbour_x < 0 || neighbour_y < 0)
					continue;
				this.updateSlope(this.map.tiles[neighbour_y][neighbour_x]);
			}
			this.updateSlope(tile);
		}
	}

	/* Package: */
	boolean debug;
	IDrawer drawer;
	Rectangle string_bounds;
	Point real_coordinates, virtual_coordinates; /* Employed for various mappings. */
	Sprite[] sprites;
	int real_w, real_h; /* They are 1-based indices. */
	int viewport_offset_x, viewport_offset_y; /* Real coordinates of upper left corner. It always reflects the same information of viewport_center. */
	int virtual_world_tile_size, slope_height, tile_h, tile_w, w, h, tile_max_z, n_affected_tiles, viewport_w_half, viewport_h_half, viewport_w, viewport_h;

	final static void toRealCoordinates(Point virtual_coordinates, Point real_coordinates) {
		assert (virtual_coordinates.z >= 0);
		real_coordinates.x = ((virtual_coordinates.y - virtual_coordinates.x) << 1);
		real_coordinates.y = (virtual_coordinates.x + virtual_coordinates.y - virtual_coordinates.z);
	}

	final static void toVirtualCoordinates(Point virtual_coordinates, Point real_coordinates) {
		/* It always assumes that virtual_coordinates.z is 0. */
		virtual_coordinates.x = (real_coordinates.y >> 1) - (real_coordinates.x >> 2);
		virtual_coordinates.y = virtual_coordinates.x + (real_coordinates.x >> 1);
		virtual_coordinates.z = 0;
	}

	void drawPoint(Point virtual_coordinates) {
		toRealCoordinates(virtual_coordinates, this.real_coordinates);
		this.real_coordinates.x += (-this.viewport_offset_x);
		this.real_coordinates.y += (-this.viewport_offset_y);
		this.drawer.drawLine(this.real_coordinates.x, this.real_coordinates.y, this.real_coordinates.x + 1, this.real_coordinates.y);
	}

	/* Private: */
	/* Used to iterate through neighbors during the height adjustment without repeat the same tile. */
	private final static int CROSS_SIZE = 12;
	private final static byte n_neighbours[] = {3, 3, 3, 3, 1, 1, 1, 1};
	//@formatter:off
	private final static byte neighbour_direction[][] = {{Tile.DIRECTION_N, Tile.DIRECTION_NW, Tile.DIRECTION_NE} /* N */,
			{Tile.DIRECTION_S, Tile.DIRECTION_SE, Tile.DIRECTION_SW}, /* S */
			{Tile.DIRECTION_W, Tile.DIRECTION_NW, Tile.DIRECTION_SW}, /* W */
			{Tile.DIRECTION_E, Tile.DIRECTION_SE, Tile.DIRECTION_NE}, /* E */
			{Tile.DIRECTION_NE}, /* NE */
			{Tile.DIRECTION_SE}, /* SE */
			{Tile.DIRECTION_SW}, /* SW */
			{Tile.DIRECTION_NW} /* NW */
	};
	private final static byte neighbour_x_offset[][] = {{-1, -1, 0} /* N */, 
			{1, 1, 0}, /* S */
			{-1, -1, 0}, /* W */
			{1, 1, 0}, /* E */
			{0}, /* NE */
			{1}, /* SE */
			{0}, /* SW */
			{-1} /* NW */
	};
	private final static byte neighbour_y_offset[][] = {{-1, 0, -1}, /* N */
			{1, 0, 1}, /* S */
			{1, 0, 1}, /* W */
			{-1, 0, -1}, /* E */
			{-1}, /* NE */
			{0}, /* SE */
			{1}, /* SW */
			{0} /* NW */
	};
	//@formatter:on

	private boolean use_dirty_rectangle_system;
	private UIsoMap map;
	private MapingHelper maping_helper;
	private ObjectsGridManager objects_grid_manager;
	private Point point; /* Available for offset and other calculations. */
	private Point viewport_point; /* Used to draw the scene. */
	private Point viewport_center; /* Virtual coordinates. */
	private ISimulationLogic simulation_logic;
	private SceneObjectsManager scene_objects_manager;
	private Tile[] affected_tiles;

	private void addTileToAffectedList(Tile tile) {
		assert (this.n_affected_tiles < this.affected_tiles.length);

		this.affected_tiles[this.n_affected_tiles++] = tile;

		for (int i = 3; i <= 5; i++) {
			int neighbour_x = tile.getX() + Tile.neighbour_x_offset[i];
			int neighbour_y = tile.getY() + Tile.neighbour_y_offset[i];
			if (neighbour_x < 0 || neighbour_y < 0)
				continue;
			this.map.tiles[neighbour_y][neighbour_x].setMustCorrectTheSlope(true);
		}
		tile.setMustCorrectTheSlope(true);
	}

	private void drawCentralCross() {
		this.drawer.drawLine(this.viewport_w_half - (CROSS_SIZE >> 1), this.viewport_h_half, this.viewport_w_half + (CROSS_SIZE >> 1), this.viewport_h_half);
		this.drawer.drawLine(this.viewport_w_half, this.viewport_h_half - (CROSS_SIZE >> 1), this.viewport_w_half, this.viewport_h_half + (CROSS_SIZE >> 1));
	}

	private void drawTile(Tile tile) {
		int i = 0;
		this.drawer.getTileSprite(tile, this.sprites);

		while (this.sprites[i] != null) {
			Sprite sprite = this.sprites[i++];
			UIsoImage image = sprite.image;

			this.virtual_coordinates.x = (tile.getX() * this.virtual_world_tile_size);
			this.virtual_coordinates.y = (tile.getY() * this.virtual_world_tile_size);
			this.virtual_coordinates.z = (tile.getZ() * this.slope_height);

			toRealCoordinates(this.virtual_coordinates, this.real_coordinates);
			this.real_coordinates.x += (-sprite.getAnchorX() - this.viewport_offset_x);
			this.real_coordinates.y += (-sprite.getAnchorY() - this.viewport_offset_y);

			/* Check the rectangles intersection. */
			if (this.real_coordinates.y + image.getH() < 0 || this.real_coordinates.y >= this.viewport_h || this.real_coordinates.x + image.getW() < 0
					|| this.real_coordinates.x >= this.viewport_w)
				continue;

			this.drawer.drawImage(this.real_coordinates.x, this.real_coordinates.y, image);
		}

		/* Debug: */
		/*
		{
			if(tile.GetSlope() == Tile.WNE){
				int x , y;
				for(x = 0 ; x < 32 ; x++){
					for(y = 0 ; y < 32 ; y++){
						virtual_coordinates.x = x + tile.GetX() * 32;
						virtual_coordinates.y = y + tile.GetY() * 32;
						virtual_coordinates.z = GetAbsoluteHeightOfPointInTileSlopeSurface(tile , x , y);
						ToRealCoordinates(virtual_coordinates , real_coordinates);
						real_coordinates.x += ( - viewport_offset_x);
						real_coordinates.y += ( - viewport_offset_y);
						drawer.DrawLine(real_coordinates.x , real_coordinates.y , real_coordinates.x , real_coordinates.y);
					}
				}
			}
		}
		*/
	}

	private void drawObjects() {
		int min_x, min_y, max_x, max_y;
		UIsoObjectsGridCell objects_grid_cell;

		this.scene_objects_manager.startScene();

		objects_grid_cell = this.objects_grid_manager.getObjectsGridCellAndCellCoordinates(this.viewport_offset_x, this.viewport_offset_y, this.point);
		assert (objects_grid_cell != null);
		min_x = this.point.x;
		min_y = this.point.y;

		objects_grid_cell =
				this.objects_grid_manager.getObjectsGridCellAndCellCoordinates(this.viewport_offset_x + this.viewport_w - 1, this.viewport_offset_y + this.viewport_h - 1,
						this.point);
		assert (objects_grid_cell != null);
		max_x = this.point.x;
		max_y = this.point.y;

		for (int y = min_y; y <= max_y; y++) {
			for (int x = min_x; x <= max_x; x++) {
				UIsoObject object;
				objects_grid_cell = this.objects_grid_manager.getObjectsGridCell(x, y);
				assert (objects_grid_cell != null);
				int vertex = 0, next_vertex = 0;

				object = objects_grid_cell.isometric_engine_object;
				if (object != null)
					vertex = object.getVertexFromPreviousElement(objects_grid_cell);
				while (object != null) {
					this.scene_objects_manager.insertObjectInScene(object);
					next_vertex = object.getVertexOfNextElementThatContinuesTheListInVertex(vertex);
					object = (UIsoObject) object.getNextElement(vertex);
					vertex = next_vertex;
				}
			}
		}
		this.scene_objects_manager.drawSceneObjects();
	}

	private boolean internalCanSetTileZ(Tile tile, int z) {
		int x = tile.getX(), y = tile.getY();
		/* Does a change in the tile z affects user tiles? */
		if (this.tile_max_z <= x && x <= this.tile_max_z + this.w && this.tile_max_z <= y && y <= this.tile_max_z + this.h)
			return this.simulation_logic.canSetTileZ(this, tile, z);
		return true;
	}

	private Tile internalGetTileFromRealCoordinates(Point real_coordinates, Point fine_coordinates) {
		int tile_x, tile_y, x, y, rest_x, rest_y, position, c = 0;

		/* 
		 * Convert x and y to coordinates relative to
		 *  
		 *              |--------> (X)
		 *              |
		 *              |
		 *             \|/
		 *             (Y)
		 * this corner.
		 *         |
		 *        \|/
		 *          ________             
		 *          |  /\  |
		 *          | /  \ |
		 *          |/    \|<---- Tile (0,0)
		 *          /\    /\
		 *         /  \  /  \
		 *             \/    
		 */
		x = (real_coordinates.x + this.viewport_offset_x);
		y = (real_coordinates.y + this.viewport_offset_y);
		x += (this.tile_w >> 1);

		/*
		 *      -2           -1        0
		 * |-8 -7 -6 -5|-4 -3 -2 -1| 0  1  2  3|
		 *             |-3 -2 -1  0| 
		 * Adjust negative numbers so we can correctly divide and employ mod operator. */
		if (x < 0) {
			tile_x = (-(x + 1)) / this.tile_w;
			tile_x = -(tile_x + 1);
			rest_x = (-(x + 1)) % (this.tile_w);
			rest_x = (this.tile_w - 1) - rest_x;
		} else {
			tile_x = x / this.tile_w;
			rest_x = (x % this.tile_w);
		}

		if (y < 0) {
			tile_y = (-(y + 1)) / this.tile_h;
			tile_y = -(tile_y + 1);
			rest_y = (-(y + 1)) % (this.tile_h);
			rest_y = (this.tile_h - 1) - rest_y;
		} else {
			tile_y = y / this.tile_h;
			rest_y = (y % this.tile_h);
		}

		/* Rotate the axis. */
		tile_y = tile_x + tile_y;
		tile_x = tile_y - (tile_x << 1);

		/* Set the points. */
		this.point.x = rest_x;
		this.point.y = rest_y;

		int min_z;
		int slope_index;
		/* While it does not stabilize. */
		do {
			int n_z;

			/* Debug: */
			assert (c++ <= 30);

			if (0 > tile_x || 0 > tile_y || tile_x > this.real_w || tile_y > this.real_h) {
				n_z = 0;
				slope_index = 0;
			} else {
				n_z = this.map.tiles[tile_y][tile_x].getZ();
				slope_index = this.map.tiles[tile_y][tile_x].getSlopeIndex();
			}
			min_z = n_z + Tile.min_z_difference_relative_to_tile_z[slope_index];

			/* As it is shifted to simulate the height. */
			this.point.y += (min_z * this.slope_height);
			position = this.maping_helper.getPositionRelativeToSlopePolygon(this.point, slope_index);
			this.point.y -= (min_z * this.slope_height);

			switch (position) {
				case UIsoConstants.ABOVE_NE_LINE:
					tile_x--;
					this.point.x -= this.tile_w >> 1;
					this.point.y += this.tile_h >> 1;
				break;

				case UIsoConstants.BELOW_ES_LINE:
					tile_y++;
					this.point.x -= this.tile_w >> 1;
					this.point.y -= this.tile_h >> 1;
				break;

				case UIsoConstants.BELOW_SW_LINE:
					tile_x++;
					this.point.x += this.tile_w >> 1;
					this.point.y -= this.tile_h >> 1;
				break;

				case UIsoConstants.ABOVE_WN_LINE:
					tile_y--;
					this.point.x += this.tile_w >> 1;
					this.point.y += this.tile_h >> 1;
				break;
			}
		} while (position != UIsoConstants.INSIDE_POLYGON);

		this.tile_position_relative_map_polygon = UIsoConstants.INSIDE_POLYGON;
		tile_x = clampAndNotify(0, this.real_w, tile_x);
		if (clamped_to_min) {
			this.tile_position_relative_map_polygon |= UIsoConstants.ABOVE_NE_LINE;
		} else if (clamped_to_max) {
			this.tile_position_relative_map_polygon |= UIsoConstants.BELOW_SW_LINE;
		}

		tile_y = clampAndNotify(0, this.real_h, tile_y);
		if (clamped_to_min) {
			this.tile_position_relative_map_polygon |= UIsoConstants.ABOVE_WN_LINE;
		} else if (clamped_to_max) {
			this.tile_position_relative_map_polygon |= UIsoConstants.BELOW_ES_LINE;
		}

		/* If the tile is not inside the map, the {@code fine_coordinates} are meaningless. */
		if (fine_coordinates != null && this.tile_position_relative_map_polygon == UIsoConstants.INSIDE_POLYGON) {
			Tile tile = this.map.tiles[tile_y][tile_x];
			int r_x = this.point.x - (this.tile_w >> 1);
			int r_y = this.point.y + (min_z + Tile.corner_n_z_relative_to_min_z[slope_index]) * this.slope_height;
			float f_v_x = 0, f_v_y = 0, f_v_z = 0;
			float h = this.slope_height;
			float l = this.virtual_world_tile_size;
			float a = l / h;

			switch (tile.getSlope()) {
				case Tile.FLAT:
					f_v_x = (2 * r_y - r_x) / 4;
					f_v_y = (r_x + (f_v_x * 2)) / 2;
				break;
				case Tile.NE:
					f_v_x = (2 * r_y - r_x) / ((4 * a + 2) / a);
					f_v_y = (r_x + (f_v_x * 2)) / 2;
					f_v_z = h - f_v_x / a;
				break;
				case Tile.SW:
					f_v_x = (2 * r_y - r_x) / ((4 * a - 2) / a);
					f_v_y = (r_x + (f_v_x * 2)) / 2;
					f_v_z = f_v_x / a;
				break;
				case Tile.ES:
					f_v_y = (2 * r_y + r_x) / ((4 * a - 2) / a);
					f_v_x = (-r_x + (f_v_y * 2)) / 2;
					f_v_z = f_v_y / a;
				break;
				case Tile.WN:
					f_v_y = (2 * r_y + r_x) / ((4 * a + 2) / a);
					f_v_x = (-r_x + (f_v_y * 2)) / 2;
					f_v_z = h - f_v_y / a;
				break;
				case Tile.N:
					if (r_y >= h + l) {
						r_y -= h;
						f_v_x = (2 * r_y - r_x) / 4;
						f_v_y = (r_x + (f_v_x * 2)) / 2;
					} else {
						f_v_x = (2 * l * r_y - l * r_x - h * r_x) / (4 * (l + h));
						f_v_y = (r_x + (f_v_x * 2)) / 2;
						f_v_z = ((l * h - f_v_x * h - h * f_v_y) / l);
					}
				break;
				case Tile.S:
					if (r_y >= l) {
						r_y -= l / 2;
						f_v_x = (2 * l * r_y - l * r_x + h * r_x) / (4 * (l - h));
						f_v_y = (r_x + (f_v_x * 2)) / 2;
						f_v_z = ((l * h - f_v_x * h - h * f_v_y) / -l);
					} else {
						f_v_x = (2 * r_y - r_x) / 4;
						f_v_y = (r_x + (f_v_x * 2)) / 2;
					}
				break;
				case Tile.W:
					if (r_x >= 0) {
						f_v_x = (2 * r_y - r_x) / 4;
						f_v_y = (r_x + (f_v_x * 2)) / 2;
					} else {
						f_v_x = (2 * l * r_y - l * r_x - h * r_x) / (4 * l);
						f_v_y = (r_x + (f_v_x * 2)) / 2;
						f_v_z = (h * f_v_x - h * f_v_y) / l;
					}
				break;

				case Tile.E:
					if (r_x <= 0) {
						f_v_x = (2 * r_y - r_x) / 4;
						f_v_y = (r_x + (f_v_x * 2)) / 2;
					} else {
						f_v_x = (2 * l * r_y - l * r_x + h * r_x) / (4 * l);
						f_v_y = (r_x + (f_v_x * 2)) / 2;
						f_v_z = (h * f_v_y - h * f_v_x) / l;
					}
				break;
				case Tile.WE:
					if (r_y > h) {
						r_y += l;
						f_v_x = (2 * l * r_y - l * r_x - h * r_x) / (4 * (l + h));
						f_v_y = (r_x + (f_v_x * 2)) / 2;
						f_v_z = (h * f_v_x + h * f_v_y - 2 * h * l) / -l;
					} else {
						f_v_x = (2 * l * r_y - l * r_x + h * r_x) / (4 * (l - h));
						f_v_y = (r_x + (f_v_x * 2)) / 2;
						f_v_z = (h * f_v_x + h * f_v_y) / l;
					}
				break;
				case Tile.NS:
					if (r_x <= 0) {
						f_v_x = (2 * l * r_y - l * r_x + h * r_x) / (4 * l);
						f_v_y = (r_x + (f_v_x * 2)) / 2;
						f_v_z = (l * h - h * f_v_x + h * f_v_y) / l;
					} else {
						f_v_x = (2 * l * r_y - l * r_x - h * r_x) / (4 * l);
						f_v_y = (r_x + (f_v_x * 2)) / 2;
						f_v_z = (-l * h + h * f_v_y - h * f_v_x) / -l;
					}
				break;
				case Tile.NES:
					if (r_x < 0) {
						f_v_x = (2 * l * r_y - l * r_x + h * r_x) / (4 * l);
						f_v_y = (r_x + (f_v_x * 2)) / 2;
						f_v_z = (l * h - h * f_v_x + h * f_v_y) / l;
					} else {
						f_v_x = (2 * r_y - r_x) / 4;
						f_v_y = (r_x + (f_v_x * 2)) / 2;
						f_v_z = h;
					}
				break;
				case Tile.SWN:
					if (r_x <= 0) {
						f_v_x = (2 * r_y - r_x) / 4;
						f_v_y = (r_x + (f_v_x * 2)) / 2;
						f_v_z = h;
					} else {
						f_v_x = (2 * l * r_y - l * r_x - h * r_x) / (4 * l);
						f_v_y = (r_x + (f_v_x * 2)) / 2;
						f_v_z = (-l * h + h * f_v_y - h * f_v_x) / -l;
					}
				break;
				case Tile.ESW:
					if (r_y >= h) {
						r_y += h;
						f_v_x = (2 * r_y - r_x) / 4;
						f_v_y = (r_x + (f_v_x * 2)) / 2;
						f_v_z = h;
					} else {
						f_v_x = (2 * l * r_y - l * r_x + h * r_x) / (4 * (l - h));
						f_v_y = (r_x + (f_v_x * 2)) / 2;
						f_v_z = (h * f_v_x + h * f_v_y) / l;
					}
				break;
				case Tile.WNE:
					if (r_y <= l) {
						f_v_x = (2 * r_y - r_x) / 4;
						f_v_y = (r_x + (f_v_x * 2)) / 2;
						f_v_z = h;
					} else {
						r_y += h;
						f_v_x = (2 * l * r_y - l * r_x - h * r_x) / (4 * (l + h));
						f_v_y = (r_x + (f_v_x * 2)) / 2;
						f_v_z = (h * f_v_x + h * f_v_y - 2 * h * l) / -l;
					}
				break;
				default:
					assert (false);
			}
			fine_coordinates.x = UIsoEngine.clamp(0, this.virtual_world_tile_size - 1, MathUtils.round(f_v_x));
			fine_coordinates.y = UIsoEngine.clamp(0, this.virtual_world_tile_size - 1, MathUtils.round(f_v_y));
			fine_coordinates.z = UIsoEngine.clamp(0, this.slope_height, MathUtils.round(f_v_z));
		}

		return this.map.tiles[tile_y][tile_x];
	}

	private void internalSetTileZ(Tile tile, int z) {
		assert (z != tile.getZ());

		int old_z = tile.getZ(), x = tile.getX(), y = tile.getY();
		tile.setZ(z);
		if (this.tile_max_z <= x && x < this.tile_max_z + this.w && this.tile_max_z <= y && y < this.tile_max_z + this.h)
			this.simulation_logic.informTileZUpdate(this, tile, old_z);
	}

	private boolean recursiveSetTileZ(Tile tile, int z, boolean increasing, int direction, boolean test_mode) {
		boolean can_set_z = true;
		int difference, new_z = clamp(0, this.tile_max_z, z), x = tile.getX(), y = tile.getY();

		if (new_z == tile.getZ())
			return true;
		else if (!test_mode)
			this.internalSetTileZ(tile, new_z);
		else if (!this.internalCanSetTileZ(tile, new_z))
			return false;

		for (int i = 0; i < n_neighbours[direction] && can_set_z; i++) {
			int neighbour_x = x + neighbour_x_offset[direction][i];
			int neighbour_y = y + neighbour_y_offset[direction][i];

			if (0 <= neighbour_x && neighbour_x <= this.real_w && 0 <= neighbour_y && neighbour_y <= this.real_h) {
				Tile neighbour_tile = this.map.tiles[neighbour_y][neighbour_x];
				difference = (new_z - neighbour_tile.getZ());

				if (difference >= 2 && increasing) {
					can_set_z = can_set_z && this.recursiveSetTileZ(neighbour_tile, new_z - 1, increasing, neighbour_direction[direction][i], test_mode);
				} else if (difference <= -2 && !increasing) {
					can_set_z = can_set_z && this.recursiveSetTileZ(neighbour_tile, new_z + 1, increasing, neighbour_direction[direction][i], test_mode);
				}
			}
		}
		if (!test_mode)
			this.addTileToAffectedList(tile);
		return can_set_z;
	}

	private void internalScrollToCoordinate(Point coordinates, boolean adjust) {
		if (adjust) {
			coordinates.x += this.tile_max_z * this.virtual_world_tile_size;
			coordinates.y += this.tile_max_z * this.virtual_world_tile_size;
		}

		toRealCoordinates(coordinates, this.real_coordinates);
		toVirtualCoordinates(this.viewport_center, this.real_coordinates);
		this.viewport_center.x =
				clamp((this.tile_max_z * this.virtual_world_tile_size), ((this.tile_max_z + this.w) * this.virtual_world_tile_size) - 1, this.viewport_center.x);
		this.viewport_center.y =
				clamp((this.tile_max_z * this.virtual_world_tile_size), ((this.tile_max_z + this.h) * this.virtual_world_tile_size) - 1, this.viewport_center.y);
		this.viewport_center.z = 0;
		toRealCoordinates(this.viewport_center, this.real_coordinates);

		this.viewport_offset_x = this.real_coordinates.x - this.viewport_w_half;
		this.viewport_offset_y = this.real_coordinates.y - this.viewport_h_half;
		assert this.objects_grid_manager.isViewportPositionValid(this.viewport_offset_x, this.viewport_offset_y);
	}

	private void updateSlope(Tile tile) {
		if (!tile.mustCorrectTheSlope())
			return;
		tile.setMustCorrectTheSlope(false);

		int highest_z = tile.getZ(), slope = 0, old_slope, next_slope, tile_x = tile.getX(), tile_y = tile.getY();
		boolean same_z = true;

		if (tile_x > this.real_w || tile_y > this.real_h)
			return;

		for (int i = 0; i < 3; i++) {
			int neighbour_x = tile_x + Tile.neighbour_x_offset[i];
			int neighbour_y = tile_y + Tile.neighbour_y_offset[i];
			Tile neighbour_tile = this.map.tiles[neighbour_y][neighbour_x];

			if (highest_z != neighbour_tile.getZ()) {
				same_z = false;
				if (highest_z < neighbour_tile.getZ()) {
					highest_z = neighbour_tile.getZ();
				}
			}
		}

		if (highest_z == tile.getZ())
			slope |= Tile.CORNER_N;
		if (highest_z == this.map.tiles[tile_y + 1][tile_x + 1].getZ())
			slope |= Tile.CORNER_S;
		if (highest_z == this.map.tiles[tile_y + 0][tile_x + 1].getZ())
			slope |= Tile.CORNER_W;
		if (highest_z == this.map.tiles[tile_y + 1][tile_x + 0].getZ())
			slope |= Tile.CORNER_E;

		if (same_z)
			next_slope = Tile.FLAT;
		else
			next_slope = slope;
		old_slope = tile.getSlope();
		if (old_slope != next_slope) {
			tile.setSlope(next_slope);
			if (tile.isVisible())
				this.simulation_logic.informTileSlopeUpdate(this, tile, old_slope);
		}
	}
}
