package client;

import com.google.common.base.Objects;

/**
 * A simple object to represent a player and its URL for viewing.
 * 
 * @author jessfog
 * 
 */
public class Challenge {

	private long id;	
	private long challengerId;
	private long challengedUserId;
	private boolean completed;
	private float score;
	private String challengerFirst;
	private String challengerLast;

	public Challenge() {
		
	}
	
	public Challenge(long challenger, long challenged) {
		this.challengerId = challenger;
		this.challengedUserId = challenged;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getChallengerId() {
		return challengerId;
	}

	public void setChallengerId(long challengerId) {
		this.challengerId = challengerId;
	}

	public long getChallengedUserId() {
		return challengedUserId;
	}

	public void setChallengedUserId(long challengedUserId) {
		this.challengedUserId = challengedUserId;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}
	
	public float getScore() {
		return score;
	}

	public void setScore(float score) {
		this.score = score;
	}

	public String getChallengerFirst() {
		return challengerFirst;
	}

	public void setChallengerFirst(String challengerFirst) {
		this.challengerFirst = challengerFirst;
	}

	public String getChallengerLast() {
		return challengerLast;
	}

	public void setChallengerLast(String challengerLast) {
		this.challengerLast = challengerLast;
	}

	/**
	 * Two Players are considered equal if they have exactly the same values for
	 * their first, email, and password.
	 * 
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Challenge) {
			Challenge other = (Challenge) obj;
			// Google Guava provides great utilities for equals too!
			return Objects.equal(challengerId, other.challengerId)
					&& Objects.equal(challengedUserId, other.challengedUserId)
					&& completed == other.completed;
		} else {
			return false;
		}
	}
	/**
	 * Two Videos will generate the same hashcode if they have exactly the same
	 * values for their first, email, and password.
	 * 
	 */
	@Override
	public int hashCode() {
		// Google Guava provides great utilities for hashing
		return Objects.hashCode(challengerId, challengedUserId, completed);
	}


}
