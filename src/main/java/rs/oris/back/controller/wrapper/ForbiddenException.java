package rs.oris.back.controller.wrapper;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception koji se izgleda koristi samo kad na vozilu nije definisam tip vozila
 *
 * S obzirom da je uvek koriscen isti string kao ulaz, mogao je da se napravi samo prazan konstruktor koji uvek
 * poziva metodu nadklase sa tim istim stringom
 */
@ResponseStatus(value= HttpStatus.FORBIDDEN,reason = "Authentication failure.")
public class ForbiddenException extends RuntimeException {

    public ForbiddenException(String text) {
        super(text);
    }
}
