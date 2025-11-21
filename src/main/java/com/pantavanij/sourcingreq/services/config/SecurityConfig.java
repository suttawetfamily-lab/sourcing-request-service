package com.pantavanij.sourcingreq.services.config;

import com.pantavanij.sourcingreq.services.security.CustomAccessDeniedHandler;
import com.pantavanij.sourcingreq.services.security.JwtAuthenticationEntryPoint;
import com.pantavanij.sourcingreq.services.security.JwtAuthenticationFilter;
import com.pantavanij.sourcingreq.services.security.JwtEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.firewall.DefaultHttpFirewall;
import org.springframework.security.web.firewall.HttpFirewall;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

//    @Autowired
//    JwtFilter authenticationTokenFilter;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private CustomAccessDeniedHandler customAccessDeniedHandler;


    @Autowired
    private JwtEntryPoint unauthorizedHandler;

    @Value("${api.endpoint.version}")
    private String endpointVersion;

    @Value("${jwt.route.authentication.path}")
    private String oAuthPath;

    @Value("${jwt.route.authentication.refresh}")
    private String refreshPath;

    @Bean
    public PasswordEncoder passwordEncoderBean() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        String[] AUTH_WHITELIST = {
                // -- swagger ui
                "/v2/api-docs",
                "/swagger-resources/**",
                "/configuration/ui",
                "/configuration/security",
                "/swagger-ui.html",
                "/swagger-ui/**",
                "/v3/api-docs/**",
                "/webjars/**",
                "/actuator/**",
                "/api/sourcingreq/v1/oauth/token",
                "/api/sourcingreq/v1/api/erfx/update-status"
        };

        httpSecurity
                .csrf().disable()
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler)
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers(AUTH_WHITELIST).permitAll()
                .anyRequest().authenticated()
                .and().exceptionHandling().accessDeniedHandler(customAccessDeniedHandler)
                .and().exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint);

        httpSecurity.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        httpSecurity.cors();
        httpSecurity.headers()
                .xssProtection()
                .and()
                .contentSecurityPolicy("default-src 'self'; " +
                        "object-src 'self'; " +
                        "img-src 'self' *.pantavanij.com; " +
                        "form-action 'self' *.pantavanij.com *.oneplanets.com; " +
                        "script-src 'self'; ");
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        super.configure(web);
        web.ignoring()
                .antMatchers(HttpMethod.POST, endpointVersion + oAuthPath)
                .and()
                .ignoring()
                .antMatchers(endpointVersion + refreshPath)
                // allow anonymous resource requests
                .and()
                .ignoring()
                .antMatchers(
                        HttpMethod.GET,
                        "/",
                        "/*.html",
                        "/favicon.ico",
                        "/**/*.html",
                        "/**/*.css",
                        "/**/*.js"
                );
        web.httpFirewall(allowUrlEncodedSlashHttpFirewall());
    }

    @Bean
    public HttpFirewall allowUrlEncodedSlashHttpFirewall() {
        DefaultHttpFirewall firewall = new DefaultHttpFirewall();
        firewall.setAllowUrlEncodedSlash(true);
        return firewall;
    }
}