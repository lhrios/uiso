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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

import uiso.Point;
import uiso.Tile;
import uiso.UIsoEngine;
import uiso_awt_demo.map.MyTile;
import uiso_awt_demo.simulation.SimulationConstants;
import uiso_awt_demo.util.DistanceUtils;

public class PathFinder {

	public static List<Point> findPathTo(UIsoEngine uiso_engine, MyTile start_tile, MyTile goal_tile) {
		Queue<Node> open = new PriorityQueue<Node>();
		Map<MyTile, Node> all_nodes = new HashMap<MyTile, Node>();
		Node n = new Node();
		int n_expansions = 0;

		n.tile = start_tile;
		open.offer(n);

		while (!open.isEmpty()) {
			n = open.poll();
			if (!n.closed) {
				n.closed = true;
				if (goal_tile == n.tile)
					break;

				n_expansions++;
				for (Node child : expand(uiso_engine, n, all_nodes, goal_tile)) {
					open.offer(child);
				}
			}
		}

		/* Has a solution been found ? */
		if (goal_tile == n.tile) {
			List<Point> path = new ArrayList<Point>();
			while (n != null) {
				assert (n.tile.isPassable());
				//@formatter:off
				path.add(new Point(uiso_engine.getTileX(n.tile) * SimulationConstants.TILE_VIRTUAL_SIZE + SimulationConstants.TILE_VIRTUAL_SIZE / 2, 
						uiso_engine.getTileY(n.tile) * SimulationConstants.TILE_VIRTUAL_SIZE + SimulationConstants.TILE_VIRTUAL_SIZE / 2));
				//@formatter:on
				n = n.parent;
			}

			if (SimulationConstants.UISO_CONFIGURATION.debug) {
				System.out.printf("Nº Expansions: %d Nº Visited: %d\n", n_expansions, all_nodes.size());
			}

			Collections.reverse(path);
			return path;
		}

		return null;
	}

	/* Private: */
	private static boolean isDiagonalFree(int x, int y, int n_x, int n_y, UIsoEngine uiso_engine) {
		MyTile diagonal_tile_1 = (MyTile) uiso_engine.getTile(n_x, y);
		MyTile diagonal_tile_2 = (MyTile) uiso_engine.getTile(x, n_y);
		return diagonal_tile_1.isPassable() && diagonal_tile_2.isPassable();
	}

	private static class Node implements Comparable<Node> {
		public int f, g, h;
		public MyTile tile;
		public Node parent;
		public boolean closed;

		@Override
		public boolean equals(Object o) {
			if (o == this) {
				return true;
			} else if (o instanceof PathFinder.Node) {
				Node n = (Node) o;

				return this.tile == n.tile;
			}

			return false;
		}

		@Override
		public int compareTo(Node o) {
			if (this.f == o.f) {
				return o.g - this.g;
			} else {
				return this.f - o.f;
			}
		}

	}

	private static List<Node> expand(UIsoEngine uiso_engine, Node n, Map<MyTile, Node> all_nodes, MyTile goal_tile) {
		int x = uiso_engine.getTileX(n.tile), y = uiso_engine.getTileY(n.tile);
		List<Node> children = new ArrayList<Node>();

		for (int i = 0; i < Tile.N_NEIGHBORS; i++) {
			int n_x = x + Tile.neighbour_x_offset[i], n_y = y + Tile.neighbour_y_offset[i];

			if (uiso_engine.isValidTileCoordinates(n_x, n_y)) {
				MyTile tile = (MyTile) uiso_engine.getTile(n_x, n_y);
				if (tile.getSlope() == Tile.FLAT && tile.isPassable() && (!Tile.neighbour_on_diagonal_direction[i] || isDiagonalFree(x, y, n_x, n_y, uiso_engine))) {
					Node child = all_nodes.get(tile);
					int c = Tile.neighbour_on_diagonal_direction[i] ? 141 : 100;
					if (child == null) {
						child = new Node();
						child.tile = tile;
						all_nodes.put(tile, child);

						child.h =
								100 * DistanceUtils.diagonalDistance(uiso_engine.getTileX(tile), uiso_engine.getTileY(tile), uiso_engine.getTileX(goal_tile), uiso_engine
										.getTileY(goal_tile));

						child.g = n.g + c;
						child.f = child.g + child.h;
						child.parent = n;

						children.add(child);
					} else if (!child.closed && child.g > n.g + c) {
						child.g = n.g + c;
						child.f = child.g + child.h;
						child.parent = n;

						children.add(child);
					}

				}
			}
		}

		return children;
	}
}
