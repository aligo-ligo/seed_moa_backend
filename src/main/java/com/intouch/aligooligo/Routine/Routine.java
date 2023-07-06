package com.intouch.aligooligo.Routine;

import com.intouch.aligooligo.Target.Target;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;


@Entity
@Getter
@Builder
@Table(name = "routine")
public class Routine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "value", nullable = false, length = 50, unique = true)
    String value;


    @ManyToOne
    @JoinColumn(name = "target_id")
    private Target target;


}
