package kr.tracom.smps.message;

import java.util.Map;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;

public class MessageController {
	@MessageMapping("/message")
	@SendTo("/message")
	public Map<String, Object> processMessage(Map<String, Object> message) {
		return message;
	}
}
