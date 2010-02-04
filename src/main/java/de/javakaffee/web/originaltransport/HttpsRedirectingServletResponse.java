/*
 * Copyright 2009 Martin Grotzke
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an &quot;AS IS&quot; BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package de.javakaffee.web.originaltransport;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This HttpServletResponseWrapper always redirects to the fully qualified
 * HTTPS URL.<br>
 * Created on: Sep 24, 2007<br>
 *
 * @author <a href="mailto:martin.grotzke@javakaffee.de">Martin Grotzke</a>
 */
public final class HttpsRedirectingServletResponse extends
        HttpServletResponseWrapper {

    public enum RedirectType {
        /** Any incoming URL will be converted to https, even if it has an explicit protocol set as 'http'. */
        OVERRIDE_HTTP,
        /** Any incoming URL will be converted to https if it does not yet have an explicit protocol set. */
        KEEP_HTTP;
    }

    protected static final RedirectType DEFAULT_REDIRECT_TYPE = RedirectType.OVERRIDE_HTTP;

    private static final Logger LOG = LoggerFactory.getLogger( HttpsRedirectingServletResponse.class );

    private static final String TARGET_SCHEME = "https";

    private final HttpServletRequest _request;

    private final RedirectType _redirectType;

    /**
     * @deprecated backwards compatibility constructor, use explicit one instead.
     */
    @Deprecated
    public HttpsRedirectingServletResponse( final HttpServletRequest request, final HttpServletResponse response ) {
        this(request, response, DEFAULT_REDIRECT_TYPE);
    }

    public HttpsRedirectingServletResponse( final HttpServletRequest request, final HttpServletResponse response, final RedirectType redirectType ) {
        super( response );
        _request = request;
        _redirectType = redirectType;
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServletResponseWrapper#sendRedirect(java.lang.String)
     */
    @Override
    public void sendRedirect( String location ) throws IOException {
        if ( !location.startsWith( "https://" ) ) {
            if ( location.startsWith( "http://" ) ) {
                if ( _redirectType == RedirectType.OVERRIDE_HTTP ) {
                    LOG.debug( "Got url starting with http://, changing http to https as response type is set to 'Override'..." );
                    location = location.replaceFirst( "http://", "https://" );
                }
            }
            else if ( location.startsWith( "/" ) ) {
                LOG.debug( "Got url starting with /, putting https:// and the servername in front of it..." );
                final String serverName = _request.getServerName();
                location = TARGET_SCHEME + "://" + serverName + location;
            }
            else {
                /* we have a relative location, this must be seen to be relative to the
                 * current requestURI
                 */
                final String requestedLocation = location;
                final String requestURI = _request.getRequestURI();
                final int lastSlashIdx = requestURI.lastIndexOf( '/' );
                if ( lastSlashIdx > 0 ) {
                    location = requestURI.substring( 0, lastSlashIdx + 1 ) + location;
                }
                else {
                    location = "/" + location;
                }
                final String serverName = _request.getServerName();
                location = TARGET_SCHEME + "://" + serverName + location;
                LOG.debug( "Got url starting with not with http:// and not with /," +
                        "put https://, the servername and the current requestURI in front of it." +
                        "\nRequested location: {}" +
                        "\nRedirecting to location: {}",requestedLocation, location );
            }
        }
        super.sendRedirect( location );
    }

}
