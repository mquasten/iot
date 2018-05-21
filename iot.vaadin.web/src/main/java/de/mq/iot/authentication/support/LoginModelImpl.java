package de.mq.iot.authentication.support;

import de.mq.iot.authentication.Authentication;

public class LoginModelImpl  implements LoginModel{
	
	private String login;
	
	
	@Override
	public String getLogin() {
		return login;
	}

	
	@Override
	public void setLogin(String login) {
		this.login = login;
	}

	
	@Override
	public String getPassword() {
		return password;
	}

	
	@Override
	public void setPassword(String password) {
		this.password = password;
	}

	private String password;


	@Override
	public boolean  authenticate(final Authentication authentication) {
		 return authentication.authenticate(password);
	}

}
