package com.example.membership.repository;

import com.example.membership.entity.Membership;
import com.example.membership.entity.MembershipType;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
public class MembershipRepositoryTests{

    @Autowired
    private MembershipRepository membershipRepository;

    @Test
    public void 멤버쉽등록(){
        //given
        Membership membership = Membership.builder()
                .userId("userId")
                .membershipType(MembershipType.NAVER)
                .point(10000)
                .build();

        //when
        Membership membership1 = membershipRepository.save(membership);

        //then
        assertThat(membership1.getId()).isNotNull();
        assertThat(membership1.getUserId()).isEqualTo("userId");
        assertThat(membership1.getMembershipType()).isEqualTo(MembershipType.NAVER);
        assertThat(membership1.getPoint()).isEqualTo(10000);
    }

    @Test
    public void 멤버쉽존재여부(){
        //given
        Membership membership = Membership.builder()
                .userId("userId")
                .membershipType(MembershipType.NAVER)
                .point(10000)
                .build();

        //when
        membershipRepository.save(membership);
        Membership findMembership =  membershipRepository.findByUserIdAndMembershipType("userId", MembershipType.NAVER);

        //then
        assertThat(findMembership).isNotNull();
        assertThat(findMembership.getId()).isNotNull();
        assertThat(findMembership.getUserId()).isEqualTo("userId");
        assertThat(findMembership.getMembershipType()).isEqualTo(MembershipType.NAVER);
        assertThat(findMembership.getPoint()).isEqualTo(10000);
    }

    @Test
    public void 멤버십조회_사이즈0() throws Exception{
        //given

        //when
        List<Membership> result = membershipRepository.findAllByUserId("userId");

        //then
        assertThat(result.size()).isEqualTo(0);
    }

    @Test
    public void 멤버십조회_사이즈2() throws Exception{
        //given
        final Membership naverMembership = Membership.builder()
                .userId("userId")
                .membershipType(MembershipType.NAVER)
                .point(10000)
                .build();

        final Membership kakaoMembership = Membership.builder()
                .userId("userId")
                .membershipType(MembershipType.KAKAO)
                .point(10000)
                .build();

        membershipRepository.save(naverMembership);
        membershipRepository.save(kakaoMembership);

        //when
        List<Membership> result = membershipRepository.findAllByUserId("userId");

        //then
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    public void 멤버십추가후삭제() throws Exception{
        //given
        Membership naverMembership = Membership.builder()
                .userId("userId")
                .membershipType(MembershipType.NAVER)
                .point(10000)
                .build();
        Membership saveMembership = membershipRepository.save(naverMembership);

        //when
        membershipRepository.deleteById(saveMembership.getId());

        //then
    }
}


