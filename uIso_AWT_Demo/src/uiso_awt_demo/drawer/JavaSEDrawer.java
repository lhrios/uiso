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

package uiso_awt_demo.drawer;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uiso.Rectangle;
import uiso.Sprite;
import uiso.SpriteObject;
import uiso.Tile;
import uiso.UIsoEngine;
import uiso.UIsoImage;
import uiso.interfaces.IDrawer;
import uiso.util.sprite_loader.SimpleSpriteLoader;
import uiso_awt_demo.image.JavaSEImage;
import uiso_awt_demo.image.JavaSEImageManipulator;
import uiso_awt_demo.map.MyTile;
import uiso_awt_demo.map.TileType;
import uiso_awt_demo.object.Common;
import uiso_awt_demo.object.MySpriteObject;
import uiso_awt_demo.simulation.SimulationConstants;

public class JavaSEDrawer implements IDrawer {

	/* Public: */
	@SuppressWarnings("unchecked")
	public JavaSEDrawer(int canvas_w, int canvas_h) {
		{
			JavaSEImageManipulator javaSEImageManipulator = new JavaSEImageManipulator();
			SimpleSpriteLoader simpleSpriteLoader = new SimpleSpriteLoader(javaSEImageManipulator);
			List<Sprite> sprites;
			int i = 0;

			sprites = simpleSpriteLoader.createSpritesFromImage("grass_slopes.png", 64, 40, Tile.N_SLOPES);
			for (Sprite s : sprites) {
				this.grass_sprites.put(i++, s);
			}

			sprites = simpleSpriteLoader.createSpritesFromImage("selection_slopes.png", 64, 40, Tile.N_SLOPES);
			i = 0;
			for (Sprite s : sprites) {
				this.selection_sprites.put(i++, s);
			}

			sprites = simpleSpriteLoader.createSpritesFromImage("bare_land_slopes.png", 64, 40, Tile.N_SLOPES);
			i = 0;
			for (Sprite s : sprites) {
				this.bare_land.put(i++, s);
			}

			this.castle_floor =
					Common.createSpritesFromImage("castle_floor.png", 64, 40, Arrays.asList(new TileType[]{TileType.CASTLE_FLOOR_FULL,
							TileType.CASTLE_FLOOR_SE,
							TileType.CASTLE_FLOOR_NW,
							TileType.CASTLE_FLOOR_NE,
							TileType.CASTLE_FLOOR_SW}), 1);
		}

		this.wall = new Sprite();
		this.wall.setImage(JavaSEImage.loadJavaSEImage("wall.png"));
		this.wall.setAnchorX(15);
		this.wall.setAnchorY(119);

		this.canvas_h = canvas_h;
		this.canvas_w = canvas_w;
	}

	/*
	 * TODO: There are better ways to draw a multiline text. For example, with LineBreakMeasurer.
	 */
	public static void drawString(Graphics2D g2, int x, int y, String s, Font font, Color color, Color background_color) {
		if (s != null) {
			g2.setFont(font);
			g2.setColor(color);

			String[] lines = s.split("\n");
			for (String line : lines) {
				FontMetrics fontMetrics = getFontMetrics(g2, font);

				if (background_color != null) {
					getStringBounds(g2, line, string_bounds, font);
					g2.setColor(background_color);
					g2.fillRect(x, y, string_bounds.w, string_bounds.h);
				}

				g2.setColor(color);
				g2.drawString(line, x, y + fontMetrics.getAscent());
				y += fontMetrics.getHeight();
			}
		}
	}

	public static void getStringBounds(Graphics2D g2, String s, Rectangle bounds, Font font) {
		bounds.w = bounds.h = 0;

		if (s != null) {
			FontMetrics fontMetrics = getFontMetrics(g2, font);

			String[] lines = s.split("\n");
			for (String line : lines) {
				bounds.h += fontMetrics.getHeight();
				bounds.w = Math.max(bounds.w, fontMetrics.stringWidth(line));
			}
		}
	}

	public void setGraphics2D(Graphics2D g2) {
		this.g2 = g2;
	}

	@Override
	public void beginDrawing(UIsoEngine uiso_engine) {
	}

	@Override
	public void clear() {
		this.clear(0, 0, this.canvas_w, this.canvas_h);
	}

	@Override
	public void drawImage(int x, int y, UIsoImage image) {
		this.g2.drawImage(((JavaSEImage) image).getBufferedImage(), x, y, null);
	}

	@Override
	public void drawLine(int x1, int y1, int x2, int y2) {
		this.g2.setColor(SimulationConstants.LINE_COLOR);
		this.g2.drawLine(x1, y1, x2, y2);
	}

	@Override
	public void drawString(int x, int y, String s) {
		drawString(this.g2, x, y, s, SimulationConstants.DEFAULT_FONT, SimulationConstants.DEFAULT_TEXT_COLOR, SimulationConstants.CLEAR_COLOR);
	}

	@Override
	public void getStringBounds(String s, Rectangle bounds, Object font) {
		getStringBounds(this.g2, s, bounds, (Font) font);
	}

	@Override
	public void endDrawing() {
	}

	@Override
	public void getTileSprite(Tile tile, Sprite sprites[]) {
		int tile_index = tile.getSlopeIndex();
		MyTile myTile = (MyTile) tile;

		switch (myTile.getTileType()) {
			case GRASS:
				sprites[0] = this.grass_sprites.get(tile_index);
			break;
			case BARE_GROUND:
				sprites[0] = this.bare_land.get(tile_index);
			break;
			default:
				sprites[0] = this.castle_floor.get(myTile.getKey());
			break;
		}

		if (tile == this.selected_tile && myTile.getTileType() != TileType.BARE_GROUND) {
			sprites[1] = this.selection_sprites.get(tile_index);
			sprites[2] = null;
		} else
			sprites[1] = null;
	}

	@Override
	public void getObjectSprite(SpriteObject object, Sprite[] sprites) {
		MySpriteObject<?> mySpriteObject = (MySpriteObject<?>) object;
		sprites[0] = MySpriteObject.sprites.get(mySpriteObject.getObjectType()).get(mySpriteObject.getKey());

		assert (sprites[0] != null);

		sprites[1] = sprites[2] = null;
	}

	@Override
	public void setClip(int x, int y, int w, int h) {
		this.g2.setClip(x, y, w, h);
	}

	@Override
	public void copyArea(int origin_x, int origin_y, int w, int h, int delta_x, int delta_y) {
		this.g2.copyArea(origin_x, origin_y, w, h, delta_x, delta_y);
	}

	@Override
	public void clear(int x, int y, int w, int h) {
		this.g2.setColor(SimulationConstants.CLEAR_COLOR);
		this.g2.fillRect(x, y, w, h);
	}

	@Override
	public void drawString(int x, int y, String s, Object font, Object color) {
		drawString(this.g2, x, y, s, (Font) font, (Color) color, SimulationConstants.CLEAR_COLOR);
	}

	public void setSelectedTile(Tile tile) {
		this.selected_tile = tile;
	}

	public Tile getSelectedTile() {
		return this.selected_tile;
	}

	/* Package: */

	/* Private: */
	private static Rectangle string_bounds = new Rectangle();

	private Map<Integer, Sprite> grass_sprites = new HashMap<Integer, Sprite>(), selection_sprites = new HashMap<Integer, Sprite>(),
			bare_land = new HashMap<Integer, Sprite>(), castle_floor = new HashMap<Integer, Sprite>();
	private Graphics2D g2;
	private Sprite wall;
	private Tile selected_tile;
	private int canvas_w, canvas_h;

	private static FontMetrics getFontMetrics(Graphics2D g2, Font font) {
		Font f = font == null ? SimulationConstants.DEFAULT_FONT : font;
		return g2.getFontMetrics(f);
	}
}
