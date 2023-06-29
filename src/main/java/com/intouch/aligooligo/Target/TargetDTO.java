package com.intouch.aligooligo.Target;

import com.intouch.aligooligo.Routine.Routine;
import com.intouch.aligooligo.Subgoal.Subgoal;
import com.intouch.aligooligo.User.User;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class TargetDTO {
    private Long userId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String goal;
    private List<Subgoal> subGoal;
    private List<Routine> routine;
    private String penalty;
    private Double subGoalTotal;
    private Integer successCount;
    private Integer failureVote;
    private Integer successVote;
    private Integer voteTotal;


}
