package rs.oris.back.controller.wrapper;


/**
 * Klasa koja sluzi kao wrapper za povratnu vrednost
 * Menja spring-ov ResponseEntity
 *
 *
 * @param <T>
 */
public class Response<T> {

    private T data;

    public Response() {
    }

    public Response(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
