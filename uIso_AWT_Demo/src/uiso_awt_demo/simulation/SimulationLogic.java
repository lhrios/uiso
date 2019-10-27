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

import java.awt.EventQueue;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Queue;

import uiso.Point;
import uiso.Tile;
import uiso.UIsoConstants;
import uiso.UIsoEngine;
import uiso.UIsoObject;
import uiso.interfaces.ISimulationLogic;
import uiso.interfaces.IUIsoObjectComparator;
import uiso_awt_demo.drawer.JavaSEDrawer;
import uiso_awt_demo.gui.DebugInformationPanel;
import uiso_awt_demo.map.MyTile;
import uiso_awt_demo.object.Minotaur;
import uiso_awt_demo.object.MySpriteObject;
import uiso_awt_demo.object.PathFinder;
import uiso_awt_demo.object.TerraformIcon;
import uiso_awt_demo.simulation.SimulationCoordinator.ScrollToVirtualCoordinatesEvent;
import uiso_awt_demo.simulation.SimulationCoordinator.ScrollViewportCenterWithRealCoordinatesDeltaEvent;
import uiso_awt_demo.util.TerraformUtils;

class SimulationLogic implements ISimulationLogic {

	public void init(UIsoEngine uiso_engine) {
		MySpriteObject.init();
		this.simulation_state = new SimulationState(uiso_engine);
		this.tile_under_mouse_pointer = uiso_engine.getTile(0, 0);
	}

	public boolean updateState(UIsoEngine uiso_engine, JavaSEDrawer drawer, Queue<KeyEvent> key_event_queue, Queue<MouseEvent> mouse_event_queue,
			Queue<FocusEvent> focus_event_queue, Queue<Object> scroll_to_event_queue) {
		this.processKeyEvents(key_event_queue);
		this.processMouseEvents(uiso_engine, drawer, mouse_event_queue);
		this.processFocusEvents(focus_event_queue);
		this.processScrollToEvents(uiso_engine, scroll_to_event_queue);

		this.doScroll(uiso_engine, drawer);

		this.simulation_state.tick++;

		this.simulation_state.minotaur.update(uiso_engine, this.simulation_state.tick);

		this.simulation_state.castle_entrance.update(uiso_engine, this.simulation_state.tick);

		return false;
	}

	@Override
	public boolean canSetTileZ(UIsoEngine uiso_engine, Tile tile, int z) {
		return true;
	}

	@Override
	public void informTileZUpdate(UIsoEngine uiso_engine, Tile tile, int old_z) {
	}

	@Override
	public void informTileSlopeUpdate(UIsoEngine uiso_engine, Tile tile, int old_slope) {
	}

	public void updateDebugInformationPanel(UIsoEngine uIsoEngine, final DebugInformationPanel debugInformationPanel) {
		final Point fine_coordinates = new Point();
		fine_coordinates.copyFrom(SimulationLogic.this.fine_coordinates);

		final int heightFromGetRelativeHeightOfPointInSlopeSurface =
				uIsoEngine.getRelativeHeightOfPointInSlopeSurface(this.tile_under_mouse_pointer.getSlope(), fine_coordinates.x, fine_coordinates.y);
		final int heightFromGetAbsoluteHeightOfPointInTileSlopeSurface =
				uIsoEngine.getAbsoluteHeightOfPointInTileSlopeSurface(this.tile_under_mouse_pointer, fine_coordinates.x, fine_coordinates.y);

		final Point tileUnderMousePointerCoordinates = new Point();
		tileUnderMousePointerCoordinates.x = uIsoEngine.getTileX(this.tile_under_mouse_pointer);
		tileUnderMousePointerCoordinates.y = uIsoEngine.getTileY(this.tile_under_mouse_pointer);
		tileUnderMousePointerCoordinates.z = uIsoEngine.getTileZ(this.tile_under_mouse_pointer);

		final Point tileUnderMinoutar = new Point();
		tileUnderMinoutar.x = uIsoEngine.getTileX(this.simulation_state.minotaur);
		tileUnderMinoutar.y = uIsoEngine.getTileY(this.simulation_state.minotaur);

		final Point virtualViewportCenterCoordinates = new Point();
		uIsoEngine.getVirtualViewportCenterCoordinates(virtualViewportCenterCoordinates);

		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				debugInformationPanel.update(tileUnderMousePointerCoordinates, fine_coordinates, heightFromGetRelativeHeightOfPointInSlopeSurface,
						heightFromGetAbsoluteHeightOfPointInTileSlopeSurface, tileUnderMinoutar, virtualViewportCenterCoordinates);
			}
		});
	}

	/* Default: */
	static class MySpriteObjectComparator implements IUIsoObjectComparator {
		@Override
		public boolean doesBMustBeDrawnBeforeA(UIsoEngine uiso_engine, UIsoObject a, UIsoObject b) {
			int a_tile_x = uiso_engine.getTileX(a);
			int b_tile_x = uiso_engine.getTileX(b);
			int a_tile_y = uiso_engine.getTileY(a);
			int b_tile_y = uiso_engine.getTileY(b);

			if (a_tile_x == b_tile_x) {
				if (a_tile_y == b_tile_y) {
					return b instanceof Minotaur;
				}
				return a_tile_y > b_tile_y;
			}
			return a_tile_x > b_tile_x;
		}
	};

	static class MyStringObjectComparator implements IUIsoObjectComparator {
		@Override
		public boolean doesBMustBeDrawnBeforeA(UIsoEngine uiso_engine, UIsoObject a, UIsoObject b) {
			return true;
		}
	};

	/* Private: */
	private Point viewport_move_delta = new Point(), mouse_event = new Point(), fine_coordinates = new Point();
	private Tile tile_under_mouse_pointer, tile_under_terraform_icon;
	private SimulationState simulation_state;

	private void doScroll(UIsoEngine uiso_engine, JavaSEDrawer drawer) {
		if (this.viewport_move_delta.x != 0 || this.viewport_move_delta.y != 0) {
			uiso_engine.scrollViewportCenterWithRealCoordinatesDelta(this.viewport_move_delta);
			this.updateTileUnderMousePointer(uiso_engine, drawer);
			this.updateTerraformIcon(uiso_engine, drawer);
		}
	}

	private void processScrollToEvents(UIsoEngine uiso_engine, Queue<Object> scroll_to_event_queue) {
		while (!scroll_to_event_queue.isEmpty()) {
			Object object = scroll_to_event_queue.poll();

			if (object instanceof ScrollToVirtualCoordinatesEvent) {
				ScrollToVirtualCoordinatesEvent event = (ScrollToVirtualCoordinatesEvent) object;
				uiso_engine.scrollToVirtualCoordinates(event.virtualCoordinates);

			} else if (object instanceof ScrollViewportCenterWithRealCoordinatesDeltaEvent) {
				ScrollViewportCenterWithRealCoordinatesDeltaEvent event = (ScrollViewportCenterWithRealCoordinatesDeltaEvent) object;
				uiso_engine.scrollViewportCenterWithVirtualCoordinatesDelta(event.delta);
			}
		}
	}

	private void processKeyEvents(Queue<KeyEvent> key_event_queue) {
		while (!key_event_queue.isEmpty()) {
			KeyEvent e = key_event_queue.poll();

			int move = e.getID() == KeyEvent.KEY_PRESSED ? 1 : 0;

			switch (e.getKeyCode()) {
				case KeyEvent.VK_UP:
					this.viewport_move_delta.y = -move * SimulationConstants.VIEWPORT_MOVE_DELTA;
				break;
				case KeyEvent.VK_DOWN:
					this.viewport_move_delta.y = move * SimulationConstants.VIEWPORT_MOVE_DELTA;
				break;
				case KeyEvent.VK_LEFT:
					this.viewport_move_delta.x = -move * SimulationConstants.VIEWPORT_MOVE_DELTA;
				break;
				case KeyEvent.VK_RIGHT:
					this.viewport_move_delta.x = move * SimulationConstants.VIEWPORT_MOVE_DELTA;
				break;
			}
		}
	}

	private void processFocusEvents(Queue<FocusEvent> focus_event_queue) {
		while (!focus_event_queue.isEmpty()) {
			FocusEvent e = focus_event_queue.poll();

			if (e.getID() == FocusEvent.FOCUS_LOST) {
				this.viewport_move_delta.y = this.viewport_move_delta.x = 0;
			}
		}
	}

	private void processMouseEvents(UIsoEngine uiso_engine, JavaSEDrawer drawer, Queue<MouseEvent> mouse_event_queue) {
		while (!mouse_event_queue.isEmpty()) {

			MouseEvent e = mouse_event_queue.poll();

			switch (e.getID()) {
				case MouseEvent.MOUSE_MOVED:
					this.mouse_event.x = e.getX();
					this.mouse_event.y = e.getY();

					this.updateTileUnderMousePointer(uiso_engine, drawer);
					this.updateTerraformIcon(uiso_engine, drawer);
				break;
				case MouseEvent.MOUSE_PRESSED:
					if (this.mouse_event.x != e.getX() || this.mouse_event.y != e.getY()) {
						this.mouse_event.x = e.getX();
						this.mouse_event.y = e.getY();

						this.updateTileUnderMousePointer(uiso_engine, drawer);
						this.updateTerraformIcon(uiso_engine, drawer);
					}

					boolean left_click = e.getButton() == MouseEvent.BUTTON1;
					int tile_x = uiso_engine.getTileX(this.tile_under_mouse_pointer);
					int tile_y = uiso_engine.getTileY(this.tile_under_mouse_pointer);

					if (SimulationConstants.EDITABLE_AREA.contains(tile_x, tile_y)) {
						int delta = left_click ? -1 : 1;
						TerraformUtils.setTileHeight(uiso_engine, this.tile_under_terraform_icon, uiso_engine.getTileZ(this.tile_under_terraform_icon) + delta);
						this.updateTerraformIconPosition(uiso_engine, drawer);

					} else if (SimulationConstants.CASTLE_LAND_AREA.contains(tile_x, tile_y)) {
						if (this.tile_under_mouse_pointer.getSlope() == Tile.FLAT && uiso_engine.getTileZ(this.tile_under_mouse_pointer) == 0
								&& (this.simulation_state.minotaur.getTileX() != tile_x || this.simulation_state.minotaur.getTileY() != tile_y)) {

							this.simulation_state.minotaur.setPathToBeTraversed(PathFinder.findPathTo(uiso_engine, (MyTile) uiso_engine.getTile(this.simulation_state.minotaur
									.getTileX(), this.simulation_state.minotaur.getTileY()), (MyTile) uiso_engine.getTile(tile_x, tile_y)), uiso_engine);
						}
					}
				break;
			}
		}
	}

	private void updateTerraformIconPosition(UIsoEngine uiso_engine, JavaSEDrawer drawer) {
		int delta_x = 0, delta_y = 0;

		if (this.fine_coordinates.x >= SimulationConstants.TILE_VIRTUAL_SIZE / 2) {
			delta_x = 1;
		}
		if (this.fine_coordinates.y >= SimulationConstants.TILE_VIRTUAL_SIZE / 2) {
			delta_y = 1;
		}

		this.tile_under_terraform_icon =
				uiso_engine.getTile(uiso_engine.getTileX(this.tile_under_mouse_pointer) + delta_x, uiso_engine.getTileY(this.tile_under_mouse_pointer) + delta_y);
		int x = uiso_engine.getTileX(this.tile_under_terraform_icon) * SimulationConstants.TILE_VIRTUAL_SIZE;
		int y = uiso_engine.getTileY(this.tile_under_terraform_icon) * SimulationConstants.TILE_VIRTUAL_SIZE;
		int z = uiso_engine.getTileZ(this.tile_under_terraform_icon) * SimulationConstants.SLOPE_HEIGHT;

		TerraformIcon.terraform_icon.setX(x);
		TerraformIcon.terraform_icon.setY(y);
		TerraformIcon.terraform_icon.setZ(z);

		uiso_engine.informObjectMotion(TerraformIcon.terraform_icon);
	}

	private void updateTerraformIcon(UIsoEngine uiso_engine, JavaSEDrawer drawer) {
		int tile_x = uiso_engine.getTileX(this.tile_under_mouse_pointer), tile_y = uiso_engine.getTileY(this.tile_under_mouse_pointer);
		if (SimulationConstants.EDITABLE_AREA.contains(tile_x, tile_y) && uiso_engine.tile_position_relative_map_polygon == UIsoConstants.INSIDE_POLYGON) {
			this.updateTerraformIconPosition(uiso_engine, drawer);
			drawer.setSelectedTile(this.tile_under_mouse_pointer);
		} else {
			uiso_engine.removeObject(TerraformIcon.terraform_icon);
			if (SimulationConstants.CASTLE_LAND_AREA.contains(tile_x, tile_y) && uiso_engine.tile_position_relative_map_polygon == UIsoConstants.INSIDE_POLYGON) {
				drawer.setSelectedTile(this.tile_under_mouse_pointer);
			} else {
				drawer.setSelectedTile(null);
			}
		}
	}

	private void updateTileUnderMousePointer(UIsoEngine uiso_engine, JavaSEDrawer drawer) {
		this.tile_under_mouse_pointer = uiso_engine.getTileFromRealCoordinates(this.mouse_event, this.fine_coordinates);
	}
}
