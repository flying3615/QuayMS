package security

import grails.rest.Resource
import org.springframework.security.crypto.password.PasswordEncoder

@Resource(uri='/user', formats=['json','xml'])
class User {

    String username
    String password
    boolean enabled = true
    boolean accountExpired
    boolean accountLocked
    boolean credentialsExpired

    //This could be defined as `def passwordEncoder` as well and the import would be unnecessary
    PasswordEncoder passwordEncoder


    static constraints = {
        username blank: false, unique: true
        password blank: false
    }

    static mapping = {
        password column: '`password`'
    }

    Set getAuthorities(){
        UserAuthority.findAllByUser(this).collect{it.authority}
    }

    static transients = ['passwordEncoder']

    def beforeInsert(){
        encodePassword()
    }

    def beforeUpdate(){
        if(isDirty('password')){
            encodePassword()
        }
    }

    protected void encodePassword(){
        password = passwordEncoder.encode(password)
    }
}
