package backend.mapper;

import backend.dto.account.request.AccountCreationRequest;
import backend.dto.account.request.AccountUpdateRequest;
import backend.dto.account.response.AccountResponse;
import backend.entity.Account;
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