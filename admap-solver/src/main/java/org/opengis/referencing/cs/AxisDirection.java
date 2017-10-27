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
package org.opengis.referencing.cs;

import java.util.List;
import java.util.ArrayList;

import org.opengis.util.CodeList;
import org.opengis.annotation.UML;

import static org.opengis.annotation.Obligation.*;
import static org.opengis.annotation.Specification.*;


/**
 * The direction of positive increments in the coordinate value for a coordinate system
 * axis. This direction is exact in some cases, and is approximate in other cases.
 * <p>
 * Some coordinate systems use non-standard orientations.  For example,
 * the first axis in South African grids usually points West, instead of
 * East. This information is obviously relevant for algorithms converting
 * South African grid coordinates into Lat/Long.
 *
 * @author  Martin Desruisseaux (IRD)
 * @version 3.0
 * @since   1.0
 */
@UML(identifier="CS_AxisDirection", specification=ISO_19111)
public final class AxisDirection extends CodeList<AxisDirection> {
    /**
     * Serial number for compatibility with different versions.
     */
    private static final long serialVersionUID = -4405275475770755714L;

    /**
     * List of all enumerations of this type.
     * Must be declared before any enum declaration.
     */
    private static final List<AxisDirection> VALUES = new ArrayList<AxisDirection>(32);

    /**
     * Unknown or unspecified axis orientation.
     *
     * @category Other
     */
    @UML(identifier="CS_AxisOrientationEnum.CS_AO_Other", specification=OGC_01009)
    public static final AxisDirection OTHER = new AxisDirection("OTHER");

    /**
     * Axis positive direction is north. In a geographic or projected CRS,
     * north is defined through the geodetic datum. In an engineering CRS,
     * north may be defined with respect to an engineering object rather
     * than a geographical direction.
     *
     * @category Rose
     */
    @UML(identifier="north", obligation=CONDITIONAL, specification=ISO_19111)
    public static final AxisDirection NORTH = new AxisDirection("NORTH");

    /**
     * Axis positive direction is approximately north-north-east.
     *
     * @category Rose
     * @since 2.0
     */
    @UML(identifier="northNorthEast", obligation=CONDITIONAL, specification=ISO_19111)
    public static final AxisDirection NORTH_NORTH_EAST = new AxisDirection("NORTH_NORTH_EAST");

    /**
     * Axis positive direction is approximately north-east.
     *
     * @category Rose
     * @since 2.0
     */
    @UML(identifier="northEast", obligation=CONDITIONAL, specification=ISO_19111)
    public static final AxisDirection NORTH_EAST = new AxisDirection("NORTH_EAST");

    /**
     * Axis positive direction is approximately east-north-east.
     *
     * @category Rose
     * @since 2.0
     */
    @UML(identifier="eastNorthEast", obligation=CONDITIONAL, specification=ISO_19111)
    public static final AxisDirection EAST_NORTH_EAST = new AxisDirection("EAST_NORTH_EAST");

    /**
     * Axis positive direction is &pi;/2 radians clockwise from north.
     * This is usually used for Grid X coordinates and Longitude.
     *
     * @category Rose
     */
    @UML(identifier="east", obligation=CONDITIONAL, specification=ISO_19111)
    public static final AxisDirection EAST = new AxisDirection("EAST");

    /**
     * Axis positive direction is approximately east-south-east.
     *
     * @category Rose
     * @since 2.0
     */
    @UML(identifier="eastSouthEast", obligation=CONDITIONAL, specification=ISO_19111)
    public static final AxisDirection EAST_SOUTH_EAST = new AxisDirection("EAST_SOUTH_EAST");

    /**
     * Axis positive direction is approximately south-east.
     *
     * @category Rose
     * @since 2.0
     */
    @UML(identifier="southEast", obligation=CONDITIONAL, specification=ISO_19111)
    public static final AxisDirection SOUTH_EAST = new AxisDirection("SOUTH_EAST");

    /**
     * Axis positive direction is approximately south-south-east.
     *
     * @category Rose
     * @since 2.0
     */
    @UML(identifier="southSouthEast", obligation=CONDITIONAL, specification=ISO_19111)
    public static final AxisDirection SOUTH_SOUTH_EAST = new AxisDirection("SOUTH_SOUTH_EAST");

    /**
     * Axis positive direction is &pi; radians clockwise from north.
     *
     * @category Rose
     */
    @UML(identifier="south", obligation=CONDITIONAL, specification=ISO_19111)
    public static final AxisDirection SOUTH = new AxisDirection("SOUTH");

    /**
     * Axis positive direction is approximately south-south-west.
     *
     * @category Rose
     * @since 2.0
     */
    @UML(identifier="southSouthWest", obligation=CONDITIONAL, specification=ISO_19111)
    public static final AxisDirection SOUTH_SOUTH_WEST = new AxisDirection("SOUTH_SOUTH_WEST");

    /**
     * Axis positive direction is approximately south-west.
     *
     * @category Rose
     * @since 2.0
     */
    @UML(identifier="southWest", obligation=CONDITIONAL, specification=ISO_19111)
    public static final AxisDirection SOUTH_WEST = new AxisDirection("SOUTH_WEST");

    /**
     * Axis positive direction is approximately west-south-west.
     *
     * @category Rose
     * @since 2.0
     */
    @UML(identifier="westSouthWest", obligation=CONDITIONAL, specification=ISO_19111)
    public static final AxisDirection WEST_SOUTH_WEST = new AxisDirection("WEST_SOUTH_WEST");

    /**
     * Axis positive direction is 3&pi;/2 radians clockwise from north.
     * This is usually used for Grid X coordinates and Longitude.
     *
     * @category Rose
     */
    @UML(identifier="west", obligation=CONDITIONAL, specification=ISO_19111)
    public static final AxisDirection WEST = new AxisDirection("WEST");

    /**
     * Axis positive direction is approximately west-north-west.
     *
     * @category Rose
     * @since 2.0
     */
    @UML(identifier="westNorthWest", obligation=CONDITIONAL, specification=ISO_19111)
    public static final AxisDirection WEST_NORTH_WEST = new AxisDirection("WEST_NORTH_WEST");

    /**
     * Axis positive direction is approximately north-west.
     *
     * @category Rose
     * @since 2.0
     */
    @UML(identifier="northWest", obligation=CONDITIONAL, specification=ISO_19111)
    public static final AxisDirection NORTH_WEST = new AxisDirection("NORTH_WEST");

    /**
     * Axis positive direction is approximately north-north-west.
     *
     * @category Rose
     * @since 2.0
     */
    @UML(identifier="northNorthWest", obligation=CONDITIONAL, specification=ISO_19111)
    public static final AxisDirection NORTH_NORTH_WEST = new AxisDirection("NORTH_NORTH_WEST");

    /**
     * Axis positive direction is up relative to gravity.
     * This is used for {@linkplain org.opengis.referencing.crs.VerticalCRS vertical}
     * coordinate reference systems.
     *
     * @category Vertical
     */
    @UML(identifier="up", obligation=CONDITIONAL, specification=ISO_19111)
    public static final AxisDirection UP = new AxisDirection("UP");

    /**
     * Axis positive direction is down relative to gravity.
     * This is used for {@linkplain org.opengis.referencing.crs.VerticalCRS vertical}
     * coordinate reference systems.
     *
     * @category Vertical
     */
    @UML(identifier="down", obligation=CONDITIONAL, specification=ISO_19111)
    public static final AxisDirection DOWN = new AxisDirection("DOWN");

    /**
     * Axis positive direction is in the equatorial plane from the centre of the
     * modelled earth towards the intersection of the equator with the prime meridian.
     *
     * @category Geocentric
     * @since 2.0
     */
    @UML(identifier="geocentricX", obligation=CONDITIONAL, specification=ISO_19111)
    public static final AxisDirection GEOCENTRIC_X = new AxisDirection("GEOCENTRIC_X");

    /**
     * Axis positive direction is in the equatorial plane from the centre of the
     * modelled earth towards the intersection of the equator and the meridian &pi;/2
     * radians eastwards from the prime meridian.
     *
     * @category Geocentric
     * @since 2.0
     */
    @UML(identifier="geocentricY", obligation=CONDITIONAL, specification=ISO_19111)
    public static final AxisDirection GEOCENTRIC_Y = new AxisDirection("GEOCENTRIC_Y");

    /**
     * Axis positive direction is from the centre of the modelled earth parallel to
     * its rotation axis and towards its north pole.
     *
     * @category Geocentric
     * @since 2.0
     */
    @UML(identifier="geocentricZ", obligation=CONDITIONAL, specification=ISO_19111)
    public static final AxisDirection GEOCENTRIC_Z = new AxisDirection("GEOCENTRIC_Z");

    /**
     * Axis positive direction is towards the future.
     * This is used for {@linkplain org.opengis.referencing.crs.TemporalCRS temporal}
     * coordinate reference systems.
     *
     * @departure historic
     *   This code was defined in an older specification (2003) and removed in more recent
     *   edition (2007), but has been kept in GeoAPI.
     *
     * @category Temporal
     */
    @UML(identifier="future", obligation=CONDITIONAL, specification=ISO_19111)
    public static final AxisDirection FUTURE = new AxisDirection("FUTURE");

    /**
     * Axis positive direction is towards the past.
     * This is used for {@linkplain org.opengis.referencing.crs.TemporalCRS temporal}
     * coordinate reference systems.
     *
     * @departure historic
     *   This code was defined in an older specification (2003) and removed in more recent
     *   edition (2007), but has been kept in GeoAPI.
     *
     * @category Temporal
     */
    @UML(identifier="past", obligation=CONDITIONAL, specification=ISO_19111)
    public static final AxisDirection PAST = new AxisDirection("PAST");

    /**
     * Axis positive direction is towards higher pixel column.
     *
     * @category Image
     * @since 2.0
     */
    @UML(identifier="columnPositive", obligation=CONDITIONAL, specification=ISO_19111)
    public static final AxisDirection COLUMN_POSITIVE = new AxisDirection("COLUMN_POSITIVE");

    /**
     * Axis positive direction is towards lower pixel column.
     *
     * @category Image
     * @since 2.0
     */
    @UML(identifier="columnNegative", obligation=CONDITIONAL, specification=ISO_19111)
    public static final AxisDirection COLUMN_NEGATIVE = new AxisDirection("COLUMN_NEGATIVE");

    /**
     * Axis positive direction is towards higher pixel row.
     *
     * @category Image
     * @since 2.0
     */
    @UML(identifier="rowPositive", obligation=CONDITIONAL, specification=ISO_19111)
    public static final AxisDirection ROW_POSITIVE = new AxisDirection("ROW_POSITIVE");

    /**
     * Axis positive direction is towards lower pixel row.
     *
     * @category Image
     * @since 2.0
     */
    @UML(identifier="rowNegative", obligation=CONDITIONAL, specification=ISO_19111)
    public static final AxisDirection ROW_NEGATIVE = new AxisDirection("ROW_NEGATIVE");

    /**
     * Axis positive direction is right in display.
     *
     * @category Display
     * @since 2.0
     */
    @UML(identifier="displayRight", obligation=CONDITIONAL, specification=ISO_19111)
    public static final AxisDirection DISPLAY_RIGHT = new AxisDirection("DISPLAY_RIGHT");

    /**
     * Axis positive direction is left in display.
     *
     * @category Display
     * @since 2.0
     */
    @UML(identifier="displayLeft", obligation=CONDITIONAL, specification=ISO_19111)
    public static final AxisDirection DISPLAY_LEFT = new AxisDirection("DISPLAY_LEFT");

    /**
     * Axis positive direction is towards top of approximately vertical display surface.
     *
     * @category Display
     * @since 2.0
     */
    @UML(identifier="displayUp", obligation=CONDITIONAL, specification=ISO_19111)
    public static final AxisDirection DISPLAY_UP = new AxisDirection("DISPLAY_UP");

    /**
     * Axis positive direction is towards bottom of approximately vertical display surface.
     *
     * @category Display
     * @since 2.0
     */
    @UML(identifier="displayDown", obligation=CONDITIONAL, specification=ISO_19111)
    public static final AxisDirection DISPLAY_DOWN = new AxisDirection("DISPLAY_DOWN");

    /**
     * Constructs an enum with the given name. The new enum is
     * automatically added to the list returned by {@link #values}.
     *
     * @param name The enum name. This name must not be in use by an other enum of this type.
     */
    private AxisDirection(final String name) {
        super(name, VALUES);
    }

    /**
     * Returns the list of {@code AxisDirection}s.
     *
     * @return The list of codes declared in the current JVM.
     */
    public static AxisDirection[] values() {
        synchronized (VALUES) {
            return VALUES.toArray(new AxisDirection[VALUES.size()]);
        }
    }

    /**
     * Returns the list of enumerations of the same kind than this enum.
     */
    public AxisDirection[] family() {
        return values();
    }

    /**
     * Returns the axis direction that matches the given string, or returns a
     * new one if none match it.
     *
     * @param code The name of the code to fetch or to create.
     * @return A code matching the given name.
     */
    public static AxisDirection valueOf(String code) {
        return valueOf(AxisDirection.class, code);
    }
}
