package com.security.defense;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    private String command;
    private Object data;

    public Message(String command) {
        this.command = command;
    }
}
