
package com.game.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "gameId",
    "state",
    "players",
    "count",
    "gameAmount"
})
public class Game {

   
	@JsonProperty("gameId")
    private String gameId;
    @JsonProperty("state")
    private String state;
    @JsonProperty("gameType")
    private String gameType;
    public String getGameType() {
		return gameType;
	}

	public void setGameType(String gameType) {
		this.gameType = gameType;
	}

	@JsonProperty("count")
    private int count;
    @JsonProperty("isDeclared")
    private String isDeclared;
    public double getGameAmount() {
		return gameAmount;
	}

	public void setGameAmount(double gameAmount) {
		this.gameAmount = gameAmount;
	}

	@JsonProperty("gameAmount")
    private double gameAmount;
    
    @JsonProperty("players")
    private List<Player> players = null;
	private Admin admin;
    

    @JsonProperty("gameId")
    public String getGameId() {
        return gameId;
    }

    @JsonProperty("gameId")
    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    @JsonProperty("state")
    public String getState() {
        return state;
    }

    @JsonProperty("state")
    public void setState(String state) {
        this.state = state;
    }

    @JsonProperty("players")
    public List<Player> getPlayers() {
        return players;
    }

    @JsonProperty("players")
    public void setPlayers(List<Player> players) {
        this.players = players;
    }
    
   
    public void setAdmin(Admin admin) {
        this.admin = admin;
    }

   

    public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public Admin getAdmin() {
		return admin;
	}

	public String getIsDeclared() {
		return isDeclared;
	}

	public void setIsDeclared(String isDeclared) {
		this.isDeclared = isDeclared;
	}

	

}
