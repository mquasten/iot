package de.mq.iot.authentication.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.springframework.data.annotation.Id;
import org.springframework.util.Assert;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import de.mq.iot.authentication.Authentication;
import de.mq.iot.authentication.Authority;

class UserAuthenticationImpl implements Authentication {

	@Id
	private final String username;
	private String credentials;
	private final Collection<Authority> authorities = new ArrayList<>();

	@SuppressWarnings("unused")
	private UserAuthenticationImpl() {
		this.username = "";
		this.credentials = "";

	}

	UserAuthenticationImpl(final String username, final String credentials, final Collection<Authority> authorities) {
		Assert.hasText(credentials, "Credentials is mandatory");
		Assert.notNull(username, "Username is mandatory");
		this.username = username;
		this.authorities.addAll(authorities);
		this.credentials = DigestUtils.md5DigestAsHex(credentials.getBytes());

	}

	/*
	 * (non-Javadoc)
	 * @see de.mq.iot.authentication.Authentication#authorities()
	 */
	@Override
	public Collection<Authority> authorities() {
		return Collections.unmodifiableCollection(authorities);
	}

	/*
	 * (non-Javadoc)
	 * @see de.mq.iot.authentication.Authentication#username()
	 */
	@Override
	public String username() {
		return username;
	}

	/*
	 * (non-Javadoc)
	 * @see de.mq.iot.authentication.Authentication#authenticate(java.lang.String)
	 */
	@Override
	public boolean authenticate(final String credentials) {
		Assert.hasText(credentials, "Credentials is mandatory");
		if (!StringUtils.hasText(this.credentials)) {
			return false;
		}

		return this.credentials.equalsIgnoreCase(DigestUtils.md5DigestAsHex(credentials.getBytes()));
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return username.hashCode();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object user) {
		if (!(user instanceof Authentication)) {
			return false;

		}

		final Authentication authentication = (Authentication) user;
		return authentication.username().equals(username);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.mq.iot.authentication.Authentication#hasRole(de.mq.iot.authentication.
	 * Authority)
	 */
	@Override
	public boolean hasRole(final Authority authority) {
		Assert.notNull(authority, "Authority is required.");
		return this.authorities.contains(authority);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return username;
	}

}
