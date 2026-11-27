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
package org.esupportail.publisher.config;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.service.bean.ServiceUrlHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;

import java.util.List;


@Slf4j
@Configuration
public class GeneralBeansConfiguration {
    private static final String APP_CONTEXT_PATH = "server.servlet.context-path";
    private final ESUPPublisherProperties esupPublisherProperties;
    private Environment env;

    public GeneralBeansConfiguration(Environment environment, ESUPPublisherProperties esupPublisherProperties) {
        this.env = environment;
        this.esupPublisherProperties = esupPublisherProperties;
    }

    @Bean
    public ServiceUrlHelper serviceUrlHelper() {
        String ctxPath = env.getRequiredProperty(APP_CONTEXT_PATH);
        if (!ctxPath.startsWith("/")) ctxPath = "/" + ctxPath;
        final String protocol = esupPublisherProperties.getSecurity().getProtocol();
        Assert.isTrue(Lists.newArrayList("http://", "https://").contains(protocol), "Protocol param doesn't match required value, see property 'app.security.protocol'");
        final List<String> domainNames = Lists.newArrayList(esupPublisherProperties.getSecurity().getAuthorizedDomainNames());
        Assert.notEmpty(domainNames, "The list of the application Domain Names set shouldn't be empty, see property 'app.security.authorizedDomainNames'");
        ServiceUrlHelper serviceUrlHelper = new ServiceUrlHelper(ctxPath, domainNames, protocol, "/view/item/", "/news/item/");
        log.info("ServiceUrlHelper is configured with properties : {}", serviceUrlHelper.toString());
        return serviceUrlHelper;
    }
}
