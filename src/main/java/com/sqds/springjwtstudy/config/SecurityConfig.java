package com.sqds.springjwtstudy.config;

import com.sqds.springjwtstudy.filter.JwtAuthFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    com.sqds.springjwtstudy.service.dataservice dataservice;
    @Autowired
    JwtAuthFilter jwtAuthFilter;



    @Bean
    public DefaultSecurityFilterChain defaultSecurityilterChain(HttpSecurity httpSecurity) throws  Exception
    {
        httpSecurity.csrf(s->s.disable());

               httpSecurity.authorizeHttpRequests(r->r.requestMatchers(CorsUtils::isPreFlightRequest).permitAll().requestMatchers("/auth/login","/auth/getalluser","/auth/adduesr","/auth/getuserinfo/*","/auth/validtoken").permitAll().anyRequest().authenticated()
                        );
        httpSecurity.sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        //httpSecurity.formLogin(s->s.loginProcessingUrl("/login"));
        httpSecurity.authenticationProvider(authenticationProvider()).addFilterBefore(jwtAuthFilter,UsernamePasswordAuthenticationFilter.class);



        return httpSecurity.build();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource()
    {
        CorsConfiguration corsConfiguration=new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(Arrays.asList());
        corsConfiguration.setAllowedMethods(Arrays.asList("*"));
        corsConfiguration.setAllowedHeaders(Arrays.asList("*"));
        corsConfiguration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source=new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",corsConfiguration);
        return source;

    }
    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return  new BCryptPasswordEncoder();
    }




    private void jwtsuccesshandle(HttpServletRequest request, HttpServletResponse httpServletResponse, Authentication authentication) {
        System.out.println("success");
    }
    @Bean(name ="redistemplate")
    public RedisTemplate<String,Object> getredis(RedisConnectionFactory redisConnectionFactory)
    {

        RedisTemplate<String,Object> redisTemplate=new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        RedisSerializer stringSerializer = new StringRedisSerializer();
        GenericJackson2JsonRedisSerializer serializer=new GenericJackson2JsonRedisSerializer();
        redisTemplate.setKeySerializer(stringSerializer);
        redisTemplate.setHashKeySerializer(stringSerializer);
        redisTemplate.setValueSerializer(serializer);
        redisTemplate.afterPropertiesSet();


        return redisTemplate;

    }

    @Bean
    public AuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authenticationProvider=new DaoAuthenticationProvider();
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        authenticationProvider.setUserDetailsService(dataservice);

        return  authenticationProvider;

    }

    @Bean
    public AuthenticationManager authenticationManagerBean(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return  authenticationConfiguration.getAuthenticationManager();
    }
}
