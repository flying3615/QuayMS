import grails.util.Environment
import security.Authority
import security.User
import security.UserAuthority

class BootStrap {

    def init = { servletContext ->
        switch (Environment.current){
            case Environment.DEVELOPMENT:
                User user = new User(username:'user',password:'user',enable:true,accountExpired: false,accountLocked: false,creddentialsExpired: false)
                .save(failOnError:true)
                User admin = new User(username:'admin',password: 'admin', enable: true, accountExpired: false,accountLocked: false,creddentialsExpired: false)
                .save(failOnError: true)

                def roleUser = new Authority(authority: 'ROLE_USER').save(failOnError: true)
                def roleAdmin = new Authority(authority: 'ROLE_ADMIN').save(failOnError: true)

                UserAuthority.create(user,roleUser,true)
                UserAuthority.create(admin,roleUser,true)
                UserAuthority.create(admin,roleAdmin,true)

                println '----------init user--------------'
                break
            case Environment.PRODUCTION:
                break

        }
    }
    def destroy = {
    }
}
