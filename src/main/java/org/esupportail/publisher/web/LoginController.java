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
package org.esupportail.publisher.web;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.security.CustomUserDetails;
import org.esupportail.publisher.security.SecurityUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@RestController
@RequestMapping("/app")
@Slf4j
public class LoginController {

    @RequestMapping("/login")
    public Object login(Model model, HttpServletRequest request) throws IOException {

        CustomUserDetails user = SecurityUtils.getCurrentUserDetails();
        if (user == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        String uri = request.getRequestURI() + (request.getQueryString() != null ? "?" + request.getQueryString() : "");

        String jsUser = new ObjectMapper().writeValueAsString(user);
        String content, type;
        if (request.getParameter("postMessage") != null) {
            type = "text/html";
            content = "Login success, please wait...\n<script>\n (window.opener ? (window.opener.postMessage ? window.opener : window.opener.document) : window.parent).postMessage('loggedUser=' + JSON.stringify("
                + jsUser + "), '*');\n</script>";
        } else if (request.getParameter("callback") != null) {
            type = "application/x-javascript";
            content = request.getParameter("callback") + "(" + jsUser + ")";
        } else {
            type = "application/json";
            content = jsUser;
        }
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.setContentType(MediaType.valueOf(type));
        return new ResponseEntity<String>(content, responseHeaders, HttpStatus.OK);
    }
}
