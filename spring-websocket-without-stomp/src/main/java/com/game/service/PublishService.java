package com.game.service;

import java.io.IOException;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.game.dto.Game;
import com.google.gson.Gson;

@Component
public class PublishService {
	
	
	
	public void publishGameState(Game game, Set<WebSocketSession> wsSession) {
		
		String str = new Gson().toJson(game);
		byte[] gameString = str.getBytes();
		if(game!=null && game.getGameId()!=null){
		     publish(gameString,wsSession );
		}
	}

	public void publish(byte[] gameString, Set<WebSocketSession> wsSession) {
		wsSession.forEach(ws -> {
			try {
				TextMessage ts = new TextMessage(gameString);
				if (ws.isOpen() ) {
					ws.sendMessage(ts);

				} else {
					wsSession.remove(ws);
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}
	

}
