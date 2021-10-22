package com.example.membership.dto;

import com.example.membership.entity.MembershipType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Builder
public class MembershipDetailResponseDTO {

    private Long id;
    private MembershipType membershipType;
    private Integer point;
    private LocalDateTime createdAt;

}
