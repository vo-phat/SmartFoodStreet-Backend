package SmartFoodStreet_Backend.mapper;

import SmartFoodStreet_Backend.dto.account.request.AccountCreationRequest;
import SmartFoodStreet_Backend.dto.account.request.AccountUpdateRequest;
import SmartFoodStreet_Backend.dto.account.response.AccountResponse;
import SmartFoodStreet_Backend.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    @Mapping(target = "roles", ignore = true)
    Account toAccount(AccountCreationRequest request);

    @Mapping(target = "roles", ignore = true)
    void updateAccount(@MappingTarget Account account, AccountUpdateRequest request);

    AccountResponse toAccountResponse(Account account);
}