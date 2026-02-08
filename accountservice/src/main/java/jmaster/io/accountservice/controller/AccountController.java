package jmaster.io.accountservice.controller;

import jmaster.io.accountservice.client.NotificationService;
import jmaster.io.accountservice.client.StatisticService;
import jmaster.io.accountservice.model.AccountDTO;
import jmaster.io.accountservice.model.MessageDTO;
import jmaster.io.accountservice.model.StatisticDTO;
import jmaster.io.accountservice.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
public class AccountController {
    @Autowired
    NotificationService notificationService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private AccountService accountService;
    @Autowired
    private StatisticService statisticService;
    // add new
    @PostMapping("/account")
    public AccountDTO addAccount(@RequestBody AccountDTO accountDTO) {
        accountService.add(accountDTO);

//        statisticService.add(new StatisticDTO("Account " + accountDTO.getUsername() + " is created", new Date()));

        //send notificaiton
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setFrom("jmaster.io@gmail.com");
        messageDTO.setTo(accountDTO.getUsername());//username is email
        messageDTO.setToName(accountDTO.getName());
        messageDTO.setSubject("Welcome to JMaster.io");
        messageDTO.setContent("JMaster is online learning platform.");

        notificationService.sendNotification(messageDTO);

        return accountDTO;
    }

    // get all
    @GetMapping("/accounts")
    public List<AccountDTO> getAll() {
        logger.info("AccountService Controller: get all accounts");

        statisticService.add(new StatisticDTO("Get all accounts", new Date()));

        return accountService.getAll();
    }

    @GetMapping("/account/{id}")
    public ResponseEntity<AccountDTO> get(@PathVariable(name = "id") Long id) {
        return Optional.of(new ResponseEntity<AccountDTO>(accountService.getOne(id), HttpStatus.OK))
                .orElse(new ResponseEntity<AccountDTO>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/account/{id}")
    public void delete(@PathVariable(name = "id") Long id) {
        statisticService.add(new StatisticDTO("Delete account id " + id, new Date()));

        accountService.delete(id);
    }

    @PutMapping("/account")
    public void update(@RequestBody AccountDTO accountDTO) {
        statisticService.add(new StatisticDTO("Update account: " + accountDTO.getUsername(), new Date()));

        accountService.update(accountDTO);
    }
}
