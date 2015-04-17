package client;

import com.google.common.base.Objects;

/**
 * A simple object to represent a player and its URL for viewing.
 * 
 * @author jessfog
 * 
 */
public class Player {

	private long id;

	private String first;
	private String last;

	private String email;
	private String password;

	public Player() {
	}

	public Player(String first, String last, String email, String password) {
		super();
		this.first = first;
		this.last = last;
		this.email = email;
		this.password = password;
	}

	public String getFirst() {
		return first;
	}

	public void setFirst(String first) {
		this.first = first;
	}

	public String getLast() {
		return last;
	}

	public void setLast(String last) {
		this.last = last;
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	/**
	 * Two Videos will generate the same hashcode if they have exactly the same
	 * values for their first, email, and password.
	 * 
	 */
	@Override
	public int hashCode() {
		// Google Guava provides great utilities for hashing
		return Objects.hashCode(first, email, password);
	}

	/**
	 * Two Videos are considered equal if they have exactly the same values for
	 * their first, email, and password.
	 * 
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Player) {
			Player other = (Player) obj;
			// Google Guava provides great utilities for equals too!
			return Objects.equal(first, other.first)
					&& Objects.equal(email, other.email)
					&& password == other.password;
		} else {
			return false;
		}
	}

}
