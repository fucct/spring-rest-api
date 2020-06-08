package me.dd.restapi.config;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Set;

import org.bouncycastle.est.ESTAuth;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import me.dd.restapi.BaseControllerTest;
import me.dd.restapi.accounts.Account;
import me.dd.restapi.accounts.AccountRole;
import me.dd.restapi.accounts.AccountService;

class AuthServerConfigTest extends BaseControllerTest {

    @Autowired
    AccountService accountService;

    @Test
    @DisplayName("인증 토큰을 발급받는 서비스")
    public void getAuthToken() throws Exception {
        // Given
        String username = "dqrd123@gmail.com";
        String password = "c940429kk";
        Account account = Account.builder()
            .email(username)
            .password(password)
            .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
            .build();
        accountService.saveAccount(account);

        String clientId = "myApp";
        String clientSecret = "pass";

        // When

        // Then
        this.mockMvc.perform(post("/oauth/token")
            .with(httpBasic(clientId, clientSecret))
            .param("username", username)
            .param("password", password)
            .param("grant_type", "password"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("access_token").exists());
    }

}