package com.game.service;

import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import com.game.dto.Card;
import com.game.dto.Game;
import com.game.dto.Player;
import com.game.gameutl.Dealer;
import com.game.service.wallet.WalletClient;

@Component
public class GameService {
	

	private PublishService publishService=new PublishService(); ;
	

	WalletClient walletClient =new WalletClient();
	List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

	Map<String, Set<WebSocketSession>> Wsmap = new HashMap<String, Set<WebSocketSession>>();
	Map<String, String> SessionMap = new HashMap<String, String>();

	Map<String, Game> gameMap = new HashMap<String, Game>();
	Dealer dealer = new Dealer();
	Map<String, Float> betMap = new HashMap<String, Float>();

	double STARTGAMEBET = 20;
	double PUSHCARD1BET = 50;
	double PUSHCARD2BET = 50;
	private static final double GAME_COMMISION = 10.00;
	
	
	@Async
	public void delayGame(Game startgame,int delay) {
		newSingleThreadScheduledExecutor().schedule(() -> {
			processGameUpdateAfterDelay(startgame);
		}, delay, TimeUnit.SECONDS);

	}
	
	
	public void processStartGame(Game startgame) {		
		processGameUpdateAfterDelay(startgame);		
	}
	
	
	public void handleJoin(Game game,WebSocketSession session){
		if( (gameMap.get(game.getGameId())== null ) || (gameMap.get(game.getGameId()) != null
				&& (gameMap.get(game.getGameId()).getState().equals("ADMIN") || gameMap.get(game.getGameId()).getState().equals("JOINROOM")))){
			assignRoom(session, game);
			addPlayer(gameMap, game);
			Game curGame = gameMap.get(game.getGameId());
			System.out.println("handleAdmin :"+curGame.getState());
			publishService.publishGameState(curGame,Wsmap.get(game.getGameId()));
		}	
	}
	
	public void handleStart(Game game,WebSocketSession session){
		System.out.println(game.getGameId());
		System.out.println(gameMap.get(game.getGameId()));
		if(gameMap.get(game.getGameId()) != null
				&& (gameMap.get(game.getGameId()).getState().equals("ADMIN") || gameMap.get(game.getGameId()).getState().equals("JOINROOM"))){
			processGameStart(game);
		}	
	}
	
	public void handleDrop(Game game,WebSocketSession session){
	Game curGame = gameMap.get(game.getGameId());
	String gameCurrrentState =curGame.getState();
	if (!game.getPlayers().isEmpty() && game.getPlayers().get(0) != null) {
		System.out.println("IN Drop Execute");
		removePlayer(curGame, game.getPlayers().get(0).getName(), "DROP");
		gameMap.put(game.getGameId(), curGame);
		System.out.println("handleDrop"+curGame.getState()+curGame.getGameId());
		
		curGame.setState("DROP");
		publishService.publishGameState(curGame,Wsmap.get(game.getGameId()));
		curGame.setState(gameCurrrentState);
	  }
	}
	
	public void handleAbort(Game game,WebSocketSession session){		
		Game gameFromMap = gameMap.get(game.getGameId());
		if(gameFromMap != null){
			System.out.println("In Abort Execute");
			gameFromMap.setState("ABORT");
			publishService.publishGameState(gameFromMap,Wsmap.get(game.getGameId()));
			if(gameFromMap.getPlayers()!=null && !gameFromMap.getPlayers().isEmpty())
				gameFromMap.getPlayers().forEach(player -> {
					returnGameAmount(game.getGameAmount(),player.getName());
				});
			
		}		
	}
	
	
	
	
	@Async
	private void returnGameAmount(double gameAmount, String name) {
		newSingleThreadScheduledExecutor().schedule(() -> {
			System.out.println("returnGameAmount");
			walletClient.updateWallet(name, gameAmount);
		}, 0, TimeUnit.SECONDS);

	}


	private void processGameUpdateAfterDelay(Game game) {		   
			Game gameFromMaP = gameMap.get(game.getGameId());
			System.out.println("processGameUpdateAfterDelay"+gameFromMaP.getState()+game.getGameId());
			if(gameFromMaP!=null && gameFromMaP.getState().equals("JOINROOM") &&  gameFromMaP.getPlayers()!=null && 
					!gameFromMaP.getPlayers().isEmpty()){//shows game Not Started Yet 
				          if(gameFromMaP.getPlayers().size()>=3){				        	  
				        	  processGameStart(gameFromMaP);
				          }else{
				        	   processGameAbort(game);
				          }				         
				}
		else if(gameFromMaP!=null && gameFromMaP.getState().contains("PUSHCARD")){
			processPushCard(game);
		}else if(gameFromMaP!=null && gameFromMaP.getState().equals("DECLARE")){
			handleDeclare(gameFromMaP);
		}
		
	}

  void processPushCard(Game game){
	  publishService.publishGameState(game,Wsmap.get(game.getGameId()));	  
	  processNextGameUpdate(game);
  }
	
	
	void processGameStart(Game game){
		System.out.println("processGameStart");
		Game curGame = gameMap.get(game.getGameId());
		removeAllCard(curGame);
		addCard(curGame);
		curGame.setState("STARTGAME");
		publishService.publishGameState(curGame,Wsmap.get(game.getGameId()));
		gameMap.put(game.getGameId(), curGame);
		processNextGameUpdate(curGame);
	}
	
	private void removeAllCard(Game curGame) {
		if(curGame!=null && curGame.getPlayers()!=null &&!curGame.getPlayers().isEmpty())	{
			curGame.getPlayers().forEach(player -> {
				if(player.getCard()!=null && !player.getCard().isEmpty())
				player.getCard().clear();
			});
		}
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
	
	void processGameAbort(Game game){
    	game.setState("ABORT");    	
    	publishService.publishGameState(game,Wsmap.get(game.getGameId()));
    	removeGame(game);
	}

	void removeGame(Game game) {
		Wsmap.remove(game.getGameId());
		gameMap.remove(game.getGameId());
	}
	
	
	
	public void handleAdmin(Game game, WebSocketSession session){
		joinAdmin(game);
		assignRoom(session, game);
		Game curGame = gameMap.get(game.getGameId());
		System.out.println("handleAdmin :"+curGame.getState());
		publishService.publishGameState(curGame,Wsmap.get(game.getGameId()));
	}
	public void joinAdmin(Game game){
		 delayGame(game,20);
		if (gameMap.get(game.getGameId()) != null) {
			gameMap.get(game.getGameId()).setAdmin(game.getAdmin());			
		}else{
			gameMap.put(game.getGameId(), game);
			//Game Id Coming for First TIme so Timer Start to Kick Start the Game
		
		}
	}
	
	private void assignRoom(WebSocketSession session, Game game) {
		if (Wsmap.get(game.getGameId()) != null) {
			Set<WebSocketSession> wsList = Wsmap.get(game.getGameId());
			wsList.add(session);
			Wsmap.put(game.getGameId(), wsList);
		} else {
			Set<WebSocketSession> wsList = new HashSet<WebSocketSession>();
			wsList.add(session);
			Wsmap.put(game.getGameId(), wsList);
		}
		if(game.getPlayers()!=null && !game.getPlayers().isEmpty())
		SessionMap.put(session.getId(), game.getGameId() + ':' + game.getPlayers().get(0).getName());
	}
	
	private void addPlayer(Map<String, Game> gameMap2, Game game) {
		try {
			if (gameMap2.get(game.getGameId()) != null) {
				Game gameE = gameMap2.get(game.getGameId());
				updateAdminBet(gameE);
				List<Player> playerList=gameE.getPlayers();
				if(playerList!=null && playerList.size() >0){
					playerList.add(game.getPlayers().get(0));
					gameE.setPlayers(playerList);
				}else{
				gameE.setPlayers(game.getPlayers());
				}				
				gameMap2.put(game.getGameId(), gameE);
			} else {
				gameMap2.put(game.getGameId(), game);
				 //processStartGame(game);//Start on Initial Player Join Indicates no  Admin came
			}
		} catch (Exception e) {
			System.out.println("Add card Exception");
			e.printStackTrace();
		}
	}
	
	private void addCard(Game gameE) {
		List<Player> playerList = gameE.getPlayers();
		Card[] shuffledCard = dealer.shuffleCards(dealer.getDeckOfCards());
		dealer.dealCards(playerList, shuffledCard);
		gameE.setPlayers(playerList);
	}

	private void updateAdminBet(Game gameE) {
		if (gameE.getAdmin()!=null ) {
			gameE.getAdmin().setBetAmount(gameE.getAdmin().getBetAmount()+gameE.getGameAmount());
		}
	}

	
	
	
	
	public void closeHandler(WebSocketSession session) {
		String GamePlayer = SessionMap.get(session.getId());
		System.out.println(GamePlayer);
		if(GamePlayer!=null && GamePlayer.split(":")[0]!=null ){
		String gameId = GamePlayer.split(":")[0];
		System.out.println(gameId);
		String playerName = GamePlayer.split(":")[1];
		removeWsSession(gameId, session.getId());
		Game game = gameMap.get(gameId);
		removePlayer(game, playerName, "LEFT");
		if(game!=null){
		publishService.publishGameState(game,Wsmap.get(gameId));
		}
		}
	}
	
	
	private void removePlayer(Game gameE, String Player, String type) {
		if(gameE!=null){
		List<Player> playerList = gameE.getPlayers();
		double returnAmount = 0;
		double betAmount = 0;
		for (int i = 0; i < playerList.size(); i++) {
			if (playerList.get(i).getName().equals(Player)) {
				if (type == "DROP") {
					playerList.get(i).setActive("N");
				} else if (type == "LEFT") {
					playerList.get(i).setActive("L");
				}
				betAmount = gameE.getGameAmount() * (PUSHCARD2BET / 100);
				playerList.get(i).setBetAmount(betAmount);
				returnAmount = gameE.getGameAmount()-betAmount;
			} 			
		}
		handleDropBetAmount(gameE, returnAmount,Player);
		gameE.setPlayers(playerList);
		}
	}

	double getBetAmount(String gameState, double gameAmount) {
		double betAmount = 0;
		switch (gameState) {
		case "STARTGAME":
			betAmount = gameAmount * (STARTGAMEBET / 100);
			break;
		case "PUSHCARD1":
			betAmount = gameAmount * (PUSHCARD1BET / 100);
			break;
		case "PUSHCARD2":
			betAmount = gameAmount * (PUSHCARD2BET / 100);
			break;
		}

		return betAmount;

	}

	

	void handleDropBetAmount(Game game, double returnAmount,String player) {
		if(game.getAdmin()!=null && game.getAdmin().getBetAmount() >0){
		game.getAdmin().setBetAmount(game.getAdmin().getBetAmount() - returnAmount);
		returnGameAmount(returnAmount, player);
		}
		

	 
	}
	
	void removeWsSession(String gameId, String SessionId) {
		Set<WebSocketSession> wsSessionList = Wsmap.get(gameId);
		if (wsSessionList != null && !wsSessionList.isEmpty() && wsSessionList.size() > 0) {
			wsSessionList.forEach(ws -> {
				if (ws.getId().equals(SessionId)) {
					wsSessionList.remove(ws);
				}
			});
		}
		
		Wsmap.put(gameId, wsSessionList);
	}
	
	void setAdminCommision(Game game) {
		game.getAdmin().setBetAmount(game.getAdmin().getBetAmount()*(GAME_COMMISION/100));
		//Call Service to Update DB for Admin player Amount
		
		
	}

	
	void handleDeclare(Game game){
		Game gameCurrent = gameMap.get(game.getGameId());
		dealer.declareWinner(gameCurrent);
		if(gameCurrent.getIsDeclared().equals("Y")){
			gameCurrent.setState("DECLARED");					
			setAdminCommision(gameCurrent);
			
			publishService.publishGameState(gameCurrent,Wsmap.get(game.getGameId()));
			removeGame(game);
			// Removing Gamme ID Map			
		}else{
			gameCurrent.setState("DECLARED");					
			setAdminCommision(gameCurrent);
			publishService.publishGameState(gameCurrent,Wsmap.get(game.getGameId()));
			removeGame(game);
		}
	}
	

	

	
	

}
