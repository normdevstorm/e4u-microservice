package jmaster.io.accountservice.service;

import jmaster.io.accountservice.entity.Account;
import jmaster.io.accountservice.model.AccountDTO;
import jmaster.io.accountservice.repository.AccountRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

public interface AccountService {
    void add(AccountDTO accountDTO);

    void update(AccountDTO accountDTO);

    void updatePassword(AccountDTO accountDTO);

    void delete(Long id);

    List<AccountDTO> getAll();

    AccountDTO getOne(Long id);
}

@Transactional
@Service
class AccountServiceImpl implements AccountService {
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    ModelMapper modelMapper;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Override
    public void add(AccountDTO accountDTO) {
        Account account = modelMapper.map(accountDTO, Account.class);
        //account.setPassword(new BCryptPasswordEncoder().encode(accountDTO.getPassword()));

        accountRepository.save(account);

        accountDTO.setId(account.getId());
    }

    @Override
    public void update(AccountDTO accountDTO) {
        Account account = accountRepository.findById(accountDTO.getId()).orElse(null);
        if (account != null) {
            modelMapper.typeMap(AccountDTO.class, Account.class)
                    .addMappings(mapper -> mapper.skip(Account::setPassword)).map(accountDTO, account);

            accountRepository.save(account);
        }
    }

    @Override
    public void updatePassword(AccountDTO accountDTO) {
        accountRepository.findById(accountDTO.getId()).ifPresent(account -> accountRepository.save(account));
    }

    @Override
    public void delete(Long id) {
        accountRepository.findById(id).ifPresent(account -> accountRepository.delete(account));
    }

    @Override
    public List<AccountDTO> getAll() {
        logger.info("Get All in AccountService");
        return accountRepository.findAll().stream().map((account) -> (modelMapper.map(account, AccountDTO.class))).collect(Collectors.toList());
    }

    @Override
    public AccountDTO getOne(Long id) {
        Account account = accountRepository.findById(id).orElse(null);

        if (account != null) {
            return modelMapper.map(account, AccountDTO.class);
        }

        return null;
    }
}