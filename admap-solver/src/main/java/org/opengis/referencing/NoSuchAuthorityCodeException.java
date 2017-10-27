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
package org.opengis.referencing;

import org.opengis.util.NoSuchIdentifierException;


/**
 * Thrown when an {@linkplain AuthorityFactory authority factory} can't find
 * the requested authority code.
 *
 * @departure extension
 *   This exception is not part of the OGC specification.
 *
 * @author  Martin Desruisseaux (IRD)
 * @version 3.0
 * @since   1.0
 *
 * @see org.opengis.referencing.crs.CRSAuthorityFactory
 * @see org.opengis.referencing.cs.CSAuthorityFactory
 * @see org.opengis.referencing.datum.DatumAuthorityFactory
 */
public class NoSuchAuthorityCodeException extends NoSuchIdentifierException {
    /**
     * Serial number for inter-operability with different versions.
     */
    private static final long serialVersionUID = -1573748311981746573L;

    /**
     * The authority.
     */
    private final String authority;

    /**
     * The invalid authority code.
     */
    private final String code;

    /**
     * Constructs an exception with the specified detail message and authority code.
     *
     * @param  message The detail message. The detail message is saved
     *         for later retrieval by the {@link #getMessage()} method.
     * @param  authority The authority, saved for retrieval by the {@link #getAuthority()} method.
     * @param  code The invalid authority code, saved for retrieval by the {@link #getAuthorityCode()} method.
     */
    public NoSuchAuthorityCodeException(String message, String authority, String code) {
        this(message, authority, code, (authority == null) ? code : (code == null) ? authority : authority + ':' + code);
    }

    /**
     * Constructs an exception with the specified detail message, authority code and identifier.
     * The identifier argument is optional. If omitted, then "{@code authority:code}" will be used.
     *
     * @param  message The detail message. The detail message is saved
     *         for later retrieval by the {@link #getMessage()} method.
     * @param  authority The authority, saved for retrieval by the {@link #getAuthority()} method.
     * @param  code The invalid authority code, saved for retrieval by the {@link #getAuthorityCode()} method.
     * @param  identifier The full identifier as a concatenation of the authority and the code,
     *         saved for retrieval by the {@link #getIdentifierCode()} method.
     */
    public NoSuchAuthorityCodeException(String message, String authority, String code, String identifier) {
        super(message, identifier);
        this.authority = authority;
        this.code = code;
    }

    /**
     * Returns the authority.
     *
     * @return The authority, or {@code null} if unknown.
     */
    public String getAuthority() {
        return authority;
    }

    /**
     * Returns the invalid authority code.
     *
     * @return The authority code, or {@code null} if unknown.
     */
    public String getAuthorityCode() {
        return code;
    }
}
