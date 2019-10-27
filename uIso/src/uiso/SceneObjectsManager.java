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

import uiso.interfaces.IDrawer;
import uiso.interfaces.IUIsoObjectComparator;

class SceneObjectsManager {
	/* Package: */
	SceneObjectsManager(UIsoEngine isometric_engine, int max_sprite_objects_in_the_scene, int max_string_objects_in_the_scene,
			IUIsoObjectComparator sprite_object_comparator, IUIsoObjectComparator string_object_comparator) {
		int i;

		this.isometric_engine = isometric_engine;
		this.debug = isometric_engine.debug;
		this.virtual_coordinates = isometric_engine.virtual_coordinates;
		this.real_coordinates = isometric_engine.real_coordinates;
		this.string_bounds = isometric_engine.string_bounds;
		this.virtual_world_tile_size = isometric_engine.virtual_world_tile_size;
		this.tile_max_z = isometric_engine.tile_max_z;
		this.sprites = isometric_engine.sprites;
		this.drawer = isometric_engine.drawer;
		this.viewport_w = isometric_engine.viewport_w;
		this.viewport_h = isometric_engine.viewport_h;
		this.sprite_object_comparator = sprite_object_comparator;
		this.string_object_comparator = string_object_comparator;

		this.sprite_scene_objects = new SpriteSceneObject[max_sprite_objects_in_the_scene];
		for (i = 0; i < this.sprite_scene_objects.length; i++) {
			this.sprite_scene_objects[i] = new SpriteSceneObject();
		}
		this.string_scene_objects = new StringSceneObject[max_string_objects_in_the_scene];
		for (i = 0; i < this.string_scene_objects.length; i++) {
			this.string_scene_objects[i] = new StringSceneObject();
		}
	}

	void startScene() {
		this.n_sprite_scene_objects = this.n_string_scene_objects = 0;
		this.viewport_offset_x = this.isometric_engine.viewport_offset_x;
		this.viewport_offset_y = this.isometric_engine.viewport_offset_y;
	}

	void insertObjectInScene(UIsoObject object) {
		if (!object.isSelected() && object.isVisible()) {
			this.virtual_coordinates.x = object.getX() + this.tile_max_z * this.virtual_world_tile_size;
			this.virtual_coordinates.y = object.getY() + this.tile_max_z * this.virtual_world_tile_size;
			this.virtual_coordinates.z = object.getZ();

			UIsoEngine.toRealCoordinates(this.virtual_coordinates, this.real_coordinates);
			this.real_coordinates.x -= this.viewport_offset_x;
			this.real_coordinates.y -= this.viewport_offset_y;

			if (object instanceof SpriteObject) {
				Sprite sprite;
				UIsoImage image;

				this.drawer.getObjectSprite((SpriteObject) object, this.sprites);
				sprite = this.sprites[0];
				if (sprite == null)
					return;
				image = sprite.image;

				this.real_coordinates.x -= sprite.getAnchorX();
				this.real_coordinates.y -= sprite.getAnchorY();

				/* Check the rectangles intersection. */
				if (this.real_coordinates.y + image.getH() < 0 || this.real_coordinates.y >= this.viewport_h || this.real_coordinates.x + image.getW() < 0
						|| this.real_coordinates.x >= this.viewport_w)
					return;

				if (this.n_sprite_scene_objects < this.sprite_scene_objects.length) {
					object.setSelected(true);
					this.sprite_scene_objects[this.n_sprite_scene_objects].image = image;
					this.sprite_scene_objects[this.n_sprite_scene_objects].sprite_object = (SpriteObject) object;
					this.sprite_scene_objects[this.n_sprite_scene_objects].real_coordinates.copyFrom(this.real_coordinates);
					this.n_sprite_scene_objects++;

				} else if (this.debug) {
					System.err.println("[WARN] There was no sufficient space to draw this object in the scene. " + "Try to increase \"max_sprite_objects_in_the_scene\".");
				}
			} else if (object instanceof StringObject) {
				StringObject stringObject = (StringObject) object;
				this.drawer.getStringBounds(stringObject.getString(), this.string_bounds, stringObject.getFont());
				this.real_coordinates.x -= (this.string_bounds.w >> 1);
				this.real_coordinates.y -= (this.string_bounds.h >> 1);

				/* Check the rectangles intersection. */
				if (this.real_coordinates.y + this.string_bounds.h < 0 || this.real_coordinates.y >= this.viewport_h || this.real_coordinates.x + this.string_bounds.w < 0
						|| this.real_coordinates.x >= this.viewport_w)
					return;

				if (this.n_string_scene_objects < this.string_scene_objects.length) {
					object.setSelected(true);
					this.string_scene_objects[this.n_string_scene_objects].string_object = (StringObject) object;
					this.string_scene_objects[this.n_string_scene_objects].real_coordinates.copyFrom(this.real_coordinates);
					this.n_string_scene_objects++;
				} else if (this.debug) {
					System.err.println("[WARN] There was no sufficient space to draw this object in the scene. " + "Try to increase \"max_string_objects_in_the_scene\".");
				}
			}
		}
	}

	void drawSceneObjects() {
		int i;
		SpriteSceneObject sprite_scene_object;
		StringSceneObject string_scene_object;

		this.sortSpriteSceneObjects();
		for (i = 0; i < this.n_sprite_scene_objects; i++) {
			sprite_scene_object = this.sprite_scene_objects[i];
			sprite_scene_object.sprite_object.setSelected(false);
			this.drawer.drawImage(sprite_scene_object.real_coordinates.x, sprite_scene_object.real_coordinates.y, sprite_scene_object.image);
			if (this.debug) {
				this.virtual_coordinates.x = sprite_scene_object.sprite_object.getX() + this.tile_max_z * this.virtual_world_tile_size;
				this.virtual_coordinates.y = sprite_scene_object.sprite_object.getY() + this.tile_max_z * this.virtual_world_tile_size;
				this.virtual_coordinates.z = sprite_scene_object.sprite_object.getZ();
				UIsoEngine.toRealCoordinates(this.virtual_coordinates, this.real_coordinates);
				this.drawer.drawString(this.real_coordinates.x - this.viewport_offset_x, this.real_coordinates.y - this.viewport_offset_y, Integer.toString(i));
			}
		}

		this.sortStringSceneObjects();
		for (i = 0; i < this.n_string_scene_objects; i++) {
			string_scene_object = this.string_scene_objects[i];
			string_scene_object.string_object.setSelected(false);
			this.drawer.drawString(string_scene_object.real_coordinates.x, string_scene_object.real_coordinates.y, string_scene_object.string_object.getString(),
					string_scene_object.string_object.getFont(), string_scene_object.string_object.getColor());
			if (this.debug)
				this.drawStringSceneObjectBounds(string_scene_object);
		}

	}

	/* Private: */
	private boolean debug;
	private int n_sprite_scene_objects, n_string_scene_objects, virtual_world_tile_size, tile_max_z, viewport_offset_x, viewport_offset_y, viewport_w, viewport_h;
	private IDrawer drawer;
	private UIsoEngine isometric_engine;
	private StringSceneObject[] string_scene_objects;
	private SpriteSceneObject[] sprite_scene_objects;
	private Sprite[] sprites;
	private Point virtual_coordinates, real_coordinates;
	private Rectangle string_bounds;
	private IUIsoObjectComparator sprite_object_comparator, string_object_comparator;

	private void drawStringSceneObjectBounds(StringSceneObject string_scene_object) {
		Point real_coordinates = string_scene_object.real_coordinates;

		this.drawer.getStringBounds(string_scene_object.string_object.getString(), this.string_bounds, string_scene_object.string_object.getFont());
		this.drawer.drawLine(real_coordinates.x, real_coordinates.y, real_coordinates.x, real_coordinates.y + this.string_bounds.h);
		this.drawer.drawLine(real_coordinates.x, real_coordinates.y, real_coordinates.x + this.string_bounds.w, real_coordinates.y);
		this.drawer.drawLine(real_coordinates.x + this.string_bounds.w, real_coordinates.y + this.string_bounds.h, real_coordinates.x, real_coordinates.y
				+ this.string_bounds.h);
		this.drawer.drawLine(real_coordinates.x + this.string_bounds.w, real_coordinates.y + this.string_bounds.h, real_coordinates.x + this.string_bounds.w,
				real_coordinates.y);
	}

	private void sortSpriteSceneObjects() {
		this.sortSceneObjectsArray(this.sprite_scene_objects, this.n_sprite_scene_objects, true);
	}

	private void sortStringSceneObjects() {
		this.sortSceneObjectsArray(this.string_scene_objects, this.n_string_scene_objects, false);
	}

	/*
	 * Insertion sort implementation methods:
	 */
	private void swap(SceneObject[] array, int a, int b) {
		SceneObject aux = array[a];
		array[a] = array[b];
		array[b] = aux;
	}

	private void sortSceneObjectsArray(SceneObject[] array, int n_sprite_scene_objects, boolean sprite_scene_object) {
		for (int i = 0; i < n_sprite_scene_objects; i++) {
			int smallest = i;
			for (int j = i + 1; j < n_sprite_scene_objects; j++) {
				if (sprite_scene_object) {
					UIsoObject a = ((SpriteSceneObject) array[smallest]).sprite_object;
					UIsoObject b = ((SpriteSceneObject) array[j]).sprite_object;

					if (this.sprite_object_comparator.doesBMustBeDrawnBeforeA(this.isometric_engine, a, b)) {
						smallest = j;
					}

				} else {
					UIsoObject a = ((StringSceneObject) array[smallest]).string_object;
					UIsoObject b = ((StringSceneObject) array[j]).string_object;

					if (this.string_object_comparator.doesBMustBeDrawnBeforeA(this.isometric_engine, a, b)) {
						smallest = j;
					}
				}
			}
			this.swap(array, i, smallest);
		}
	}

	/*
	 * End of insertion sort implementation methods.
	 */
}
