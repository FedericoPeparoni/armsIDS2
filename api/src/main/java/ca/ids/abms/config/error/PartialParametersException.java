package ca.ids.abms.config.error;

/**
 * Created by c.talpa on 15/03/2017.
 */
public class PartialParametersException extends  CustomParametrizedException{

    private static final long serialVersionUID = 1L;

	public PartialParametersException(String message, String... params) {
        super(message, params);
    }

    public PartialParametersException(String message, Throwable cause, String... params) {
        super(message, cause, params);
    }
}
