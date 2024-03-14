package com.intouch.aligooligo.Target.Controller.Dto;

import com.intouch.aligooligo.Target.Entity.Target;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SubgoalDTO {
    String value;
    LocalDate completedDate;
    private Target target;
}
