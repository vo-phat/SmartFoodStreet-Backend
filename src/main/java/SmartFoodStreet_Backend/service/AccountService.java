package SmartFoodStreet_Backend.service;

import SmartFoodStreet_Backend.common.exception.AppException;
import SmartFoodStreet_Backend.common.exception.ErrorCode;
import SmartFoodStreet_Backend.dto.account.request.AccountCreationRequest;
import SmartFoodStreet_Backend.dto.account.request.AccountUpdateRequest;
import SmartFoodStreet_Backend.entity.Account;
import SmartFoodStreet_Backend.entity.Role;
import SmartFoodStreet_Backend.mapper.AccountMapper;
import SmartFoodStreet_Backend.repository.AccountRepository;
import SmartFoodStreet_Backend.repository.RoleRepository;
import SmartFoodStreet_Backend.service.interfaces.IAccount;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AccountService implements IAccount {
    AccountRepository accountRepository;
    RoleRepository roleRepository;
    AccountMapper accountMapper;
    PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public Account createAccount(AccountCreationRequest request) {

        if (accountRepository.existsByUserName(request.getUserName())) {
            throw new AppException(ErrorCode.USER_ALREADY_EXISTS);
        }

        Account account = accountMapper.toAccount(request);

        account.setPassword(passwordEncoder.encode(request.getPassword()));
        account.setIsActive(true);
        account.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        Set<Role> roles = new HashSet<>(
                roleRepository.findAllByNameIn(request.getRoles())
        );

        if (roles.size() != request.getRoles().size()) {
            throw new AppException(ErrorCode.ROLE_NOT_FOUND);
        }

        account.setRoles(roles);

        return accountRepository.save(account);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public List<Account> getAccounts() {
        return accountRepository.findAll();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public Account getAccount(int accountId) {
        return accountRepository.findById(accountId).orElseThrow(() -> new RuntimeException(ErrorCode.USER_NOT_EXISTS.getMessage()));
    }

    @Override
    public Account getAccountByUserName(String username) {
        return accountRepository.findByUserName(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTS));
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public Account updateAccount(int accountId, AccountUpdateRequest request) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTS));

        accountMapper.updateAccount(account, request);

        if (request.getPassword() != null) {
            account.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        if (request.getRoles() != null) {

            if (!isAdmin) {
                throw new AppException(ErrorCode.UNAUTHORIZED);
            }

            Set<Role> roles = new HashSet<>(
                    roleRepository.findAllByNameIn(request.getRoles())
            );

            if (roles.size() != request.getRoles().size()) {
                throw new AppException(ErrorCode.ROLE_NOT_FOUND);
            }

            account.setRoles(roles);
        }

        return accountRepository.save(account);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteAccount(int accountId) {
        accountRepository.deleteById(accountId);
    }
}
