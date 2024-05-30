package com.intouch.aligooligo.domain.seed.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.intouch.aligooligo.domain.user.entity.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "seed")
public class Seed {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @Column(name = "seed", nullable = false, length = 100)
    private String seed;

    @Column(name = "state", length = 10, nullable = false)
    private String state;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder
    public Seed(Long id, LocalDateTime startDate, LocalDateTime endDate, String seed, User user, List<Routine> routine){
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.seed = seed;
        this.user = user;
    }

    public void updateSeedState(String state) {
        this.state = state;
    }
}