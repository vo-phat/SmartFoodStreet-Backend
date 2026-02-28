package SmartFoodStreet_Backend.controller;

import SmartFoodStreet_Backend.common.response.ApiResponse;
import SmartFoodStreet_Backend.dto.account.request.AccountCreationRequest;
import SmartFoodStreet_Backend.dto.account.request.AccountUpdateRequest;
import SmartFoodStreet_Backend.entity.Account;
import SmartFoodStreet_Backend.service.AccountService;
import SmartFoodStreet_Backend.util.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public ApiResponse<List<Account>> getAllAccounts() {
        ApiResponse<List<Account>> apiResponse = new ApiResponse<>();

        apiResponse.setResult(accountService.getAccounts());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        authentication.getAuthorities()
                .forEach(a -> System.out.println(a.getAuthority()));

        return apiResponse;
    }

    @GetMapping("/{accountId}")
    public ApiResponse<Account> getAccount(@PathVariable long accountId) {
        return ApiResponse.<Account>builder()
                .result(accountService.getAccount(accountId))
                .build();
    }

    @GetMapping("/getMyInfo")
    public ApiResponse<Account> getMyInfo() {
        Account account = accountService.getAccountByUserName(SecurityUtils.getCurrentUsername());

        return ApiResponse.<Account>builder()
                .result(account)
                .build();
    }

    @PutMapping("/{accountId}")
    public ApiResponse<Account> updateAccount(@PathVariable long accountId, @RequestBody @Valid AccountUpdateRequest accountUpdateRequest) {
        return ApiResponse.<Account>builder()
                .result(accountService.updateAccount(accountId, accountUpdateRequest))
                .build();
    }

    @DeleteMapping("/{accountId}")
    public ApiResponse<Void> deleteAccount(@PathVariable long accountId) {
        accountService.deleteAccount(accountId);
        return ApiResponse.<Void>builder()
                .message("Delete successfully")
                .build();
    }
}
