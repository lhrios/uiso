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

import uiso.interfaces.IDrawer;

/**
 * The following polygon represents the simulation visible area (i.e, the area viewport can be over). It is manly used to determine which objects grid manager cells needs
 * to exist during simulation in order to hold objects.
 * 
 * <pre>
 *           A    viewport_w  B
 *   NW      XXXXXXXXXXXXXXXX
 *           XXXXXXXXXXXXXXXX
 *         XX                XX
 *         XX                XX
 *       XX 1                2 XX
 *       XX                    XX
 *   H XX           N            XX C
 *     XX           X            XX
 *     XX          X X           XX
 *     XX         X   X          XX
 *     XX      W X     X E       XX viewport_h
 *     XX         X   X          XX
 *     XX          X X           XX
 *     XX           X            XX
 *   G XX           S            XX D
 *       XX                    XX
 *       XX 4                3 XX
 *         XX                XX
 *         XX                XX
 *           XXXXXXXXXXXXXXXX
 *           XXXXXXXXXXXXXXXX       ES
 *           F  viewport_w    E
 * </pre>
 * 
 * @author luis
 */
class ObjectsGridManager {
	/* Public: */
	public ObjectsGridManager(UIsoEngine isometric_engine) {
		int viewport_w_half = isometric_engine.viewport_w_half;
		Point n_point = new Point(), s_point = new Point(), w_point = new Point(), e_point = new Point();

		this.isometric_engine = isometric_engine;
		this.drawer = isometric_engine.drawer;
		this.viewport_w = isometric_engine.viewport_w;
		this.viewport_h = isometric_engine.viewport_h;
		this.string_bounds = isometric_engine.string_bounds;
		this.nw_point = new Point();
		this.es_point = new Point();
		this.objects_grid_cell_size =
				Math.max(viewport_w_half, viewport_w_half) <= MIN_OBJECTS_GRID_CELL_SIZE ? MIN_OBJECTS_GRID_CELL_SIZE : Math.max(viewport_w_half, viewport_w_half);

		/* Calculate the points N, S, W, E, EW and ES. */
		{
			this.isometric_engine.virtual_coordinates.x = this.isometric_engine.tile_max_z * this.isometric_engine.virtual_world_tile_size;
			this.isometric_engine.virtual_coordinates.y = this.isometric_engine.tile_max_z * this.isometric_engine.virtual_world_tile_size;
			this.isometric_engine.virtual_coordinates.z = 0;
			if (isometric_engine.debug) {
				System.out.printf("N (virtual)=%s\n", this.isometric_engine.virtual_coordinates);
			}
			UIsoEngine.toRealCoordinates(this.isometric_engine.virtual_coordinates, n_point);
			this.nw_point.y = n_point.y - this.isometric_engine.viewport_h_half;

			this.isometric_engine.virtual_coordinates.x = (this.isometric_engine.tile_max_z + this.isometric_engine.w) * this.isometric_engine.virtual_world_tile_size - 1;
			if (isometric_engine.debug) {
				System.out.printf("W (virtual)=%s\n", this.isometric_engine.virtual_coordinates);
			}
			UIsoEngine.toRealCoordinates(this.isometric_engine.virtual_coordinates, w_point);
			this.nw_point.x = w_point.x - this.isometric_engine.viewport_w_half;

			this.isometric_engine.virtual_coordinates.y = (this.isometric_engine.tile_max_z + this.isometric_engine.h) * this.isometric_engine.virtual_world_tile_size - 1;
			if (isometric_engine.debug) {
				System.out.printf("S (virtual)=%s\n", this.isometric_engine.virtual_coordinates);
			}
			UIsoEngine.toRealCoordinates(this.isometric_engine.virtual_coordinates, s_point);
			/* We are using the same formula scroll function uses. */
			this.es_point.y = s_point.y - this.isometric_engine.viewport_h_half + this.viewport_h - 1;

			this.isometric_engine.virtual_coordinates.x = this.isometric_engine.tile_max_z * this.isometric_engine.virtual_world_tile_size;
			if (isometric_engine.debug) {
				System.out.printf("E (virtual)=%s\n", this.isometric_engine.virtual_coordinates);
			}
			UIsoEngine.toRealCoordinates(this.isometric_engine.virtual_coordinates, e_point);
			/* We are using the same formula scroll function uses. */
			this.es_point.x = e_point.x - this.isometric_engine.viewport_w_half + this.viewport_w - 1;
		}

		/* Creates the necessary cells. */
		int h_length = (this.es_point.y - this.nw_point.y + 1) / this.objects_grid_cell_size + 1;
		int w_length = (this.es_point.x - this.nw_point.x + 1) / this.objects_grid_cell_size + 1;

		this.grid = new UIsoObjectsGridCell[h_length][w_length];

		for (int y = 0; y < h_length; y++) {
			for (int x = 0; x < this.grid[y].length; x++) {
				this.grid[y][x] = new UIsoObjectsGridCell();
			}
		}
	}

	public void drawObjectsGrid() {
		int min_x, min_y, max_x, max_y, x, y, i, j, viewport_offset_x = this.isometric_engine.viewport_offset_x, viewport_offset_y =
				this.isometric_engine.viewport_offset_y;
		boolean started;

		min_x = viewport_offset_x - this.nw_point.x;
		min_x /= this.objects_grid_cell_size;

		max_x = viewport_offset_x - this.nw_point.x + this.viewport_w - 1;
		max_x /= this.objects_grid_cell_size;

		min_y = viewport_offset_y - this.nw_point.y;
		min_y /= this.objects_grid_cell_size;

		max_y = viewport_offset_y - this.nw_point.y + this.viewport_h - 1;
		max_y /= this.objects_grid_cell_size;

		/* Otherwise the last line won't be drawn. */
		max_x++;
		max_y++;

		started = false;
		y = (min_y * this.objects_grid_cell_size) + this.nw_point.y - viewport_offset_y;
		for (i = min_y; i <= max_y; i++, y += this.objects_grid_cell_size) {
			if (0 <= y && y <= this.viewport_h) {
				/* Draws both lines (each one belongs to a different cell). */
				this.drawer.drawLine(0, y - 1, this.viewport_w - 1, y - 1);
				this.drawer.drawLine(0, y, this.viewport_w - 1, y);
				started = true;
			} else if (started)
				break;
		}

		started = false;
		x = (min_x * this.objects_grid_cell_size) + this.nw_point.x - viewport_offset_x;
		for (i = min_x; i <= max_x; i++, x += this.objects_grid_cell_size) {
			if (0 <= x && x <= this.viewport_w) {
				/* Draws both lines (each one belongs to a different cell). */
				this.drawer.drawLine(x, 0, x, this.viewport_h - 1);
				this.drawer.drawLine(x - 1, 0, x - 1, this.viewport_h - 1);
				started = true;
			} else if (started)
				break;
		}

		/* Draw the coordinates. */
		for (i = min_y; i <= max_y; i++) {
			for (j = min_x; j <= max_x; j++) {
				String coordinates = "(" + j + "," + i + ")";
				this.drawer.getStringBounds(coordinates, this.string_bounds, null);

				x = (j * this.objects_grid_cell_size) + this.nw_point.x - viewport_offset_x /*- (this.string_bounds.x >> 1)*/;
				y = (i * this.objects_grid_cell_size) + this.nw_point.y - viewport_offset_y;
				this.drawer.drawString(x, y, coordinates);
			}
		}
	}

	public UIsoObjectsGridCell getObjectsGridCellAndCellCoordinates(int x, int y, Point coordinates) {
		int cell_x, cell_y;
		UIsoObjectsGridCell cell;

		if (!(this.nw_point.x <= x && x <= this.es_point.x && this.nw_point.y <= y && y <= this.es_point.y))
			return null;

		cell_x = (x - this.nw_point.x) / this.objects_grid_cell_size;
		cell_y = (y - this.nw_point.y) / this.objects_grid_cell_size;
		cell = this.getObjectsGridCell(cell_x, cell_y);
		if (cell != null && coordinates != null) {
			coordinates.x = cell_x;
			coordinates.y = cell_y;
		}
		return cell;
	}

	public UIsoObjectsGridCell getObjectsGridCell(int cell_x, int cell_y) {
		if (0 <= cell_y && cell_y < this.grid.length && 0 <= cell_x && cell_x < this.grid[cell_y].length)
			return this.grid[cell_y][cell_x];
		return null;
	}

	public void checkObjectLimits(int w, int h) {
		int min_grid_cells_occupied_by_viewport_w = this.viewport_w / this.objects_grid_cell_size;
		int min_grid_cells_occupied_by_viewport_h = this.viewport_h / this.objects_grid_cell_size;

		/* An object size can have at most the same length as the minimum number of cells occupied by viewport plus one. 
		 * Otherwise, the object will not be always visible as its corners are used as anchors on the grid. */
		min_grid_cells_occupied_by_viewport_w = Math.max(1, min_grid_cells_occupied_by_viewport_w) + 1;
		min_grid_cells_occupied_by_viewport_h = Math.max(1, min_grid_cells_occupied_by_viewport_h) + 1;

		int max_w = min_grid_cells_occupied_by_viewport_w * this.objects_grid_cell_size;
		if (w > max_w) {
			System.err.println("[WARN] The object width is too large. The max size is \"" + max_w + "\".");
		}

		int max_h = min_grid_cells_occupied_by_viewport_h * this.objects_grid_cell_size;
		if (h > max_h) {
			System.err.println("[WARN] The object height is too large. The max size is \"" + max_h + "\".");
		}
	}

	public boolean isViewportPositionValid(int viewport_offset_x, int viewport_offset_y) {
		if (!(this.nw_point.x <= viewport_offset_x && viewport_offset_x + this.viewport_w - 1 <= this.es_point.x)
				|| !(this.nw_point.y <= viewport_offset_y && viewport_offset_y + this.viewport_h - 1 <= this.es_point.y)) {
			return false;
		}
		return true;
	}

	/* Private: */
	private static final int MIN_OBJECTS_GRID_CELL_SIZE = 200;

	private IDrawer drawer;
	private UIsoEngine isometric_engine;
	private int objects_grid_cell_size, viewport_h, viewport_w;
	private UIsoObjectsGridCell grid[][];
	private Point es_point, nw_point;
	private Rectangle string_bounds;
}