package com.tekarch.UserBankService.Exception;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;

    @ControllerAdvice
    public class GlobalExceptionHandler {

        private static final Logger logger = LogManager.getLogger(GlobalExceptionHandler.class);

        @ExceptionHandler(Exception.class)
        public ResponseEntity<String> handleException(Exception e) {
            // Log the exception details
            logger.error("Exception occurred: {}", e.getMessage(), e);
            return new ResponseEntity<>("Error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


