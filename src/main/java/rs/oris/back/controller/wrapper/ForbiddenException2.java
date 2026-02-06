package rs.oris.back.controller.wrapper;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ResponseStatus;
/**
 * Custom klasa za auth exception, koristi se u auth filteru
 */
@ResponseStatus(value= HttpStatus.FORBIDDEN,reason = "You fucked up.")
public class ForbiddenException2 extends AuthenticationException {

    public ForbiddenException2(String text) {
        super(text);
    }
}
