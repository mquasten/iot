package de.mq.iot.authentication;

import java.util.Collection;
import java.util.Optional;



public interface AuthentificationService {

	Optional<Authentication> authentification(String username);
	
	Collection<Authentication> authentifications();

	void changePassword(String username, String newPassword);

	boolean changeAuthorities(String username, Collection<Authority> authorities);

	boolean create(String username, String password);

	void delete(String username);

}