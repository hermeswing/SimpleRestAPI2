package octopus.base.exception;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class ExUserNotFoundException extends UsernameNotFoundException {

    private static final long serialVersionUID = 1L;

    public ExUserNotFoundException( String msg, Throwable t ) {
        super( msg, t );
    }

    public ExUserNotFoundException( String msg ) {
        super( msg );
    }

}