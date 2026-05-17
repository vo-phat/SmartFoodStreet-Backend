package backend.service.interfaces;

import backend.dto.account.request.AccountCreationRequest;
import backend.dto.account.request.AccountUpdateRequest;
import backend.entity.Account;

import java.util.List;

public interface IAccount {
    Account createAccount(AccountCreationRequest accountRequest);

    List<Account> getAccounts();

    Account getAccount(long accountId);

    Account getAccountByUserName(String userName);

    Account updateAccount(long accountId, AccountUpdateRequest accountUpdateRequest);

    void deleteAccount(long accountId);
}