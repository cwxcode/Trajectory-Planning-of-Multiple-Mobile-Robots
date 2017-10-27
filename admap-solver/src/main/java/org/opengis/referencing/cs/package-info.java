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
 * {@linkplain org.opengis.referencing.cs.CoordinateSystem Coordinate systems} and their
 * {@linkplain org.opengis.referencing.cs.CoordinateSystemAxis axis}. The following is adapted from
 * {@linkplain org.opengis.annotation.Specification#ISO_19111 OpenGIS&reg; Spatial Referencing by
 * Coordinates (Topic 2)} specification.
 *
 * <H3>Coordinate system</H3>
 * <P ALIGN="justify">The coordinates of points are recorded in a coordinate
 * system. A coordinate system is the set of coordinate system axes that spans
 * the coordinate space. This concept implies the set of mathematical rules that
 * determine how coordinates are associated with invariant quantities such as
 * angles and distances. In other words, a coordinate system implies how coordinates
 * are calculated from geometric elements such as distances and angles and vice
 * versa. The calculus required to derive angles and distances from point coordinates
 * and vice versa in a map plane is simple Euclidean 2D arithmetic. To do the same
 * on the surface of an ellipsoid (curved 2D space) involves more complex ellipsoidal
 * calculus. These rules cannot be specified in detail, but are implied in the
 * geometric properties of the coordinate space.</P>
 *
 * <BLOCKQUOTE><P ALIGN="justify"><FONT SIZE=-1><B>NOTE:</B> The word "distances"
 * is used loosely in the above description. Strictly speaking distances are not invariant
 * quantities, as they are expressed in the unit of measure defined for the coordinate system;
 * ratios of distances are invariant.</FONT></P></BLOCKQUOTE>
 *
 * <P ALIGN="justify">One {@linkplain org.opengis.referencing.cs.CoordinateSystem coordinate system}
 * may be used by multiple {@linkplain org.opengis.referencing.crs.CoordinateReferenceSystem coordinate
 * reference systems}. A coordinate system is composed of an ordered set of coordinate
 * system axes, the number of axes being equal to the dimension of the space of which
 * it describes the geometry. Its axes can be spatial, temporal, or mixed. Coordinates
 * in coordinate tuples shall be supplied in the same order as the coordinate axes are
 * defined.</P>
 *
 * <P ALIGN="justify">The dimension of the coordinate space, the names, the units
 * of measure, the directions and sequence of the axes are all part of the Coordinate
 * System definition. The number of coordinates in a tuple and consequently the number
 * of coordinate axes in a coordinate system shall be equal to the number of coordinate
 * axes in the coordinate system. It is therefore not permitted to supply a coordinate
 * tuple with two heights of different definition in the same coordinate tuple.</P>
 *
 * <P ALIGN="justify">Coordinate systems are divided into subtypes by the geometric
 * properties of the coordinate space spanned and the geometric properties of the axes
 * themselves (straight or curved; perpendicular or not). Certain subtypes of coordinate
 * system can only be used with specific subtypes of coordinate reference system.</P>
 *
 * <BR>
 * <H3>Coordinate system axis</H3>
 * <P ALIGN="justify">A coordinate system is composed of an ordered set of coordinate
 * system axes. Each of its axes is completely characterised by a unique combination
 * of axis name, axis abbreviation, axis direction and axis unit of measure.</P>
 *
 * <P ALIGN="justify">The concept of coordinate axis requires some clarification.
 * Consider an arbitrary <VAR>x</VAR>, <VAR>y</VAR>, <VAR>z</VAR> coordinate system.
 * The <VAR>x</VAR>-axis may be defined as the locus of points with
 * <VAR>y</VAR>&nbsp;=&nbsp;<VAR>z</VAR>&nbsp;=&nbsp;0. This is easily enough
 * understood if the <VAR>x</VAR>, <VAR>y</VAR>, <VAR>z</VAR> coordinate system is a
 * Cartesian system and the space it describes is Euclidean. It becomes a bit more
 * difficult to understand in the case of a strongly curved space, such as the surface
 * of an ellipsoid, its geometry described by an ellipsoidal coordinate system (2D or 3D).
 * Applying the same definition by analogy to the curvilinear latitude and longitude
 * coordinates the latitude axis would be the equator and the longitude axis would be
 * the prime meridian, which is not a satisfactory definition.</P>
 *
 * <P ALIGN="justify">Bearing in mind that the order of the coordinates in a coordinate
 * tuple must be the same as the defined order of the coordinate axes, the "<VAR>i</VAR>-th"
 * coordinate axis of a coordinate system is defined as the locus of points for which all
 * coordinates with sequence number not equal to "<VAR>i</VAR>", have a constant value
 * locally (whereby <VAR>i</VAR> = 1...<VAR>n</VAR>, and <VAR>n</VAR> is the dimension
 * of the coordinate space).</P>
 *
 * <P ALIGN="justify">It will be evident that the addition of the word "locally" in this
 * definition apparently adds an element of ambiguity and this is intentional. However,
 * the definition of the coordinate parameter associated with any axis must be unique.
 * The coordinate axis itself should not be interpreted as a unique mathematical object,
 * the associated coordinate parameter should. For example, geodetic latitude is defined
 * as the "Angle from the equatorial plane to the perpendicular to the ellipsoid through
 * a given point, northwards usually treated as positive". However, when used in an ellipsoidal
 * coordinate system the geodetic latitude axis will be described as pointing "north". In two
 * different points on the ellipsoid the direction "north" will be a spatially different direction,
 * but the concept of latitude is the same.</P>
 *
 * <P ALIGN="justify">Furthermore the specified direction of the coordinate axes is often
 * only approximate; two geographic coordinate reference systems will make use of the same
 * ellipsoidal coordinate system. These coordinate systems are associated with the earth
 * through two different geodetic datums, which may lead to the two systems being slightly
 * rotated w.r.t. each other.</P>
 *
 * <P ALIGN="justify"><A NAME="AxisNames">Usage of coordinate system axis names is constrained
 * by geodetic custom in a number of cases, depending mainly on the coordinate reference system
 * type.</A> These constraints are shown in the table below. This constraint works in two directions;
 * for example the names "geodetic latitude" and "geodetic longitude" shall be used to
 * designate the coordinate axis names associated with a geographic coordinate reference
 * system. Conversely, these names shall not be used in any other context.</P>
 *
 * <TABLE ALIGN="center" CELLPADDING='6' BORDER='1'>
 * <TR BGCOLOR="#EEEEFF"><TH>CS</TH><TH>CRS</TH><TH NOWRAP>Permitted coordinate system axis names</TH></TR>
 * <TR><TD>Cartesian</TD><TD>Geocentric</TD>
 *     <TD><cite>Geocentric X</cite>,
 *         <cite>Geocentric Y</cite>,
 *         <cite>Geocentric Z</cite></TD></TR>
 * <TR><TD>Spherical</TD><TD>Geocentric</TD>
 *     <TD><cite>Spherical Latitude</cite>,
 *         <cite>Spherical Longitude</cite>,
 *         <cite>Geocentric Radius</cite></TD></TR>
 * <TR><TD>Ellipsoidal</TD><TD>Geographic</TD>
 *     <TD><cite>Geodetic Latitude</cite>,
 *         <cite>Geodetic Longitude</cite>,
 *         <cite>Ellipsoidal height</cite> (if 3D)</TD></TR>
 * <TR><TD>Vertical</TD><TD>Vertical</TD>
 *     <TD><cite>Gravity-related height</cite> or <cite>Depth</cite></TD></TR>
 * <TR><TD>Cartesian</TD><TD>Projected</TD>
 *     <TD><cite>Easting</cite> or <cite>Westing</cite>,
 *         <cite>Northing</cite> or <cite>Southing</cite></TD></TR>
 * </TABLE>
 *
 * <P ALIGN="justify">Image and engineering coordinate reference systems may make
 * use of names specific to the local context or custom and are therefore not included
 * as constraints in the above list.</P>
 *
 * @departure constraint
 *   ISO 19111 defines <code>GeodeticCS</code>, <code>EngineeringCS</code> and <code>ImageCS</code>
 *   unions for type safety, which ensures, for example, that a <code>GeodeticCRS</code> only be
 *   associated to a <code>CartesianCS</code>, an <code>EllipsoidalCS</code> or a <code>SphericalCS</code>.
 *   However the <code>union</code> construct found in some languages like C/C++ is not available
 *   in Java. In the particular case of <code>ImageCS</code>, the same type-safety objective can
 *   be obtained through a slight change in the interface hierarchy (see the departure documented
 *   in <code>CartesianCS</code>). For the other two unions (<code>GeodeticCS</code> and
 *   <code>EngineeringCS</code>), no workaround is proposed.
 *
 * @author  Martin Desruisseaux (IRD)
 * @version 3.0
 * @since   1.0
 */
package org.opengis.referencing.cs;
