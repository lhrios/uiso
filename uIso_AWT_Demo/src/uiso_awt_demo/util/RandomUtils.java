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

package uiso_awt_demo.util;

import java.util.Random;

import uiso.Point;

public class RandomUtils {
	public static void randomPointInsideCircle(int center_x, int center_y, int radious, Random random, Point p) {
		float r = random.nextFloat() * radious;
		float angle = (float) (random.nextDouble() * 2 * Math.PI);
		float x = (float) (r * Math.cos(angle));
		float y = (float) (r * Math.sin(angle));

		assert (Math.sqrt(x * x + y * y) <= radious);

		p.x = Math.round(x) + center_x;
		p.y = Math.round(y) + center_y;
	}
}
