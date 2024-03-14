package com.intouch.aligooligo.Target.Controller.Dto;

import com.intouch.aligooligo.Target.Entity.Target;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RoutineDTO {
    String value;
    private Target target;
}
