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

package net.sf.freecol.common.model;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;


/**
 * Objects implementing the Consumer interface consume Goods. Examples
 * include units that eat food, buildings that convert Goods into
 * other Goods, or buildings that store Goods.
 *
 */
public interface Consumer {

    /** Compare consumers by descending priority. */
    Comparator<Consumer> COMPARATOR
        = Comparator.comparingInt(Consumer::getPriority).reversed();

    /**
     * Default consumption priority for the Colony when producing new
     * colonists (from food).
     */
    int POPULATION_PRIORITY = 300;

    /**
     * The consumption priority of the colony build queue.
     */
    int COLONY_PRIORITY = 500;

    /**
     * Default consumption priority for buildings. Individual building
     * types may have different priorities.
     */
    int BUILDING_PRIORITY = 800;

    /**
     * Default consumption priority for units. Individual unit types
     * may have different priorities. Slave units, or converts, or
     * petty criminals, for example, might have a lower priority.
     */
    int UNIT_PRIORITY = 1000;

    /**
     * Returns a list of GoodsTypes this Consumer consumes.
     *
     * @return a {@code List} value
     */
    List<AbstractGoods> getConsumedGoods();

    /**
     * The priority of this Consumer. The higher the priority, the
     * earlier will the Consumer be allowed to consume the goods it
     * requires.
     *
     * @return an {@code int} value
     */
    int getPriority();


    /**
     * Returns whether the consumer has the ability with the given
     * identifier.  The ability most relevant to consumers is
     * "consumeAllOrNothing", which implies that the consumer will not
     * consume any goods if its requirements can not be met and is
     * used by BuildQueues.
     *
     * @param id The object identifier.
     * @return True if the ability is present.
     */
    boolean hasAbility(String id);


    /**
     * Get the modifier set with the given id. The modifier most
     * relevant to consumers is "consumeOnlySurplusProduction", which
     * implies that the consumer does not consume stored goods (used
     * by the country and stables).
     *
     * @param id The object identifier.
     * @return The stream of {@code Modifier}s found.
     */
    Stream<Modifier> getModifiers(String id);
}
