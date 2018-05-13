package de.mq.iot.authentication;

import java.util.Optional;

import de.mq.iot.authentication.Authentication;

public interface SecurityContext {

	Optional<Authentication> authentication();

	void assign(Authentication authentication);

}