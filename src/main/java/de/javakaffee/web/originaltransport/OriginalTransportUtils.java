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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple helper class.
 * 
 * Created on: Sep 22, 2007<br>
 * 
 * @author <a href="mailto:martin.grotzke@javakaffee.de">Martin Grotzke</a>
 */
public class OriginalTransportUtils {

    private static final Logger LOG = LoggerFactory.getLogger( OriginalTransportUtils.class );

    public static void sendRedirect( HttpServletRequest request, HttpServletResponse response, String url ) throws IOException {

        url = getRedirectUrl( url, request );

        LOG.info( "Sending redirect to " + url );
        response.sendRedirect(response.encodeRedirectURL(url));
        
    }

    /**
     * Returns a url that contains the scheme (http/https) if necessary and
     * evaluates the HTTP header "Original-Transport" for this.
     * The url is conceived to be relative to the servlet context, and therefore
     * the fully qualified url will be s.th. like
     * <code>https:// + servername + contextPath + url</code>. 
     * @param url a fully qualified (starting with http/https) or context relative url.
     * @param request the current http servlet request
     * @return the fully qualified url if necessary, or the url containing
     * the context path.
     * @author Martin Grotzke
     */
    public static String getRedirectUrl( String url, HttpServletRequest request ) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            
            if ( isOriginalTransportHttps( request ) ) {
                LOG.info( "Got relative redirect url but found header 'Original-Transport'" +
                        ", therefore a redirect to the absolute url is required..." );
                
                String serverName = request.getServerName();
                String contextPath = request.getContextPath();
                
                url = "https://" + serverName
                        + contextPath
                        + ( url.startsWith( "/" ) ? url : "/" + url );
            }
            else {
                url = request.getContextPath() + url;
            }
            
        }
        else if ( url.startsWith("http://") && isOriginalTransportHttps( request ) ) {
            LOG.info( "Found header 'Original-Transport' and therefore changing http to https..." );
            url = url.replaceFirst( "http://", "https://" );
        }
        return url;
    }
    
    /**
     * This method expects a fully qualified url and changes it to https
     * if one of the following conditions is met:
     * <ul>
     * <li>the given request has https as scheme</li>
     * <li>the request contains the http header "Original Transport" with the value "SSL"
     * </ul>
     * @param request
     * @param url
     * @return the url
     * @author Martin Grotzke
     * @throws IllegalArgumentException thrown if the url does not start with http:// or https://
     */
    public static String getFixedHttpsUrl( HttpServletRequest request, String url ) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            throw new IllegalArgumentException( "The url must start with http:// or with https://  URL: " + url );
        }
        if ( !url.startsWith("https://") && isHttpsRequest( request ) ) {
            url = url.replaceFirst( "http://", "https://" );
        }
        return url;
    }

    public static boolean isHttpsRequest( HttpServletRequest request ) {
        return request.getScheme().equals("https") || isOriginalTransportHttps( request );
    }

    public static boolean isOriginalTransportHttps( HttpServletRequest request ) {
        return "SSL".equalsIgnoreCase( request.getHeader("Original-Transport") );
    }
    
}
