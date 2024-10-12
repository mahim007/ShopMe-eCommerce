package com.mahim.shopme.security;

import com.mahim.shopme.customer.CustomerRepository;
import com.mahim.shopme.oauth.CustomerOAuth2UserService;
import com.mahim.shopme.oauth.OAuth2LoginSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomerRepository customerRepository;
    private final CustomerOAuth2UserService customerOAuth2UserService;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final DatabaseLoginSuccessHandler dbLoginSuccessHandler;

    public WebSecurityConfig(CustomerRepository customerRepository,
                             CustomerOAuth2UserService customerOAuth2UserService,
                             OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler,
                             DatabaseLoginSuccessHandler dbLoginSuccessHandler) {
        this.customerRepository = customerRepository;
        this.customerOAuth2UserService = customerOAuth2UserService;
        this.oAuth2LoginSuccessHandler = oAuth2LoginSuccessHandler;
        this.dbLoginSuccessHandler = dbLoginSuccessHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService customerUserDetails() {
        return new CustomerUserDetailsService(customerRepository);
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customerUserDetails());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
       auth.authenticationProvider(authenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                    .antMatchers("/account_details", "/update_account_details", "/cart", "/address_book/**",
                            "/checkout", "/checkout/**", "/process_paypal_order").authenticated()
                    .anyRequest().permitAll()
                .and()
                    .formLogin()
                        .loginPage("/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .successHandler(dbLoginSuccessHandler)
                        .permitAll()
                .and()
                    .oauth2Login()
                    .loginPage("/login")
                    .userInfoEndpoint()
                        .userService(customerOAuth2UserService)
                    .and()
                        .successHandler(oAuth2LoginSuccessHandler)
                .and()
                    .logout().permitAll()
                .and()
                    .rememberMe()
                        .key("ABCD1234abcd")
                        .tokenValiditySeconds(14 * 24 * 60 * 60)
                .and()
                    .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.ALWAYS);

    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web
                .ignoring()
                .antMatchers("/images/**", "/webjars/**", "/js/**");
    }
}
