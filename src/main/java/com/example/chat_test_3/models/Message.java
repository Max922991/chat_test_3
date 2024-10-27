package com.example.chat_test_3.models;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    String content;
    String sender;
    String receiver;
    String encryptedKey;
    String encryptedContent;
    String decryptedContent;
}
