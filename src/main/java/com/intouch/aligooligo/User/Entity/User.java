package com.intouch.aligooligo.User.Entity;


import com.intouch.aligooligo.seed.domain.Seed;
import jakarta.persistence.*;
import java.util.Collection;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user")
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_email", length = 50, nullable = false, unique = true)
    private String email;

    @Column(name = "nickname", length = 10, nullable = false)
    private String nickName;

    @Enumerated(EnumType.STRING)

    @Column(name = "roles")
    private Role roles;

    public User(String email, Role roles) {
        this.email = email;
        this.roles = roles;
    }

    public User(Long id, String email, String nickName){
        this.id = id;
        this.email = email;
        this.nickName = nickName;
    }

}
