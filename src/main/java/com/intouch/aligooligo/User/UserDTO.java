package com.intouch.aligooligo.User;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user")
@Builder
public class UserDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_email", length = 50, nullable = false, unique = true)
    private String email;

    @Column(name = "user_password", length = 20, nullable = false)
    private String password;

    @Column(name = "nickname", length = 10, nullable = false)
    private String nickName;

    UserDTO(String email, String password, String nickName){
        this.email = email;
        this.password = password;
        this.nickName = nickName;
    }

}
