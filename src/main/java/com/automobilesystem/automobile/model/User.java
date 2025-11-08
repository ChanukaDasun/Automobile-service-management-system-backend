package com.automobilesystem.automobile.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document
public class User {
    private String nickname;
    private String fullname;
    private UserStatus status;
}
