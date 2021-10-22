package com.example.membership.cotroller;

import com.example.membership.dto.MembershipDetailResponseDTO;
import com.example.membership.dto.MembershipRequestDTO;
import com.example.membership.dto.MembershipAddResponseDTO;
import com.example.membership.entity.MembershipType;
import com.example.membership.error.DefaultRestController;
import com.example.membership.service.MembershipService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.List;

import static com.example.membership.cotroller.MembershipConstants.USER_ID_HEADER;

@RestController
@RequiredArgsConstructor
public class MembershipController extends DefaultRestController {

    private final MembershipService membershipService;

    /* 멤버쉽 등록 */
    @PostMapping("/api/v1/membership")
    public ResponseEntity<MembershipAddResponseDTO> addMembership(
            @RequestHeader(USER_ID_HEADER) String userId,
            @RequestBody @Valid MembershipRequestDTO membershipRequestDTO)
    {
        MembershipAddResponseDTO membershipAddResponseDTO = membershipService.addMembership(userId, membershipRequestDTO.getMembershipType(), membershipRequestDTO.getPoint());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(membershipAddResponseDTO);
    }

    /* 멤버쉽 리스트 조회 */
    @GetMapping("/api/v1/membership/list")
    public ResponseEntity<List<MembershipDetailResponseDTO>> getMembershipList(
            @RequestHeader(USER_ID_HEADER) final String userId
    )  {
        return ResponseEntity.ok(membershipService.getMembershipList(userId));
    }

    /* 멤버쉽 상세 조회 */
    @GetMapping ("/api/v1/membership")
    public ResponseEntity<MembershipDetailResponseDTO> getMembership(
            @RequestHeader(USER_ID_HEADER) String userId,
            @RequestParam MembershipType membershipType)
    {

        return ResponseEntity.ok(membershipService.getMembership(userId, membershipType));
    }

    /* 멤버십 삭제 */
    @DeleteMapping("/api/v1/membership/{id}")
    public ResponseEntity<Void> deleteMembership(
        @RequestHeader(USER_ID_HEADER) String userId,
        @PathVariable Long id
    ){
        membershipService.removeMembership(id,userId);
        return ResponseEntity.noContent().build();
    }
}
