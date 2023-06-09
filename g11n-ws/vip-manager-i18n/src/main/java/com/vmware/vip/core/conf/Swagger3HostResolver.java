package com.vmware.vip.core.conf;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import springfox.documentation.oas.web.OpenApiTransformationContext;
import springfox.documentation.oas.web.WebMvcOpenApiTransformationFilter;
import springfox.documentation.spi.DocumentationType;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;


@Component
public class Swagger3HostResolver implements WebMvcOpenApiTransformationFilter {

    @Value("${springfox.documentation.swagger-ui.server.url:}")
    private String hostUrl;

    @Value("${springfox.documentation.swagger-ui.server.description:Inferred Url}")
    private String hostDesp;

    @Override
    public OpenAPI transform(OpenApiTransformationContext<HttpServletRequest> context) {
        OpenAPI swagger = context.getSpecification();
        if (StringUtils.isNotEmpty(hostUrl)){
            List<Server> servers = new ArrayList<>();
            Server server = new Server();
            server.setUrl(hostUrl);
            server.setDescription(hostDesp);
            servers.add(server);
            swagger.setServers(servers);
        }
        return swagger;
    }


    @Override
    public boolean supports(DocumentationType documentationType) {
        return documentationType.equals(DocumentationType.OAS_30);
    }

    public void setHostUrl(String hostUrl) {
        this.hostUrl = hostUrl;
    }

}
