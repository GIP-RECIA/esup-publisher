/**
 * Copyright (C) 2014 Esup Portail http://www.esup-portail.org
 * @Author (C) 2012 Julien Gribonvald <julien.gribonvald@recia.fr>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *                 http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.esupportail.publisher.web.filter;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;

import java.io.File;
import java.io.IOException;

/**
 * This filter is used in production, to serve static resources generated by "grunt build".
 * <p/>
 * <p>
 * It is configured to serve resources from the "dist" directory, which is the Grunt
 * destination directory.
 * </p>
 */
@Slf4j
public class StaticResourcesProductionFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Nothing to initialize
    }

    @Override
    public void destroy() {
        // Nothing to destroy
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String contextPath = ((HttpServletRequest) request).getContextPath();
        String requestURI = httpRequest.getRequestURI();
        requestURI = StringUtils.substringAfter(requestURI, contextPath);
        if (StringUtils.equals("/", requestURI) || StringUtils.equals("/ui/", requestURI)) {
            requestURI = "/ui/index.html";
        }
        String newURI = requestURI.replace("/ui/", "/dist/");

        // Si l'URI ne pointe pas vers une ressource existante, on retourne la page index.html
        if (!existsStaticResource(httpRequest.getServletContext(), newURI)) {
            newURI = "/dist/index.html";
        }

        log.debug("RequestDispatcher - setting newURI to {}", newURI);
        request.getRequestDispatcher(newURI).forward(request, response);
    }

    /**
     * Test si une URI pointe vers une ressource statique existante.
     * @param context Servlet contexte
     * @param requestURI URI à tester
     * @return {@code True} si l'URI pointe vers une ressource statique existante, {@code False} sinon
     */
    private boolean existsStaticResource(ServletContext context, String requestURI) {
        String path = context.getRealPath(requestURI);
        return new File(path).isFile();
    }
}
