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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import uiso.Point;
import uiso.Sprite;
import uiso.UIsoEngine;
import uiso_awt_demo.util.DistanceUtils;

public class Minotaur extends MySpriteObjectWithDirection {
	/* Public: */
	/* TODO: Improve this code. Both loops are doing almost the same thing. */
	public static Map<Integer, Sprite> createSprites() {
		Map<Integer, Sprite> return_map = new HashMap<Integer, Sprite>();
		final Direction directions[] = {Direction.W, Direction.NW, Direction.N, Direction.NE, Direction.E, Direction.SE, Direction.S, Direction.SW};

		Map<Integer, Sprite> sprites = Common.createSpritesFromImage("minotaur_walking.png", 126, 126, Arrays.asList(directions), 8);
		MySpriteObject.setBoundingBox(sprites.values(), 3, 3, 0, 10, 10, 32);
		for (Map.Entry<Integer, Sprite> e : sprites.entrySet()) {
			return_map.put(State.WALKING.ordinal() << 16 | e.getKey(), e.getValue());
		}

		sprites = Common.createSpritesFromImage("minotaur_stopped.png", 126, 126, Arrays.asList(directions), 4);
		MySpriteObject.setBoundingBox(sprites.values(), 3, 3, 0, 10, 10, 32);
		for (Map.Entry<Integer, Sprite> e : sprites.entrySet()) {
			return_map.put(State.STOPPED.ordinal() << 16 | e.getKey(), e.getValue());
		}

		return return_map;
	}

	public Minotaur() {
		this.setObjectType(ObjectType.MINOTAUR);
		this.setDirection(Direction.SW);
		this.state = State.STOPPED;
	}

	@Override
	public void update(int tick) {
		final int DELTA_MODULE = 3;

		if (this.state == State.WALKING) {

			if (tick % 3 == 0) {
				int next_sprite = this.getSpriteIndex() + 1;
				int max_sprites = 8;

				if (next_sprite >= max_sprites) {
					next_sprite = 0;
				}
				this.setSpriteIndex(next_sprite);

				int delta_x = 0, delta_y = 0;
				boolean compute_delta = true;

				while (compute_delta) {
					if (this.getX() < this.destination_x) {
						delta_x = 1;
					} else if (this.getX() > this.destination_x) {
						delta_x = -1;
					}
					delta_x *= UIsoEngine.clamp(0, DELTA_MODULE, Math.abs(this.getX() - this.destination_x));

					if (this.getY() < this.destination_y) {
						delta_y = 1;
					} else if (this.getY() > this.destination_y) {
						delta_y = -1;
					}
					delta_y *= UIsoEngine.clamp(0, DELTA_MODULE, Math.abs(this.getY() - this.destination_y));

					if (delta_x == 0 && delta_y == 0) {
						if (this.next_point_index < this.path.size()) {
							this.destination_x = this.path.get(this.next_point_index).x;
							this.destination_y = this.path.get(this.next_point_index).y;
							this.next_point_index++;
						} else {
							this.stopped_state_index = 0;
							this.setSpriteIndex(stopped_state_sprite_index[this.stopped_state_index]);
							this.state = State.STOPPED;
							compute_delta = false;
						}
					} else {
						compute_delta = false;
						Direction next_direction = Direction.getDirectionFromDelta(delta_x, delta_y);
						if (next_direction != null) {
							this.setDirection(next_direction);
						}

						this.setX(this.getX() + delta_x);
						this.setY(this.getY() + delta_y);
					}
				}
			}

		} else {

			if (tick % 5 == 0) {
				this.stopped_state_index++;
				int max_sprites = 8;

				if (max_sprites <= this.stopped_state_index) {
					this.stopped_state_index = 0;
				}
				this.setSpriteIndex(stopped_state_sprite_index[this.stopped_state_index]);
			}
		}

	}

	public void setPathToBeTraversed(List<Point> path, UIsoEngine uiso_engine) {
		this.path = path;
		this.next_point_index = 1;

		if (path != null && !path.isEmpty()) {
			/* Check if it must skip the first tile because it is closer to the second tile in the path. */
			if (path.size() > 1
					&& DistanceUtils.euclideanDistance(this.getX(), this.getY(), path.get(0).x, path.get(0).y) >= DistanceUtils.euclideanDistance(path.get(1).x,
							path.get(0).y, path.get(0).x, path.get(0).y)) {
				this.destination_x = path.get(1).x;
				this.destination_y = path.get(1).y;
			} else {
				this.destination_x = path.get(0).x;
				this.destination_y = path.get(0).y;
			}

			this.state = State.WALKING;
		} else {
			this.state = State.STOPPED;
		}
	}

	public State getState() {
		return this.state;
	}

	@Override
	public int getKey() {
		return (this.state.ordinal() << 16) | super.getKey();
	}

	/* Private: */
	private List<Point> path;
	private int next_point_index;
	private State state;
	private int destination_x, destination_y, stopped_state_index;

	private static final int stopped_state_sprite_index[] = {0, 1, 2, 3, 3, 2, 1, 0};
}