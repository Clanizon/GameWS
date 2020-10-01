package com.game.dto;

public class Card {

	    public String suit;
	    public int rank;

	    public Card(String cardSuit, int cardRank){
	        this.suit = cardSuit;
	        this.rank = cardRank;
	    }

		public Card() {
			// TODO Auto-generated constructor stub
		}

		public String getSuit() {
			return suit;
		}

		public void setSuit(String suit) {
			this.suit = suit;
		}

		public int getRank() {
			return rank;
		}

		public void setRank(int rank) {
			this.rank = rank;
		}
	}


