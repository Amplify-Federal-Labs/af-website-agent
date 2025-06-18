package net.starkenberg.ai.springaiagent.controllers;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import net.starkenberg.ai.springaiagent.chat.Answer;
import net.starkenberg.ai.springaiagent.chat.CustomerSupportAssistant;
import net.starkenberg.ai.springaiagent.chat.Question;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class ChatController {

    private final CustomerSupportAssistant assistant;

    @GetMapping
    String generation(@RequestParam(name = "question", defaultValue = "What are Amplify Federal's core values?") String userInput, HttpSession session) {
        return this.assistant.chat(session.getId(),userInput);
    }

    @PostMapping
    Answer getAnswer(@RequestBody Question question,  HttpSession session) {
        return new Answer(this.assistant.chat(session.getId(), question.question()));
    }

}
