package com.pantavanij.sourcingreq.services.validator;

import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import javax.validation.ConstraintValidatorContext;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SearchTermValidatorTest {

    ConstraintValidatorContext constraintValidatorContext = mock(ConstraintValidatorContext.class);
    SearchTermValidator searchTermValidator = new SearchTermValidator();

    @Test
    public void searchTermValidator_valid() {
        List<String> expectedValidList = Arrays.asList("", "ABC", "12345");

        for (String expectedValid: expectedValidList){
            assertTrue(searchTermValidator.isValid(expectedValid, constraintValidatorContext));
        }
    }

    @Test
    public void searchTermValidator_invalid() {
        List<String> expectedInvalidList = Arrays.asList("A", "AB");

        when(constraintValidatorContext.buildConstraintViolationWithTemplate(
                ApiMessage.E7034.name() + ":" + ApiMessage.E7034.description()))
                .thenReturn(mock(ConstraintValidatorContext.ConstraintViolationBuilder.class));

        for (String expectedInvalid: expectedInvalidList){
            assertFalse(searchTermValidator.isValid(expectedInvalid, constraintValidatorContext));
        }
    }

    @Test
    public void decimalConversion_invalid() {
        BigDecimal bigDecimal = new BigDecimal("123");
        assertEquals(new BigDecimal(123), bigDecimal);
    }
}