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

package uiso_awt_demo;

import java.awt.Canvas;
import java.awt.EventQueue;

import uiso_awt_demo.simulation.SimulationCoordinator;

public class MyCanvas extends Canvas {
	/* Public: */
	public MyCanvas(SimulationCoordinator simulation_coordinator) {
		this.simulation_coordinator = simulation_coordinator;
		this.setIgnoreRepaint(true);
	}

	@Override
	public void addNotify() {
		super.addNotify();

		EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {
				MyCanvas.this.simulation_coordinator.init();
			}
		});
	}

	/* Private: */
	private static final long serialVersionUID = -1785897683046485515L;

	private SimulationCoordinator simulation_coordinator;
}
