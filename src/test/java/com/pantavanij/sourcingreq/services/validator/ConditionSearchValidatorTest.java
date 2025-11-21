package com.pantavanij.sourcingreq.services.validator;

import com.pantavanij.sourcingreq.services.domain.request.ConditionSearchRequest;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ConditionSearchValidatorTest {

    ConstraintValidatorContext constraintValidatorContext = mock(ConstraintValidatorContext.class);
    ConditionSearchValidator conditionSearchValidator = new ConditionSearchValidator();

    @Test
    public void conditionSearchValidator_valid() {
        List<ConditionSearchRequest> expectedValid1 = Arrays.asList(
                ConditionSearchRequest.builder().searchField("requestNo").searchValue("202209000010").build()
        );

        List<ConditionSearchRequest> expectedValid2 = Arrays.asList(
                ConditionSearchRequest.builder().searchField("requestNo").searchValue("202209000010").build(),
                ConditionSearchRequest.builder().searchField("requestName").searchValue("Req").build(),
                ConditionSearchRequest.builder().searchField("projectName").searchValue("Pro").build()
        );

        List<ConditionSearchRequest> expectedValid3 = Arrays.asList(
                ConditionSearchRequest.builder().searchField("requestNo").searchValue("").build(),
                ConditionSearchRequest.builder().searchField("requestName").searchValue(null).build()
        );

        List<List<ConditionSearchRequest>> expectedValidList = Arrays.asList(expectedValid1, expectedValid2, expectedValid3);

        for (List<ConditionSearchRequest> expectedValid: expectedValidList){
            assertTrue(conditionSearchValidator.isValid(expectedValid, constraintValidatorContext));
        }
    }

    @Test
    public void conditionSearchValidator_invalid() {
        List<ConditionSearchRequest> expectedValid1 = Arrays.asList(
                ConditionSearchRequest.builder().searchField("requestNo").searchValue("202209000010").build(),
                ConditionSearchRequest.builder().searchField("requestName").searchValue("Req").build(),
                ConditionSearchRequest.builder().searchField("projectName").searchValue("Pr").build()
        );

        List<ConditionSearchRequest> expectedValid2 = Arrays.asList(
                ConditionSearchRequest.builder().searchField("requestNo").searchValue("202209000010").build(),
                ConditionSearchRequest.builder().searchField("requestName").searchValue("R").build()
        );

        List<ConditionSearchRequest> expectedValid3 = Arrays.asList(
                ConditionSearchRequest.builder().searchField("requestNo").searchValue("1").build(),
                ConditionSearchRequest.builder().searchField("requestName").searchValue("R").build()
        );

        List<List<ConditionSearchRequest>> expectedInvalidList = Arrays.asList(expectedValid1, expectedValid2, expectedValid3);

        when(constraintValidatorContext.buildConstraintViolationWithTemplate(
                ApiMessage.E7034.name() + ":" + ApiMessage.E7034.description()))
                .thenReturn(mock(ConstraintValidatorContext.ConstraintViolationBuilder.class));

        for (List<ConditionSearchRequest> expectedValid: expectedInvalidList){
            assertFalse(conditionSearchValidator.isValid(expectedValid, constraintValidatorContext));
        }
    }
}