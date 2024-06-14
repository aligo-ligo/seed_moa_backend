package com.intouch.aligooligo.domain.seed.domain;

import com.intouch.aligooligo.domain.member.entity.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "cheering")
public class Cheering {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "seed_id", nullable = false)
    private Seed seed;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Member member;

    public Cheering(Seed seed, Member member) {
        this.seed = seed;
        this.member = member;
    }
}
