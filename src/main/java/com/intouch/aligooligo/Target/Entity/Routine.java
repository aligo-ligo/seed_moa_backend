package com.intouch.aligooligo.Target.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
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
    private Integer id;

    @Column(name = "value", nullable = false, length = 50, unique = true)
    String value;


    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "target_id")
    private Target target;

    @Builder
    Routine(Integer id, String value, Target target){
        this.id = id;
        this.value = value;
        this.target = target;
    }

    public Routine(String value, Target target){
        this.value = value;
        this.target = target;
    }

}
