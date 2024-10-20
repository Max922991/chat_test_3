package com.example.chat_test_3.models;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Message {

    String content;
    String sender;
    String receiver;
}
