package com.intouch.aligooligo.User.Entity;


import com.intouch.aligooligo.Target.Entity.Target;
import com.intouch.aligooligo.User.Enum.SocialType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user")
@Builder
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "user_email", length = 50, nullable = false)
    private String email;

    @Column(name = "user_password", length = 20)
    private String password;

    @Column(name = "nickname", length = 10, nullable = false)
    private String nickName;

    //socialType 추가
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SocialType socialType;

    @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "id"))
    @Column(name = "roles")
    private List<String> roles = new ArrayList<>();

    @OneToMany
    @JoinColumn(name = "target_id")
    private List<Target> targetList;

    public User(String email, List<String> roles) {
        this.email = email;
        this.roles = roles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }


    public User(Integer id, String email, String nickName){
        this.id = id;
        this.email = email;
        this.nickName = nickName;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
