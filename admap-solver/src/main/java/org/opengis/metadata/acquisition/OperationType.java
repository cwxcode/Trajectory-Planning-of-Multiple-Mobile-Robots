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
 * Code indicating whether the data contained in this packet is real (originates from live-fly
 * or other non-simulated operational sources), simulated (originates from target simulator sources),
 * or synthesized (a mix of real and simulated data).
 *
 * @author  Cédric Briançon (Geomatys)
 * @version 3.0
 * @since   2.3
 */
@UML(identifier="MI_OperationTypeCode", specification=ISO_19115_2)
public final class OperationType extends CodeList<OperationType> {
    /**
     * Serial number for compatibility with different versions.
     */
    private static final long serialVersionUID = -4952647967684867284L;

    /**
     * List of all enumerations of this type.
     * Must be declared before any enum declaration.
     */
    private static final List<OperationType> VALUES = new ArrayList<OperationType>(3);

    /**
     * Originates from live-fly or other non-simulated operational source.
     */
    @UML(identifier="real", obligation=CONDITIONAL, specification=ISO_19115_2)
    public static final OperationType REAL = new OperationType("REAL");

    /**
     * Originates from target simulator sources.
     */
    @UML(identifier="simulated", obligation=CONDITIONAL, specification=ISO_19115_2)
    public static final OperationType SIMULATED = new OperationType("SIMULATED");

    /**
     * Mix of real and simulated data.
     */
    @UML(identifier="synthesized", obligation=CONDITIONAL, specification=ISO_19115_2)
    public static final OperationType SYNTHESIZED = new OperationType("SYNTHESIZED");

    /**
     * Constructs an enum with the given name. The new enum is
     * automatically added to the list returned by {@link #values}.
     *
     * @param name The enum name. This name must not be in use by an other enum of this type.
     */
    private OperationType(final String name) {
        super(name, VALUES);
    }

    /**
     * Returns the list of {@code OperationType}s.
     *
     * @return The list of codes declared in the current JVM.
     */
    public static OperationType[] values() {
        synchronized (VALUES) {
            return VALUES.toArray(new OperationType[VALUES.size()]);
        }
    }

    /**
     * Returns the list of enumerations of the same kind than this enum.
     */
    public OperationType[] family() {
        return values();
    }

    /**
     * Returns the operation type that matches the given string, or returns a
     * new one if none match it.
     *
     * @param code The name of the code to fetch or to create.
     * @return A code matching the given name.
     */
    public static OperationType valueOf(String code) {
        return valueOf(OperationType.class, code);
    }
}
