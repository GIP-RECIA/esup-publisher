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
package org.esupportail.publisher.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.esupportail.publisher.service.bean.ServiceUrlHelper;
import org.springframework.security.cas.web.CasAuthenticationEntryPoint;

/**
 * Standard CAS entry point, only overridden to validate the requesting domain against
 * app.security.authorized-domain-names before building the service URL.
 */
public class CustomCasAuthenticationEntryPoint extends CasAuthenticationEntryPoint {

    private final ServiceUrlHelper urlHelper;

    public CustomCasAuthenticationEntryPoint(ServiceUrlHelper urlHelper) {
        this.urlHelper = urlHelper;
    }

    @Override
    protected String createServiceUrl(HttpServletRequest request, HttpServletResponse response) {
        return SecurityUtils.makeDynamicCASServiceUrl(urlHelper, request) + getServiceProperties().getService();
    }
}
