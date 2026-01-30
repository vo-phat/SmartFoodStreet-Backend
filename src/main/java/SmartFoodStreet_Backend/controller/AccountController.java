package SmartFoodStreet_Backend.controller;

import SmartFoodStreet_Backend.common.response.ApiResponse;
import SmartFoodStreet_Backend.dto.account.request.AccountCreationRequest;
import SmartFoodStreet_Backend.dto.account.request.AccountUpdateRequest;
import SmartFoodStreet_Backend.entity.Account;
import SmartFoodStreet_Backend.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @PostMapping()
    public ApiResponse<Account> createAccount(@RequestBody @Valid AccountCreationRequest accountCreationRequest) {
        ApiResponse<Account> apiResponse = new ApiResponse<>();

        apiResponse.setResult(accountService.createAccount(accountCreationRequest));

        return apiResponse;
    }

    @GetMapping
    public List<Account> getAllAccounts() {
        return accountService.getAccounts();
    }

    @GetMapping("/{accountId}")
    public Account getAccount(@PathVariable int accountId) {
        return accountService.getAccount(accountId);
    }

    @PutMapping("/{accountId}")
    public Account updateAccount(@PathVariable int accountId, @RequestBody @Valid AccountUpdateRequest accountUpdateRequest) {
        return accountService.updateAccount(accountId, accountUpdateRequest);
    }

    @DeleteMapping("{accountId}")
    public String deleteAccount(@PathVariable int accountId) {
        accountService.deleteAccount(accountId);
        return "Account has been deleted";
    }
}
