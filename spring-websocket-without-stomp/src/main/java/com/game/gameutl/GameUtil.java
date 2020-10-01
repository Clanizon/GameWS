package com.game.gameutl;

import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.PostConstruct;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import com.game.dto.Admin;
import com.game.dto.Game;
import com.game.dto.Player;
import com.google.gson.Gson;

@Component
@EnableAsync
public class GameUtil {

	

	
	private void processStartGame(Queue<Game> gameQueue, String gameType,int gameAmount) {
		Game startgame = gameQueue.peek();
		startgame.setGameType(gameType);
		startgame.setGameAmount(gameAmount);
		startgame.setState("ADMIN");
	    startgame.setAdmin(addAdmin());
	    sendGameUpdate(startgame);
		startgame.setState("STARTGAME");
		delayGame(startgame, 20);
	}
	
	private Admin addAdmin(){
		Admin admin= new Admin();
		admin.setName("Admin");
		admin.setActive("Y");
		return admin;
    }
	
	private void sendGameUpdate(Game startgame) {		
		    String messagetoSend = new Gson().toJson(startgame);
		    processNextGameUpdate(startgame);
	}

	public void processNextGameUpdate(Game game) {
		if (game.getState().equals("STARTGAME")) {
			game.setState("PUSHCARD1");
			delayGame(game,10);
		} else if (game.getState().equals("PUSHCARD1")) {
			game.setState("PUSHCARD2");
			delayGame(game,10);
		} else if (game.getState().equals("PUSHCARD2")) {
			game.setState("PUSHCARD3");
			delayGame(game,20);
		}else if (game.getState().equals("PUSHCARD3")) {
			game.setState("DECLARE");
			delayGame(game,5);
		}
	}

	@Async
	void delayGame(Game startgame,int delay) {
		newSingleThreadScheduledExecutor().schedule(() -> {
			processGameUpdateAfterDelay(startgame);
		}, delay, TimeUnit.SECONDS);

	}

	private void processGameUpdateAfterDelay(Game game) {
		if(game.getState().equals("STARTGAME") ){
			Queue<Game> gamequeue = null;//gameQMap.get(game.getGameType());
			if(gamequeue!=null && !gamequeue.isEmpty() && 
					gamequeue.peek().getGameId().equals(game.getGameId())){//shows game Not Started Yet 
				          if(gamequeue.peek().getCount()>=1){				        	  
				        	  processGameStart(game);
				          }else{
				        	 // processGameAbort(game);
				          }				         
				}
		}
		else{
			sendGameUpdate(game);
		}
		
	}
	
	void processGameStart(Game game){
		sendGameUpdate(game);
		removeGame(game);
	}
	
    void processGameAbort(Game game){
    	game.setState("ABORT");    	
    	sendGameUpdate(game);
    	removeGame(game);
	}

	void removeGame(Game startgame) {
		if(startgame.getState().equals("STARTGAME")){
		//System.out.println(gameQMap.get(startgame.getGameType()).remove(startgame));
		}
	}

	
	@SuppressWarnings("deprecation")
	public String getpaymentTransId(String email) {
		Date date = new Date();
		return date.getDay()+'_'+date.getMonth()+"_"+date.getYear()+"_"+date.getHours()+"_"+date.getMinutes()+"_"+date.getSeconds()+email;
	}

}
