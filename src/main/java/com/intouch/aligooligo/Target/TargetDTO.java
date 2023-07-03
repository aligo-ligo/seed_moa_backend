package com.intouch.aligooligo.Target;

import com.intouch.aligooligo.Routine.Routine;
import com.intouch.aligooligo.Subgoal.Subgoal;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TargetDTO {
    private Long id;
    private Long userId;
    private String startDate;
    private String endDate;
    private String goal;
    private List<Subgoal> subGoal;
    private List<Routine> routine;
    private String penalty;
    private Integer subGoalTotal;
    private Integer successCount;
    private Integer failureVote;
    private Integer successVote;
    private Integer voteTotal;


}
