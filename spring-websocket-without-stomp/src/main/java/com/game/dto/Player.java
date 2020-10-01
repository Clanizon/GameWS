
package com.game.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "name",
    "displayName",
    "win",
    "card",
    "betAmount",
    "active"
})
public class Player {

    @Override
	public String toString() {
		return "Player [name=" + name + ", card=" + card + "]";
	}

	@JsonProperty("name")
    private String name;
	
	@JsonProperty("displayName")
    private String displayName;
	
	public String getDisplayName() {
		return displayName;
	}



	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	@JsonProperty("win")
    private String win;
	
	
	
	@JsonProperty("count")
    private int count;
	
	@JsonProperty("betAmount")
    private double betAmount;
	@JsonProperty("winAmount")
    private double winAmount;
	@JsonProperty("active")
    private String active;
	
	
    


	public String getActive() {
		return active;
	}



	public void setActive(String active) {
		this.active = active;
	}



	public String getWin() {
		return win;
	}



	public void setWin(String win) {
		this.win = win;
	}



	public int getCount() {
		return count;
	}



	public void setCount(int count) {
		this.count = count;
	}

	@JsonProperty("card")
    private List<Card> card = null;
    

    @JsonProperty("name")
    public String getName() {
        return name;
    }
    
    

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("card")
    public List<Card> getCard() {
        return card;
    }

    @JsonProperty("card")
    public void setCard(List<Card> card) {
        this.card = card;
    }



	public double getBetAmount() {
		return betAmount;
	}



	public void setBetAmount(double betAmount) {
		this.betAmount = betAmount;
	}



	public double getWinAmount() {
		return winAmount;
	}



	public void setWinAmount(double winAmount) {
		this.winAmount = winAmount;
	}
    
    

    
}
