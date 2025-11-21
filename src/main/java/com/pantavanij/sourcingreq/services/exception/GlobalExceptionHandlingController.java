package com.pantavanij.sourcingreq.services.exception;

import com.pantavanij.sourcingreq.services.domain.response.ApiErrorResponse;
import com.pantavanij.sourcingreq.services.domain.response.ErrorResponse;
import com.pantavanij.sourcingreq.services.domain.response.HttpMessageResponse;
import com.pantavanij.sourcingreq.services.enums.ApiMessage;
import lombok.val;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"unchecked", "rawtypes"})
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class GlobalExceptionHandlingController extends ResponseEntityExceptionHandler {
    private static final Logger LOGGER = LogManager.getLogger(GlobalExceptionHandlingController.class);

    @ExceptionHandler(ConstraintViolationException.class)
    ResponseEntity handleConstraintViolationException(ConstraintViolationException ex) {
        LOGGER.error(ex, ex);
        String errorMessage = ex.getConstraintViolations().iterator().next().getMessage();
        String[] errorMessagePair = errorMessage.split(":");
        if (errorMessagePair.length == 2) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ApiErrorResponse(errorMessagePair[0], errorMessagePair[1]));
        }
        return ResponseEntity.badRequest().body(new ApiErrorResponse(ApiMessage.E1002, ApiMessage.E1002.description()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public final ResponseEntity<Object> handleAccessDeniedException(Exception ex) {
        LOGGER.error(ex, ex);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiErrorResponse(ApiMessage.E7046, ApiMessage.E7046.description()));
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
        List<String> details = new ArrayList<>();
        details.add(ex.getLocalizedMessage());
        ErrorResponse error = new ErrorResponse("Exception Error", details);
        LOGGER.error(ex, ex);
        return new ResponseEntity(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RuntimeException.class)
    public final ResponseEntity<Object> handleAllRuntimeException(RuntimeException ex, WebRequest request) {
        ApiErrorResponse error = new ApiErrorResponse("Runtime Exception Error", ex.getLocalizedMessage());
        LOGGER.error(ex, ex);
        return new ResponseEntity(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(SQLException.class)
    public final ResponseEntity<Object> handleSQLException(RuntimeException ex, WebRequest request) {
        List<String> details = new ArrayList<>();
        details.add(ex.getLocalizedMessage());
        ErrorResponse error = new ErrorResponse("SQLException Error", details);
        LOGGER.error(ex, ex);
        return new ResponseEntity(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(AppException.class)
    public final ResponseEntity<Object> handleAppException(RuntimeException ex, WebRequest request) {
        String errorCode = ((AppException) ex).getApiMessage().name();
        String errorDescription = ((AppException) ex).getDescription();
        ApiErrorResponse error = new ApiErrorResponse(errorCode, errorDescription);
        LOGGER.error(ex, ex);
        return new ResponseEntity(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RecordNotFoundException.class)
    public final ResponseEntity<Object> handleRecordNotFoundException(RecordNotFoundException ex, WebRequest request) {
        List<String> details = new ArrayList<>();
        details.add(ex.getLocalizedMessage());
        LOGGER.warn(ex, ex);
        ErrorResponse error = new ErrorResponse("Record Not Found", details);
        return new ResponseEntity(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BusinessException.class)
    public final ResponseEntity<Object> handleBusinessException(BusinessException ex) {
        LOGGER.error(ex, ex);
        return ResponseEntity.internalServerError().body(new ApiErrorResponse(ex.getApiMessage()));
    }

    @ExceptionHandler(BadRequestException.class)
    public final ResponseEntity<Object> handleBadRequestException(BadRequestException ex) {
        LOGGER.error(ex, ex);
        return ResponseEntity.badRequest().body(new ApiErrorResponse(ex.getMessage()));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<String> details = new ArrayList<>();
        for (ObjectError error : ex.getBindingResult().getAllErrors()) {
            details.add(error.getDefaultMessage());
        }
        ErrorResponse error = new ErrorResponse("Validation Failed", details);
        LOGGER.error(ex , ex);
        return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String error = ex.getParameterName() + " parameter is missing";
        LOGGER.error(ex , ex);
        return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ MethodArgumentTypeMismatchException.class })
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex, WebRequest request) {
        String error = ex.getName() + " parameter is missing";
        LOGGER.error(ex , ex);
        return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<String> details = new ArrayList<>();
        details.add(ex.getCause().toString());
        ErrorResponse error = new ErrorResponse("Malformed JSON request", details);
        LOGGER.error(ex.getCause() , ex);
        return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FileStorageException.class)
    public ResponseEntity<Object> handleStorageFileNotFound(FileStorageException ex) {
        String error = ex.getMessage();
        LOGGER.error(ex.getCause() , ex);
        return new ResponseEntity(error, HttpStatus.NOT_FOUND);
    }

//    @ExceptionHandler(BadRequestException.class)
//    public ResponseEntity<HttpMessageResponse> handleAppBadRequestException(BadRequestException exception) {
//        val error = new HttpMessageResponse(
//                new HttpMessageResponse.Status(
//                        "E1003",
//                        "Invalid data."
//                ),
//                exception.getMessage(),
//                null
//        );
//        return new ResponseEntity<>(error, HttpStatus.OK);
//    }

    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<ApiErrorResponse> handleExternalServiceException(ExternalServiceException ex) {
        ApiErrorResponse apiErrorResponse = new ApiErrorResponse(ApiMessage.E1014, ApiMessage.E1014.description());
        LOGGER.error( apiErrorResponse + " : " + ex.toString(), ex);
        return ResponseEntity.internalServerError().body(apiErrorResponse);
    }

    @Override
    protected ResponseEntity<Object> handleAsyncRequestTimeoutException(AsyncRequestTimeoutException ex, HttpHeaders headers, HttpStatus status,
        WebRequest webRequest) {
        LOGGER.error(ex.getCause() , ex);
        return super.handleAsyncRequestTimeoutException(ex, headers, status, webRequest);
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        LOGGER.error(ex.getCause() , ex);
        return super.handleTypeMismatch(ex, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleMissingPathVariable(MissingPathVariableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        LOGGER.error(ex.getCause() , ex);
        return super.handleMissingPathVariable(ex, headers, status, request);
    }

}
