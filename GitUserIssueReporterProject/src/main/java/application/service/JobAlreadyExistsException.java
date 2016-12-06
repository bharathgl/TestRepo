package application.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Group already exists")
public class JobAlreadyExistsException extends RuntimeException {
}
