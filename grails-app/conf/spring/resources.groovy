// Place your Spring DSL code here
beans = {
    webSecurityConfiguration(quayms.InterceptMapSecurityConfig)
    userDetailsService(security.GormUserDetailService)
    passwordEncoder(org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder)
}

