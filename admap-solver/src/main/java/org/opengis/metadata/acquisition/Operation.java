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

import java.util.Collection;

import org.opengis.annotation.UML;
import org.opengis.metadata.Identifier;
import org.opengis.metadata.citation.Citation;
import org.opengis.metadata.identification.Progress;
import org.opengis.util.InternationalString;

import static org.opengis.annotation.Obligation.*;
import static org.opengis.annotation.Specification.*;


/**
 * Designations for the operation used to acquire the dataset.
 *
 * @author  Cédric Briançon (Geomatys)
 * @version 3.0
 * @since   2.3
 *
 * @navassoc 1 - - Citation
 * @navassoc 1 - - Identifier
 * @navassoc 1 - - Progress
 * @navassoc 1 - - OperationType
 * @navassoc - - - Operation
 * @navassoc - - - Objective
 * @navassoc - - - Plan
 * @navassoc - - - Platform
 * @navassoc - - - Event
 */
@UML(identifier="MI_Operation", specification=ISO_19115_2)
public interface Operation {
    /**
     * Description of the mission on which the platform observations are made and the
     * objectives of that mission.
     *
     * @return Description of the mission.
     */
    @UML(identifier="description", obligation=OPTIONAL, specification=ISO_19115_2)
    InternationalString getDescription();

    /**
     * Identification of the mission.
     *
     * @return Identification of the mission.
     */
    @UML(identifier="citation", obligation=OPTIONAL, specification=ISO_19115_2)
    Citation getCitation();

    /**
     * Unique identification of the operation.
     *
     * @return Unique identification of the operation.
     */
    @UML(identifier="identifier", obligation=MANDATORY, specification=ISO_19115_2)
    Identifier getIdentifier();

    /**
     * Status of the data acquisition.
     *
     * @return Status of the data acquisition.
     */
    @UML(identifier="status", obligation=MANDATORY, specification=ISO_19115_2)
    Progress getStatus();

    /**
     * Collection technique for the operation.
     *
     * @return Collection technique for the operation.
     */
    @UML(identifier="type", obligation=OPTIONAL, specification=ISO_19115_2)
    OperationType getType();

    /**
     * Sub-missions that make up part of a larger mission.
     *
     * @return Sub-missions.
     */
    @UML(identifier="childOperation", obligation=OPTIONAL, specification=ISO_19115_2)
    Collection<? extends Operation> getChildOperations();

    /**
     * Object(s) or area(s) of interest to be sensed.
     *
     * @return Object(s) or area(s) of interest.
     */
    @UML(identifier="objective", obligation=OPTIONAL, specification=ISO_19115_2)
    Collection<? extends Objective> getObjectives();

    /**
     * Heritage of the operation.
     *
     * @return Heritage of the operation.
     */
    @UML(identifier="parentOperation", obligation=MANDATORY, specification=ISO_19115_2)
    Operation getParentOperation();

    /**
     * Plan satisfied by the operation.
     *
     * @return Plan satisfied by the operation.
     */
    @UML(identifier="plan", obligation=OPTIONAL, specification=ISO_19115_2)
    Plan getPlan();

    /**
     * Platform (or platforms) used in the operation.
     *
     * @return Platforms used in the operation.
     */
    @UML(identifier="platform", obligation=OPTIONAL, specification=ISO_19115_2)
    Collection<? extends Platform> getPlatforms();

    /**
     * Record of an event occurring during an operation.
     *
     * @return Record of an event occurring during an operation.
     */
    @UML(identifier="significantEvent", obligation=OPTIONAL, specification=ISO_19115_2)
    Collection<? extends Event> getSignificantEvents();
}
