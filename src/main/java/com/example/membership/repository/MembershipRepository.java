package com.example.membership.repository;

import com.example.membership.entity.Membership;
import com.example.membership.entity.MembershipType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MembershipRepository extends JpaRepository<Membership, Long> {
    Membership findByUserIdAndMembershipType(String userId, MembershipType naver);

    List<Membership> findAllByUserId(String userId);
}
