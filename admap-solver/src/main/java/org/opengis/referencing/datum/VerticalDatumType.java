/*
 *    GeoAPI - Java interfaces for OGC/ISO standards
 *    http://www.geoapi.org
 *
 *    Copyright (C) 2004-2011 Open Geospatial Consortium, Inc.
 *    All Rights Reserved. http://www.opengeospatial.org/ogc/legal
 *
 *    Permission to use, copy, and modify this software and its documentation, with
 *    or without modification, for any purpose and without fee or royalty is hereby
 *    granted, provided that you include the following on ALL copies of the software
 *    and documentation or portions thereof, including modifications, that you make:
 *
 *    1. The full text of this NOTICE in a location viewable to users of the
 *       redistributed or derivative work.
 *    2. Notice of any changes or modifications to the OGC files, including the
 *       date changes were made.
 *
 *    THIS SOFTWARE AND DOCUMENTATION IS PROVIDED "AS IS," AND COPYRIGHT HOLDERS MAKE
 *    NO REPRESENTATIONS OR WARRANTIES, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 *    TO, WARRANTIES OF MERCHANTABILITY OR FITNESS FOR ANY PARTICULAR PURPOSE OR THAT
 *    THE USE OF THE SOFTWARE OR DOCUMENTATION WILL NOT INFRINGE ANY THIRD PARTY
 *    PATENTS, COPYRIGHTS, TRADEMARKS OR OTHER RIGHTS.
 *
 *    COPYRIGHT HOLDERS WILL NOT BE LIABLE FOR ANY DIRECT, INDIRECT, SPECIAL OR
 *    CONSEQUENTIAL DAMAGES ARISING OUT OF ANY USE OF THE SOFTWARE OR DOCUMENTATION.
 *
 *    The name and trademarks of copyright holders may NOT be used in advertising or
 *    publicity pertaining to the software without specific, written prior permission.
 *    Title to copyright in this software and any associated documentation will at all
 *    times remain with copyright holders.
 */
package org.opengis.referencing.datum;

import java.util.List;
import java.util.ArrayList;
import org.opengis.util.CodeList;
import org.opengis.annotation.UML;

import static org.opengis.annotation.Obligation.*;
import static org.opengis.annotation.Specification.*;


/**
 * Type of a vertical datum.
 *
 * {@note ISO 19111 ommits the definition of an <code>ELLIPSOIDAL</code> vertical height on intend.
 *        <code>GeographicCRS</code> with ellipsoidal height shall be backed by a three-dimensional
 *        <code>EllipsoidalCS</code>; they should never be built as <code>CompoundCRS</code>. If
 *        nevertheless an ellipsoidal height is needed (for example in order to process a CRS in
 *        the <A HREF="../doc-files/WKT.html">WKT format</A>), implementors can get a suitable
 *        vertical datum type using <code>VerticalDatumType.valueOf("ELLIPSOIDAL")</code>.
 *        Implementors are encouraged to not expose that datum type in public API however.}
 *
 * @author  Martin Desruisseaux (IRD)
 * @version 3.0
 * @since   1.0
 */
@UML(identifier="CD_VerticalDatumType", specification=ISO_19111)
public final class VerticalDatumType extends CodeList<VerticalDatumType> {
    /**
     * Serial number for compatibility with different versions.
     */
    private static final long serialVersionUID = -8161084528823937553L;

    /**
     * List of all enumerations of this type.
     * Must be declared before any enum declaration.
     */
    private static final List<VerticalDatumType> VALUES = new ArrayList<VerticalDatumType>(4);

    /**
     * In some cases, e.g. oil exploration and production, a geological feature, such as the top
     * or bottom of a geologically identifiable and meaningful subsurface layer, is used as a
     * vertical datum. Other variations to the above three vertical datum types may exist
     * and are all included in this type.
     */
    @UML(identifier="other surface", obligation=CONDITIONAL, specification=ISO_19111)
    public static final VerticalDatumType OTHER_SURFACE = new VerticalDatumType("OTHER_SURFACE");

    /**
     * The zero value of the associated vertical coordinate system axis is defined to approximate
     * a constant potential surface, usually the geoid. Such a reference surface is usually
     * determined by a national or scientific authority, and is then a well-known, named datum.
     */
    @UML(identifier="geoidal", obligation=CONDITIONAL, specification=ISO_19111)
    public static final VerticalDatumType GEOIDAL = new VerticalDatumType("GEOIDAL");

    /**
     * The zero point of the vertical axis is defined by a surface that has meaning for the
     * purpose which the associated vertical measurements are used for. For hydrographic charts,
     * this is often a predicted nominal sea surface (i.e., without waves or other wind and current
     * effects) that occurs at low tide. Depths are measured in the direction perpendicular
     * (approximately) to the actual equipotential surfaces of the earth's gravity field,
     * using such procedures as echo-sounding.
     */
    @UML(identifier="depth", obligation=CONDITIONAL, specification=ISO_19111)
    public static final VerticalDatumType DEPTH = new VerticalDatumType("DEPTH");

    /**
     * Atmospheric pressure is the basis for the definition of the origin of the
     * associated vertical coordinate system axis. These are approximations of
     * orthometric heights obtained with the help of a barometer or a barometric
     * altimeter. These values are usually expressed in one of the following units:
     * meters, feet, millibars (used to measure pressure levels), or theta value
     * (units used to measure geopotential height).
     */
    @UML(identifier="barometric", obligation=CONDITIONAL, specification=ISO_19111)
    public static final VerticalDatumType BAROMETRIC = new VerticalDatumType("BAROMETRIC");

    /**
     * Constructs an enum with the given name. The new enum is
     * automatically added to the list returned by {@link #values}.
     *
     * @param name The enum name. This name must not be in use by an other enum of this type.
     */
    private VerticalDatumType(final String name) {
        super(name, VALUES);
    }

    /**
     * Returns the list of {@code VerticalDatumType}s.
     *
     * @return The list of codes declared in the current JVM.
     */
    public static VerticalDatumType[] values() {
        synchronized (VALUES) {
            return VALUES.toArray(new VerticalDatumType[VALUES.size()]);
        }
    }

    /**
     * Returns the list of enumerations of the same kind than this enum.
     */
    public VerticalDatumType[] family() {
        return values();
    }

    /**
     * Returns the vertical datum type that matches the given string, or returns a
     * new one if none match it.
     *
     * @param code The name of the code to fetch or to create.
     * @return A code matching the given name.
     */
    public static VerticalDatumType valueOf(String code) {
        return valueOf(VerticalDatumType.class, code);
    }
}
