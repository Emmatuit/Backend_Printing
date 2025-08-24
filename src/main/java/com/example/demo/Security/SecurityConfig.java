package com.example.demo.Security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Autowired
	private JwtFilter jwtFilter;

	@Autowired
	private UserDetailsService userDetailsService;

	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
			throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	@Bean
	AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService);
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();

		configuration.setAllowedOrigins(List.of("http://localhost:59327"));
		configuration.setAllowedMethods(List.of("GET", "POST", "DELETE", "PATCH"));
		configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);

		return source;
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(12);
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable()).cors(Customizer.withDefaults()).authorizeHttpRequests(auth -> auth
				// 1️⃣ Permit Swagger UI & API docs WITHOUT authentication
				.requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/swagger-resources/**",
						"/webjars/**")
				.permitAll()

				// 2️⃣ Permit public API endpoints
				.requestMatchers("/api/auth/register", "/api/auth/login1").permitAll()

				// 3️⃣ Permit specific API GET/POST/DELETE requests
				.requestMatchers(HttpMethod.GET, "/api/cart/items").permitAll()
				.requestMatchers(HttpMethod.DELETE, "/api/cart/remove").permitAll()
				.requestMatchers(HttpMethod.GET, "/api/cart/count").permitAll()
				.requestMatchers(HttpMethod.POST, "/api/cart/logout").permitAll()
				.requestMatchers(HttpMethod.GET, "/api/CalculateSubtotal").permitAll()
				.requestMatchers(HttpMethod.GET, "/api/admin/orders/{id}").permitAll()

				// 4️⃣ Admin endpoint with role restriction
				.requestMatchers("/api/admin/login").permitAll().requestMatchers("/api/admin/**").hasRole("ADMIN")

				// 5️⃣ Authenticated users only for some endpoints
				.requestMatchers("/api/place", "/api/auth/change-password1").authenticated()

				// 6️⃣ All other requests require authentication
				.anyRequest().authenticated())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
				.authenticationProvider(authenticationProvider())
				.logout(logout -> logout.logoutUrl("/api/logout")
						.logoutSuccessHandler(
								(request, response, authentication) -> response.setStatus(HttpServletResponse.SC_OK))
						.invalidateHttpSession(true).deleteCookies("JSESSIONID").permitAll());

		return http.build();
	}

}
