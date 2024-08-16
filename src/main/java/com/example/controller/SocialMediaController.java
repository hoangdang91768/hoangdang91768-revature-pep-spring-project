package com.example.controller;

import com.example.entity.Account;
import com.example.entity.Message;
import com.example.service.AccountService;
import com.example.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller using Spring. The endpoints you will need can be
 * found in readme.md as well as the test cases. You be required to use the @GET/POST/PUT/DELETE/etc Mapping annotations
 * where applicable as well as the @ResponseBody and @PathVariable annotations. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
@RestController
@RequestMapping("/")  // Adjust the base path to make sure /messages is correctly resolved.
public class SocialMediaController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private MessageService messageService;

    // User Registration
    @PostMapping("/register")
    public ResponseEntity<Account> registerAccount(@RequestBody Account account) {
        Account newAccount = accountService.registerAccount(account.getUsername(), account.getPassword());
        return ResponseEntity.ok(newAccount);
    }

    // User Login
    @PostMapping("/login")
    public ResponseEntity<Account> login(@RequestBody Account account) {
        Optional<Account> loggedInAccount = accountService.login(account.getUsername(), account.getPassword());
        return loggedInAccount.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(401).build());
    }

    // Create a new message
    @PostMapping("/messages")
    public ResponseEntity<?> createMessage(@RequestBody Message message) {
        try {
            Message createdMessage = messageService.createMessage(message);
            return new ResponseEntity<>(createdMessage, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // Retrieve all messages
    @GetMapping("/messages")
    public ResponseEntity<List<Message>> getAllMessages() {
        List<Message> messages = messageService.getMessagesByAccountId(null);
        return ResponseEntity.ok(messages);
    }

    // Retrieve a message by its ID
    @GetMapping("/messages/{messageId}")
    public ResponseEntity<Message> getMessageById(@PathVariable Integer messageId) {
        Message message = messageService.getMessageById(messageId);
        return ResponseEntity.ok(message);
    }

    // Delete a message by its ID
    @DeleteMapping("/messages/{messageId}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Integer messageId) {
        boolean deleted = messageService.deleteMessage(messageId);
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    // Update a message by its ID
    @PatchMapping("/messages/{messageId}")
    public ResponseEntity<Message> updateMessage(@PathVariable Integer messageId, @RequestBody String newMessageText) {
        Message updatedMessage = messageService.updateMessage(messageId, newMessageText);
        return ResponseEntity.ok(updatedMessage);
    }

    // Retrieve all messages by a specific user
    @GetMapping("/accounts/{accountId}/messages")
    public ResponseEntity<List<Message>> getMessagesByAccountId(@PathVariable Integer accountId) {
        List<Message> messages = messageService.getMessagesByAccountId(accountId);
        return ResponseEntity.ok(messages);
    }
}

