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

package uiso.interfaces;

import uiso.Rectangle;
import uiso.Sprite;
import uiso.SpriteObject;
import uiso.Tile;
import uiso.UIsoConfiguration;
import uiso.UIsoEngine;
import uiso.UIsoImage;

/**
 * Interface with the methods employed by the engine to draw a scene. It is not allowed to change the engine state while inside these methods. Also, it is not allowed to
 * return different values on methods {@link #getObjectSprite(SpriteObject, Sprite[])} and {@link #getTileSprite(Tile, Sprite[])} while the method
 * {@link UIsoEngine#draw()} is being executed.
 * 
 * @author luis
 */
public interface IDrawer {
	/* Public: */
	/**
	 * Called before beginning the scene drawing process.
	 * 
	 * @param uiso_engine
	 *           the {@link UIsoEngine} from which the method {@link UIsoEngine#draw()} has been called
	 */
	public void beginDrawing(UIsoEngine uiso_engine);

	/**
	 * Must fill the entire available area with the background color/texture.
	 */
	public void clear();

	/**
	 * Must fill the rectangle area with the background color/texture.
	 * 
	 * @param x
	 *           the rectangle top-left corner x-coordinate
	 * @param y
	 *           the rectangle top-left corner y-coordinate
	 * @param w
	 *           the rectangle width
	 * @param h
	 *           the rectangle height
	 */
	public void clear(int x, int y, int w, int h);

	/**
	 * Draws the image informed using the provided coordinates as its top-left corner.
	 * 
	 * @param x
	 *           the x-coordinate of image top-left corner
	 * @param y
	 *           the y-coordinate of image top-left corner
	 * @param image
	 *           the image that will be drawn
	 */
	public void drawImage(int x, int y, UIsoImage image);

	/**
	 * Must draw a line from point p1 to point p2. Used for debug purposes only.
	 * 
	 * @param x1
	 *           the p1 x-coordinate
	 * @param y1
	 *           the p1 y-coordinate
	 * @param x2
	 *           the p2 x-coordinate
	 * @param y2
	 *           the p2 y-coordinate
	 */
	public void drawLine(int x1, int y1, int x2, int y2);

	/**
	 * This version of {@link #drawString(int, int, String, Object, Object)} is only employed for debug purposes. The point informed is assumed to be {@link s} top-left
	 * corner.
	 * 
	 * @param x
	 *           the point x-coordinate
	 * @param y
	 *           the point y-coordinate
	 * @param s
	 *           the {@link String} to be drawn
	 */
	public void drawString(int x, int y, String s);

	/**
	 * Draws a {@link String} using the informed font and color. The point informed is assumed to be {@link s} top-left corner.
	 * 
	 * @param x
	 *           the point x-coordinate
	 * @param y
	 *           the point y-coordinate
	 * @param s
	 *           the {@link String} to be drawn
	 * @param font
	 *           the font
	 * @param color
	 *           the color
	 */
	public void drawString(int x, int y, String s, Object font, Object color);

	/**
	 * Gets the bounds of the {@link String} informed. It does not need to be exactly but can not be smaller.
	 * 
	 * @param s
	 *           the {@link String} for which the bounds will be calculated
	 * @param bounds
	 *           the {@link Rectangle} where the bounds will be stored
	 * @param font
	 *           the font that will be used to draw {@code s}. It will be null in the case of debugging information.
	 */
	public void getStringBounds(String s, Rectangle bounds, Object font);

	/**
	 * The last method called during a scene drawing.
	 */
	public void endDrawing();

	/**
	 * Given the tile returns (using the informed array) the sprites that will be used to draw it. If it is not completed filled, the first non used position must be
	 * filled with the {@code null} value. The number of available positions is configured using {@link UIsoConfiguration#max_sprites_per_tile}. The {@link Sprite} to be
	 * drawn first should be in the array first position and so on.
	 * 
	 * @param tile
	 *           the tile
	 * @param sprites
	 *           its sprites
	 */
	public void getTileSprite(Tile tile, Sprite[] sprites);

	/* TODO: Right now, it considers only the first position. */
	/**
	 * Returns the sprite associated with the object informed.
	 * 
	 * @param object
	 *           the object for which a sprite will be returned
	 * @param sprites
	 *           the object sprite
	 */
	public void getObjectSprite(SpriteObject object, Sprite[] sprites);

	/**
	 * Copies an area delimited by a rectangle to the area defined by the second rectangle. The method should be able to deal with overlapped areas.
	 * 
	 * @param origin_x
	 *           the origin rectangle top-left corner x-coordinate
	 * @param origin_y
	 *           the origin rectangle top-left corner y-coordinate
	 * @param w
	 *           the rectangles width
	 * @param h
	 *           the rectangles height
	 * @param delta_x
	 *           the x-coordinate delta to be combined with {@link origin_x} in order to obtain the destination rectangle top-left corner x-coordinate
	 * @param delta_y
	 *           the y-coordinate delta to be combined with {@link origin_y} in order to obtain the destination rectangle top-left corner y-coordinate
	 */
	public void copyArea(int origin_x, int origin_y, int w, int h, int delta_x, int delta_y);

	/**
	 * Sets the clip rectangle.
	 * 
	 * @param x
	 *           the rectangle top-left corner x-coordinate
	 * @param y
	 *           the rectangle top-left corner y-coordinate
	 * @param w
	 *           the rectangle width
	 * @param h
	 *           the rectangle height
	 */
	public void setClip(int x, int y, int w, int h);
}
