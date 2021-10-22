package com.example.membership.service;

import com.example.membership.dto.MembershipAddResponseDTO;
import com.example.membership.dto.MembershipDetailResponseDTO;
import com.example.membership.entity.Membership;
import com.example.membership.entity.MembershipType;
import com.example.membership.exception.MembershipException;
import com.example.membership.repository.MembershipRepository;
import com.example.membership.error.MembershipErrorResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MembershipService {

    private final MembershipRepository membershipRepository;
    private final PointService ratePointService;

    public MembershipAddResponseDTO addMembership(final String userId, final MembershipType membershipType, final Integer point){
        final Membership result = membershipRepository.findByUserIdAndMembershipType(userId, membershipType);
        if(result != null){
            throw new MembershipException(MembershipErrorResult.DUPLICATED_MEMBERSHIP_REGISTER);
        }

        final Membership membership = Membership.builder()
                .userId(userId)
                .point(point)
                .membershipType(membershipType)
                .build();

        Membership saveMember = membershipRepository.save(membership);

        return MembershipAddResponseDTO.builder()
                .id(saveMember.getId())
                .membershipType(saveMember.getMembershipType())
                .build();

    }

    public List<MembershipDetailResponseDTO> getMembershipList(String userId) {
        List<Membership> membershipList = membershipRepository.findAllByUserId(userId);

        return membershipList.stream()
                .map(v -> MembershipDetailResponseDTO.builder()
                    .id(v.getId())
                    .membershipType(v.getMembershipType())
                    .point(v.getPoint())
                    .createdAt(v.getCreatedAt())
                    .build())
                .collect(Collectors.toList());
    }

    public MembershipDetailResponseDTO getMembership(String userId, MembershipType membershipType) {
        Membership findResult = membershipRepository.findByUserIdAndMembershipType(userId,membershipType);
        if(findResult == null){
            throw new MembershipException(MembershipErrorResult.MEMBERSHIP_NOT_FOUND);
        }

        return MembershipDetailResponseDTO.builder()
                .id(findResult.getId())
                .membershipType(findResult.getMembershipType())
                .point(findResult.getPoint())
                .createdAt(findResult.getCreatedAt())
                .build();
    }

    public void removeMembership(Long membershipId, String userId) {
        Optional<Membership> optionalMembership = membershipRepository.findById(membershipId);
        Membership membership = optionalMembership.orElseThrow(() -> new MembershipException(MembershipErrorResult.MEMBERSHIP_NOT_FOUND));

        if(!membership.getUserId().equals(userId)){
            throw new MembershipException(MembershipErrorResult.NOT_MEMBERSHIP_OWNER);
        }

        membershipRepository.deleteById(membershipId);
    }

    public void accumulateMembershipPoint(Long membershipId, String userId, int amount) {
        Optional<Membership> optionalMembership = membershipRepository.findById(membershipId);
        Membership membership = optionalMembership.orElseThrow(() -> new MembershipException(MembershipErrorResult.MEMBERSHIP_NOT_FOUND));

        if(!membership.getUserId().equals(userId)){
            throw new MembershipException(MembershipErrorResult.NOT_MEMBERSHIP_OWNER);
        }

        int additionalAmount = ratePointService.calculateAmount(amount);

        membership.addPoint(additionalAmount);
    }
}
