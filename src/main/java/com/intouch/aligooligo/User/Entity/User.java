package com.intouch.aligooligo.User.Entity;


import com.intouch.aligooligo.seed.domain.Seed;
import jakarta.persistence.*;
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

    @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "id"))
    @Column(name = "roles")
    private List<String> roles = new ArrayList<>();

    @OneToMany
    @JoinColumn(name = "target_id")
    private List<Seed> seedList;

    public User(String email, List<String> roles) {
        this.email = email;
        this.roles = roles;
    }

    public User(Long id, String email, String nickName){
        this.id = id;
        this.email = email;
        this.nickName = nickName;
    }

}
