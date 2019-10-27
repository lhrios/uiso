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

package uiso_awt_demo.drawer;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import uiso_awt_demo.image.JavaSEImage;
import uiso_awt_demo.simulation.SimulationConstants;

public class CanvasBorderDrawer {
	private BufferedImage canvas_border;

	public CanvasBorderDrawer(int w, int h) {
		this.canvas_border = new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g2 = this.canvas_border.createGraphics();

		JavaSEImage background_image = JavaSEImage.loadJavaSEImage("parchment.png");
		g2.drawImage(background_image.getBufferedImage(), 0, 0, null);

		g2.setColor(Color.GRAY);
		g2.fillRect(SimulationConstants.UISO_ENGINE_VIEWPORT_DELTA_W - SimulationConstants.UISO_ENGINE_VIEWPORT_BORDER_WIDTH,
				SimulationConstants.UISO_ENGINE_VIEWPORT_DELTA_H - SimulationConstants.UISO_ENGINE_VIEWPORT_BORDER_WIDTH, w - 2
						* SimulationConstants.UISO_ENGINE_VIEWPORT_DELTA_W + 2 * SimulationConstants.UISO_ENGINE_VIEWPORT_BORDER_WIDTH, h - 2
						* SimulationConstants.UISO_ENGINE_VIEWPORT_DELTA_H + 2 * SimulationConstants.UISO_ENGINE_VIEWPORT_BORDER_WIDTH);

		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR));
		g2.fillRect(SimulationConstants.UISO_ENGINE_VIEWPORT_DELTA_W, SimulationConstants.UISO_ENGINE_VIEWPORT_DELTA_H, w - 2
				* SimulationConstants.UISO_ENGINE_VIEWPORT_DELTA_W, h - 2 * SimulationConstants.UISO_ENGINE_VIEWPORT_DELTA_H);
		g2.dispose();

	}

	public void drawCanvasBorder(Graphics2D g2) {
		g2.drawImage(this.canvas_border, 0, 0, null);
	}
}
