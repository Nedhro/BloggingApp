package com.prodev.bloggingservice.config;

import com.prodev.bloggingservice.security.JwtAuthenticationFilter;
import com.prodev.bloggingservice.service.CustomUserDetailsService;
import com.prodev.bloggingservice.util.UrlConstraints;
import org.h2.server.web.WebServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

import java.util.Arrays;

@Configuration
@EnableTransactionManagement
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private MyAuthenticationEntryPoint myAuthenticationEntryPoint;
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder);
    }

    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(
                "/webjars/**",
                "/images/**",
                "/css/**",
                "/js/**")
                .addResourceLocations(
                        "classpath:/META-INF/resources/webjars/",
                        "classpath:/static/images/",
                        "classpath:/static/css/",
                        "classpath:/static/js/");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        String allPrefix = "/*";
        http.csrf().disable()
                .cors().and()
                .exceptionHandling().authenticationEntryPoint(myAuthenticationEntryPoint).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/index", "/resources/**").permitAll()
                .antMatchers(HttpMethod.POST, "/index", "/").permitAll()
                .antMatchers(HttpMethod.GET, "/favicon.ico","/js/**", "/css/**", "/images/**",
                "/public/**", "/register", "/", "/login").permitAll()
                .antMatchers(UrlConstraints.AuthManagement.ROOT + allPrefix, UrlConstraints.UserManagement.ROOT).permitAll()
                .antMatchers("/", UrlConstraints.AuthManagement.ROOT + allPrefix).permitAll()
                .antMatchers("/h2/**").permitAll()
                .anyRequest()
                .authenticated();
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        http.headers().cacheControl();
        http.headers().frameOptions().disable();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addExposedHeader("Access-Control-Expose-Headers,authorization");
        configuration.addAllowedOrigin("*");
        configuration.addAllowedHeader("*");
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        source.registerCorsConfiguration(UrlConstraints.AuthManagement.ROOT, configuration);
        return source;
    }

    /*  @Bean
      public DataSource dataSource() {
          return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2).build();
      }
  */
    @Bean
    ServletRegistrationBean h2servletRegistration() {
        ServletRegistrationBean registrationBean = new ServletRegistrationBean(new WebServlet());
        registrationBean.addUrlMappings("/h2/*");
        return registrationBean;
    }
}
