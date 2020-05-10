package de.mq.iot.authentication;

import java.util.Optional;


public interface SecurityContext {

	Optional<Authentication> authentication();

	void assign(Authentication authentication);

}