package com.intouch.aligooligo.seed.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "routine")
public class Routine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title", nullable = false, length = 50)
    String title;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "seed_id", nullable = false)
    private Seed seed;

    public void updateRoutine(String routineTitle) {
        this.title = routineTitle;
    }

    @Builder
    public Routine (String title, Seed seed) {
        this.title = title;
        this.seed = seed;
    }



}
