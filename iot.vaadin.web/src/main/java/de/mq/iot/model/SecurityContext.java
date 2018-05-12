package de.mq.iot.model;

import java.util.Optional;

public interface SecurityContext {

	Optional<Authentication> authentication();

	void assign(Authentication authentication);

}