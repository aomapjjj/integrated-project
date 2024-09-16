package sit.int221.servicetasksj3.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//import sit.int221.servicetasksj3.controller.CustomAuthenticationEntryPoint;
import sit.int221.servicetasksj3.sharedatabase.filters.JwtAuthFilter;
import sit.int221.servicetasksj3.sharedatabase.services.JwtUserDetailsService;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    @Autowired
    private JwtAuthFilter jwtAuthFilter;
    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;
//    @Autowired
//    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf(csrf -> csrf.disable()).authorizeRequests(
                        authorize -> authorize.requestMatchers("/login").permitAll()
//                                .requestMatchers("/v2/tasks/**").authenticated()
//                                .requestMatchers("/v2/statuses/**").authenticated()
//                                .requestMatchers("/v3/boards/**").authenticated()
//                                .requestMatchers("/v3/boards/{id}/tasks/**").authenticated()
//                                .requestMatchers("/v3/boards/{id}/statuses/**").authenticated()
//                                .requestMatchers("/validate-token").permitAll()
//                                .requestMatchers("/statuses/**").permitAll()
//                                .requestMatchers("/tasks/**").permitAll()
                                .anyRequest().authenticated())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .httpBasic(withDefaults());
//                .exceptionHandling(handling -> handling.authenticationEntryPoint(customAuthenticationEntryPoint));
        return httpSecurity.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(jwtUserDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager(); }
}
