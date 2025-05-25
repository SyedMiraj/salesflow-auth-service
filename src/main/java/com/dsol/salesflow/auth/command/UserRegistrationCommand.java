package com.dsol.salesflow.auth.command;

import com.dsol.salesflow.asset.Command;
import com.dsol.salesflow.asset.CommandHandler;
import com.dsol.salesflow.auth.model.User;

public class UserRegistrationCommand extends Command<User> {

    public UserRegistrationCommand(User payload) {
        super(payload);
    }
}
