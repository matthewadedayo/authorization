package com.authorization.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    @Autowired
    private ResourceServerTokenServices tokenServices;

    //@Value("${security.jwt.resource-ids}")
    //private String resourceIds;

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources.resourceId(null)
            .tokenServices(tokenServices);
    }

    
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.requestMatchers()
            .and()
            .authorizeRequests()
            .antMatchers("/**/actuator/**", "/**/api-docs/**", "/**/swagger-ui.html", "/**/webjars/**", "/**/api/v1/", "/**/swagger-resources/**", "/**/docs/**", "/**/health/**", "/**/user/**",
            		"/**/user/login/**").permitAll()
            .antMatchers(HttpMethod.POST, "/**/user/**", "/**/application/**").permitAll()
            .antMatchers(HttpMethod.GET, "/**/user/**").permitAll()
            .antMatchers(HttpMethod.POST, "/**/send-mail/**").permitAll()
            .anyRequest()
            .authenticated();
    }
}
