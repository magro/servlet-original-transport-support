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

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.javakaffee.web.originaltransport.HttpsRedirectingServletResponse.ServletResponseType;


/**
 * This Filter checks, if there's the HTTP header "Original-Transport"
 * with a value of "SSL" that indicates that the original request was done with HTTPS,
 * and in this case wraps the current servlet request with a
 * {@link HttpsServletRequest}.<br>
 * Created on: Sep 22, 2007<br>
 *
 * @author <a href="mailto:martin.grotzke@javakaffee.de">Martin Grotzke</a>
 */
public final class OriginalTransportCheckingFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger( OriginalTransportCheckingFilter.class );
	public static final String RESPONSE_TYPE_PARAM = "servletResponseType";

    private HttpsRedirectingServletResponse.ServletResponseType _servletResponseType = ServletResponseType.OVERRIDE_HTTP;


    /* (non-Javadoc)
     * @see javax.servlet.Filter#destroy()
     */
    public void destroy() {
    }

    /* (non-Javadoc)
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    public void doFilter( ServletRequest request, ServletResponse response,
            final FilterChain chain ) throws IOException, ServletException {
        final HttpServletRequest httpRequest = (HttpServletRequest) request;
        if ( OriginalTransportUtils.isOriginalTransportHttps( httpRequest ) ) {
            LOG.debug( "Found header 'Original-Transport', wrapping request and response with fixes." );
            request = new HttpsServletRequest( httpRequest );
            response = new HttpsRedirectingServletResponse( httpRequest, (HttpServletResponse) response, _servletResponseType );
        }
        chain.doFilter( request, response );
    }

    /* (non-Javadoc)
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    public void init( final FilterConfig filterConfig ) throws ServletException {
        final String servletResponseType = filterConfig.getInitParameter(RESPONSE_TYPE_PARAM);
        try {
            _servletResponseType = HttpsRedirectingServletResponse.ServletResponseType.valueOf( servletResponseType );
        } catch (final Throwable f) {
            final StringBuilder sb = new StringBuilder();
            for (final HttpsRedirectingServletResponse.ServletResponseType t : HttpsRedirectingServletResponse.ServletResponseType.values()) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append( t.name() );
            }
            throw new IllegalArgumentException( "Illegal argument to " + RESPONSE_TYPE_PARAM + ", allowed values are: " + sb.toString());
        }
    }

}
