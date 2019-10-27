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

package uiso;

/**
 * This class represents a map cell. It can be extended to add more features and/or fields as necessary. Each {@link Tile} has 4 corners whose names are {@link #CORNER_N}
 * , {@link #CORNER_S}, {@link #CORNER_W} and {@link #CORNER_E}. Their placement follow the diagram presented above. This diagram also shows the tile indexes axes of the
 * corresponding Cartesian coordinates system.
 * 
 * <pre>
 *                    N
 *                   / \
 *                 /     \
 *             W /         \ E
 *             / \   tile  / \
 *           /     \     /     \
 * x-axis \/_        \ /        _\/ y-axis
 *                    S
 * </pre>
 * 
 * The different heights (z-coordinate) of these corners relative to the smallest one determine the slope type. There are {@link #N_SLOPES} possible slopes ({@link #N},
 * {@link #S}, {@link #W}, {@link #E}, {@link #NE}, {@link #ES}, {@link #SW}, {@link #WN}, {@link #NS}, {@link #WE}, {@link #NES}, {@link #ESW}, {@link #WNE} and
 * {@link #SWN}). The maximum possible height difference is 1.
 * <p>
 * Each tile has up to {@link #N_NEIGHBORS}. The arrays {@link #neighbour_x_offset} and {@link #neighbour_y_offset} provide the offsets necessary to determine all the
 * possible neighbors.
 * <p>
 * Some {@link Tile} informations (x-coordinate, y-coordinate and z-coordinate) can not be accessed directly because some transformations are necessary. The {@link Tile}
 * height (z-coordinate) is defined as the {@link #CORNER_N} height.
 * 
 * @author luis
 */
public class Tile {
	/* Public: */
	public final static int CORNER_N = 1;
	public final static int CORNER_S = 2;
	public final static int CORNER_W = 4;
	public final static int CORNER_E = 8;

	public final static int FLAT = 0;
	public final static int N = CORNER_N;
	public final static int S = CORNER_S;
	public final static int E = CORNER_E;
	public final static int W = CORNER_W;

	public final static int NE = CORNER_N | CORNER_E;
	public final static int ES = CORNER_E | CORNER_S;
	public final static int SW = CORNER_S | CORNER_W;
	public final static int WN = CORNER_W | CORNER_N;
	public final static int NS = CORNER_N | CORNER_S;
	public final static int WE = CORNER_W | CORNER_E;

	public final static int NES = CORNER_N | CORNER_E | CORNER_S;
	public final static int ESW = CORNER_E | CORNER_S | CORNER_W;
	public final static int WNE = CORNER_W | CORNER_N | CORNER_E;
	public final static int SWN = CORNER_S | CORNER_W | CORNER_N;

	public final static int FLAT_INDEX = 0;
	public final static int N_INDEX = 1;
	public final static int S_INDEX = 2;
	public final static int E_INDEX = 3;
	public final static int W_INDEX = 4;

	public final static int NE_INDEX = 5;
	public final static int ES_INDEX = 6;
	public final static int SW_INDEX = 7;
	public final static int WN_INDEX = 8;
	public final static int NS_INDEX = 9;
	public final static int WE_INDEX = 10;

	public final static int NES_INDEX = 11;
	public final static int ESW_INDEX = 12;
	public final static int WNE_INDEX = 13;
	public final static int SWN_INDEX = 14;

	public final static int N_SLOPES = 15;
	public final static int N_NEIGHBORS = 8;
	/* XXX: The library depends on contents order. */
	public final static byte[] neighbour_x_offset = {1, 0, 1, -1, -1, 0, 1, -1};
	public final static byte[] neighbour_y_offset = {0, 1, 1, -1, 0, -1, -1, 1};
	public final static boolean[] neighbour_on_diagonal_direction = {false, false, true, true, false, false, true, true};

	/* TODO: This can be improved as min_z_difference_relative_to_tile_z = -corner_n_z_relative_to_min_z. */
	/* Tiles that have CORNER_N bit. */
	public final static byte[] min_z_difference_relative_to_tile_z = {0, -1, 0, 0, 0, -1, 0, 0, -1, -1, 0, -1, 0, -1, -1};

	/* Tiles that have CORNER_X bit. */
	public final static byte[] corner_n_z_relative_to_min_z = {0, 1, 0, 0, 0, 1, 0, 0, 1, 1, 0, 1, 0, 1, 1};
	public final static byte[] corner_s_z_relative_to_min_z = {0, 0, 1, 0, 0, 0, 1, 1, 0, 1, 0, 1, 1, 0, 1};
	public final static byte[] corner_e_z_relative_to_min_z = {0, 0, 0, 1, 0, 1, 1, 0, 0, 0, 1, 1, 1, 1, 0};
	public final static byte[] corner_w_z_relative_to_min_z = {0, 0, 0, 0, 1, 0, 0, 1, 1, 0, 1, 0, 1, 1, 1};

	public Tile() {
		this.setVisibility(true);
	}

	/**
	 * @return this tile slope
	 */
	public int getSlope() {
		return (this.data & 0x00000F00) >>> 8;
	}

	/**
	 * @return the slope index of this tile slope. The possible values are {@link #N_INDEX}, {@link #S_INDEX}, {@link #W_INDEX}, {@link #E_INDEX}, {@link #NE_INDEX},
	 *         {@link #ES_INDEX}, {@link #SW_INDEX}, {@link #WN_INDEX}, {@link #NS_INDEX}, {@link #WE_INDEX}, {@link #NES_INDEX}, {@link #ESW_INDEX}, {@link #WNE_INDEX}
	 *         and {@link #SWN_INDEX}. It can be used with the following arrays: {@link #corner_s_z_relative_to_min_z}, {@link #corner_n_z_relative_to_min_z} ,
	 *         {@link #corner_w_z_relative_to_min_z}, {@link #corner_e_z_relative_to_min_z} and {@link #min_z_difference_relative_to_tile_z}.
	 */
	public int getSlopeIndex() {
		switch (this.getSlope()) {
			default:
				assert (false);
			break;
			case Tile.FLAT:
				return FLAT_INDEX;
			case Tile.N:
				return N_INDEX;
			case Tile.S:
				return S_INDEX;
			case Tile.E:
				return E_INDEX;
			case Tile.W:
				return W_INDEX;
			case Tile.NE:
				return NE_INDEX;
			case Tile.ES:
				return ES_INDEX;
			case Tile.SW:
				return SW_INDEX;
			case Tile.WN:
				return WN_INDEX;
			case Tile.NS:
				return NS_INDEX;
			case Tile.WE:
				return WE_INDEX;
			case Tile.NES:
				return NES_INDEX;
			case Tile.ESW:
				return ESW_INDEX;
			case Tile.WNE:
				return WNE_INDEX;
			case Tile.SWN:
				return SWN_INDEX;
		}
		return 0;
	}

	/**
	 * Each {@link Tile} has 6 bits that can be used freely to store data.
	 * 
	 * @return the user data stored in this {@link Tile}
	 */
	public int getUserData() {
		return (this.data & 0x0000003F);
	}

	/**
	 * Each {@link Tile} has 6 bits that can be used freely to store data.
	 * 
	 * @param the
	 *           user data to be stored in this {@link Tile}. It uses only the 6 least significative bits.
	 */
	public void setUserData(int user_data) {
		this.data = (user_data & 0x3F) | (this.data & 0xFFFFFFC0);
		assert this.getUserData() == user_data;
	}

	@Override
	public String toString() {
		return "[" + this.getX() + "," + this.getY() + "," + this.getZ() + "]";
	}

	public boolean isVisible() {
		return (this.data & 0x00000080) != 0;
	}

	public void setVisibility(boolean visibility) {
		if (visibility)
			this.data = (1 << 7) | (this.data & 0xFFFFFF7F);
		else
			this.data &= 0xFFFFFF7F;
	}

	/* Package: */
	final static int DIRECTION_N = 0;
	final static int DIRECTION_S = 1;
	final static int DIRECTION_W = 2;
	final static int DIRECTION_E = 3;
	final static int DIRECTION_NE = 4;
	final static int DIRECTION_SE = 5;
	final static int DIRECTION_SW = 6;
	final static int DIRECTION_NW = 7;

	final static byte[] neighbour_direction = {DIRECTION_SE, DIRECTION_SW, DIRECTION_S, DIRECTION_N, DIRECTION_NW, DIRECTION_NE, DIRECTION_E, DIRECTION_W};

	int getX() {
		return (this.data & 0xFF000000) >>> 24;
	}

	int getY() {
		return (this.data & 0x00FF0000) >>> 16;
	}

	int getZ() {
		return (this.data & 0x0000F000) >>> 12;
	}

	boolean mustCorrectTheSlope() {
		return (this.data & 0x00000040) != 0;
	}

	void setMustCorrectTheSlope(boolean must_correct_the_slope) {
		if (must_correct_the_slope)
			this.data = (1 << 6) | (this.data & 0xFFFFFFBF);
		else
			this.data &= 0xFFFFFFBF;
	}

	void setSlope(int slope) {
		this.data = ((slope & 0xF) << 8) | (this.data & 0xFFFFF0FF);
	}

	void setX(int x) {
		assert (x <= 0xFF);
		this.data = ((x & 0xFF) << 24) | (this.data & 0x00FFFFFF);
	}

	void setY(int y) {
		assert (y <= 0xFF);
		this.data = ((y & 0xFF) << 16) | (this.data & 0xFF00FFFF);
	}

	void setZ(int z) {
		assert (z <= 0xF);
		this.data = ((z & 0xF) << 12) | (this.data & 0xFFFF0FFF);
	}

	/* Private: */
	/* All data is stored in one field to save memory. */
	/* The compiler allocates 4 bytes for byte, boolean, short and int types (at least when I have tested). */
	/* [31 ... 24] (8 bits): x */
	/* [23 ... 16] (8 bits): y */
	/* [15 ... 12] (4 bits): z */
	/* [11 ... 8] (4 bits): slope */
	/* [ 7 ... 7] (1 bit ): visibility */
	/* [ 6 ... 6] (1 bit ): must correct the slope */
	/* [ 5 ... 0] (6 bits): user data */
	private int data;
}
