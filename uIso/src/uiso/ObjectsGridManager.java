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
 *     XX      W X     X E       XX
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
		int a_x, a_y, b_x, b_y, c_x, c_y, d_x, d_y, e_x, e_y, f_x, f_y, g_x, g_y, h_x, h_y, viewport_w_half = isometric_engine.viewport_w_half, viewport_h_half =
				isometric_engine.viewport_h_half;
		int x, y, h_length, current_h;
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

		if (isometric_engine.debug) {
			System.out.printf("N (real)=%s\n", n_point);
			System.out.printf("E (real)=%s\n", e_point);
			System.out.printf("S (real)=%s\n", s_point);
			System.out.printf("W (real)=%s\n", w_point);
		}

		a_x = n_point.x - viewport_w_half;
		a_y = n_point.y - viewport_h_half;

		b_x = a_x + this.viewport_w - 1;
		b_y = a_y;
		assert (this.nw_point.x <= a_x && a_x <= this.es_point.x);
		assert (this.nw_point.x <= b_x && b_x <= this.es_point.x);
		assert (this.nw_point.y <= a_y && a_y <= this.es_point.y);
		assert (this.nw_point.y <= b_y && b_y <= this.es_point.y);

		c_x = e_point.x - viewport_w_half + this.viewport_w - 1;
		c_y = e_point.y - viewport_h_half;

		d_x = c_x;
		d_y = c_y + this.viewport_h - 1;
		assert (this.nw_point.x <= c_x && c_x <= this.es_point.x);
		assert (this.nw_point.x <= d_x && d_x <= this.es_point.x);
		assert (this.nw_point.y <= c_y && c_y <= this.es_point.y);
		assert (this.nw_point.y <= d_y && d_y <= this.es_point.y);

		f_x = s_point.x - viewport_w_half;
		f_y = s_point.y - viewport_h_half + this.viewport_h - 1;

		e_x = f_x + this.viewport_w - 1;
		e_y = f_y;
		assert (this.nw_point.x <= e_x && e_x <= this.es_point.x);
		assert (this.nw_point.x <= f_x && f_x <= this.es_point.x);
		assert (this.nw_point.y <= e_y && e_y <= this.es_point.y);
		assert (this.nw_point.y <= f_y && f_y <= this.es_point.y);

		h_x = w_point.x - viewport_w_half;
		h_y = w_point.y - viewport_h_half;

		g_x = h_x;
		g_y = h_y + this.viewport_h - 1;
		assert (this.nw_point.x <= g_x && g_x <= this.es_point.x);
		assert (this.nw_point.x <= h_x && h_x <= this.es_point.x);
		assert (this.nw_point.y <= g_y && g_y <= this.es_point.y);
		assert (this.nw_point.y <= h_y && h_y <= this.es_point.y);

		if (isometric_engine.debug) {
			System.out.printf("viewport_w=%d viewport_h=%d\n", this.viewport_w, this.viewport_h);
			System.out.printf("objects_grid_cell_size=%d\n", this.objects_grid_cell_size);
			System.out.printf("NW=[%4d,%4d] ES=[%4d,%4d]\n", this.nw_point.x, this.nw_point.y, this.es_point.x, this.es_point.y);
			System.out.printf("A=[%4d,%4d] B=[%4d,%4d]\n", a_x, a_y, b_x, b_y);
			System.out.printf("C=[%4d,%4d] D=[%4d,%4d]\n", c_x, c_y, d_x, d_y);
			System.out.printf("E=[%4d,%4d] F=[%4d,%4d]\n", e_x, e_y, f_x, f_y);
			System.out.printf("G=[%4d,%4d] H=[%4d,%4d]\n", g_x, g_y, h_x, h_y);
		}

		/* As the mapping has an error of +-2 pixels due to usage of equations to represent the simulation visible area polygon boundaries. */
		final int DELTA = 2;
		this.nw_point.x -= DELTA;
		this.nw_point.y -= DELTA;
		this.es_point.x += DELTA;
		this.es_point.y += DELTA;
		a_x -= DELTA;
		a_y -= DELTA;
		b_x += DELTA;
		b_y -= DELTA;
		c_x += DELTA;
		c_y -= DELTA;
		d_x += DELTA;
		d_y += DELTA;
		e_x += DELTA;
		e_y += DELTA;
		f_x -= DELTA;
		f_y += DELTA;
		g_x -= DELTA;
		g_y += DELTA;
		h_x -= DELTA;
		h_y -= DELTA;

		if (isometric_engine.debug) {
			System.out.printf("NW=[%4d,%4d] ES=[%4d,%4d]\n", this.nw_point.x, this.nw_point.y, this.es_point.x, this.es_point.y);
			System.out.printf("A=[%4d,%4d] B=[%4d,%4d]\n", a_x, a_y, b_x, b_y);
			System.out.printf("C=[%4d,%4d] D=[%4d,%4d]\n", c_x, c_y, d_x, d_y);
			System.out.printf("E=[%4d,%4d] F=[%4d,%4d]\n", e_x, e_y, f_x, f_y);
			System.out.printf("G=[%4d,%4d] H=[%4d,%4d]\n", g_x, g_y, h_x, h_y);
		}

		/* Now the origin will be NW. */
		{
			a_x -= this.nw_point.x;
			b_x -= this.nw_point.x;
			c_x -= this.nw_point.x;
			d_x -= this.nw_point.x;
			e_x -= this.nw_point.x;
			f_x -= this.nw_point.x;
			g_x -= this.nw_point.x;
			h_x -= this.nw_point.x;

			a_y -= this.nw_point.y;
			b_y -= this.nw_point.y;
			c_y -= this.nw_point.y;
			d_y -= this.nw_point.y;
			e_y -= this.nw_point.y;
			f_y -= this.nw_point.y;
			g_y -= this.nw_point.y;
			h_y -= this.nw_point.y;
		}

		/* Line 1: From h to a. */
		this.m1 = (float) (h_y - a_y) / (float) (h_x - a_x);
		this.b1 = h_y - (h_x * this.m1);
		/* Line 2: From b to c. */
		this.m2 = (float) (c_y - b_y) / (float) (c_x - b_x);
		this.b2 = b_y - (b_x * this.m2);
		/* Line 3: From d to e. */
		this.m3 = (float) (d_y - e_y) / (float) (d_x - e_x);
		this.b3 = d_y - (d_x * this.m3);
		/* Line 4: From f to g. */
		this.m4 = (float) (f_y - g_y) / (float) (f_x - g_x);
		this.b4 = f_y - (f_x * this.m4);
		// TODO: It assumes the division result will be exactly |0.5f| and also compares it using == operator.
		/* As the points have the same offset. 
		 * For example, a_x and h_x use -viewport_w_half, a_y and h_y use -viewport_h_half. 
		 * Of course they are also mapped employing the same formulas.
		 * */
		assert (Math.abs(this.m1) == 0.5f && Math.abs(this.m2) == 0.5f && Math.abs(this.m3) == 0.5f && Math.abs(this.m4) == 0.5f);

		h_length = e_y / this.objects_grid_cell_size + 1;
		int min_left_x[] = new int[h_length], max_right_x[] = new int[h_length];

		/*
		 * Now it scans each line in order to find its first and last reachable cell index.
		 */
		for (y = 0; y <= e_y; y++) {
			int left_x, right_x;
			/* Left. */
			if (y < h_y) {
				left_x = (int) ((y - this.b1) / this.m1);
			} else if (y <= g_y) {
				left_x = h_x;
				assert y != h_y || left_x == (int) ((y - this.b1) / this.m1);
				assert y != g_y || left_x == (int) ((y - this.b4) / this.m4);
			} else {
				assert (y <= f_y);
				left_x = (int) ((y - this.b4) / this.m4);
			}
			assert (0 <= left_x && left_x <= (this.es_point.x - this.nw_point.x));
			left_x = left_x / this.objects_grid_cell_size;

			/* Right. */
			if (y < c_y) {
				right_x = (int) ((y - this.b2) / this.m2);
			} else if (y <= d_y) {
				right_x = c_x;
				assert y != c_y || right_x == (int) ((y - this.b2) / this.m2);
				assert y != d_y || right_x == (int) ((y - this.b3) / this.m3);
			} else {
				assert (y <= e_y);
				right_x = (int) ((y - this.b3) / this.m3);
			}
			assert (0 <= right_x && right_x <= (this.es_point.x - this.nw_point.x));
			assert (left_x <= right_x);
			right_x = right_x / this.objects_grid_cell_size;

			current_h = y / this.objects_grid_cell_size;

			min_left_x[current_h] = Math.min(min_left_x[current_h], left_x);
			max_right_x[current_h] = Math.max(max_right_x[current_h], right_x);
		}

		/* Finally it creates only the necessary cells. */
		this.offsets = new int[h_length];
		this.grid = new UIsoObjectsGridCell[h_length][];

		for (int h = 0; h < h_length; h++) {
			this.offsets[h] = min_left_x[h];
			this.grid[h] = new UIsoObjectsGridCell[max_right_x[h] - min_left_x[h] + 1];
			for (x = 0; x < (max_right_x[h] - min_left_x[h] + 1); x++) {
				this.grid[h][x] = new UIsoObjectsGridCell();
			}
		}

		/* Debug: */
		/* It will test the objects grid limits. It assumes 0 height map (z == 0). */
		/*
		{
			Point p = new Point(), center = new Point(), delta = new Point();
			int line1_x, line2_x, line3_x, line4_x;

			// Line 1 (from 0,0 to x_max,0).
			isometric_engine.scrollToVirtualCoordinate(p);
			isometric_engine.getVirtualViewportCenterCoordinates(center);
			delta.x = 1;
			while (true) {
				isometric_engine.getVirtualViewportCenterCoordinates(center);
				if (center.x == (isometric_engine.w * isometric_engine.virtual_world_tile_size) - 1)
					break;
				isometric_engine.scrollViewportCenterWithVirtualCoordinatesDelta(delta);

				x = isometric_engine.viewport_offset_x;
				y = isometric_engine.viewport_offset_y;

				line1_x = (int) (((y - this.nw_point.y) - b1) / m1);
				assert ((x - this.nw_point.x) >= line1_x);
				assert (this.getObjectsGridCellAndCellCoordinates(x, y, null) != null);
			}

			// Line 2 (from 0,0 to 0,y_max).
			isometric_engine.scrollToVirtualCoordinate(p);
			isometric_engine.getVirtualViewportCenterCoordinates(center);
			delta.x = 0;
			delta.y = 1;
			while (true) {
				isometric_engine.getVirtualViewportCenterCoordinates(center);
				if (center.y == (isometric_engine.h * isometric_engine.virtual_world_tile_size) - 1)
					break;
				isometric_engine.scrollViewportCenterWithVirtualCoordinatesDelta(delta);

				x = isometric_engine.viewport_offset_x + this.viewport_w - 1;
				y = isometric_engine.viewport_offset_y;

				line2_x = (int) (((y - this.nw_point.y) - b2) / m2);
				assert ((x - this.nw_point.x) <= line2_x);
				assert (this.getObjectsGridCellAndCellCoordinates(x, y, null) != null);
			}

			// Line 3 (from 0,y_max to x_max,y_max).
			isometric_engine.getVirtualViewportCenterCoordinates(center);
			delta.x = 1;
			delta.y = 0;
			while (true) {
				isometric_engine.getVirtualViewportCenterCoordinates(center);
				if (center.x == (isometric_engine.w * isometric_engine.virtual_world_tile_size) - 1)
					break;
				isometric_engine.scrollViewportCenterWithVirtualCoordinatesDelta(delta);

				x = isometric_engine.viewport_offset_x + this.viewport_w - 1;
				y = isometric_engine.viewport_offset_y + this.viewport_h - 1;

				line3_x = (int) (((y - this.nw_point.y) - b3) / m3);
				assert ((x - this.nw_point.x) <= line3_x);
				assert (this.getObjectsGridCellAndCellCoordinates(x, y, null) != null);
			}
			
			// Line 4 (from x_max,y_max to x_max,0).
			isometric_engine.getVirtualViewportCenterCoordinates(center);
			delta.x = 1;
			delta.y = -1;
			while (true) {
				isometric_engine.getVirtualViewportCenterCoordinates(center);
				if (center.y == 0)
					break;
				isometric_engine.scrollViewportCenterWithVirtualCoordinatesDelta(delta);

				x = isometric_engine.viewport_offset_x;
				y = isometric_engine.viewport_offset_y + this.viewport_h - 1;
				;

				line4_x = (int) (((y - this.nw_point.y) - b4) / m4);
				assert ((x - this.nw_point.x) >= line4_x);
				assert (this.getObjectsGridCellAndCellCoordinates(x, y, null) != null);
			}
		}
		*/

		/* Prints the amount of memory saved. */
		if (isometric_engine.debug) {
			int total_n_cells = (e_y / this.objects_grid_cell_size + 1) * (d_x / this.objects_grid_cell_size + 1);
			int used_n_cells = 0;

			for (int h = 0; h < h_length; h++) {
				used_n_cells += this.grid[h].length;
			}

			System.err.println("[INFO] Percentage of usable cells: " + (((float) (used_n_cells)) / total_n_cells * 100.f) + "%.");
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

		/* We can only activate this if we are not using objects. */
		// assert (this.nw_point.x <= x && x <= this.es_point.x);
		// assert (this.nw_point.y <= y && y <= this.es_point.y);

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
		if (0 <= cell_y && cell_y < this.offsets.length && this.offsets[cell_y] <= cell_x && (cell_x - this.offsets[cell_y]) < this.grid[cell_y].length)
			return this.grid[cell_y][cell_x - this.offsets[cell_y]];
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
		if (!(this.nw_point.x <= viewport_offset_x && viewport_offset_x <= this.es_point.x)
				|| !(this.nw_point.y <= viewport_offset_y && viewport_offset_y <= this.es_point.y)) {
			return false;
		}

		/* As NW is the origin. */
		viewport_offset_x -= this.nw_point.x;
		viewport_offset_y -= this.nw_point.y;

		int line_x;
		line_x = (int) ((viewport_offset_y - this.b1) / this.m1);
		if (line_x > viewport_offset_x) {
			return false;
		}

		viewport_offset_x += this.viewport_w - 1;
		line_x = (int) ((viewport_offset_y - this.b2) / this.m2);
		if (line_x < viewport_offset_x) {
			return false;
		}

		viewport_offset_y += (this.viewport_h - 1);
		line_x = (int) ((viewport_offset_y - this.b3) / this.m3);
		if (line_x < viewport_offset_x) {
			return false;
		}

		viewport_offset_x -= this.viewport_w - 1;
		line_x = (int) ((viewport_offset_y - this.b4) / this.m4);
		if (line_x > viewport_offset_x) {
			return false;
		}

		return true;
	}

	/* Private: */
	private static final int MIN_OBJECTS_GRID_CELL_SIZE = 200;

	private IDrawer drawer;
	private UIsoEngine isometric_engine;
	private int objects_grid_cell_size, viewport_h, viewport_w;
	private int offsets[];
	private UIsoObjectsGridCell grid[][];
	private Point es_point, nw_point, string_bounds;
	/** Lines equation parameters. */
	private float b1, b2, b3, b4, m1, m2, m3, m4;
}