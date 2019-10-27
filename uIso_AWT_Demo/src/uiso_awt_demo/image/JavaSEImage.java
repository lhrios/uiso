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

package uiso_awt_demo.image;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import uiso.UIsoImage;

public class JavaSEImage extends UIsoImage {
	/* Public: */
	public static JavaSEImage loadJavaSEImage(String path) {
		JavaSEImage image = new JavaSEImage();

		try {
			image.image = ImageIO.read(image.getClass().getClassLoader().getResource(path));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		return image;
	}

	public JavaSEImage(BufferedImage image) {
		this.image = image;
	}

	@Override
	public int getW() {
		return this.image.getWidth();
	}

	@Override
	public int getH() {
		return this.image.getHeight();
	}

	public BufferedImage getBufferedImage() {
		return this.image;
	}

	/* Private: */
	private BufferedImage image;

	private JavaSEImage() {

	}
}
