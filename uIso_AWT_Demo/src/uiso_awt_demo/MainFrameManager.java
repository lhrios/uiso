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

package uiso_awt_demo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import uiso_awt_demo.gui.GUIConstants;
import uiso_awt_demo.gui.OptionPane;
import uiso_awt_demo.simulation.SimulationConstants;
import uiso_awt_demo.simulation.SimulationCoordinator;

public class MainFrameManager implements WindowListener {

	public static void main(String args[]) {
		/* Creates everything inside the Event Dispatch Thread. */
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				getInstance();
			}
		});
	}

	public static MainFrameManager getInstance() {
		if (main_frame_manager == null) {
			main_frame_manager = new MainFrameManager();

			boolean debug = 0 == OptionPane.showDialog("Do you want to debug?", GUIConstants.WINDOW_TITLE, "Yes", "No");

			main_frame_manager.frame = new Frame(GUIConstants.WINDOW_TITLE);
			main_frame_manager.frame.setLayout(new BorderLayout());
			main_frame_manager.simulation_coordinator = new SimulationCoordinator(debug);

			main_frame_manager.simulation_coordinator.getCanvas().setPreferredSize(new Dimension(SimulationConstants.CANVAS_W, SimulationConstants.CANVAS_H));
			main_frame_manager.frame.add(main_frame_manager.simulation_coordinator.getCanvas(), BorderLayout.CENTER);
			if (debug) {
				main_frame_manager.frame.add(main_frame_manager.simulation_coordinator.getDebugInformationPanel(), BorderLayout.SOUTH);
			}

			main_frame_manager.frame.pack();
			main_frame_manager.frame.setResizable(false);
			main_frame_manager.frame.setLocationRelativeTo(null);
			main_frame_manager.frame.setVisible(true);
			main_frame_manager.frame.addWindowListener(new MainFrameManager());
		}
		main_frame_manager.frame.toFront();
		main_frame_manager.simulation_coordinator.requestFocusOnCanvas();

		return main_frame_manager;
	}

	public static void dispose() {
		if (main_frame_manager != null) {
			main_frame_manager.simulation_coordinator.finish();
			main_frame_manager.frame.dispose();
			main_frame_manager = null;
		}
	}

	public SimulationCoordinator getSimulationCoordinator() {
		return this.simulation_coordinator;
	}

	public void setSimulationCoordinator(SimulationCoordinator simulationCoordinator) {
		this.simulation_coordinator = simulationCoordinator;
	}

	public Frame getFrame() {
		return this.frame;
	}

	public void setFrame(Frame frame) {
		this.frame = frame;
	}

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
		dispose();
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

	/* Private: */
	private static MainFrameManager main_frame_manager;

	private SimulationCoordinator simulation_coordinator;
	private Frame frame;

	private MainFrameManager() {
	}
}
