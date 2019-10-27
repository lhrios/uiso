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

package uiso_awt_demo.image;

import uiso.UIsoImage;
import uiso.util.sprite_loader.IImageManipulator;

public class JavaSEImageManipulator implements IImageManipulator {

	@Override
	public UIsoImage loadImageFromFile(String path) {
		return JavaSEImage.loadJavaSEImage(path);
	}

	@Override
	public UIsoImage getSubImage(UIsoImage source, int x, int y, int w, int h) {
		JavaSEImage javaSEImage = (JavaSEImage) source;
		JavaSEImage subImage = new JavaSEImage(javaSEImage.getBufferedImage().getSubimage(x, y, w, h));

		return subImage;
	}

	@Override
	public boolean isAnchorPoint(UIsoImage source, int x, int y) {
		JavaSEImage javaSEImage = (JavaSEImage) source;
		return javaSEImage.getBufferedImage().getRGB(x, y) == 0xFFFFFFFF;
	}

}
