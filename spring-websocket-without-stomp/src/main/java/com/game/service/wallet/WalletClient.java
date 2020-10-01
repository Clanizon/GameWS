package com.game.service.wallet;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.game.dto.wallet.Wallet;
import com.game.rest.RestHandler;



@Component
public class WalletClient {
	
	
	public Object updateWallet(String player,double amount) {
		RestHandler restHandler = new RestHandler(); 
		    String URL_WALLET_UPDATE="/api/user/updatewalletbyuser";
		    Wallet wallet = new Wallet();
		    wallet.setUserId(player);
		    wallet.setWalletAmount(amount);
		    Object object = new Object();
		    try {
		    	System.out.println("restHandler"+player);
				restHandler.callServicePost(URL_WALLET_UPDATE, wallet, object);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		return amount;		
	}
	
	
}
