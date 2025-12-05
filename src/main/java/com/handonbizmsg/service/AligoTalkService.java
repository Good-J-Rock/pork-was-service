package com.handonbizmsg.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;

//@Service
public interface AligoTalkService {
    Mono<JsonNode> sendAligoTalk(String receiver, String message);
}
