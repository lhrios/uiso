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

package uiso.util.sprite_loader;

import java.util.ArrayList;
import java.util.List;

import uiso.Sprite;
import uiso.UIsoImage;

public class SimpleSpriteLoader {

	public SimpleSpriteLoader(IImageManipulator image_manipulator) {
		this.image_manipulator = image_manipulator;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	public List createSpritesFromImage(String path, int w, int h, int sprite_count) {
		List sprites = new ArrayList();
		UIsoImage image = this.image_manipulator.loadImageFromFile(path);

		if (image.getW() % (w + 2) != 0 || image.getH() % (h + 2) != 0 || image.getW() / (w + 2) * image.getH() / (h + 2) < sprite_count) {
			throw new IllegalArgumentException();
		}

		int x = 0, y = 0;
		for (int c = 0; c < sprite_count; c++) {
			Sprite sprite = new Sprite();
			sprites.add(sprite);
			sprite.setImage(this.image_manipulator.getSubImage(image, x + 1, y + 1, w, h));

			/* Now find the anchor point using the border. */
			for (int i = 0; i < w; i++) {
				if (this.image_manipulator.isAnchorPoint(image, i + x + 1, y)) {
					sprite.setAnchorX(i);
					break;
				}
			}
			for (int j = 0; j < h; j++) {
				if (this.image_manipulator.isAnchorPoint(image, x, j + y + 1)) {
					sprite.setAnchorY(j);
					break;
				}
			}

			x += 2 + w;
			if (x >= image.getW()) {
				x = 0;
				y += 2 + h;
			}
		}

		return sprites;
	}

	/* Private: */
	private IImageManipulator image_manipulator;
}
