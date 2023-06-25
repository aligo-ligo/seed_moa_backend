package com.intouch.aligooligo.Target;


import com.intouch.aligooligo.User.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @Column(name = "goal", nullable = false)
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

    @Column(name = "penalty", nullable = false)
    String penalty;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
