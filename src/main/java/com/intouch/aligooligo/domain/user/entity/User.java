package com.intouch.aligooligo.domain.user.entity;


import jakarta.persistence.*;
import lombok.*;

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

    public User(String email, String nickName, Role roles){
        this.email = email;
        this.nickName = nickName;
        this.roles = roles;
    }

}
