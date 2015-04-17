package client;

import com.google.common.base.Objects;

/**
 * A simple object to represent a player and its URL for viewing.
 * 
 * @author jessfog
 * 
 */
public class Score {

	private long id;

	private long player_id;
	private String first;
	private String last;
	private float score;

	public Score() {
	}

	public Score(long player, String first, String last, float s) {
		super();
		this.player_id = player;
		this.first = first;
		this.last = last;
		this.score = s;
	}

	
	public long getId() {
		return id;
	}

	public long getPlayer_id() {
		return player_id;
	}

	public void setPlayer_id(long player_id) {
		this.player_id = player_id;
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

	public float getScore() {
		return score;
	}

	public void setScore(float score) {
		this.score = score;
	}

	/**
	 * Two Videos will generate the same hashcode if they have exactly the same
	 * values for their first, email, and password.
	 * 
	 */
	@Override
	public int hashCode() {
		// Google Guava provides great utilities for hashing
		return Objects.hashCode(first, last, score);
	}

	/**
	 * Two Videos are considered equal if they have exactly the same values for
	 * their first, email, and password.
	 * 
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Score) {
			Score other = (Score) obj;
			// Google Guava provides great utilities for equals too!
			return Objects.equal(first, other.first)
					&& Objects.equal(last, other.last)
					&& score == other.score;
		} else {
			return false;
		}
	}

}
