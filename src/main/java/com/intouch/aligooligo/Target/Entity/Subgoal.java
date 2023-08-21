package com.intouch.aligooligo.Target.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "subgoal")
public class Subgoal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "value", nullable = false, length = 100, unique = true)
    String value;

    @Column(name = "completed_date")
    LocalDate completedDate;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "target_id")
    private Target target;

    @Builder
    public Subgoal(Integer id, String value, LocalDate completedDate, Target target){
        this.id = id;
        this.value = value;
        this.completedDate = completedDate;
        this.target = target;
    }
    public Subgoal(String value, LocalDate completedDate, Target target){
        this.value = value;
        this.completedDate = completedDate;
        this.target = target;
    }

    public void updateDate(LocalDate completedDate){
        this.completedDate = completedDate;
    }
}