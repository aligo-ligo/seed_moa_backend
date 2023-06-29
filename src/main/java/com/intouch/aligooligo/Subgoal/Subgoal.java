package com.intouch.aligooligo.Subgoal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.intouch.aligooligo.Target.Target;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "subgoal")
@Builder
public class Subgoal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "value", nullable = false, length = 100)
    String value;

    @Column(name = "success")
    boolean success;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "target_idid")
    private Target target;

}
