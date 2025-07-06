package com.example.bankcards.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ParamSearchAdminUser {
    private Integer offset;
    private Integer limit;
}
