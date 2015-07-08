grails.plugin.springsecurity.interceptUrlMap = [
        '/':                  ['permitAll'],
        '/logout':            ['permitAll'],
        '/resources/**':      ['permitAll'],
        '/admin/**':           ['ROLE_ADMIN'],
        '/home/**':           ['ROLE_USER']
]