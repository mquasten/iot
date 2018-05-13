package de.mq.iot.authentication;

import java.util.Optional;

import de.mq.iot.authentication.Authentication;

public interface AuthentificationService {

	Optional<Authentication> authentification(String username);

}