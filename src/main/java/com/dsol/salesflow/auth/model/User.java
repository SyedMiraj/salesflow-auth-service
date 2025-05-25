package com.dsol.salesflow.auth.model;

import com.dsol.salesflow.type.Role;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class User {
    private long id;
    private String firstName;
    private String lastName;
    private String email;
    private byte[] profilePicture;
    private String password;
    private Role role;
}
