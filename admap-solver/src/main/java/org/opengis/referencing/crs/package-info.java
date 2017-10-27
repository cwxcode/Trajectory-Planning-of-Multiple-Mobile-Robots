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
 * {@linkplain org.opengis.referencing.crs.CoordinateReferenceSystem Coordinate reference systems}
 * ({@linkplain org.opengis.referencing.cs.CoordinateSystem coordinate systems} with a
 * {@linkplain org.opengis.referencing.datum.Datum datum}). The following is adapted from
 * {@linkplain org.opengis.annotation.Specification#ISO_19111 OpenGIS&reg; Spatial Referencing by
 * Coordinates (Topic 2)} specification.
 *
 * <P ALIGN="justify">A coordinate reference system consists of one coordinate
 * system that is related to the earth through one datum. The coordinate system
 * is composed of a set of coordinate axes with specified units of measure. This
 * concept implies the mathematical rules that define how coordinate values are
 * calculated from distances, angles and other geometric elements and vice versa.</P>
 *
 * <P ALIGN="justify">A datum specifies the relationship of a coordinate system to the
 * earth, thus ensuring that the abstract mathematical concept "coordinate system" can
 * be applied to the practical problem of describing positions of features on or
 * near the earth's surface by means of coordinates. The resulting combination
 * of coordinate system and datum is a coordinate reference system. Each datum
 * subtype can be associated with only specific types of coordinate systems. The
 * datum implicitly (occasionally explicitly) contains the values chosen for the
 * set parameters that represent the degrees of freedom of the coordinate system.
 * A datum therefore implies a choice regarding the approximate origin and
 * orientation of the coordinate system.</P>
 *
 * <P ALIGN="justify">For the purposes of this specification, a coordinate
 * reference system shall not change with time, with the exception of engineering
 * coordinate reference systems defined on moving platforms such as cars, ships,
 * aircraft and spacecraft. The intention is to exclude the option to describe
 * the time variability of geodetic coordinate reference systems as a result of
 * e.g. tectonic motion. This variability is part of the subject matter of geophysical
 * and geodetic science. The model for spatial referencing by coordinates described
 * in this specification is in principle not suitable for such zero-order geodetic
 * problems. Such time-variability of coordinate reference systems shall be covered
 * in the spatial referencing model described in this specification by creating
 * different coordinate reference systems, each with a different datum, for
 * (consecutive) epochs. The date of realisation of the datum shall then be
 * included in its definition. It is further recommended to include the date of
 * realisation in the names of those datums and coordinate reference systems.</P>
 *
 * <P>&nbsp;</P>
 * <H3>Principal sub-types of coordinate reference system</H3>
 * <P ALIGN="justify">Geodetic survey practice usually divides coordinate
 * reference systems into a number of sub-types. The common classification
 * criterion for sub-typing of coordinate reference systems can be described
 * as the way in which they deal with earth curvature. This has a direct effect
 * on the portion of the earth's surface that can be covered by that type of CRS
 * with an acceptable degree of error. Thus the following principal sub-types of
 * coordinate reference system are distinguished:</P>
 *
 * <BLOCKQUOTE>
 * <P ALIGN="justify"><B>Geocentric</B>:
 * Type of coordinate reference system that deals with the earth's curvature
 * by taking the 3D spatial view, which obviates the need to model the earth's
 * curvature. The origin of a geocentric CRS is at the approximate centre of
 * mass of the earth.</P>
 *
 * <P ALIGN="justify"><B>Geographic</B>:
 * Type of coordinate reference system based on an ellipsoidal approximation of
 * the geoid. This provides an accurate representation of the geometry of geographic
 * features for a large portion of the earth's surface. Geographic coordinate reference
 * systems can be 2D or 3D. A 2D Geographic CRS is used when positions of features are
 * described on the surface of the reference ellipsoid; a 3D Geographic CRS is used when
 * positions are described on, above or below the reference ellipsoid.</P>
 *
 * <P ALIGN="justify"><B>Projected</B>:
 * Type of coordinate reference system that is based on an approximation of the
 * shape of the earth's surface by a plane. The distortion that is inherent to the
 * approximation is carefully controlled and known. Distortion correction is commonly
 * applied to calculated bearings and distances to produce values that are a close
 * match to actual field values.</P>
 *
 * <P ALIGN="justify"><B>Engineering</B>:
 * Type of coordinate reference system that is that is used only in a contextually
 * local sense. This sub-type is used to model two broad categories of local
 * coordinate reference systems:</P>
 * <UL>
 *   <LI>earth-fixed systems, applied to engineering activities on or near the
 *       surface of the earth;</LI>
 *   <LI>coordinates on moving platforms such as road vehicles, vessels, aircraft
 *       or spacecraft.</LI>
 * </UL>
 *
 * <P ALIGN="justify"><B>Image</B>:
 * An Image CRS is an Engineering CRS applied to images. Image CRSs are treated as
 * a separate sub-type because a separate user community exists for images with its
 * own vocabulary. The definition of the associated Image Datum contains two data
 * attributes not relevant for other datums and coordinate reference systems.</P>
 *
 * <P ALIGN="justify"><B>Vertical</B>:
 * Type of coordinate reference system used for the recording of heights or depths.
 * Vertical CRSs make use of the direction of gravity to define the concept of
 * height or depth, but its relationship with gravity may not be straightforward.
 * By implication ellipsoidal heights (h) cannot be captured in a vertical coordinate
 * reference system. Ellipsoidal heights cannot exist independently, but only as
 * inseparable part of a 3D coordinate tuple defined in a geographic 3D coordinate
 * reference system.</P>
 *
 * <P ALIGN="justify"><B>Temporal</B>:
 * Used for the recording of time in association with any of the listed spatial
 * coordinate reference systems only.</P>
 * </BLOCKQUOTE>
 *
 * <P>&nbsp;</P>
 * <H3>Additional sub-types of coordinate reference system</H3>
 * <P ALIGN="justify">In addition to the principal sub-types, so called because
 * they represent concepts generally known in geodetic practice, two more sub-types
 * have been defined to permit modelling of certain relationships and constraints
 * that exist between the principal sub-types. These additional sub-types are
 * <A HREF="#CompoundCRS">Compound coordinate reference system</A> and
 * <A HREF="#DerivedCRS">Derived coordinate reference system</A>.</P>
 *
 * <BLOCKQUOTE>
 * <P ALIGN="justify"><B><A NAME="CompoundCRS">Compound coordinate reference system</A></B><BR>
 * The traditional separation of horizontal and vertical position has resulted in
 * coordinate reference systems that are horizontal (2D) in nature and vertical (1D).
 * It is established practice to combine the horizontal coordinates of a point with
 * a height or depth from a different coordinate reference system. The coordinate
 * reference system to which these 3D coordinates are referenced combines the separate
 * horizontal and vertical coordinate reference systems of the horizontal and vertical
 * coordinates. Such a coordinate system is called a compound coordinate reference
 * system (Compound CRS). It consists of an ordered sequence of the two or more single
 * coordinate reference systems.</P>
 *
 * <P ALIGN="justify"><B><A NAME="DerivedCRS">Derived coordinate reference system</A></B><BR>
 * Some coordinate reference systems are defined by applying a coordinate conversion to
 * another coordinate reference system. Such a coordinate reference system is called a
 * Derived CRS and the coordinate reference system it was derived from by applying the
 * conversion is called the Source or Base CRS. A coordinate conversion is an arithmetic
 * operation with zero or more parameters that have defined values. The Source CRS and
 * Derived CRS have the same Datum. The best-known example of a Derived CRS is a
 * Projected CRS, which is always derived from a source Geographic CRS by applying the
 * coordinate conversion known as a map projection.</P>
 *
 * <P ALIGN="justify">In principle, all sub-types of coordinate reference system may
 * take on the role of either Source or Derived CRS with the exception of a Geocentric
 * CRS and a Projected CRS. The latter is modelled as an object class under its own name,
 * rather than as a general Derived CRS of type "projected". This has been done to honour
 * common practice, which acknowledges Projected CRSs as one of the best known types of
 * coordinate reference systems.</P>
 *
 * <P ALIGN="justify">An example of a Derived CRS:
 * one of which the unit of measure has been modified with respect to an earlier
 * defined Geographic CRS, which then takes the role of Source CRS.</P>
 * </BLOCKQUOTE>
 *
 * @author  Martin Desruisseaux (IRD)
 * @version 3.0
 * @since   1.0
 */
package org.opengis.referencing.crs;
