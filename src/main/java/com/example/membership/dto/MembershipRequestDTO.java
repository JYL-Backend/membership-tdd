package com.example.membership.dto;


import com.example.membership.entity.MembershipType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MembershipRequestDTO {

    @NotNull
    @Min(0)
    private Integer point;

    @NotNull
    private MembershipType membershipType;
}
