package com.vsobakekot.natlex.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    // Create 2 users for demo
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication()
                .withUser("user").password("{noop}userpw").roles("USER")
                .and()
                .withUser("admin").password("{noop}adminpw").roles("USER", "ADMIN");
    }

    // Secure the endpoints with HTTP Basic authentication
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.httpBasic()
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/api/sections").hasAnyRole("USER", "ADMIN")
                .antMatchers(HttpMethod.GET, "/api/sections/**").hasAnyRole("USER", "ADMIN")
                .antMatchers(HttpMethod.POST, "/api/sections").hasRole("ADMIN")
                .antMatchers(HttpMethod.PUT, "/api/sections/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/api/sections/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.GET, "/api/jobs/**").hasAnyRole("USER", "ADMIN")
                .antMatchers(HttpMethod.POST, "/api/jobs/**").hasRole("ADMIN")
                .and()
                .csrf().disable();
    }
}
