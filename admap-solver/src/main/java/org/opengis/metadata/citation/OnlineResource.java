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
package org.opengis.metadata.citation;

import java.net.URI;
import org.opengis.util.InternationalString;
import org.opengis.annotation.UML;
import org.opengis.annotation.Profile;

import static org.opengis.annotation.Obligation.*;
import static org.opengis.annotation.ComplianceLevel.*;
import static org.opengis.annotation.Specification.*;


/**
 * Information about on-line sources from which the dataset, specification, or
 * community profile name and extended metadata elements can be obtained.
 *
 * @author  Martin Desruisseaux (IRD)
 * @author  Cory Horner (Refractions Research)
 * @version 3.0
 * @since   1.0
 *
 * @navassoc 1 - - OnLineFunction
 */
@UML(identifier="CI_OnlineResource", specification=ISO_19115)
public interface OnlineResource {
    /**
     * Location (address) for on-line access using a Uniform Resource Locator address or
     * similar addressing scheme such as {@code "http://www.statkart.no/isotc211"}.
     *
     * @return Location for on-line access using a Uniform Resource Locator address or similar scheme.
     */
    @Profile(level=CORE)
    @UML(identifier="linkage", obligation=MANDATORY, specification=ISO_19115)
    URI getLinkage();

    /**
     * Connection protocol to be used. Returns {@code null} if none.
     *
     * @return Connection protocol to be used, or {@code null}.
     */
    @UML(identifier="protocol", obligation=OPTIONAL, specification=ISO_19115)
    String getProtocol();

    /**
     * Name of an application profile that can be used with the online resource.
     * Returns {@code null} if none.
     *
     * @return Application profile that can be used with the online resource, or {@code null}.
     */
    @UML(identifier="applicationProfile", obligation=OPTIONAL, specification=ISO_19115)
    String getApplicationProfile();

    /**
     * Name of the online resource. Returns {@code null} if none.
     *
     * @return Name of the online resource, or {@code null}.
     *
     * @since 2.1
     */
    @UML(identifier="name", obligation=OPTIONAL, specification=ISO_19115)
    String getName();

    /**
     * Detailed text description of what the online resource is/does.
     * Returns {@code null} if none.
     *
     * @return Text description of what the online resource is/does, or {@code null}.
     */
    @UML(identifier="description", obligation=OPTIONAL, specification=ISO_19115)
    InternationalString getDescription();

    /**
     * Code for function performed by the online resource.
     * Returns {@code null} if unspecified.
     *
     * @return Function performed by the online resource, or {@code null}.
     */
    @UML(identifier="function", obligation=OPTIONAL, specification=ISO_19115)
    OnLineFunction getFunction();
}
