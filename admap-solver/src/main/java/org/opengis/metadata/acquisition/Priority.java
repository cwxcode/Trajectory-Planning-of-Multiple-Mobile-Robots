/*
 *    GeoAPI - Java interfaces for OGC/ISO standards
 *    http://www.geoapi.org
 *
 *    Copyright (C) 2009-2011 Open Geospatial Consortium, Inc.
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
package org.opengis.metadata.acquisition;

import java.util.ArrayList;
import java.util.List;

import org.opengis.annotation.UML;
import org.opengis.util.CodeList;

import static org.opengis.annotation.Obligation.*;
import static org.opengis.annotation.Specification.*;


/**
 * Ordered list of priorities.
 *
 * @author  Cédric Briançon (Geomatys)
 * @version 3.0
 * @since   2.3
 */
@UML(identifier="MI_PriorityCode", specification=ISO_19115_2)
public final class Priority extends CodeList<Priority> {
    /**
     * Serial number for compatibility with different versions.
     */
    private static final long serialVersionUID = -3504801926504645861L;

    /**
     * List of all enumerations of this type.
     * Must be declared before any enum declaration.
     */
    private static final List<Priority> VALUES = new ArrayList<Priority>(4);

    /**
     * Decisive importance.
     */
    @UML(identifier="critical", obligation=CONDITIONAL, specification=ISO_19115_2)
    public static final Priority CRITICAL = new Priority("CRITICAL");

    /**
     * Requires resources to be made available.
     */
    @UML(identifier="highImportance", obligation=CONDITIONAL, specification=ISO_19115_2)
    public static final Priority HIGH_IMPORTANCE = new Priority("HIGH_IMPORTANCE");

    /**
     * Normal operation priority.
     */
    @UML(identifier="mediumImportance", obligation=CONDITIONAL, specification=ISO_19115_2)
    public static final Priority MEDIUM_IMPORTANCE = new Priority("MEDIUM_IMPORTANCE");

    /**
     * To be completed when resources are available
     */
    @UML(identifier="lowImportance", obligation=CONDITIONAL, specification=ISO_19115_2)
    public static final Priority LOW_IMPORTANCE = new Priority("LOW_IMPORTANCE");

    /**
     * Constructs an enum with the given name. The new enum is
     * automatically added to the list returned by {@link #values}.
     *
     * @param name The enum name. This name must not be in use by an other enum of this type.
     */
    private Priority(final String name) {
        super(name, VALUES);
    }

    /**
     * Returns the list of {@code Priority}s.
     *
     * @return The list of codes declared in the current JVM.
     */
    public static Priority[] values() {
        synchronized (VALUES) {
            return VALUES.toArray(new Priority[VALUES.size()]);
        }
    }

    /**
     * Returns the list of enumerations of the same kind than this enum.
     */
    public Priority[] family() {
        return values();
    }

    /**
     * Returns the priority that matches the given string, or returns a
     * new one if none match it.
     *
     * @param code The name of the code to fetch or to create.
     * @return A code matching the given name.
     */
    public static Priority valueOf(String code) {
        return valueOf(Priority.class, code);
    }
}
