package com.intouch.aligooligo.Subgoal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.intouch.aligooligo.Target.Target;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

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
    Date completedDate;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "target_id")
    private Target target;

    @Builder
    Subgoal(Integer id, String value, Date completedDate, Target target){
        this.id = id;
        this.value = value;
        this.completedDate = completedDate;
        this.target = target;
    }

    public void updateDate(Date completedDate){
        this.completedDate = completedDate;
    }
}