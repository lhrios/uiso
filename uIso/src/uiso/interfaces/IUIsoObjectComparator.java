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

import uiso.UIsoEngine;
import uiso.UIsoObject;

/**
 * Compares two {@link UIsoObject} and decides which one must be drawn first. As this class is used to sort {@link UIsoObject}, the criteria must be transitive.
 * 
 * @author luis
 */
public interface IUIsoObjectComparator {
	/* Public: */
	public abstract boolean doesBMustBeDrawnBeforeA(UIsoEngine uiso_engine, UIsoObject a, UIsoObject b);
}
