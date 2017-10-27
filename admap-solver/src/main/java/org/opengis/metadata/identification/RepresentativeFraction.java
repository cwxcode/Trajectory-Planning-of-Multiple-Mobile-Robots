/*
 *    GeoAPI - Java interfaces for OGC/ISO standards
 *    http://www.geoapi.org
 *
 *    Copyright (C) 2007-2011 Open Geospatial Consortium, Inc.
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

import org.opengis.annotation.UML;

import static org.opengis.annotation.Obligation.MANDATORY;
import static org.opengis.annotation.Specification.ISO_19115;


/**
 * A scale defined as the inverse of a denominator. This is derived from ISO 19103 {@code Scale}
 * where {@linkplain #getDenominator denominator} = 1 / <var>scale</var>.
 * <p>
 * Implementations are encouraged to extend {@link Number} in a manner equivalent to:
 *
 * <blockquote><pre>
 *  class MyRepresentedFraction extends Number implements RepresentedFraction {
 *      ...
 *      public double doubleValue() {
 *          return 1.0 / (double) denominator;
 *      }
 *      public float floatValue() {
 *          return 1.0f / (float) denominator;
 *      }
 *      public long longValue() {
 *          return 1 / denominator; // Result is zero except for denominator=[0,1].
 *      }
 *      ...
 *  }
 * </pre></blockquote>
 *
 * @author  Ely Conn (Leica Geosystems Geospatial Imaging, LLC)
 * @version 3.0
 * @since   2.1
 */
@UML(identifier="MD_RepresentativeFraction", specification=ISO_19115)
public interface RepresentativeFraction {
    /**
     * Returns the scale value in a form usable for computation.
     *
     * @return <code>1.0 / (double) {@linkplain #getDenominator()}</code>
     *
     * @since 2.2
     */
    double doubleValue();

    /**
     * The number below the line in a vulgar fraction.
     *
     * @return The denominator.
     */
    @UML(identifier = "denominator", obligation = MANDATORY, specification = ISO_19115)
    long getDenominator();

    /**
     * Compares this representative fraction with the specified object for equality.
     * {@code RepresentativeFraction} is a data object - {@code equals} is defined
     * according to {@link #getDenominator()};
     * <p>
     * Implementations should match the following:
     *
     * <blockquote><pre>
     * public boolean equals(Object object) {
     *     if (object instanceof RepresentativeFraction) {
     *         final RepresentativeFraction that = (RepresentativeFraction) object;
     *         return getDenominator() == that.getDenominator();
     *     }
     *     return false;
     * }
     * </pre></blockquote>
     *
     * @param other The object to compare with.
     * @return {@code true} if {@code other} is a {@code RepresentedFraction} with the same
     *         {@linkplain #getDenominator denominator} value.
     */
    @Override
    boolean equals(Object other);

    /**
     * Returns a hash value for this representative fraction.
     * {@code RepresentativeFraction} is a data object - {@code hashcode} is defined
     * according to {@link #getDenominator()}.
     * <p>
     * Implementations should match the following:
     *
     * <blockquote><pre>
     * public int hashCode() {
     *     return (int) getDenominator();
     * }
     * </pre></blockquote>
     *
     * @return A hash code value for this representative fraction.
     */
    @Override
    int hashCode();
}
