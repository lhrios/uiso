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

import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferStrategy;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import uiso.UIsoConfiguration;
import uiso.UIsoEngine;
import uiso_awt_demo.MyCanvas;
import uiso_awt_demo.drawer.CanvasBorderDrawer;
import uiso_awt_demo.drawer.JavaSEDrawer;

public class SimulationCoordinator implements Runnable, MouseListener, MouseMotionListener, KeyListener, FocusListener {
	public void finish() {
		this.finished = true;
		try {
			if (this.t != null) {
				this.t.join();
			}
		} catch (InterruptedException e) {
		}
	}

	/* Public: */
	public SimulationCoordinator() {
		this.canvas = new MyCanvas(this);
		this.canvas.addKeyListener(this);
		this.canvas.addMouseListener(this);
		this.canvas.addMouseMotionListener(this);
		this.canvas.addFocusListener(this);
	}

	public void init() {
		/* When it is called the component must be visible. */
		this.canvas.createBufferStrategy(2);
		this.strategy = this.canvas.getBufferStrategy();
		this.canvas_w = this.canvas.getWidth();
		this.canvas_h = this.canvas.getHeight();

		{
			System.out.printf("BufferStrategy capabilities:\n\tisMultiBufferAvailable: %b\n\tisFullScreenRequired: %b\n\tisPageFlipping:%b\n", this.strategy
					.getCapabilities().isMultiBufferAvailable(), this.strategy.getCapabilities().isFullScreenRequired(), this.strategy.getCapabilities().isPageFlipping());
		}

		do {

			do {
				Graphics2D g2 = this.createGraphics2D();

				g2.setBackground(SimulationConstants.CLEAR_COLOR);
				g2.fillRect(0, 0, this.canvas_w, this.canvas_h);

				g2.setColor(SimulationConstants.DEFAULT_TEXT_COLOR);
				g2.setFont(SimulationConstants.DEFAULT_FONT.deriveFont(100.f));

				String loading = "Loading...";
				FontMetrics fontMetrics = g2.getFontMetrics(g2.getFont());
				Rectangle2D r = fontMetrics.getStringBounds(loading, g2);
				g2.drawString(loading, (int) ((this.canvas_w - r.getWidth()) / 2 - r.getX()), (int) ((this.canvas_h - r.getHeight()) / 2 - r.getY()));

				g2.dispose();
			} while (this.strategy.contentsRestored());

		} while (this.strategy.contentsLost());

		this.strategy.show();
		Toolkit.getDefaultToolkit().sync();

		this.t = new Thread(this);
		this.t.start();
	}

	public void requestFocusOnCanvas() {
		this.canvas.requestFocus();
	}

	@Override
	public void run() {

		try {
			int frame_count = 1;
			long frame_time_before, second_time_before = System.nanoTime(), time_after;
			float simulation_fps = 30.f;

			{
				UIsoConfiguration configuration = SimulationConstants.UISO_CONFIGURATION;
				this.uiso_engine_viewport_w = configuration.viewport_w = this.canvas_w - 2 * SimulationConstants.UISO_ENGINE_VIEWPORT_DELTA_W;
				this.uiso_engine_viewport_h = configuration.viewport_h = this.canvas_h - 2 * SimulationConstants.UISO_ENGINE_VIEWPORT_DELTA_H;
				configuration.drawer = this.drawer = new JavaSEDrawer(this.uiso_engine_viewport_w, this.uiso_engine_viewport_h);
				configuration.simulation_logic = this.simulation_logic = new SimulationLogic();

				Graphics2D g2 = this.createGraphics2D();
				this.drawer.setGraphics2D(g2);

				this.uiso_engine = new UIsoEngine(configuration);
				this.canvas_border_drawer = new CanvasBorderDrawer(this.canvas_w, this.canvas_h);
				this.simulation_logic.init(this.uiso_engine);

				this.drawer.setGraphics2D(null);
				g2.dispose();
			}

			while (!this.finished) {
				frame_time_before = System.nanoTime();

				if (!this.paused) {
					this.simulation_logic.updateState(this.uiso_engine, this.drawer, this.key_event_queue, this.mouse_event_queue, this.focus_event_queue);
				}

				do {

					do {
						Graphics2D g2 = this.createGraphics2D();

						if (!this.paused) {
							g2.setClip(SimulationConstants.UISO_ENGINE_VIEWPORT_DELTA_W, SimulationConstants.UISO_ENGINE_VIEWPORT_DELTA_H, this.uiso_engine_viewport_w,
									this.uiso_engine_viewport_h);
							g2.translate(SimulationConstants.UISO_ENGINE_VIEWPORT_DELTA_W, SimulationConstants.UISO_ENGINE_VIEWPORT_DELTA_H);
							this.drawer.setGraphics2D(g2);
							this.uiso_engine.draw();
						}

						g2.translate(-SimulationConstants.UISO_ENGINE_VIEWPORT_DELTA_W, -SimulationConstants.UISO_ENGINE_VIEWPORT_DELTA_H);
						g2.setClip(0, 0, this.canvas_w, this.canvas_h);

						this.canvas_border_drawer.drawCanvasBorder(g2);

						time_after = System.nanoTime();
						if (time_after - second_time_before >= 1000000000L) {
							simulation_fps = frame_count * 1000.f / ((time_after - second_time_before) / 1000000.f);
							second_time_before = time_after;
							frame_count = 0;
						}
						this.drawSimulationFPS(g2, simulation_fps);

						g2.dispose();

					} while (this.strategy.contentsRestored());

				} while (this.strategy.contentsLost());

				this.strategy.show();
				Toolkit.getDefaultToolkit().sync();

				frame_count++;
				time_after = System.nanoTime();
				int time_to_sleep = Math.round(SimulationConstants.FRAME_TIME - (time_after - frame_time_before) / 1000000.f);
				time_to_sleep = time_to_sleep <= 0 ? SimulationConstants.MIN_SLEEP_TIME : time_to_sleep;
				Thread.sleep(time_to_sleep);
			}

			this.strategy.dispose();

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		this.processKeyEvent(e);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		this.processKeyEvent(e);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		this.processKeyEvent(e);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		this.processMouseEvent(e);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		this.processMouseEvent(e);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		this.processMouseEvent(e);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		this.processMouseEvent(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		this.processMouseEvent(e);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		this.processMouseEvent(e);
	}

	@Override
	public void mouseExited(MouseEvent e) {
		this.processMouseEvent(e);
	}

	@Override
	public void focusGained(FocusEvent e) {
		this.processFocusEvent(e);

	}

	@Override
	public void focusLost(FocusEvent e) {
		this.processFocusEvent(e);
	}

	public Component getCanvas() {
		return this.canvas;
	}

	/* Private: */
	private volatile boolean finished = false, paused = false;
	private int uiso_engine_viewport_w, uiso_engine_viewport_h, canvas_w, canvas_h;
	private BufferStrategy strategy;
	private JavaSEDrawer drawer;
	private UIsoEngine uiso_engine;
	private MyCanvas canvas;
	private Thread t;
	private SimulationLogic simulation_logic;
	private CanvasBorderDrawer canvas_border_drawer;

	private Queue<MouseEvent> mouse_event_queue = new ConcurrentLinkedQueue<MouseEvent>();
	private Queue<KeyEvent> key_event_queue = new ConcurrentLinkedQueue<KeyEvent>();
	private Queue<FocusEvent> focus_event_queue = new ConcurrentLinkedQueue<FocusEvent>();

	private void processMouseEvent(MouseEvent e) {
		e.translatePoint(-SimulationConstants.UISO_ENGINE_VIEWPORT_DELTA_W, -SimulationConstants.UISO_ENGINE_VIEWPORT_DELTA_H);
		this.mouse_event_queue.add(e);
	}

	private void processKeyEvent(KeyEvent e) {
		this.key_event_queue.add(e);
	}

	private void processFocusEvent(FocusEvent e) {
		this.focus_event_queue.add(e);
	}

	private void drawSimulationFPS(Graphics2D g2, float fps) {
		String fps_string = String.format("%06.3f", fps);
		JavaSEDrawer.drawString(g2, 0, 0, fps_string, SimulationConstants.DEFAULT_FONT, SimulationConstants.FPS_TEXT_COLOR, null);
	}

	private Graphics2D createGraphics2D() {
		Graphics2D g2 = (Graphics2D) this.strategy.getDrawGraphics();
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		return g2;
	}
}
