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
package org.opengis.metadata.lineage;

import java.util.Collection;
import org.opengis.metadata.extent.Extent;
import org.opengis.metadata.identification.RepresentativeFraction;
import org.opengis.metadata.citation.Citation;
import org.opengis.referencing.ReferenceSystem;
import org.opengis.util.InternationalString;
import org.opengis.annotation.UML;
import org.opengis.metadata.Identifier;

import static org.opengis.annotation.Obligation.*;
import static org.opengis.annotation.Specification.*;


/**
 * Information about the source data used in creating the data specified by the scope.
 *
 * @author  Martin Desruisseaux (IRD)
 * @author  Cory Horner (Refractions Research)
 * @author  Cédric Briançon (Geomatys)
 * @version 3.0
 * @since   2.0
 *
 * @navassoc & - - RepresentativeFraction
 * @navassoc 1 - - ReferenceSystem
 * @navassoc 1 - - Citation
 * @navassoc - - - Extent
 * @navassoc - - - ProcessStep
 * @navassoc 1 - - Identifier
 * @navassoc 1 - - NominalResolution
 */
@UML(identifier="LI_Source", specification=ISO_19115)
public interface Source {
    /**
     * Detailed description of the level of the source data.
     *
     * @return Description of the level of the source data, or {@code null}.
     *
     * @condition {@linkplain #getSourceExtents() Source extent} not provided.
     */
    @UML(identifier="description", obligation=CONDITIONAL, specification=ISO_19115)
    InternationalString getDescription();

    /**
     * Denominator of the representative fraction on a source map.
     *
     * @return Representative fraction on a source map, or {@code null}.
     */
    @UML(identifier="scaleDenominator", obligation=OPTIONAL, specification=ISO_19115)
    RepresentativeFraction getScaleDenominator();

    /**
     * Spatial reference system used by the source data.
     *
     * @return Spatial reference system used by the source data, or {@code null}.
     */
    @UML(identifier="sourceReferenceSystem", obligation=OPTIONAL, specification=ISO_19115)
    ReferenceSystem getSourceReferenceSystem();

    /**
     * Recommended reference to be used for the source data.
     *
     * @return Recommended reference to be used for the source data, or {@code null}.
     */
    @UML(identifier="sourceCitation", obligation=OPTIONAL, specification=ISO_19115)
    Citation getSourceCitation();

    /**
     * Information about the spatial, vertical and temporal extent of the source data.
     *
     * @return Information about the extent of the source data.
     *
     * @condition {@linkplain #getDescription() Description} not provided.
     */
    @UML(identifier="sourceExtent", obligation=CONDITIONAL, specification=ISO_19115)
    Collection<? extends Extent> getSourceExtents();

    /**
     * Information about an event in the creation process for the source data.
     *
     * @return Information about an event in the creation process.
     */
    @UML(identifier="sourceStep", obligation=OPTIONAL, specification=ISO_19115)
    Collection<? extends ProcessStep> getSourceSteps();

    /**
     * Processing level of the source data.
     *
     * @return Processing level of the source data.
     *
     * @since 2.3
     */
    @UML(identifier="processedLevel", obligation=OPTIONAL, specification=ISO_19115_2)
    Identifier getProcessedLevel();

    /**
     * Distance between consistent parts (centre, left side, right side) of two adjacent
     * pixels.
     *
     * @return Distance between consistent parts of two adjacent pixels.
     *
     * @since 2.3
     */
    @UML(identifier="resolution", obligation=OPTIONAL, specification=ISO_19115_2)
    NominalResolution getResolution();
}
