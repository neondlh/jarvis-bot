package com.jarvis.bot;

import static com.github.messenger4j.MessengerPlatform.SIGNATURE_HEADER_NAME;

import java.net.URISyntaxException;

import org.alicebot.ab.Bot;
import org.alicebot.ab.Chat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.messenger4j.exceptions.MessengerVerificationException;

@RestController
public class JarvisController {
	private static final Logger logger = LoggerFactory
			.getLogger(JarvisController.class);
	private Bot bot;
	private Chat chat;
	private final static ObjectMapper MAPPER = new ObjectMapper();

	public JarvisController() throws URISyntaxException {
		String path = getClass().getResource("").getPath();
		bot = new Bot("alice2", path, "chat");
		chat = new Chat(bot);
		bot.brain.nodeStats();
	}

	@RequestMapping("/status")
	public String status() {
		return "up an running";
	}
//
//	@RequestMapping(method = RequestMethod.GET)
//	public String chat(@RequestParam(value = "question") String question)
//			throws JsonProcessingException {
//		String response = chat.multisentenceRespond(question);
//		Message message = new Message("Jarvis", response);
//		return MAPPER.writeValueAsString(message);
//	}

	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<String> verifyWebhook(
			@RequestParam("hub.mode") final String mode,
			@RequestParam("hub.verify_token") final String verifyToken,
			@RequestParam("hub.challenge") final String challenge) {

		logger.debug(
				"Received Webhook verification request - mode: {} | verifyToken: {} | challenge: {}",
				mode, verifyToken, challenge);
		try {
			return ResponseEntity.ok(challenge);
		} catch (Exception e) {
			logger.warn("Webhook verification failed: {}", e.getMessage());
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
					e.getMessage());
		}
	}
	
	@RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Void> handleCallback(@RequestBody final String payload,
                                               @RequestHeader(SIGNATURE_HEADER_NAME) final String signature) {

        logger.debug("Received Messenger Platform callback - payload: {} | signature: {}", payload, signature);
        try {
            //this.receiveClient.processCallbackPayload(payload, signature);
            logger.debug("Processed callback payload successfully");
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            logger.warn("Processing of callback payload failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}
