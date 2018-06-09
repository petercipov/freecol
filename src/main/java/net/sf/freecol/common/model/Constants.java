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


/**
 * Contains the constants.  These constants are used by the
 * controllers and input handlers when they are communicating.
 */
public interface Constants {

    /** Actions when an armed unit contacts a settlement. */
    enum ArmedUnitSettlementAction {
        SETTLEMENT_ATTACK,
        SETTLEMENT_TRIBUTE,
    }

    /** Actions when dealing with a boycott. */
    enum BoycottAction {
        BOYCOTT_PAY_ARREARS,
        BOYCOTT_DUMP_CARGO
    }

    /** Actions when claiming land. */
    enum ClaimAction {
        CLAIM_ACCEPT,
        CLAIM_STEAL
    }

    /** Price used to denote claiming land by stealing it. */
    int STEAL_LAND = -1;

    /** Actions with a missionary at a native settlement. */
    enum MissionaryAction {
        MISSIONARY_ESTABLISH_MISSION,
        MISSIONARY_DENOUNCE_HERESY,
        MISSIONARY_INCITE_INDIANS
    }

    /** Actions in scouting a colony. */
    enum ScoutColonyAction {
        SCOUT_COLONY_NEGOTIATE,
        SCOUT_COLONY_SPY,
        SCOUT_COLONY_ATTACK
    }

    /** Actions in scouting a native settlement. */
    enum ScoutIndianSettlementAction {
        SCOUT_SETTLEMENT_SPEAK,
        SCOUT_SETTLEMENT_TRIBUTE,
        SCOUT_SETTLEMENT_ATTACK
    }

    /** Choice of sales action at a native settlement. */
    enum TradeAction {
        BUY,
        SELL,
        GIFT
    }

    /** Actions when buying from the natives. */
    enum TradeBuyAction {
        BUY,
        HAGGLE
    }

    /** Actions when selling to the natives. */
    enum TradeSellAction {
        SELL,
        HAGGLE,
        GIFT
    }
}
