package com.intouch.aligooligo.Target;

import com.intouch.aligooligo.Routine.Routine;
import com.intouch.aligooligo.Subgoal.Subgoal;
import com.intouch.aligooligo.User.User;
import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TargetDTO {
    private Integer id;
    private Integer userId;
    private String startDate;
    private String endDate;
    private String goal;
    private String url;
    private List<Subgoal> subGoal;
    private List<Routine> routine;
    private User user;
    private String penalty;
    private Integer failureVote;
    private Integer successVote;
    private Integer voteTotal;
    private Map<String, Integer> achievementDate;

    public TargetDTO(Integer id, Integer userId, String goal, Integer successVote, Integer voteTotal){
        this.id = id;
        this. userId = userId;
        this.goal = goal;
        this.successVote = successVote;
        this.voteTotal = voteTotal;

    }
    public TargetDTO(Integer id, Integer userId, String goal, String url, String penalty, String startDate, String endDate,
                     List<Subgoal> subGoal, List<Routine> routine, Integer successVote,
                     Integer failureVote, Integer voteTotal, Map<String, Integer> achievementDate){
        this.id = id;
        this.userId = userId;
        this.goal = goal;
        this.url = url;
        this.penalty = penalty;
        this.startDate = startDate;
        this.endDate = endDate;
        this.subGoal = subGoal;
        this.routine = routine;
        this.successVote = successVote;
        this.failureVote = failureVote;
        this.voteTotal = voteTotal;
        this.achievementDate = achievementDate;
    }
}