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

/**
 * {@linkplain org.opengis.metadata.identification.Identification} information
 * (includes data and service identification). The following is adapted from
 * {@linkplain org.opengis.annotation.Specification#ISO_19115 OpenGIS&reg; Metadata (Topic 11)}
 * specification.
 *
 * <P ALIGN="justify">Identification information contains information to uniquely identify the data.
 * Identification information includes information about the citation for the resource, an abstract,
 * the purpose, credit, the status and points of contact.
 * The {@linkplain org.opengis.metadata.identification.Identification identification}
 * entity is mandatory. It may be specified (subclassed) as
 * {@linkplain org.opengis.metadata.identification.DataIdentification data identification}
 * when used to identify data and as
 * {@linkplain org.opengis.metadata.identification.ServiceIdentification service identification}
 * when used to identify a service.</p>
 *
 * <P ALIGN="justify">{@linkplain org.opengis.metadata.identification.Identification}
 * is an aggregate of the following entities:</P>
 * <UL>
 *   <LI>{@link org.opengis.metadata.distribution.Format}, format of the data</LI>
 *   <LI>{@link org.opengis.metadata.identification.BrowseGraphic}, graphic overview of the data</LI>
 *   <LI>{@link org.opengis.metadata.identification.Usage}, specific uses of the data</LI>
 *   <LI>{@link org.opengis.metadata.constraint.Constraints}, constraints placed on the resource</LI>
 *   <LI>{@link org.opengis.metadata.identification.Keywords}, keywords describing the resource</LI>
 *   <LI>{@link org.opengis.metadata.maintenance.MaintenanceInformation}, how often the data is scheduled
 *       to be updated and the scope of the update</LI>
 * </UL>
 *
 * @author  Martin Desruisseaux (IRD)
 * @author  Cory Horner (Refractions Research)
 * @author  Ely Conn (Leica Geosystems Geospatial Imaging, LLC)
 * @version 3.0
 * @since   2.0
 */
package org.opengis.metadata.identification;
