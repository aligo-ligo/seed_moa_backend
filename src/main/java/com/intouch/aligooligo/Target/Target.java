package com.intouch.aligooligo.Target;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "target")
@Builder
public class Target {

    @Column(name = "start_date", nullable = false)
    Date startDate;

    @Column(name = "end_date", nullable = false)
    Date endDate;

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
}
