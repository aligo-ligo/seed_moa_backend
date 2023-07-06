package com.intouch.aligooligo.Subgoal;

import com.intouch.aligooligo.Target.Target;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "subgoal")
public class Subgoal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "value", nullable = false, length = 100, unique = true)
    String value;

    @Column(name = "completed_date")
    String completedDate;

    @ManyToOne
    @JoinColumn(name = "target_id")
    private Target target;

}
