package chat.controller;

import chat.exception.NotValidException;
import chat.payload.response.ErrorResponse;
import chat.utils.InvalidErrorMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static chat.payload.response.Error.INVALID;

public class ExceptionHandlerController {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> methodArgumentNotValidExceptionHandler (BindingResult bindingResult) {
        return ResponseEntity.badRequest ().body (
                ErrorResponse.Builder.create ()
                        .withError (INVALID)
                        .withMessages (InvalidErrorMapper.map (bindingResult))
                        .build ()
        );
    }

    @ExceptionHandler(NotValidException.class)
    public ResponseEntity<?> notValidExceptionHandler (NotValidException e) {
        return ResponseEntity.badRequest ().body (
                ErrorResponse.Builder.create ()
                        .withError (INVALID)
                        .withMessage (e.getMessage ())
                        .build ()
        );
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<?> UsernameNotFoundExceptionHandler (UsernameNotFoundException e) {
        return ResponseEntity.badRequest ().body (
                ErrorResponse.Builder.create ()
                        .withError (INVALID)
                        .withMessage (e.getMessage ())
                        .build ()
        );
    }


}
