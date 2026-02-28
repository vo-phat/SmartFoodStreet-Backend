package SmartFoodStreet_Backend.service.interfaces;

import SmartFoodStreet_Backend.dto.account.request.AccountCreationRequest;
import SmartFoodStreet_Backend.dto.account.request.AccountUpdateRequest;
import SmartFoodStreet_Backend.entity.Account;

import java.util.List;

public interface IAccount {
    Account createAccount(AccountCreationRequest accountRequest);

    List<Account> getAccounts();

    Account getAccount(long accountId);

    Account getAccountByUserName(String userName);

    Account updateAccount(long accountId, AccountUpdateRequest accountUpdateRequest);

    void deleteAccount(long accountId);
}
