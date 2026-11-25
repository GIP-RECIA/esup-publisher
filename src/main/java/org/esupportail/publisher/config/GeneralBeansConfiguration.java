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
