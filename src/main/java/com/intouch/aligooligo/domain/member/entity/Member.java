package com.intouch.aligooligo.domain.member.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member")
@Builder
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "member_email", length = 50, nullable = false, unique = true)
    private String email;

    @Column(name = "nickname", length = 10, nullable = false)
    private String nickName;

    @Enumerated(EnumType.STRING)

    @Column(name = "roles")
    private Role roles;

    public Member(String email, Role roles) {
        this.email = email;
        this.roles = roles;
    }

    public Member(String email, String nickName, Role roles){
        this.email = email;
        this.nickName = nickName;
        this.roles = roles;
    }

}
