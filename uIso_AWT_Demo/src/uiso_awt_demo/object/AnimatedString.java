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

package uiso_awt_demo.object;

import uiso.StringObject;
import uiso.UIsoEngine;

public class AnimatedString extends StringObject implements AnimatedObject {
	/* Public: */
	public AnimatedString(String content) {
		this.content = content.toCharArray();
	}

	@Override
	public void update(UIsoEngine uiso_engine, int tick) {
		boolean changed = false;
		if (tick % 6 == 0) {
			changed = true;
			this.animation_frame++;
			this.animation_frame = this.animation_frame % ANIMATION.length;
		}

		if (tick % 12 == 0) {
			changed = true;
			++this.padding_size;
			this.padding_size = this.padding_size % 21;
		}

		if (changed) {
			char buffer[] = new char[this.padding_size * 2 + 2 + this.content.length];
			int i = 0;

			buffer[i++] = ANIMATION[this.animation_frame];
			for (int j = 0; j < this.padding_size; j++) {
				buffer[i++] = ' ';
			}

			System.arraycopy(this.content, 0, buffer, i, this.content.length);
			i += this.content.length;

			for (int j = 0; j < this.padding_size; j++) {
				buffer[i++] = ' ';
			}
			buffer[i++] = ANIMATION[this.animation_frame];

			this.setString(new String(buffer));
			uiso_engine.informObjectSizeChange(this);
		}
	}

	/* Private: */
	private static final char[] ANIMATION = new char[]{'-', '\\', '|', '/'};
	private final char[] content;

	private int animation_frame;
	private int padding_size;
}
