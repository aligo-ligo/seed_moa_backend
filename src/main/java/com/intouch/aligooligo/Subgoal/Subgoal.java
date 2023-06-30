package com.intouch.aligooligo.Subgoal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.intouch.aligooligo.Target.Target;
import jakarta.persistence.*;
import lombok.*;
import org.checkerframework.common.aliasing.qual.Unique;

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

    @Column(name = "value", nullable = false, length = 100, unique = true)
    String value;

    @Column(name = "success", unique = true)
    boolean success;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "target_id")
    private Target target;

}
