package com.intouch.aligooligo.seed.domain;

import com.intouch.aligooligo.User.Entity.User;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "cheering")
public class Cheering {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "seed_id", nullable = false)
    private Seed seed;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Cheering(Seed seed, User user) {
        this.seed = seed;
        this.user = user;
    }
}
