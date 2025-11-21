package com.pantavanij.sourcingreq.services.controller;

import com.pantavanij.sourcingreq.services.domain.dto.RequestDto;
import com.pantavanij.sourcingreq.services.domain.request.SendEMailRequest;
import com.pantavanij.sourcingreq.services.domain.response.EmailResponse;
import com.pantavanij.sourcingreq.services.service.sourcingreq.EmailService;
import com.pantavanij.sourcingreq.services.service.sourcingreq.RequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.endpoint.version}")
public class EMailController {

    private final EmailService emailService;
    private final RequestService requestService;
//
//    @PostMapping(value = "/e-mail/send", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity sendMailByActivity(@RequestBody @Valid SendEMailRequest sendEMailRequest) {
//        RequestDto requestDtoForEmail = requestService.findRequestSourcingForEditingByRecId(sendEMailRequest.getRequestId());
//        List<EmailResponse> emailResponse = emailService.sendEMailNotification(sendEMailRequest,requestDtoForEmail);
//        return ResponseEntity.ok().body(emailResponse);
//    }

}