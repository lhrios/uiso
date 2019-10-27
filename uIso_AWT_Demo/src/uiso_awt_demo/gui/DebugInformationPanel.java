/*
 * Copyright 2015 Luis Henrique O. Rios
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

package uiso_awt_demo.gui;

import java.awt.Button;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionListener;

import uiso.Point;

public class DebugInformationPanel extends Panel {
	private static final long serialVersionUID = -812648559955885533L;

	private Label lblTileUnderMousePointerCoordinates = new Label();
	private Label lblTileUnderMousePointerFineCoordinates = new Label();
	private Label lblHeightFromGetRelativeHeightOfPointInSlopeSurface = new Label();
	private Label lblHeightFromGetAbsoluteHeightOfPointInTileSlopeSurface = new Label();

	private Label lblTileUnderMinoutar = new Label();
	private Label lblVirtualViewportCenterCoordinates = new Label();
	private TextField txtScrollToVirtualCoordinatesX = new TextField("0");
	private TextField txtScrollToVirtualCoordinatesY = new TextField("0");
	private Button btnScrollToVirtualCoordinates = new Button("scrollToVirtualCoordinates");
	private TextField txtScrollViewportCenterWithRealCoordinatesDeltaX = new TextField("0");
	private TextField txtScrollViewportCenterWithRealCoordinatesDeltaY = new TextField("0");
	private Button btnScrollViewportCenterWithRealCoordinatesDelta = new Button("scrollViewportCenterWithRealCoordinatesDelta");

	public DebugInformationPanel() {
		this.setLayout(new GridBagLayout());

		Insets insets = GUIConstants.DEFAULT_INSETS;

		/* Line 1: */
		GridBagConstraints c = new GridBagConstraints();
		c.insets = insets;
		c.gridheight = 1;
		c.gridwidth = 1;
		c.weightx = 0;
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		this.add(this.lblTileUnderMousePointerCoordinates, c);

		c.gridx++;
		c.gridwidth = GridBagConstraints.REMAINDER;
		this.add(this.lblTileUnderMinoutar, c);

		/* Line 2: */
		c.gridx = 0;
		c.gridwidth = 1;
		c.gridy++;
		this.add(this.lblTileUnderMousePointerFineCoordinates, c);

		c.gridx++;
		c.gridwidth = GridBagConstraints.REMAINDER;
		this.add(this.lblVirtualViewportCenterCoordinates, c);

		/* Line 3: */
		c.gridx = 0;
		c.weightx = 0.6;
		c.gridwidth = 1;
		c.gridy++;
		this.add(this.lblHeightFromGetRelativeHeightOfPointInSlopeSurface, c);

		c.gridx++;
		c.weightx = 0.20;
		this.add(this.txtScrollToVirtualCoordinatesX, c);

		c.gridx++;
		this.add(this.txtScrollToVirtualCoordinatesY, c);

		c.gridx++;
		c.weightx = 0;
		this.add(this.btnScrollToVirtualCoordinates, c);

		/* Line 4: */
		c.gridx = 0;
		c.weightx = 0.6;
		c.gridwidth = 1;
		c.gridy++;
		this.add(this.lblHeightFromGetAbsoluteHeightOfPointInTileSlopeSurface, c);

		c.gridx++;
		c.weightx = 0.2;
		this.add(this.txtScrollViewportCenterWithRealCoordinatesDeltaX, c);

		c.gridx++;
		this.add(this.txtScrollViewportCenterWithRealCoordinatesDeltaY, c);

		c.gridx++;
		c.weightx = 0;
		this.add(this.btnScrollViewportCenterWithRealCoordinatesDelta, c);
	}

	public void update(Point tileUnderMousePointerCoordinates, Point tileUnderMousePointerFineCoordinates, int heightFromGetRelativeHeightOfPointInSlopeSurface,
			int heightFromGetAbsoluteHeightOfPointInTileSlopeSurface, Point tileUnderMinoutar, Point virtualViewportCenterCoordinates) {
		this.lblTileUnderMousePointerCoordinates.setText(String.format("Tile coordinates: (%d, %d, %d)", tileUnderMousePointerCoordinates.x,
				tileUnderMousePointerCoordinates.y, tileUnderMousePointerCoordinates.z));
		this.lblTileUnderMousePointerFineCoordinates.setText(String.format("Tile fine coordinates: (%d, %d, %d)", tileUnderMousePointerFineCoordinates.x,
				tileUnderMousePointerFineCoordinates.y, tileUnderMousePointerFineCoordinates.z));
		this.lblHeightFromGetRelativeHeightOfPointInSlopeSurface.setText(String.format("getRelativeHeightOfPointInSlopeSurface: %d",
				heightFromGetRelativeHeightOfPointInSlopeSurface));
		this.lblHeightFromGetAbsoluteHeightOfPointInTileSlopeSurface.setText(String.format("getAbsoluteHeightOfPointInTileSlopeSurface: %d",
				heightFromGetAbsoluteHeightOfPointInTileSlopeSurface));

		this.lblTileUnderMinoutar.setText(String.format("Minoutar's tile coordinates: (%d, %d)", tileUnderMinoutar.x, tileUnderMinoutar.y));
		this.lblVirtualViewportCenterCoordinates.setText(String.format("getVirtualViewportCenterCoordinates: (%d, %d)", virtualViewportCenterCoordinates.x,
				virtualViewportCenterCoordinates.y));
	}

	public void addActionListenerToBtnScrollToVirtualCoordinates(ActionListener actionListener) {
		this.btnScrollToVirtualCoordinates.addActionListener(actionListener);
	}

	public void addActionListenerToBtnScrollViewportCenterWithRealCoordinatesDelta(ActionListener actionListener) {
		this.btnScrollViewportCenterWithRealCoordinatesDelta.addActionListener(actionListener);
	}

	public int getTxtScrollToVirtualCoordinatesX() throws NumberFormatException {
		return Integer.valueOf(this.txtScrollToVirtualCoordinatesX.getText());
	}

	public int getTxtScrollToVirtualCoordinatesY() throws NumberFormatException {
		return Integer.valueOf(this.txtScrollToVirtualCoordinatesY.getText());
	}

	public int getTxtScrollViewportCenterWithRealCoordinatesDeltaX() throws NumberFormatException {
		return Integer.valueOf(this.txtScrollViewportCenterWithRealCoordinatesDeltaX.getText());
	}

	public int getTxtScrollViewportCenterWithRealCoordinatesDeltaY() throws NumberFormatException {
		return Integer.valueOf(this.txtScrollViewportCenterWithRealCoordinatesDeltaY.getText());
	}
}
