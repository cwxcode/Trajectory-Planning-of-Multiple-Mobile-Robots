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
package org.opengis.metadata.identification;

import java.util.Collection;
import org.opengis.metadata.citation.Citation;
import org.opengis.metadata.citation.ResponsibleParty;
import org.opengis.metadata.maintenance.MaintenanceInformation;
import org.opengis.metadata.constraint.Constraints;
import org.opengis.metadata.distribution.Format;
import org.opengis.util.InternationalString;
import org.opengis.annotation.UML;
import org.opengis.annotation.Profile;

import static org.opengis.annotation.Obligation.*;
import static org.opengis.annotation.Specification.*;
import static org.opengis.annotation.ComplianceLevel.*;


/**
 * Basic information required to uniquely identify a resource or resources.
 *
 * @author  Martin Desruisseaux (IRD)
 * @author  Cory Horner (Refractions Research)
 * @version 3.0
 * @since   2.0
 *
 * @navassoc 1 - - Citation
 * @navassoc - - - Progress
 * @navassoc - - - ResponsibleParty
 * @navassoc - - - MaintenanceInformation
 * @navassoc - - - BrowseGraphic
 * @navassoc - - - Format
 * @navassoc - - - Keywords
 * @navassoc - - - Usage
 * @navassoc - - - Constraints
 * @navassoc - - - AggregateInformation
 */
@UML(identifier="MD_Identification", specification=ISO_19115)
public interface Identification {
    /**
     * Citation data for the resource(s).
     *
     * @return Citation data for the resource(s).
     */
    @Profile(level=CORE)
    @UML(identifier="citation", obligation=MANDATORY, specification=ISO_19115)
    Citation getCitation();

    /**
     * Brief narrative summary of the content of the resource(s).
     *
     * @return Brief narrative summary of the content.
     */
    @Profile(level=CORE)
    @UML(identifier="abstract", obligation=MANDATORY, specification=ISO_19115)
    InternationalString getAbstract();

    /**
     * Summary of the intentions with which the resource(s) was developed.
     *
     * @return The intentions with which the resource(s) was developed, or {@code null}.
     */
    @UML(identifier="purpose", obligation=OPTIONAL, specification=ISO_19115)
    InternationalString getPurpose();

    /**
     * Recognition of those who contributed to the resource(s).
     *
     * @return Recognition of those who contributed to the resource(s).
     */
    @UML(identifier="credit", obligation=OPTIONAL, specification=ISO_19115)
    Collection<String> getCredits();

    /**
     * Status of the resource(s).
     *
     * @return Status of the resource(s), or {@code null}.
     */
    @UML(identifier="status", obligation=OPTIONAL, specification=ISO_19115)
    Collection<Progress> getStatus();

    /**
     * Identification of, and means of communication with, person(s) and organizations(s)
     * associated with the resource(s).
     *
     * @return Means of communication with person(s) and organizations(s) associated with the
     *         resource(s).
     */
    @Profile(level=CORE)
    @UML(identifier="pointOfContact", obligation=OPTIONAL, specification=ISO_19115)
    Collection<? extends ResponsibleParty> getPointOfContacts();

    /**
     * Provides information about the frequency of resource updates, and the scope of those updates.
     *
     * @return Frequency and scope of resource updates.
     */
    @UML(identifier="resourceMaintenance", obligation=OPTIONAL, specification=ISO_19115)
    Collection<? extends MaintenanceInformation> getResourceMaintenances();

    /**
     * Provides a graphic that illustrates the resource(s) (should include a legend for the graphic).
     *
     * @return A graphic that illustrates the resource(s).
     */
    @UML(identifier="graphicOverview", obligation=OPTIONAL, specification=ISO_19115)
    Collection<? extends BrowseGraphic> getGraphicOverviews();

    /**
     * Provides a description of the format of the resource(s).
     *
     * @return Description of the format.
     */
    @UML(identifier="resourceFormat", obligation=OPTIONAL, specification=ISO_19115)
    Collection<? extends Format> getResourceFormats();

    /**
     * Provides category keywords, their type, and reference source.
     *
     * @return Category keywords, their type, and reference source.
     */
    @UML(identifier="descriptiveKeywords", obligation=OPTIONAL, specification=ISO_19115)
    Collection<? extends Keywords> getDescriptiveKeywords();

    /**
     * Provides basic information about specific application(s) for which the resource(s)
     * has/have been or is being used by different users.
     *
     * @return Information about specific application(s) for which the resource(s)
     *         has/have been or is being used.
     */
    @UML(identifier="resourceSpecificUsage", obligation=OPTIONAL, specification=ISO_19115)
    Collection<? extends Usage> getResourceSpecificUsages();

    /**
     * Provides information about constraints which apply to the resource(s).
     *
     * @return Constraints which apply to the resource(s).
     */
    @UML(identifier="resourceConstraints", obligation=OPTIONAL, specification=ISO_19115)
    Collection<? extends Constraints> getResourceConstraints();

    /**
     * Provides aggregate dataset information.
     *
     * @return Aggregate dataset information.
     *
     * @since 2.1
     */
    @UML(identifier="aggregationInfo", obligation=OPTIONAL, specification=ISO_19115)
    Collection<? extends AggregateInformation> getAggregationInfo();
}
