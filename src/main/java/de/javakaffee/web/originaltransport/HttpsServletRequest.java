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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;


/**
 * This HttpServletRequestWrapper mimicks HTTPS as scheme, 443 as serverPort
 * and returns the requestURL with https also.<br>
 * Created on: Sep 22, 2007<br>
 * 
 * @author <a href="mailto:martin.grotzke@javakaffee.de">Martin Grotzke</a>
 */
public final class HttpsServletRequest extends
        HttpServletRequestWrapper {
    
    private static final int HTTP_LENGTH = "http://".length();

    public HttpsServletRequest(HttpServletRequest request) {
        super( request );
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletRequestWrapper#getScheme()
     */
    @Override
    public String getScheme() {
        return "https";
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServletRequestWrapper#getRequestURL()
     */
    @Override
    public StringBuffer getRequestURL() {
        return super.getRequestURL().replace( 0, HTTP_LENGTH, "https://" );
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletRequestWrapper#getServerPort()
     */
    @Override
    public int getServerPort() {
        return 443;
    }

    /* (non-Javadoc)
     * @see javax.servlet.ServletRequestWrapper#isSecure()
     */
    @Override
    public boolean isSecure() {
        return true;
    }

}
