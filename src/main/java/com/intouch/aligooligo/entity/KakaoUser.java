package com.intouch.aligooligo.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "kakao_user")
@SuperBuilder
public class KakaoUser extends User{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

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
    private List<Target> targetList;

    public KakaoUser(String email, List<String> roles) {
        this.email = email;
        this.roles = roles;
    }
}
