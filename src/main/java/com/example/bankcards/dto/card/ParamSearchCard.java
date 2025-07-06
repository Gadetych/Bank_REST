package com.example.bankcards.dto.card;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ParamSearchCard {
    private Integer offset;
    private Integer limit;
}
