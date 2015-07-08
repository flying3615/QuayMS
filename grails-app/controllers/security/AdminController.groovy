package security

import org.springframework.security.web.bind.annotation.AuthenticationPrincipal

class AdminController {

    def userDetailsService

    def index() {
        def user = userDetailsService.currentUser
        render "${user} logged in as admin"
    }

    def list(@AuthenticationPrincipal User currentuser){
        render "${currentuser}"
    }
}
