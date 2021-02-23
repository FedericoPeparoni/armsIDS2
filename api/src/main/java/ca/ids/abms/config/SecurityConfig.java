package ca.ids.abms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.data.repository.query.SecurityEvaluationContextExtension;

import ca.ids.abms.config.auth.CustomAuthenticationProvider;
import ca.ids.abms.modules.usereventlogs.UserEventLogService;
import ca.ids.abms.security.UserDetailsService;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@SuppressWarnings("unused")
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;
    private final UserEventLogService userEventLogService;

    public SecurityConfig(UserDetailsService userDetailsService, UserEventLogService userEventLogService) {
        this.userDetailsService = userDetailsService;
        this.userEventLogService = userEventLogService;
    }

    @Bean
    @SuppressWarnings("WeakerAccess")
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private CustomAuthenticationProvider authProvider() {
        CustomAuthenticationProvider authProvider = new CustomAuthenticationProvider(userDetailsService, userEventLogService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth
            .authenticationProvider(authProvider());
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring()
            .antMatchers(HttpMethod.PUT, "/api/users/setCurrentUserLanguage")
            .antMatchers(HttpMethod.GET, "/api/system-configurations/noauth/getLanguages")
            .antMatchers(HttpMethod.GET, "/api/system-configurations/noauth/getUnitsOfMeasure")
            .antMatchers(HttpMethod.GET, "/api/system-configurations/noauth/getPasswordSettings")
            .antMatchers(HttpMethod.GET, "/api/system-configurations/noauth/getSelfCareSettings")
            .antMatchers(HttpMethod.GET, "/api/system-configurations/noauth/getAirNavigationChargesCurrency")
            .antMatchers(HttpMethod.GET, "/api/system-configurations/noauth/getANSPCurrency")
            .antMatchers(HttpMethod.GET, "/api/currency-exchange-rates/for-currency-code/**")
            .antMatchers(HttpMethod.POST, "/api/system-configurations/changelocale")
            .antMatchers(HttpMethod.GET, "/api/languages/json/**")
            .antMatchers(HttpMethod.OPTIONS, "/**")
            .antMatchers(HttpMethod.POST,"/api/query-submission")
            .antMatchers(HttpMethod.POST,"/api/sc-user-registration")
            .antMatchers(HttpMethod.GET,"/api/aircraft-types")
            .antMatchers(HttpMethod.POST,"/api/sc-user-registration/activate")
            .antMatchers(HttpMethod.POST,"/api/flight-cost-calculation")
            .antMatchers(HttpMethod.POST,"/api/users/passwordRecovery")
            .antMatchers(HttpMethod.GET, "/api/util/current-datetime")
            .antMatchers(HttpMethod.GET, "/api/util/recaptcha-public-key")
            .antMatchers(HttpMethod.GET, "/api/spec/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
        .httpBasic().realmName("abms")
        .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
            .requestMatchers().antMatchers("/oauth/authorize")
        .and()
            .authorizeRequests()
            .antMatchers("/oauth/authorize").authenticated();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public SecurityEvaluationContextExtension securityEvaluationContextExtension() {
        return new SecurityEvaluationContextExtension();
    }
}
