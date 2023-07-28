package com.intouch.aligooligo.Target;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.intouch.aligooligo.Routine.Routine;
import com.intouch.aligooligo.Subgoal.Subgoal;
import com.intouch.aligooligo.User.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "target")
public class Target {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "start_date", nullable = false)
    private Date startDate;

    @Column(name = "end_date", nullable = false)
    private Date endDate;

    @Column(name = "goal", nullable = false, length = 100)
    private String goal;

    @Column(name = "url", length = 50)
    private String url;

    @Column(name = "failure_vote")
    private Integer failureVote;

    @Column(name = "success_vote")
    private Integer successVote;

    @Column(name = "vote_total")
    private Integer voteTotal;

    @Column(name = "penalty", length = 50)
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

    @Builder
    public Target(Integer id, Date startDate, Date endDate, String goal, String penalty, Integer subGoalTotal,
                  Integer failureVote,Integer successVote, Integer voteTotal, User user, List<Subgoal> subGoal, List<Routine> routine){
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.goal = goal;
        this.penalty = penalty;
        this.failureVote = failureVote;
        this.successVote = successVote;
        this.voteTotal = voteTotal;
        this.user = user;
        this.subGoal = subGoal;
        this.routine = routine;
    }
    public void updateUrl(String url){
        this.url = url;
    }

    public void updateVote(Integer successVote, Integer failureVote, Integer voteTotal){
        this.successVote = successVote;
        this.failureVote = failureVote;
        this.voteTotal = voteTotal;
    }
}