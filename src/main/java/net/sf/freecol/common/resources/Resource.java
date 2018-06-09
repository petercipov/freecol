/**
 *  Copyright (C) 2002-2017   The FreeCol Team
 *
 *  This file is part of FreeCol.
 *
 *  FreeCol is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  FreeCol is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with FreeCol.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.sf.freecol.common.resources;

import java.net.URI;


/**
 * Represents a resource that either has been or can be loaded using a
 * {@code URI}.  There can only be one instance of
 * {@code Resource} having the same (as in URI's equals, not the
 * identity) {@code URI}.
 */
public abstract class Resource {

    /**
     * Implement the Preloadable interface if a Resource has a use for
     * calls to a preload method.
     */
    public interface Preloadable {

        /**
         * Preload the resource.
         */
        void preload();

    }

    /**
     * Implement the Cleanable interface if a Resource has a use for
     * calls to a clean method.
     */
    public interface Cleanable {

        /**
         * Clean the caches inside the resource.
         */
        void clean();

    }


    /**
     * The {@code URI} used when loading this resource.
     */
    private final URI resourceLocator;


    protected Resource() {
        // empty constructor
        resourceLocator = null;
    }


    /**
     * Do not use directly.
     *
     * @param resourceLocator The {@code URI} used when loading this
     *      resource.
     */
    public Resource(URI resourceLocator) {
        this.resourceLocator = resourceLocator;
    }


    /**
     * Returns the {@code URI} used for loading the resource.
     * @return The {@code URI}.
     */
    public URI getResourceLocator() {
        return resourceLocator;
    }
}
