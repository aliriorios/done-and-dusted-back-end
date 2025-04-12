package br.com.aliriorios.done_and_dusted.jwt;

import br.com.aliriorios.done_and_dusted.entity.User;
import org.springframework.security.core.authority.AuthorityUtils;

public class JwtUserDetails extends org.springframework.security.core.userdetails.User {
    private User user;

    public JwtUserDetails(User user) {
        super(user.getUsername(), user.getPassword(), AuthorityUtils.createAuthorityList(user.getRole().name()));
        this.user = user;
    }

    public Long getId() {
        return this.user.getId();
    }

    public String role() {
        return this.user.getRole().name();
    }
}
