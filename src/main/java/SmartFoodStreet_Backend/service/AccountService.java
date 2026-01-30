package SmartFoodStreet_Backend.service;

import SmartFoodStreet_Backend.common.exception.ErrorCode;
import SmartFoodStreet_Backend.dto.account.request.AccountCreationRequest;
import SmartFoodStreet_Backend.dto.account.request.AccountUpdateRequest;
import SmartFoodStreet_Backend.entity.Account;
import SmartFoodStreet_Backend.repository.AccountRepository;
import SmartFoodStreet_Backend.service.interfaces.IAccount;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountService implements IAccount {
    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Account createAccount(AccountCreationRequest accountCreationRequest) {
        Account account = new Account();

        if (accountRepository.existsByUserName(accountCreationRequest.getUserName())) {
            throw new RuntimeException(ErrorCode.USER_EXISTS.getMessage());
        }

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

        account.setUserName(accountCreationRequest.getUserName());
        account.setPassword(passwordEncoder.encode(accountCreationRequest.getPassword()));
        account.setFullName(accountCreationRequest.getFullName());
        account.setEmail(accountCreationRequest.getEmail());

        accountRepository.save(account);

        return account;
    }

    @Override
    public List<Account> getAccounts() {
        return accountRepository.findAll();
    }

    @Override
    public Account getAccount(int accountId) {
        return accountRepository.findById(accountId).orElseThrow(() -> new RuntimeException(ErrorCode.USER_NOT_EXISTS.getMessage()));
    }

    @Override
    public Account updateAccount(int accountId, AccountUpdateRequest accountUpdateRequest) {
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new RuntimeException(ErrorCode.USER_NOT_EXISTS.getMessage()));

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

        account.setPassword(passwordEncoder.encode(accountUpdateRequest.getPassword()));
        account.setFullName(accountUpdateRequest.getFullName());
        account.setEmail(accountUpdateRequest.getEmail());
        account.setRole(accountUpdateRequest.getRole());
        account.setIsActive(accountUpdateRequest.getIsActive());

        accountRepository.save(account);

        return account;
    }

    @Override
    public void deleteAccount(int accountId) {
        accountRepository.deleteById(accountId);
    }
}
