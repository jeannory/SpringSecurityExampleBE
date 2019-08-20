package com.example.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.savedrequest.NullRequestCache;

import static com.example.security.contants.Constants.ADMIN;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public JwtRequestFilter jwtRequestFilter() {
        return new JwtRequestFilter();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //return status 403 if not allowed
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().
                authorizeRequests()
                .antMatchers(HttpMethod.GET, "/api", "/api/*").permitAll()
                .antMatchers(HttpMethod.GET, "/roles", "/roles/*").hasRole(ADMIN)
                .antMatchers(HttpMethod.GET, "/users", "/users/*").hasRole(ADMIN)
                .antMatchers(HttpMethod.POST, "/api/*").permitAll()
                .antMatchers(HttpMethod.PUT, "/api/*").permitAll()
                .antMatchers(HttpMethod.DELETE, "/api/*").permitAll().and()
                .requestCache().requestCache(new NullRequestCache()).and()
                .cors().and()
                .csrf().disable();

        // Add custom JWT security filter,if not allowing by @Secure return status 403
        http.addFilterBefore(jwtRequestFilter(), UsernamePasswordAuthenticationFilter.class);
    }
}
