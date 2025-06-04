package br.com.aliriorios.done_and_dusted.web.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Getter @NoArgsConstructor @ToString
public class ErrorMessage {
    private String path;
    private String method;
    private int status;
    private String statusText;
    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String, String> errors;

    public ErrorMessage(HttpServletRequest request, HttpStatus status, String message) {
        populateBaseInfo(request, status, message);
    }

    public ErrorMessage(HttpServletRequest request, HttpStatus status, String message, BindingResult result) {
        populateBaseInfo(request, status, message);
        addErrors(result);
    }

    public ErrorMessage(HttpServletRequest request, HttpStatus status, String message, BindingResult result, MessageSource messageSource) {
        populateBaseInfo(request, status, message);
        addErrors(result, messageSource, request.getLocale());
    }

    private void populateBaseInfo(HttpServletRequest request, HttpStatus status, String message) {
        this.path = request.getRequestURI();
        this.method = request.getMethod();
        this.status = status.value();
        this.statusText = status.getReasonPhrase();
        this.message = message;
    }

    private void addErrors(BindingResult result) {
        this.errors = new HashMap<>();
        for (FieldError fieldError : result.getFieldErrors()) {
            this.errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
    }

    private void addErrors(BindingResult result, MessageSource messageSource, Locale locale) {
        this.errors = new HashMap<>();
        for (FieldError fieldError : result.getFieldErrors()) {
            String translatedMessage = messageSource.getMessage(fieldError, locale);
            this.errors.put(fieldError.getField(), translatedMessage);
        }
    }
}

