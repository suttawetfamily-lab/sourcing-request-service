package com.pantavanij.sourcingreq.services.domain.response;

import lombok.Data;

import java.util.List;

@Data
public class DelegateeByDelegatorResponse {
    private List<String> delegateeUserNames;
}
