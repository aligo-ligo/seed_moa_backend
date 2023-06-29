package com.intouch.aligooligo.Target;

import java.time.LocalDate;

public record TargetUpdateReq(Long id, String subGoal, String value, LocalDate completeDate) {
}
