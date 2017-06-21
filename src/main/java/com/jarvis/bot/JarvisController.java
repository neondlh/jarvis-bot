package com.jarvis.bot;

import java.net.URISyntaxException;

import org.alicebot.ab.Bot;
import org.alicebot.ab.Chat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class JarvisController {

	private Bot bot;
	private Chat chat;
	private final static ObjectMapper MAPPER = new ObjectMapper();

	public JarvisController() throws URISyntaxException {
		//file:/app/target/jarvis-bot-1.0.0-SNAPSHOT.jar!/BOOT-INF/classes!/com/jarvis/bot/bots
		//BOOT-INF/classes/com/jarvis/bot/bots/alice2/aiml
		String path = "file:/app/target/jarvis-bot-1.0.0-SNAPSHOT.jar/BOOT-INF/classes/com/jarvis/bot/bots";
		bot = new Bot("alice2",path, "chat");
		chat = new Chat(bot);
		bot.brain.nodeStats();
	}

	@RequestMapping("/status")
	public String status() {
		return "up an running";
	}

	@RequestMapping("/chat")
	public String chat(@RequestParam(value = "question") String question) throws JsonProcessingException {
		String response = chat.multisentenceRespond(question);
		Message message = new Message("Jarvis", response);
		return MAPPER.writeValueAsString(message);
	}
}
