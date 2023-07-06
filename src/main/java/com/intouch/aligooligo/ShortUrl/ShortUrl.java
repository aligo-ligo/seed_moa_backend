package com.intouch.aligooligo.ShortUrl;

import com.intouch.aligooligo.Target.Target;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "short_url")
public class ShortUrl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "short_url", nullable = false, length = 50)
    private String shortUrl;
    @Column(name = "origin_url", nullable = false, length = 200)
    private String originUrl;
    @OneToOne
    @JoinColumn(name = "target_id")
    private Target target;
}
