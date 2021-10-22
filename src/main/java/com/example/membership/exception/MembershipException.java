package com.example.membership.exception;

import com.example.membership.error.MembershipErrorResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MembershipException extends RuntimeException{
    private final MembershipErrorResult errorResult;

}
