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
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class OptionPane {
	/* Public: */
	public static int showDialog(String message, String title, final String... options) {
		final MyDialog dialog = new MyDialog(null, title, true);
		dialog.setLayout(new GridBagLayout());
		dialog.setResizable(false);

		final ActionListener actionListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String selectedOption = ((Button) e.getSource()).getLabel();

				int i = 0;
				for (String option : options) {
					if (selectedOption.equals(option)) {
						dialog.selectedOption = i;
						break;
					}
					i++;
				}

				dialog.dispose();
			}
		};

		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = GUIConstants.DEFAULT_INSETS;
		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 1;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1;
		Label label = new Label(message);
		dialog.add(label, c);

		c.gridx = 0;
		c.gridy++;
		c.gridwidth = 1;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.CENTER;
		for (String option : options) {
			Button button = new Button(option);
			button.addActionListener(actionListener);
			dialog.add(button, c);
			c.gridx++;
		}

		dialog.setLocationRelativeTo(null);
		dialog.pack();
		dialog.setVisible(true);

		return dialog.selectedOption;
	}

	/* Private: */
	private static class MyDialog extends Dialog {
		/* Public: */
		public int selectedOption = -1;

		public MyDialog(Frame owner, String title, boolean modal) {
			super(owner, title, modal);
		}

		/* Private: */
		private static final long serialVersionUID = -3810298379047588006L;
	}
}
