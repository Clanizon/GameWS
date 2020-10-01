package com.game.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class RestHandler {


	@Autowired
	ResponseEntity responseEntity;
	
	//@Value("${app.serviceBaseUrl}")
    //private String URL_CONTEXT;
    String URL_CONTEXT="http://realkeyip.in:8086";
   
   
	public Object callServicePost(String Url, Object request, Object response)
			throws IllegalArgumentException, IllegalAccessException {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		System.out.println(URL_CONTEXT);
        String requestUrl= URL_CONTEXT+Url;
		HttpEntity<Object> httprequest = new HttpEntity<>(request, headers);
		System.out.println(requestUrl);
		responseEntity = restTemplate.postForEntity(requestUrl, httprequest, Object.class);
		System.out.println(responseEntity.getStatusCode());
		System.out.println(responseEntity.getBody());
		return responseEntity;
	}

}