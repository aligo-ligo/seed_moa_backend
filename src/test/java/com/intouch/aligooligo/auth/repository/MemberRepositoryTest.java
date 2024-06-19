package com.intouch.aligooligo.auth.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.intouch.aligooligo.domain.member.entity.Member;
import com.intouch.aligooligo.domain.member.entity.Role;
import com.intouch.aligooligo.domain.member.repository.MemberRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class MemberRepositoryTest {

    @Autowired
    MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        Member member = Member.builder()
                .email("test@test.com")
                .nickName("test")
                .roles(Role.MEMBER)
                .build();
        memberRepository.save(member);
    }

    @Test
    @DisplayName("이메일로 찾기 테스트")
    void findByEmailTest() {
        //given
        //when
        Optional<Member> result = memberRepository.findByEmail("test@test.com");
        //then
        assertThat(result.isPresent()).isTrue();
    }

    @Test
    @DisplayName("이메일로 찾기 실패 테스트")
    void findByEmailFailTest() {
        //given
        //when
        Optional<Member> result = memberRepository.findByEmail("test2@test.com");
        //then
        assertThat(result.isPresent()).isFalse();
    }

    @Test
    @DisplayName("이메일 존재 여부 성공 테스트")
    void ExistsByEmailTest() {
        //given
        //when
        Boolean result = memberRepository.existsByEmail("test@test.com");
        //then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("이메일 존재 여부 실패 테스트")
    void ExistsByEmailFailTest() {
        //given
        //when
        Boolean result = memberRepository.existsByEmail("test2@test.com");
        //then
        assertThat(result).isFalse();
    }
}
