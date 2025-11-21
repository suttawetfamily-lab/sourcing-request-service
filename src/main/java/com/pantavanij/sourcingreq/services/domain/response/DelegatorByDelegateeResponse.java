package com.pantavanij.sourcingreq.services.domain.response;

import lombok.Data;

import java.util.List;

@Data
public class DelegatorByDelegateeResponse {
    private List<String> delegatorUserNames;
}
