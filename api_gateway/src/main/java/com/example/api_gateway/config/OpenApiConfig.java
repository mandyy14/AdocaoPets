package com.example.api_gateway.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    @Lazy(false)
    public List<GroupedOpenApi> apis(RouteDefinitionLocator locator) {
        List<GroupedOpenApi> groups = new ArrayList<>();
        List<RouteDefinition> definitions = locator.getRouteDefinitions().collectList().block();

        if (definitions == null) {
            return groups;
        }

        definitions.stream()
            .filter(routeDefinition -> routeDefinition.getId().matches(".*_route"))
            .forEach(routeDefinition -> {
                String name = routeDefinition.getId().replace("_route", "");
                String defaultApiDocsPath = "/v3/api-docs";
                if (name.equals("user-service")) defaultApiDocsPath = "/user-api-docs";
                if (name.equals("media-service")) defaultApiDocsPath = "/media-api-docs";
                if (name.equals("pet-service")) defaultApiDocsPath = "/pet-api-docs";

                groups.add(GroupedOpenApi.builder()
                        .pathsToMatch("/" + name + defaultApiDocsPath)
                        .group(name)
                        .build());
            });

        return groups;
    }
}
