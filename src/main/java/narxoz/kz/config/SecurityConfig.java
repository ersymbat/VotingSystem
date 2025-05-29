package narxoz.kz.config;


import narxoz.kz.auth.MyUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
   @Bean
   public MyUserService userService(){
       return new MyUserService();
   }
   @Bean
   public PasswordEncoder passwordEncoder(){
       return new BCryptPasswordEncoder();
   }
   @Bean
   public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
       AuthenticationManagerBuilder authenticationManagerBuilder =
               http.getSharedObject(AuthenticationManagerBuilder.class);
       authenticationManagerBuilder
               .userDetailsService(userService())
               .passwordEncoder(passwordEncoder());
       http.formLogin(formLogin -> formLogin
               .loginPage("/election/login")
               .loginProcessingUrl("/sign-in")
               .usernameParameter("user-email")
               .passwordParameter("user-password")
               .defaultSuccessUrl("/election/home")
               .failureUrl("/election/login?error")
       );
       http.logout(logout -> logout
               .logoutUrl("/logout")
               .logoutSuccessUrl("/election/login")
       );
       http.csrf(csrf -> csrf.disable());
       return http.build();
   }
}
