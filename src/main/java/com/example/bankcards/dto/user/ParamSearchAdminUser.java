package com.example.bankcards.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ParamSearchAdminUser {
    Integer offset;
    Integer limit;
}
