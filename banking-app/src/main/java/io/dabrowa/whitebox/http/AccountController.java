package io.dabrowa.whitebox.http;

import io.dabrowa.whitebox.domain.commands.CreateAccountCommand;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(path = "/api/v1/accounts")
public class AccountController {

    private final CommandGateway commandGateway;

    @Autowired
    public AccountController(final CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @PutMapping("/")
    public void create(@RequestParam final String id, @RequestParam final long overdraftLimit, @RequestParam final long initialBalance) {
        commandGateway.send(new CreateAccountCommand(id, initialBalance, overdraftLimit));
    }
}
