package com.example.bankcards.dto.card;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ParamSearchAdminCard {
    private Integer offset;
    private Integer limit;
}
