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

import java.util.Arrays;
import java.util.Map;

import uiso.Sprite;

public class TerraformIcon extends MySpriteObject<SingleTypeType> {

	public static final TerraformIcon terraform_icon = new TerraformIcon();

	public static Map<Integer, Sprite> createSprites() {
		Map<Integer, Sprite> sprites = Common.createSpritesFromImage("terraform_icon.png", 70, 60, Arrays.asList(SingleTypeType.values()), 1);
		return sprites;
	}

	@Override
	public void update(int tick) {
	}

	@Override
	public SingleTypeType getEnumFromOrdinal(int ordinal) {
		return SingleTypeType.values()[ordinal];
	}

	/* Private: */
	private TerraformIcon() {
		super.setObjectType(ObjectType.TERRAFORM_ICON);
	}

}
