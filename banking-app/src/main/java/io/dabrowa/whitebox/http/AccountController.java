package io.dabrowa.whitebox.http;

import io.dabrowa.whitebox.api.commands.CreateAccountCommand;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@Controller
@RequestMapping(path = "/api/v1/accounts")
public class AccountController {

    private final CommandGateway commandGateway;

    @Autowired
    public AccountController(final CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @PutMapping
    public ResponseEntity<Void> create(@RequestParam final String number, @RequestParam final BigDecimal overdraftLimit, @RequestParam final BigDecimal initialBalance) {
        commandGateway.send(new CreateAccountCommand(number, initialBalance, overdraftLimit));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
