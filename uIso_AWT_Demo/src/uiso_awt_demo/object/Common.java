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

package uiso_awt_demo.object;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import uiso.Sprite;
import uiso.util.sprite_loader.SimpleSpriteLoader;
import uiso_awt_demo.image.JavaSEImageManipulator;

public class Common {
	// TODO: Consider the possibility of a second parameterized Enum. For example, Minoutaur has directions and states.
	@SuppressWarnings("unchecked")
	public static <E extends Enum<E>> Map<Integer, Sprite> createSpritesFromImage(String path, int w, int h, List<E> enums, int sprites_per_direction) {
		JavaSEImageManipulator javaSEImageManipulator = new JavaSEImageManipulator();
		SimpleSpriteLoader simpleSpriteLoader = new SimpleSpriteLoader(javaSEImageManipulator);
		List<Sprite> sprites;
		int sprite_count = enums.size() * sprites_per_direction;

		sprites = simpleSpriteLoader.createSpritesFromImage(path, w, h, sprite_count);

		int ordinal = 0, s = 0;
		Map<Integer, Sprite> sprites_map = new LinkedHashMap<Integer, Sprite>();
		for (Sprite sprite : sprites) {
			int key = computeKey(enums.get(ordinal), s);
			assert (!sprites_map.containsKey(key));
			sprites_map.put(key, sprite);

			if (++s >= sprites_per_direction) {
				s = 0;
				ordinal++;
			}
		}

		return sprites_map;
	}

	public static <E extends Enum<E>> int computeKey(E e, int spriteIndex) {
		return (e.ordinal() << 8) | spriteIndex;
	}
}
