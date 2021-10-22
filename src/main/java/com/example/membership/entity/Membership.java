package com.example.membership.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Membership {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;
    private Integer point;

    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private MembershipType membershipType;

    public int addPoint(int point){
        this.point += point;
        return this.point;
    }

}
