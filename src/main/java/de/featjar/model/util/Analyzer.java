/* -----------------------------------------------------------------------------
 * model - Feature models and configurations
 * Copyright (C) 2022 Elias Kuiter
 * 
 * This file is part of model.
 * 
 * model is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3.0 of the License,
 * or (at your option) any later version.
 * 
 * model is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with model. If not, see <https://www.gnu.org/licenses/>.
 * 
 * See <https://github.com/FeatJAR/model> for further information.
 * -----------------------------------------------------------------------------
 */
package de.featjar.model.util;

/**
 * An object that can analyze an {@link Analyzable}.
 *
 * @param <T> the type of the analyzable object
 * @author Elias Kuiter
 */
public interface Analyzer<T> {
	T getAnalyzable();
}
