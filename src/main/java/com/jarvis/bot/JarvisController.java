package com.jarvis.bot;

import static com.github.messenger4j.MessengerPlatform.SIGNATURE_HEADER_NAME;

import java.io.IOException;
import java.net.URISyntaxException;

import org.alicebot.ab.Bot;
import org.alicebot.ab.Chat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class JarvisController {
	private static final Logger logger = LoggerFactory.getLogger(JarvisController.class);
	private static final String verifyToken = "xyz123";
	private static final String pageAccessToken = "EAADmeR5hib8BAC5Dc8lpaXjnEHFKuAyLnjVJZAjH8ctFElQVO96xn9GSMDJsqyV0AJ09js0VBEmjLMBdORHEpQd3eTmjTiRTAECuLnZCqNcq2ZBtfTp8a1veHbOYkzqxyYM1JmZBEr8cdSmLDa1O0C5WIpzDK9QxNcNtili8jQZDZD";
	private final String requestUrl = "https://graph.facebook.com/v2.6/me/messages?access_token=EAADmeR5hib8BAC5Dc8lpaXjnEHFKuAyLnjVJZAjH8ctFElQVO96xn9GSMDJsqyV0AJ09js0VBEmjLMBdORHEpQd3eTmjTiRTAECuLnZCqNcq2ZBtfTp8a1veHbOYkzqxyYM1JmZBEr8cdSmLDa1O0C5WIpzDK9QxNcNtili8jQZDZD";
	private String sendId;
	private String question;
	private final String RESPONSE_FORMAT = "{\"recipient\": {\"id\": \"%s\"},\"message\": {\"text\": \"%s\"}}";
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
	
	private void parseFbRequest(String fbRequest){
		try {
			JsonNode root = MAPPER.readTree(fbRequest);
			if (root != null) {
				JsonNode entryNode = root.path("entry");
				if (!entryNode.isMissingNode()) {
					JsonNode firstEntryNode = entryNode.get(0);
					JsonNode messagingNode = firstEntryNode.path("messaging");
					if (!messagingNode.isMissingNode()) {
						JsonNode firstMessagingNode = messagingNode.get(0);
						JsonNode senderIdNode = firstMessagingNode.path("sender").path("id");
						sendId = senderIdNode.asText();
						question = firstMessagingNode.path("message").path("text").asText();
					}
				}
			}
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<String> verifyWebhook(
			@RequestParam("hub.mode") final String mode,
			@RequestParam("hub.verify_token") final String verifyToken,
			@RequestParam("hub.challenge") final String challenge) {

		logger.error("Received Webhook verification request - mode: {} | verifyToken: {} | challenge: {}",mode, verifyToken, challenge);
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

        logger.error("Received Messenger Platform callback - payload: {} | signature: {}", payload, signature);
        try {
        	parseFbRequest(payload);
        	logger.error("sendId: {} | question: {}", sendId, question);
        	String response = "Test response";
        	
        	RestTemplate template = new RestTemplate();
        	String request = String.format(RESPONSE_FORMAT, sendId, response);
        	logger.error("Request: {}", request);
        	
        	HttpHeaders headers = new HttpHeaders();
        	headers.setContentType(MediaType.APPLICATION_JSON);
        	HttpEntity<String> requestEntity = new HttpEntity<String>(request, headers);

        	
			ResponseEntity<String> result = template.exchange(requestUrl, HttpMethod.POST, requestEntity,String.class);
			
            logger.error("Result: " + result.getBody());
            
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            logger.warn("Processing of callback payload failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}
