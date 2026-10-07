package com.educont.badges;

import org.springframework.stereotype.Service;

@Service
public class BadgeService {

    public String conceder(String alunoId, String badge) {
        return "Badge " + badge + " concedido a " + alunoId;
    }
}
