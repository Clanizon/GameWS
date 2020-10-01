package com.game.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.game.dto.Game;
import com.game.gameutl.Dealer;
import com.game.service.GameService;
import com.google.gson.Gson;

@Component
public class SocketHandler extends TextWebSocketHandler {

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

	GameService gameService = new GameService();
	@Override
	public void handleTextMessage(WebSocketSession session, TextMessage message) {
		try {
			System.out.println("In Socket Handler");			
			System.out.println(message.getPayload());
			
			Game game = new Gson().fromJson(message.getPayload(), Game.class);
			if (game.getState().equals("ADMIN")) {
				gameService.handleAdmin( game,session);
			} else if (game.getState().equals("JOINROOM")){
				gameService.handleJoin( game,session);
			} else if (game.getState().equals("STARTGAME")){
				gameService.handleStart( game,session);
			}else if (game.getState().equals("ABORT")){
				gameService.handleAbort( game,session);
			}else if (game.getState() != null && game.getState().equals("DROP")) {
				gameService.handleDrop( game,session);				
			} 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		// the messages will be broadcasted to all users.

	}
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		// the messages will be broadcasted to all users.
		GameService gameService = new GameService();
		gameService.closeHandler(session);
	}

	

}