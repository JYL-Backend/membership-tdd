package com.example.membership.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class RatePointServiceTests {
    @InjectMocks
    private RatePointService ratePointService;

    @Test
    public void _10000원의적립금은100원() throws Exception{
        //given
        final int price = 10000;

        //when
        final int result = ratePointService.calculateAmount(price);

        //then
        assertThat(result).isEqualTo(100);
    }
    @Test
    public void _20000원의적립금은200원() throws Exception{
        //given
        final int price = 20000;

        //when
        final int result = ratePointService.calculateAmount(price);

        //then
        assertThat(result).isEqualTo(200);
    }
    @Test
    public void _50000원의적립금은500원() throws Exception{
        //given
        final int price = 50000;

        //when
        final int result = ratePointService.calculateAmount(price);

        //then
        assertThat(result).isEqualTo(500);
    }
}
