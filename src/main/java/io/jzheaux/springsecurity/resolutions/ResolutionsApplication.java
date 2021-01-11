package io.jzheaux.springsecurity.resolutions;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;

@SpringBootApplication
public class ResolutionsApplication extends WebSecurityConfigurerAdapter {

	public static void main(String[] args) {
		SpringApplication.run(ResolutionsApplication.class, args);
	}
	
	@Bean
	UserDetailsService userDetailsService(UserRepository users) {
	    return new UserRepositoryUserDetailsService(users);
	}
	
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests(authz -> authz
                .mvcMatchers(GET, "/resolutions", "/resolution/**").hasAuthority("resolution:read")
                .mvcMatchers(POST, "/resolution").hasAuthority("resolution:write")
               // .anyRequest().hasAuthority("resolution:write")
                ).httpBasic(basic -> {});
    }
}
