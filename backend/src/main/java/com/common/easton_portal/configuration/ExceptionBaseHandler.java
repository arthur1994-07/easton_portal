package com.common.easton_portal.configuration;

import com.common.core.web.struct.ErrorRespond;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

public class ExceptionBaseHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(Exception.class)
    public final ResponseEntity<?> handleAllExceptions(Exception ex) {
        return ResponseEntity.badRequest().body(new ErrorRespond(ex.getMessage()));
    }

    @ExceptionHandler(Error.class)
    public final ResponseEntity<?> handleAllError(Error ex) {
        return ResponseEntity.badRequest().body(new ErrorRespond(ex.getMessage()));
    }
}