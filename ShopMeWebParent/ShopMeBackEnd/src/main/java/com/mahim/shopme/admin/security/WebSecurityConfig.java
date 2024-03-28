package com.mahim.shopme.admin.security;

import com.mahim.shopme.admin.user.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserRepository userRepository;

    public WebSecurityConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new ShopmeUserDetailsService(userRepository);
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(daoAuthenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                    .antMatchers("/users/**").hasAuthority("Admin")
                    .antMatchers("/categories/**").hasAnyAuthority("Admin", "Editor")
                    .antMatchers("/brands/**").hasAnyAuthority("Admin", "Editor")
                    .antMatchers("/products/new", "/products/delete/**")
                        .hasAnyAuthority("Admin", "Editor")
                    .antMatchers("/products/edit/**", "/products/save", "/products/check_unique")
                        .hasAnyAuthority("Admin", "Editor", "Salesperson")
                    .antMatchers("/products", "/products/", "/products/detail/**", "/products/page/**")
                        .hasAnyAuthority("Admin", "Editor", "Salesperson", "Shipper")
                    .antMatchers("/products/**").hasAnyAuthority("Admin", "Editor")
                    .antMatchers("/customers/**").hasAnyAuthority("Admin", "Salesperson")
                    .antMatchers("/shipping/**").hasAnyAuthority("Admin", "Salesperson")
                    .antMatchers("/orders/**").hasAnyAuthority("Admin", "Salesperson", "Shipper")
                    .antMatchers("/report/**").hasAnyAuthority("Admin", "Salesperson")
                    .antMatchers("/articles/**").hasAnyAuthority("Admin", "Editor")
                    .antMatchers("/menus/**").hasAnyAuthority("Admin", "Editor")
                    .antMatchers("/settings/**").hasAuthority("Admin")
                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
                    .loginPage("/login")
                    .usernameParameter("email")
                    .permitAll()
                .and()
                .rememberMe()
                    .key("ABBCDefgh_1234")
                    .tokenValiditySeconds(7*24*60*60)
                .and()
                .logout()
                    .permitAll();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web
                .ignoring()
                .antMatchers("/images/**", "/webjars/**", "/js/**");
    }
}
