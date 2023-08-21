package com.intouch.aligooligo.Target.Controller.Dto;

import com.intouch.aligooligo.Target.Entity.Routine;
import com.intouch.aligooligo.Target.Entity.Subgoal;
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
    private Integer failureVote;
    private Integer successVote;
    private Integer voteTotal;
    private Map<String, Integer> achievementDate;
}