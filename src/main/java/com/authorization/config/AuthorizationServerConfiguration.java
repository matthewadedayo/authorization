package com.authorization.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfiguration implements AuthorizationServerConfigurer {

    @Value("${security.jwt.resource-ids}")
    private String resourceIds;
    
    @Autowired
    private TokenStore tokenStore;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private DataSource dataSource;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserDetailsService userDetailsService;


    @Bean
   public TokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }
   
   @Bean
    public TokenEnhancer tokenEnhancer() {
        return new AuthorizationTokenEnhancer();
    }
   
    @Bean
   public JwtAccessTokenConverter accessTokenConverter(){
      JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setKeyPair(new KeyStoreKeyFactory(new ClassPathResource("jwt.jks"), "password".toCharArray()).getKeyPair("jwt"));
          return converter;
 }

    
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.checkTokenAccess("isAuthenticated()").tokenKeyAccess("permitAll()");

    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.jdbc(this.dataSource).passwordEncoder(passwordEncoder);

    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.tokenStore(tokenStore()).tokenEnhancer(accessTokenConverter());
        endpoints.authenticationManager(authenticationManager).userDetailsService(userDetailsService);
        
    }
    
     @Primary
    @Bean
    public DefaultTokenServices tokenServices() {
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore());
        defaultTokenServices.setTokenEnhancer(tokenEnhancer());
        defaultTokenServices.setSupportRefreshToken(true);
        return defaultTokenServices;
    }
}
