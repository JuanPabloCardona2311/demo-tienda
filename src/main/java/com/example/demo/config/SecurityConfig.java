package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ✅ Handler para redirigir según rol
    @Bean
    public AuthenticationSuccessHandler customSuccessHandler() {
        return (request, response, authentication) -> {
            var authorities = authentication.getAuthorities();
            String redirectUrl = "/";

            if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                redirectUrl = "/admin/productos";  // admin gestiona productos
            } else if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_CLIENTE"))) {
                redirectUrl = "/compras/nueva"; // cliente va directo a comprar
            }

            response.sendRedirect(redirectUrl);
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Permitir H2 y desactivar CSRF en APIs de prueba
            .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**", "/api/**", "/admin/etl/**", "/registro"))

            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/registro", "/h2-console/**", "/css/**", "/js/**", "/images/**", "/dashboard").permitAll()
                .requestMatchers("/api/clientes/**").permitAll()
                .requestMatchers("/api/analytics/**").permitAll()
                .requestMatchers("/api/usuarios/**").hasRole("ADMIN")
                .requestMatchers("/api/productos/**").hasAnyRole("ADMIN", "TECNICO")
                .requestMatchers("/admin/etl/**").hasRole("ADMIN")
                .requestMatchers("/admin/productos/**").hasRole("ADMIN")
                .requestMatchers("/compras/**").hasAnyRole("ADMIN", "CLIENTE")
                .anyRequest().authenticated()
            )

            // Login con formulario y redirección según rol
            .formLogin(form -> form
                .loginPage("/login")
                .successHandler(customSuccessHandler())
                .permitAll()
            )

            // Autenticación básica para Postman/APIs externas
            .httpBasic(basic -> {})

            // Logout
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );

        // Necesario para que /h2-console funcione
        http.headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }
}


// package com.example.demo.config;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.security.web.SecurityFilterChain;

// @Configuration
// public class SecurityConfig {

//     @Bean
//     public PasswordEncoder passwordEncoder() {
//         return new BCryptPasswordEncoder();
//     }

//     @Bean
//     public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//         http
//             // Permitir H2 y desactivar CSRF en APIs (para pruebas con Postman)
//             .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**", "/api/**"))

//             .authorizeHttpRequests(auth -> auth
//                 .requestMatchers("/login", "/h2-console/**", "/css/**", "/js/**", "/images/**").permitAll()
//                 .requestMatchers("/api/clientes/**").permitAll()
//                 .requestMatchers("/api/usuarios/**").hasRole("ADMIN")
//                 .requestMatchers("/api/productos/**").hasAnyRole("ADMIN", "TECNICO")
//                 .anyRequest().authenticated()
//             )

//             // Login con formulario
//             .formLogin(form -> form
//                 .loginPage("/login")
//                 .defaultSuccessUrl("/", true)
//                 .permitAll()
//             )

//             // Autenticación básica para Postman/APIs externas
//             .httpBasic(basic -> {})  // ✅ nueva forma recomendada

//             // Logout
//             .logout(logout -> logout
//                 .logoutSuccessUrl("/login?logout")
//                 .permitAll()
//             );

//         // Necesario para que /h2-console funcione
//         http.headers(headers -> headers.frameOptions(frame -> frame.disable()));

//         return http.build();
//     }
// }


// package com.example.demo.config;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.security.web.SecurityFilterChain;

// @Configuration
// public class SecurityConfig {

//     // Bean para encriptar contraseñas
//     @Bean
//     public PasswordEncoder passwordEncoder() {
//         return new BCryptPasswordEncoder();
//     }

//     // Configuración de seguridad
//     @Bean
//     public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//         http
//                 // Permitir H2 y desactivar CSRF en APIs (para pruebas con Postman)
//                 .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**", "/api/**"))

//                 .authorizeHttpRequests(auth -> auth
//                         // Recursos públicos
//                         .requestMatchers("/login", "/h2-console/**", "/css/**", "/js/**", "/images/**").permitAll()
//                         // Endpoints protegidos por rol
//                         .requestMatchers("/api/usuarios/**").hasRole("ADMIN")
//                         .requestMatchers("/api/productos/**").hasAnyRole("ADMIN", "TECNICO")
//                         // Todo lo demás requiere autenticación
//                         .anyRequest().authenticated()
//                 )

//                 // Configuración de login
//                 .formLogin(login -> login
//                         .loginPage("/login")              // Página de login custom (Thymeleaf)
//                         .defaultSuccessUrl("/", true)     // Redirige al home tras login
//                         .permitAll()
//                 )

//                 // Configuración de logout
//                 .logout(logout -> logout
//                         .logoutSuccessUrl("/login?logout")
//                         .permitAll()
//                 );

//         // Necesario para que /h2-console funcione (usa frames)
//         http.headers(headers -> headers.frameOptions(frame -> frame.disable()));

//         return http.build();
//     }
// }
