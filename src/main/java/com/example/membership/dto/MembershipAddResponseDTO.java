package com.example.membership.dto;

import com.example.membership.entity.MembershipType;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;

@Getter
@Builder
public class MembershipAddResponseDTO {
    private Long id;
    private MembershipType membershipType;
}
