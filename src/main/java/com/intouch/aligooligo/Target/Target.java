package com.intouch.aligooligo.Target;


import com.intouch.aligooligo.Routine.Routine;
import com.intouch.aligooligo.ShortUrl.ShortUrl;
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
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "goal", nullable = false, length = 100)
    private String goal;

    @Column(name = "subgoal_total")
    private Double subGoalTotal;

    @Column(name = "success_count")
    private Integer successCount;

    @Column(name = "failure_vote")
    private Integer failureVote;

    @Column(name = "success_vote")
    private Integer successVote;

    @Column(name = "vote_total")
    private Integer voteTotal;

    @Column(name = "penalty", nullable = false, length = 50)
    private String penalty;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "target")
    @Column(name = "subgoals")
    private List<Subgoal> subGoal;

    @OneToMany(mappedBy = "target")
    @Column(name = "routines")
    private List<Routine> routine;
}
