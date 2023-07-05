package com.intouch.aligooligo.Target;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TargetListDTO {
    private Long id;
    private Long userId;
    private String goal;
    private Integer subGoalTotal;
    private Integer successCount;
    private Integer successVote;
    private Integer voteTotal;
}
