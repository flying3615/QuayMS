package security

import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder

class HomeController {

    def index() {
        SecurityContext ctx = SecurityContextHolder.getContext()
        Authentication authentication = ctx.getAuthentication()
        def user = authentication?.principal
        render "${user} you are in the home controller"
    }
}
