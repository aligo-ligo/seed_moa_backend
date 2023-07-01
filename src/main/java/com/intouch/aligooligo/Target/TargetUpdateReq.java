package com.intouch.aligooligo.Target;

import java.time.LocalDate;

public record TargetUpdateReq(Long targetId, String subGoal, String completeDate) {
}
