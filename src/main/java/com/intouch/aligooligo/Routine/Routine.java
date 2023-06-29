package com.intouch.aligooligo.Routine;

import com.intouch.aligooligo.Target.Target;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "routine")
@Builder
public class Routine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "value", nullable = false, length = 50)
    String value;


    @ManyToOne
    @JoinColumn(name = "target_id")
    private Target target;


}
