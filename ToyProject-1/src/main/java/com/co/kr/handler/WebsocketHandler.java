package com.co.kr.handler;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
public class WebsocketHandler extends TextWebSocketHandler {
	private static List<WebSocketSession> list = new ArrayList<>();

	//handleTextmassage() 메소드는 클라이언트로부터 받은 데이터를 처리하는 역할
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
    	
        // 여기서 받아오는 페이로드는 클라이언트에서 보낸 메시지가 된다.
        String payload = message.getPayload();
        System.out.println("payload==========>" + payload);

        // 해당 TextMessage는 서버가 클라이언트로 보내는 메시지가 된다.
        for(WebSocketSession sess: list) {
            sess.sendMessage(message);
            
        }
    }
    
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception{
    	
    	list.add(session);
    	System.out.println("list에 들어 있는 값 " + list);
    	System.out.println(session + "입장하였습니다");
    }
    
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception{
    	System.out.println(session + "퇴장하였습니다");
    	list.remove(session);
    }
}