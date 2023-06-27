package com.intouch.aligooligo.Target;


import com.intouch.aligooligo.Routine.Routine;
import com.intouch.aligooligo.Subgoal.Subgoal;
import com.intouch.aligooligo.User.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "target")
@Builder
public class Target {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "start_date", nullable = false)
    LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    LocalDate endDate;

    @Column(name = "goal", nullable = false, length = 100)
    String goal;

    @Column(name = "subgoal_total")
    Double subGoalTotal;

    @Column(name = "success_count")
    Integer successCount;

    @Column(name = "failure_vote")
    Integer failureVote;

    @Column(name = "success_vote")
    Integer successVote;

    @Column(name = "vote_total")
    Integer voteTotal;

    @Column(name = "penalty", nullable = false, length = 50)
    String penalty;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "target")
    private List<Subgoal> subgoals;

    @OneToMany(mappedBy = "target")
    private List<Routine> routines;
}
