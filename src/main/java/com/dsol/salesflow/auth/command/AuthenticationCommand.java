package com.dsol.salesflow.auth.command;

import com.dsol.salesflow.asset.Command;
import com.dsol.salesflow.auth.model.AuthenticationRequest;

public class AuthenticationCommand extends Command<AuthenticationRequest> {
    public AuthenticationCommand(AuthenticationRequest payload) {
        super(payload);
    }
}
