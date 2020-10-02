package com.game.gameutl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.util.CollectionUtils;

import com.game.dto.Card;
import com.game.dto.Game;
import com.game.dto.Player;


public class Dealer {

	
	    private static final int GAME_21 = 21;
	    private static final double GAME_COMMISION = 10.00;
	    
		private  final int SIZE = 52;
	    private  Card[] deckOfCards = new Card[SIZE];
	    int numOfCardsPerPlayer=3;

	      public Card[] getDeckOfCards() {
	        int count = 0;
	        String[] suits = {"d", "c", "h", "s"};
	        int[] ranks = {1,2,3,4,5,6,7,8,9,10,11,12,13};

	        for (String s : suits) {
	            for (int r : ranks) {
	                Card card = new Card(s, r);
	                deckOfCards[count] = card;
	                count++;
	            }
	        }

	        return deckOfCards;

	    }
	      public Card[] shuffleCards(Card[] deckOfCards) {
	        Random rand = new Random();
	        int j;
	        for (int i = 0; i < SIZE; i++) {
	            j = rand.nextInt(SIZE);
	            Card temp = deckOfCards[i];
	            deckOfCards[i] = deckOfCards[j];
	            deckOfCards[j] = temp;
	        }
	        return deckOfCards;
	    }   
	    public List<Player> dealCards(List<Player> players, Card[] deck) {
	    	if(players!=null && !players.isEmpty()){
				for (int i = 0; i < numOfCardsPerPlayer * players.size(); i++) {
					if(players.get(i % players.size()).getCard()!=null){
						players.get(i % players.size()).getCard().add(deck[i]);
					}
					else{
					List<Card> cardList= new ArrayList<Card>();
					cardList.add(deck[i]);
		            players.get(i % players.size()).setCard(cardList);
					}
		        }
	    	}
		        return players;
		    }
	    
	    public Game declareWinner(Game game) {
	        List<Player> winner= new ArrayList<Player>();
	        int diff=0;
	        boolean first=true;
	        List<Player> players = game.getPlayers();
	        
	        double betAmount=0.00;
	        if(game!=null && game.getAdmin()!=null  ){
	        	betAmount=game.getAdmin().getBetAmount();
	        }
					for (int i = 0; i < players.size(); i++) {
						players.get(i).setWin("N");
						if( //Remove Dropped Player In Win calculation
								!(players.get(i).getActive()!=null && players.get(i).getActive().equals("N"))){												
						int count= getCardValue(players.get(i).getCard());
						players.get(i).setCount(count);
						if(count>GAME_21){
							players.get(i).setWin("N");							
						}else if(first){							
							winner.add(players.get(i));	
							first = false;
							diff=GAME_21-count;//0 
						}else if(diff > 21-count){
							winner.clear();
							winner.add(players.get(i));
							diff=GAME_21-count;
						}else if (diff==GAME_21-count){
							winner.add(players.get(i));
						}
					  }
					}   
					if(winner.size()>0){
						for(Player win:winner){
							if(players.indexOf(win)!=-1){
								players.get(players.indexOf(win)).setWin("Y");
								players.get(players.indexOf(win)).setWinAmount(getWinAmount(betAmount,winner.size()));
								
							}											
						}
						game.setIsDeclared("Y");
					}else{
						game.setIsDeclared("N");
					}
		return game;
	    }	    
		private int getCardValue(List<Card> cardList) {	
			int cardSum=0;
			if(!CollectionUtils.isEmpty(cardList)){
		   for(int i=0;i<cardList.size();i++){
			   if(cardList.get(i).getRank()<=10){
				   cardSum=cardSum+cardList.get(i).getRank();				   
			   }else{
				   cardSum=cardSum+10;
			   }
		   }
		}
	    return cardSum;
		}		
		private  double getWinAmount(double betAmount,int winnerCount) {	
			double winAmount= 0.00;
			if(winnerCount!=0){
			winAmount= ((betAmount/winnerCount) * ((100-GAME_COMMISION)/100));
			}
			System.out.println(winAmount);
			return winAmount;
		}
			
	
}
