package octopus.base.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class CommonResult {
    private boolean success;
    private int code;
    private String msg;
    private HttpStatus status;
}