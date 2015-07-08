package security

import grails.transaction.Transactional
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException

@Transactional
class GormUserDetailService implements UserDetailsService{


    @Transactional(readOnly = true, noRollbackFor = [IllegalArgumentException, UsernameNotFoundException])
    UserDetails loadUserByUsername(String username, boolean loadRoles) throws UsernameNotFoundException {

        def user = User.findWhere(username: username)
        if (!user) {
            log.warn "User not found: $username"
            throw new UsernameNotFoundException('User not found')
        }

        Collection authorities = loadAuthorities(user, username, loadRoles)
        createUserDetails user, authorities
    }

    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        loadUserByUsername username, true
    }

    protected Collection loadAuthorities(User user, String username, boolean loadRoles){
        if(!loadRoles){
            return []
        }
        Collection userAuthorities = user.authorities
        def authorities = userAuthorities.collect{new SimpleGrantedAuthority(it.authority)}
        return authorities?:[NO_ROLE]
    }

    protected UserDetails createUserDetails(User user, Collection authorities){

        new GrailsUser(user.username, user.password, user.enabled, !user.accountExpired, !user.credentialsExpired,
                !user.accountLocked, authorities, user.id)
    }


    def getCurrentUser(){
        SecurityContext ctx = SecurityContextHolder.getContext()
        Authentication authentication = ctx.getAuthentication()
        authentication?.principal
    }




}
