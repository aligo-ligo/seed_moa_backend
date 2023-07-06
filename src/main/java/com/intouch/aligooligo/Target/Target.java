package com.intouch.aligooligo.Target;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.intouch.aligooligo.Routine.Routine;
import com.intouch.aligooligo.Subgoal.Subgoal;
import com.intouch.aligooligo.User.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "target")
@Builder
public class Target {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "start_date", nullable = false)
    private String startDate;

    @Column(name = "end_date", nullable = false)
    private String endDate;

    @Column(name = "goal", nullable = false, length = 100)
    private String goal;

    @Column(name = "url", length = 50)
    private String url;

    @Column(name = "subgoal_total")
    private Integer subGoalTotal;

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
    @JsonIgnore
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "target")
    @Column(name = "subgoals")
    private List<Subgoal> subGoal;

    @OneToMany(mappedBy = "target")
    @Column(name = "routines")
    private List<Routine> routine;
}
