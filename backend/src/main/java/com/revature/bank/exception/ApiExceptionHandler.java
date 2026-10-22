package com.revature.bank.exception;


import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.dao.DataIntegrityViolationException;
import org.hibernate.exception.ConstraintViolationException;

//@RestControllerAdvice provides shared error handling across controllers.
//ExceptionHandler(...) selects the exception this method handles.
//getFieldErrors() retrieves validation failures.
//Map<String, String> pairs field names with messages.
//ResponseEntity.badRequest() sets HTTP status 400.
//.body(errors) supplies the response data, which Spring converts to JSON.

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(
            MethodArgumentNotValidException exception) {

        Map<String, String> errors = new LinkedHashMap<>();

        exception.getBindingResult().getFieldErrors()
                .forEach(error -> errors.putIfAbsent(
                        error.getField(),
                        error.getDefaultMessage()
                ));

        return ResponseEntity.badRequest().body(errors);
    }
   //Since both exception classes are in the same package, no import for
   // DuplicateUsernameException is needed.
    @ExceptionHandler(DuplicateUsernameException.class)
    public ResponseEntity<Map<String, String>> handleDuplicateUsername(
            DuplicateUsernameException exception) {

        return ResponseEntity.status(409).body(
                Map.of("message", exception.getMessage())
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDatabaseConflict(
            DataIntegrityViolationException exception) {

        Throwable cause = exception;
        //If your query returns another constraint name, use that name in the comparison.
        while (cause != null) {
            if (cause instanceof ConstraintViolationException violation
                    && "bank_users_username_key".equals(
                    violation.getConstraintName())) {

                return ResponseEntity.status(409).body(
                        Map.of("message", "That username is already taken.")
                );
            }

            //This covers a duplicate account created by simultaneous requests. Keep the username
            // constraint check too.
            if (cause instanceof ConstraintViolationException violation
                    && "bank_accounts_user_type_key".equals(
                    violation.getConstraintName())) {

                return ResponseEntity.status(409).body(
                        Map.of("message", "You already have an account of this type.")
                );
            }
            cause = cause.getCause();

        }

        return ResponseEntity.status(500).body(
                Map.of("message", "Unable to save the data.")
        );
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleInvalidCredentials(
            InvalidCredentialsException exception) {

        return ResponseEntity.status(401).body(
                Map.of("message", exception.getMessage())
        );
    }

    @ExceptionHandler(DuplicateAccountException.class)
    public ResponseEntity<Map<String, String>> handleDuplicateAccount(
            DuplicateAccountException exception) {

        return ResponseEntity.status(409).body(
                Map.of("message", exception.getMessage())
        );
    }

    //This returns 400 for the service’s invalid-amount, unavailable-account,
    // and balance-limit errors.
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleInvalidArgument(
            IllegalArgumentException exception) {

        return ResponseEntity.badRequest().body(
                Map.of("message", exception.getMessage())
        );
    }
}

//Expected results:
//- First request: 201 Created, with user details and no password.
//- Same request again: 409 Conflict, with the duplicate-username message.
//- Blank username: 400 Bad Request, from validation.
// This handles the initial duplicate check. We still need to handle the
// database constraint conflict possible with simultaneous registrations;