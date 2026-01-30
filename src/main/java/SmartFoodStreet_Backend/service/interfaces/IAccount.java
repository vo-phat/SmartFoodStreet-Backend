package SmartFoodStreet_Backend.service.interfaces;

import SmartFoodStreet_Backend.dto.account.request.AccountCreationRequest;
import SmartFoodStreet_Backend.dto.account.request.AccountUpdateRequest;
import SmartFoodStreet_Backend.entity.Account;

import java.util.List;

public interface IAccount {
    Account createAccount(AccountCreationRequest accountRequest);

    List<Account> getAccounts();

    Account getAccount(int accountId);

    Account updateAccount(int accountId, AccountUpdateRequest accountUpdateRequest);

    void deleteAccount(int accountId);
}
