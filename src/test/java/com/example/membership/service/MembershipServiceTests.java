package com.example.membership.service;

import com.example.membership.dto.MembershipAddResponseDTO;
import com.example.membership.dto.MembershipDetailResponseDTO;
import com.example.membership.entity.Membership;
import com.example.membership.entity.MembershipType;
import com.example.membership.exception.MembershipException;
import com.example.membership.repository.MembershipRepository;
import com.example.membership.error.MembershipErrorResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MembershipServiceTests {

    @InjectMocks
    private MembershipService target;

    @Mock
    private MembershipRepository membershipRepository;

    private final String userId = "userId";
    private final MembershipType membershipType = MembershipType.NAVER;
    private final Integer point = 10000;


    @Test
    public void 멤버십등록실패_이미존재함(){
        //given
        doReturn(Membership.builder().build()).when(membershipRepository).findByUserIdAndMembershipType(userId, membershipType);

        //when
        final MembershipException result = assertThrows(MembershipException.class, () -> target.addMembership(userId, membershipType, point));

        //then
        assertThat(result.getErrorResult()).isEqualTo(MembershipErrorResult.DUPLICATED_MEMBERSHIP_REGISTER);
    }


    @Test
    public void 멤버십등록성공(){
        //given
        doReturn(null).when(membershipRepository).findByUserIdAndMembershipType(userId, membershipType);
        doReturn(membership()).when(membershipRepository).save(any(Membership.class));

        //when
        final MembershipAddResponseDTO result = target.addMembership(userId,membershipType, point);

        //then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getMembershipType()).isEqualTo(MembershipType.NAVER);

        // verify
        verify(membershipRepository, times(1)).findByUserIdAndMembershipType(userId, membershipType);
        verify(membershipRepository, times(1)).save(any(Membership.class));
    }

    @Test
    public void 멤버십목록조회() throws Exception{
        //given
        doReturn(Arrays.asList(
                Membership.builder().build(),
                Membership.builder().build(),
                Membership.builder().build()
        )).when(membershipRepository).findAllByUserId(userId);

        //when
        final List<MembershipDetailResponseDTO> result = target.getMembershipList(userId);

        //then
        assertThat(result.size()).isEqualTo(3);
    }

    @Test
    public void 멤버십상세조회실패_존재하지않음() throws Exception{
        //given
        doReturn(null).when(membershipRepository).findByUserIdAndMembershipType(userId,membershipType);

        //when
        MembershipException result = assertThrows(MembershipException.class, () -> target.getMembership(userId, membershipType));

        //then
        assertThat(result.getErrorResult()).isEqualTo(MembershipErrorResult.MEMBERSHIP_NOT_FOUND);
    }
    @Test
    public void 멤버십상세조회성공() throws Exception{
        //given
        doReturn(Membership.builder()
                .id(-1L)
                .membershipType(membershipType)
                .point(point)
                .createdAt(LocalDateTime.now())
        .build())
                .when(membershipRepository).findByUserIdAndMembershipType(userId,membershipType);

        //when
        MembershipDetailResponseDTO result = target.getMembership(userId, membershipType);

        //then
        assertThat(result.getMembershipType()).isEqualTo(membershipType);
        assertThat(result.getPoint()).isEqualTo(point);
    }

    private final Long membershipId = -1L;

    @Test
    public void 멤버십삭제실패_존재하지않은멤버십() throws Exception{
        //given
        doReturn(Optional.empty()).when(membershipRepository).findById(membershipId);

        //when
        MembershipException result = assertThrows(MembershipException.class, () -> target.removeMembership(membershipId, userId));

        //then
        assertThat(result.getErrorResult()).isEqualTo(MembershipErrorResult.MEMBERSHIP_NOT_FOUND);
    }

    @Test
    public void 멤버십삭제실패_본인이아님() throws Exception{
        //given
        Membership membership = membership();
        doReturn(Optional.of(membership)).when(membershipRepository).findById(membershipId);

        //when
        MembershipException result = assertThrows(MembershipException.class, () -> target.removeMembership(membershipId, "notowner"));

        //then
        assertThat(result.getErrorResult()).isEqualTo(MembershipErrorResult.NOT_MEMBERSHIP_OWNER);
    }

    @Test
    public void 멤버십삭제성공() throws Exception{
        //given
        Membership membership = membership();
        doReturn(Optional.of(membership)).when(membershipRepository).findById(membershipId);

        //when
        target.removeMembership(membershipId,userId);

        //then
    }

    @Mock
    private RatePointService ratePointService;

    @Test
    public void 멤버십적립실패_존재하지않음() throws Exception{
        //given
        doReturn(Optional.empty()).when(membershipRepository).findById(membershipId);

        //when
        MembershipException result = assertThrows(MembershipException.class, () -> target.accumulateMembershipPoint(membershipId, userId, 10000));

        //then
        assertThat(result.getErrorResult()).isEqualTo(MembershipErrorResult.MEMBERSHIP_NOT_FOUND);
    }

    @Test
    public void 멤버십적립실패_본인이아님() throws Exception{
        //given
        Membership membership = membership();
        doReturn(Optional.of(membership)).when(membershipRepository).findById(membershipId);

        //when
        MembershipException result = assertThrows(MembershipException.class, () -> target.accumulateMembershipPoint(membershipId, "notowner", 10000));

        //then
        assertThat(result.getErrorResult()).isEqualTo(MembershipErrorResult.NOT_MEMBERSHIP_OWNER);
    }

    @Test
    public void 멤버십적립성공() throws Exception{
        //given
        Membership membership = membership();
        doReturn(Optional.of(membership)).when(membershipRepository).findById(membershipId);

        //when
        target.accumulateMembershipPoint(membershipId, userId, 10000);

        //then
    }


    private Membership membership() {
        return Membership.builder()
                .id(-1L)
                .userId(userId)
                .point(point)
                .membershipType(MembershipType.NAVER)
                .build();
    }
}
