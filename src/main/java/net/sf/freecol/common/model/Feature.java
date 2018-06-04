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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import javax.xml.stream.XMLStreamException;

import net.sf.freecol.common.i18n.Messages;
import net.sf.freecol.common.io.FreeColXMLReader;
import net.sf.freecol.common.io.FreeColXMLWriter;
import static net.sf.freecol.common.util.CollectionUtils.*;
import net.sf.freecol.common.util.Utils;


/**
 * The {@code Feature} class encapsulates a bonus or penalty
 * that can be applied to any action within the game, most obviously
 * combat.
 *
 * Do not make this a FCSOT because that has a FeatureContainer.  Lumping
 * features onto features would be incoherent.
 */
public abstract class Feature extends FreeColSpecObject
    implements Named, Scoped {

    /** The source of this Feature, e.g. a UnitType. */
    private FreeColObject source;

    /** The first Turn in which this Feature applies. */
    private Turn firstTurn;

    /** The last Turn in which this Feature applies. */
    private Turn lastTurn;

    /** The duration of this Feature. By default, the duration is unlimited. */
    private int duration = 0;

    /**
     * Transient features are provided by events such as disasters and
     * goods parties, and need to be serialized by the
     * FreeColGameObject they apply to.
     */
    private boolean temporary;

    /**
     * A list of Scopes limiting the applicability of this Feature.
  	 * Allocated on demand.
     */
    private List<Scope> scopes = null;


    /**
     * Deliberately trivial constructor.
     *
     * @param specification The {@code Specification} to use.
     */
    public Feature(Specification specification) {
        super(specification);
    }


    /**
     * Does this feature have a time limit?
     *
     * @return True if the feature is time limited.
     */
    public final boolean hasTimeLimit() {
        return (firstTurn != null || lastTurn != null);
    }

    /**
     * Get the first turn of a time limit.
     *
     * @return The first turn, or null if none.
     */
    public final Turn getFirstTurn() {
        return firstTurn;
    }

    /**
     * Set the first turn of a time limit.
     *
     * @param newFirstTurn The new first turn value.
     */
    public final void setFirstTurn(final Turn newFirstTurn) {
        this.firstTurn = newFirstTurn;
    }

    /**
     * Get the last turn of a time limit.
     *
     * @return The last turn, or null if none.
     */
    public final Turn getLastTurn() {
        return lastTurn;
    }

    /**
     * Set the last turn of a time limit.
     *
     * @param newLastTurn The new last turn value.
     */
    public final void setLastTurn(final Turn newLastTurn) {
        this.lastTurn = newLastTurn;
    }

    /**
     * Get the source of this feature.
     *
     * @return The source object.
     */
    public final FreeColObject getSource() {
        return source;
    }

    /**
     * Set the source of this feature.
     *
     * @param newSource The new source.
     */
    public final void setSource(final FreeColObject newSource) {
        this.source = newSource;
    }

    /**
     * Get the duration of this feature.
     *
     * @return The number of turns this feature lasts.
     */
    public final int getDuration() {
        return duration;
    }

    /**
     * Set the duration of this feature.
     *
     * @param newDuration The new duration.
     */
    public final void setDuration(final int newDuration) {
        this.duration = newDuration;
    }

    /**
     * Is this a temporary feature?
     *
     * @return True if this is a temporary feature.
     */
    public final boolean isTemporary() {
        return temporary;
    }

    /**
     * Set the temporary status.
     *
     * @param newTemporary The new temporary status.
     */
    public final void setTemporary(final boolean newTemporary) {
        this.temporary = newTemporary;
    }

    /**
     * Does this feature apply to a given object type?
     *
     * @param objectType The {@code FreeColSpecObjectType} to test.
     * @return True if there are no scopes, or at least one scope is
     *     applicable to the object.
     */
    public boolean appliesTo(final FreeColSpecObjectType objectType) {
        return (!hasScope()) ? true
            : any(this.scopes, s -> s.appliesTo(objectType));
    }

    /**
     * Does this feature apply to a given turn?
     *
     * @param turn The {@code Turn} to test.
     * @return True if the turn is null or not outside a valid time limit.
     */
    protected boolean appliesTo(final Turn turn) {
        return !(turn != null
            && ((firstTurn != null && turn.getNumber() < firstTurn.getNumber())
                || (lastTurn != null && turn.getNumber() > lastTurn.getNumber())));
    }

    /**
     * Does this feature apply to a given object type and turn.
     *
     * @param objectType The {@code FreeColSpecObjectType} to test.
     * @param turn The {@code Turn} to test.
     * @return True if the feature applies.
     */
    protected boolean appliesTo(final FreeColSpecObjectType objectType,
                                final Turn turn) {
        return appliesTo(turn) && appliesTo(objectType);
    }

    /**
     * Is this feature out of date with respect to a given turn?
     *
     * @param turn The {@code Turn} to compare to.
     * @return True if the Feature has an lastTurn turn smaller than the
     *     given turn.
     */
    public boolean isOutOfDate(Turn turn) {
        return turn != null && lastTurn != null
            && turn.getNumber() > lastTurn.getNumber();
    }

    /**
     * Is this feature an independent stand-alone one, or is it
     * derived from some other entity such as a founding father.  This
     * is important for player and colony serialization, where we do
     * *not* want to read or write derived features because they are
     * added to the player by the source.
     *
     * @return True if the feature is independent.
     */
    public boolean isIndependent() {
        if (source instanceof BuildingType
            || source instanceof FoundingFather
            || source instanceof NationType
            || source instanceof SettlementType) return false;
        return true;
    }


    // Interface Named

    /**
     * {@inheritDoc}
     */
    @Override
    public String getNameKey() {
        return Messages.nameKey(getId());
    }


    // Interface Scoped, and scope support

    /**
     * {@inheritDoc}
     */
    @Override
    public final List<Scope> getScopeList() {
        return (this.scopes == null) ? Collections.<Scope>emptyList()
            : new ArrayList<>(this.scopes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Stream<Scope> getScopes() {
        return (this.scopes == null) ? Stream.<Scope>empty()
            : getScopeList().stream();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void setScopes(List<Scope> scopes) {
        this.scopes = scopes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addScope(Scope scope) {
        if (this.scopes == null) this.scopes = new ArrayList<>();
        this.scopes.add(scope);
    }

    /**
     * Does this feature have a scope?
     *
     * @return True if there are any scopes attached to this feature.
     */
    public final boolean hasScope() {
        return this.scopes != null && !this.scopes.isEmpty();
    }


    // Override FreeColObject
    
    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends FreeColObject> boolean copyIn(T other) {
        Feature o = copyInCast(other, Feature.class);
        if (o == null || !super.copyIn(o)) return false;
        this.source = o.getSource();
        this.firstTurn = o.getFirstTurn();
        this.lastTurn = o.getLastTurn();
        this.duration = o.getDuration();
        this.temporary = o.isTemporary();
        this.setScopes(o.getScopeList());
        return true;
    }


    // Serialization

    private static final String DURATION_TAG = "duration";
    private static final String FIRST_TURN_TAG = "firstTurn";
    private static final String LAST_TURN_TAG = "lastTurn";
    private static final String SOURCE_TAG = "source";
    private static final String TEMPORARY_TAG = "temporary";


    /**
     * {@inheritDoc}
     */
    @Override
    protected void writeAttributes(FreeColXMLWriter xw) throws XMLStreamException {
        super.writeAttributes(xw);

        if (getSource() != null) {
            xw.writeAttribute(SOURCE_TAG, getSource());
        }

        if (getFirstTurn() != null) {
            xw.writeAttribute(FIRST_TURN_TAG, getFirstTurn().getNumber());
        }

        if (getLastTurn() != null) {
            xw.writeAttribute(LAST_TURN_TAG, getLastTurn().getNumber());
        }

        if (duration != 0) {
            xw.writeAttribute(DURATION_TAG, duration);
        }

        if (temporary) {
            xw.writeAttribute(TEMPORARY_TAG, temporary);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void writeChildren(FreeColXMLWriter xw) throws XMLStreamException {
        super.writeChildren(xw);

        for (Scope scope : getScopeList()) scope.toXML(xw);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void readAttributes(FreeColXMLReader xr) throws XMLStreamException {
        super.readAttributes(xr);

        final Specification spec = getSpecification();

        String str = xr.getAttribute(SOURCE_TAG, (String)null);
        if (str == null) {
            setSource(null);
        } else if (spec != null) {
            setSource(spec.findType(str));
        }

        int firstTurn = xr.getAttribute(FIRST_TURN_TAG, UNDEFINED);
        if (firstTurn != UNDEFINED) setFirstTurn(new Turn(firstTurn));

        int lastTurn = xr.getAttribute(LAST_TURN_TAG, UNDEFINED);
        if (lastTurn != UNDEFINED) setLastTurn(new Turn(lastTurn));

        duration = xr.getAttribute(DURATION_TAG, 0);

        temporary = xr.getAttribute(TEMPORARY_TAG, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void readChildren(FreeColXMLReader xr) throws XMLStreamException {
        // Clear containers.
        setScopes(null);

        super.readChildren(xr);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void readChild(FreeColXMLReader xr) throws XMLStreamException {
        final String tag = xr.getLocalName();

        if (Scope.TAG.equals(tag)) {
            Scope scope = new Scope(xr);
            if (scope != null) addScope(scope);

        } else {
            super.readChild(xr);
        }
    }

    // getXMLTagName left to subclasses


    // Override Object

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o instanceof Feature) {
            Feature feature = (Feature)o;
            if (!super.equals(o)
                || !Objects.equals(this.source, feature.source)
                || this.duration != feature.duration
                || this.temporary != feature.temporary)
                return false;
            if (firstTurn == null) {
                if (feature.firstTurn != null) return false;
            } else if (feature.firstTurn == null) {
                return false;
            } else if (firstTurn.getNumber() != feature.firstTurn.getNumber()) {
                return false;
            }
            if (lastTurn == null) {
                if (feature.lastTurn != null) return false;
            } else if (feature.lastTurn == null) {
                return false;
            } else if (lastTurn.getNumber() != feature.lastTurn.getNumber()) {
                return false;
            }
            List<Scope> tScopes = getScopeList();
            List<Scope> fScopes = feature.getScopeList();
            if (tScopes.size() != fScopes.size()
                // Not very efficient, but we do not expect many scopes
                || any(this.scopes, s -> !feature.scopes.contains(s))
                || any(feature.scopes, s -> !this.scopes.contains(s)))
                return false;
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash += 31 * hash + Utils.hashCode(source);
        hash += 31 * hash + ((firstTurn == null) ? 0 : firstTurn.getNumber());
        hash += 31 * hash + ((lastTurn == null) ? 0 : lastTurn.getNumber());
        hash += 31 * hash + duration;
        hash += 31 * ((temporary) ? 1 : 0);
        // FIXME: is this safe?  It is an easy way to ignore
        // the order of scope elements.
        hash += sum(getScopeList(), s -> Utils.hashCode(s));
        return hash;
    }
}
