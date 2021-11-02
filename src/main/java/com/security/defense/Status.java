package com.security.defense;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@NoArgsConstructor
public class Status {
    private boolean infected = false;
    private boolean detected = false;
    private boolean filtering = false;
}