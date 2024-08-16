package com.example.service;

import com.example.entity.Message;
import com.example.entity.Account;
import com.example.repository.MessageRepository;
import com.example.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private AccountRepository accountRepository;

    public Message createMessage(Message message) {
        // Check if the user exists
        Optional<Account> account = accountRepository.findById(message.getPostedBy());
        if (account.isEmpty()) {
            throw new IllegalArgumentException("User not found in the database");
        }

        // Validate the message text
        if (message.getMessageText() == null || message.getMessageText().trim().isEmpty()) {
            throw new IllegalArgumentException("Message text cannot be blank");
        }

        if (message.getMessageText().length() > 255) {
            throw new IllegalArgumentException("Message text cannot exceed 255 characters");
        }

        // Save and return the message
        return messageRepository.save(message);
    }
    

    public List<Message> getMessagesByAccountId(Integer accountId) {
        return messageRepository.findByPostedBy(accountId);
    }

    public Message getMessageById(Integer messageId) {
        return messageRepository.findById(messageId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found"));
    }

    public boolean deleteMessage(Integer messageId) {
        if (messageRepository.existsById(messageId)) {
            messageRepository.deleteById(messageId);
            return true;
        } else {
            return false;
        }
    }

    public Message updateMessage(Integer messageId, String newMessageText) {
        // validate new message text
        if (newMessageText == null || newMessageText.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Message text cannot be blank");
        }
        if (newMessageText.length() > 255) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Message text cannot exceed 255 characters");
        }

        // retrieve the old message 
        Message existingMessage = getMessageById(messageId);
        // update by new message
        existingMessage.setMessageText(newMessageText);
        // save
        return messageRepository.save(existingMessage);
    }
}
