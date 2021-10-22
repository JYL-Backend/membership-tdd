package com.example.membership.cotroller;

import com.example.membership.dto.MembershipDetailResponseDTO;
import com.example.membership.dto.MembershipRequestDTO;
import com.example.membership.dto.MembershipAddResponseDTO;
import com.example.membership.entity.MembershipType;
import com.example.membership.exception.MembershipException;
import com.example.membership.service.MembershipService;
import com.example.membership.error.MembershipErrorResult;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static com.example.membership.cotroller.MembershipConstants.USER_ID_HEADER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class MembershipControllerTests {

    @InjectMocks
    private MembershipController target;

    private MockMvc mockMvc;

    @Mock
    private MembershipService membershipService;

    private Gson gson;

    @BeforeEach
    public void init(){
        gson = new Gson();
        mockMvc = MockMvcBuilders.standaloneSetup(target).build();
    }

    @Test
    public void mockMvc가Null이아님() throws Exception{
        //given

        //when

        //then
        assertThat(target).isNotNull();
        assertThat(mockMvc).isNotNull();
    }

    @Test
    public void 멤버십등록실패_사용자식별값이헤더에없음() throws Exception {
        //given
        String url = "/api/v1/membership";

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .content(gson.toJson(membershipRequest(10000, MembershipType.NAVER)))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    public void 멤버십등록실패_포인트가null임()  throws Exception {
        //given
        String url = "/api/v1/membership";

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .header(USER_ID_HEADER,"12345")
                        .content(gson.toJson(membershipRequest(null, MembershipType.NAVER)))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        resultActions.andExpect(status().isBadRequest());
    }


    @Test
    public void 멤버십등록실패_포인트가음수()  throws Exception {
        //given
        String url = "/api/v1/membership";

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .header(USER_ID_HEADER,"12345")
                        .content(gson.toJson(membershipRequest(-1, MembershipType.NAVER)))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        resultActions.andExpect(status().isBadRequest());
    }


    @Test
    public void 멤버십등록실패_멤버십종류가null()  throws Exception {
        //given
        String url = "/api/v1/membership";

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .header(USER_ID_HEADER,"12345")
                        .content(gson.toJson(membershipRequest(10000, null)))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    public void 멤버십등록실패_MemberService에서에러Throw() throws Exception{
        //given
        String url = "/api/v1/membership";
        doThrow(new MembershipException(MembershipErrorResult.DUPLICATED_MEMBERSHIP_REGISTER))
                .when(membershipService)
                .addMembership("12345", MembershipType.NAVER, 10000);
        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .header(USER_ID_HEADER,"12345")
                        .content(gson.toJson(membershipRequest(10000, MembershipType.NAVER)))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    public void 멤버십등록성공() throws Exception{
        //given
        String url = "/api/v1/membership";
        MembershipAddResponseDTO membershipResponse = MembershipAddResponseDTO.builder()
                .id(-1L)
                .membershipType(MembershipType.NAVER)
                .build();

        doReturn(membershipResponse).when(membershipService).addMembership("12345", MembershipType.NAVER, 10000);
        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .header(USER_ID_HEADER,"12345")
                        .content(gson.toJson(membershipRequest(10000, MembershipType.NAVER)))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        //then
        resultActions.andExpect(status().isCreated());

        MembershipAddResponseDTO response = gson.fromJson(resultActions.andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8), MembershipAddResponseDTO.class);

        assertThat(response.getMembershipType()).isEqualTo(MembershipType.NAVER);
        assertThat(response.getId()).isNotNull();
    }

    @Test
    public void 멤버십목록조회실패_사용자식별값이헤더에없음() throws Exception{
        //given
        final String url = "/api/v1/membership/list";

        //when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
        );

        //then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    public void 멤버십목록조회성공() throws Exception{
        //given
        final String url= "/api/v1/membership/list";
        doReturn(Arrays.asList(
                MembershipDetailResponseDTO.builder().build(),
                MembershipDetailResponseDTO.builder().build(),
                MembershipDetailResponseDTO.builder().build()
        )).when(membershipService).getMembershipList("12345");

        //when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                .header(USER_ID_HEADER,"12345")
        );

        //then
        resultActions.andExpect(status().isOk());
    }

    @Test
    public void 멤버십상세조회실패_사용자식별값이헤더에없음() throws Exception{
        //given
        final String url = "/api/v1/membership";

        //when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
        );

        //then
        resultActions.andExpect(status().isBadRequest());
    }
    @Test
    public void 멤버십상세조회실패_멤버십타입이파라미터에없음() throws Exception{
        //given
        final String url = "/api/v1/membership";

        //when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                .header(USER_ID_HEADER, "12345")
                .param("membershipType","empty")
        );

        //then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    public void 멤버십상세조회실패_멥버십이존재하지않음() throws Exception{
        //given
        final String url = "/api/v1/membership";
        doThrow(new MembershipException(MembershipErrorResult.MEMBERSHIP_NOT_FOUND))
                .when(membershipService)
                .getMembership("12345", MembershipType.NAVER);

        //when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                .header(USER_ID_HEADER, "12345")
                .param("membershipType", MembershipType.NAVER.name())
        );

        //then
        resultActions.andExpect(status().isNotFound());
    }

    @Test
    public void 멤버십상세조회실패_멤버십상세조회성공() throws Exception{
        //given
        final String url = "/api/v1/membership";

        //when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(url)
                        .header(USER_ID_HEADER, "12345")
                        .param("membershipType", MembershipType.NAVER.name())
        );

        //then
        resultActions.andExpect(status().isOk());
    }

    @Test
    public void 멤버십삭제실패_사용자식별값이헤더에없음() throws Exception{
        //given
        final String url = "/api/v1/membership/-1";

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.delete(url)
        );

        //then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    public void 멤버삭제성공() throws Exception{
        //given
        final String url = "/api/v1/membership/-1";

        //when
        ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.delete(url)
                .header(USER_ID_HEADER,"12345")
        );

        //then
        resultActions.andExpect(status().isNoContent());
    }


    @Test
    public void 멤버십적립실패_() throws Exception{
        //given

        //when

        //then
    }


    private MembershipRequestDTO membershipRequest(Integer point, MembershipType membershipType) {
        return MembershipRequestDTO.builder()
                .point(point)
                .membershipType(membershipType)
                .build();
    }
}
