package quayms

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
@EnableWebSecurity
public class InterceptMapSecurityConfig extends WebSecurityConfigurerAdapter {

    // could we put this in resources.groovy?...
    @Bean(name = "securityConfig")
    ConfigObject securityConfig() {
        return new ConfigSlurper().parse(new ClassPathResource('spring-security-config.groovy').URL)
    }

    @Autowired
    ConfigObject securityConfig

    @Autowired
    PasswordEncoder passwordEncoder

    @Autowired
    UserDetailsService userDetailsService

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        println('-----interceptMap configure-----')
//        http
//                .authorizeRequests()
//                .antMatchers('/admin/**').hasAnyRole('ADMIN')
//                .antMatchers('/home/**').hasAnyRole('USER', 'ADMIN')
//                .antMatchers('/').permitAll()
//                .and()
//                .formLogin().loginPage("/login").permitAll()
//                .and()
//                .logout().permitAll()


        Map<String, ArrayList<String>> interceptMap = securityConfig.grails.plugin.springsecurity.interceptUrlMap
        interceptMap.each { entry ->
            if (entry.value.any { it == 'permitAll' }) {
                http.authorizeRequests().antMatchers(entry.key).permitAll()
            } else if (entry.value.any { it == 'IS_AUTHENTICATED_FULLY' || it == 'isFullyAuthenticated()' }) {
                http.authorizeRequests().antMatchers(entry.key).fullyAuthenticated().and().httpBasic()
            } else if (entry.value.any { it == 'IS_AUTHENTICATED_REMEMBERED' }) {
                http.authorizeRequests().antMatchers(entry.key).rememberMe().and().httpBasic()
            } else if (entry.value.any { it == 'IS_AUTHENTICATED_ANONYMOUSLY' }) {
                http.authorizeRequests().antMatchers(entry.key).anonymous().and().httpBasic()
            } else if (entry.value.any { it.startsWith('ROLE_') }) {
                List<String> roles = entry.value.findAll { it.startsWith('ROLE_') }
                //Spring Security doesn't need the 'ROLE_'
                roles = roles.collect {it - 'ROLE_'}
                http
                    .authorizeRequests()
                        .antMatchers(entry.key)
                        .hasAnyRole(roles as String[])
                    .and()
                        .httpBasic()
            }
        }
        // Added *ONLY* to display the dbConsole.
        // Best not to do this in production.  If you need frames, it would be best to use
        //     http.headers().frameOptions().addHeaderWriter(new XFrameOptionsHeaderWriter(XFrameOptionsMode.SAMEORIGIN));
        // or in Spring Security 4, changing .disable() to .sameOrigin()
        http.headers().frameOptions().disable()

        // Again, do not do this in production unless you fully understand how to mitigate Cross-Site Request Forgery
        // https://www.owasp.org/index.php/Cross-Site_Request_Forgery_%28CSRF%29_Prevention_Cheat_Sheet
        http.csrf().disable()
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder)
    }
}