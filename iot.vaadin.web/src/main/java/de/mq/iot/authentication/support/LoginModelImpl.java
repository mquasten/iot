package de.mq.iot.authentication.support;

import de.mq.iot.authentication.Authentication;

class LoginModelImpl  implements LoginModel{
	
	private String login;

	private String password;
	
	@Override
	public String login() {
		return login;
	}

	
	@Override
	public void assignLogin(String login) {
		this.login = login;
	}

	
	@Override
	public String password() {
		return password;
	}

	
	@Override
	public void assignPassword(String password) {
		this.password = password;
	}


	@Override
	public boolean  authenticate(final Authentication authentication) {
		 return authentication.authenticate(password);
	}

}
