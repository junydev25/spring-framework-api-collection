package com.junydev.spring.api.csv.internal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCsvDto {

    private String name;
    private String email;
    private String gender;
}
