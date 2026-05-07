package com.example.banking.controller;

import com.example.banking.dto.AccountDto;
import com.example.banking.entity.Account;
import com.example.banking.repository.AccountRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AccountControllerIT {

    @LocalServerPort
    private int port = 0;

    @Autowired
    private AccountRepository accountRepository;

    @BeforeEach
    void setUp() {
        Account account = new Account();
        account.setId(1L);
        account.setAccountHolderName("Jane Mae");
        account.setBalance(1500.0);
        accountRepository.save(account);
    }

    @AfterEach
    void tearDown() {
        accountRepository.deleteAll();
    }

    @Test
    void testCreateAccount() {
        AccountDto accountDto = new AccountDto();
        accountDto.setAccountHolderName("John Doe");
        accountDto.setBalance(1000.0);

        given()
                .baseUri("http://localhost:"+port)
                .basePath("/api/accounts")
                .contentType("application/json")
                .body(accountDto)
                .when()
                .post()
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("id", equalTo(2))
                .body("accountHolderName", equalTo("John Doe"))
                .body("balance", equalTo(1000.0f));
    }

    @Test
    void testGetAccountById() {
        Long accountId = 1L;

        given()
                .baseUri("http://localhost:"+port)
                .basePath("/api/accounts")
                .pathParam("id", accountId)
                .when()
                .get("/{id}")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(accountId.intValue()))
                .body("accountHolderName", notNullValue())
                .body("balance", equalTo(1500.0f));
    }

    @Test
    void testDeposit() {
        Long accountId = 1L;
        double depositAmount = 500.0;

        given()
                .baseUri("http://localhost:"+port)
                .basePath("/api/accounts")
                .pathParam("id", accountId)
                .queryParam("depositAmount", depositAmount)
                .when()
                .put("/{id}/deposit")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("balance", equalTo(2000.0f));
    }

    @Test
    void testWithdraw() {
        Long accountId = 1L;
        double withdrawAmount = 200.0;

        given()
                .baseUri("http://localhost:"+port)
                .basePath("/api/accounts")
                .pathParam("id", accountId)
                .queryParam("withdrawAmount", withdrawAmount)
                .when()
                .put("/{id}/withdraw")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("balance", equalTo(1300.0f));
    }

    @Test
    void testGetAllAccounts() {
        given()
                .baseUri("http://localhost:"+port)
                .basePath("/api/accounts")
                .when()
                .get()
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("$", not(empty()));
    }

    @Test
    void testDeleteAccount() {
        Long accountId = 1L;

        given()
                .baseUri("http://localhost:"+port)
                .basePath("/api/accounts")
                .pathParam("id", accountId)
                .when()
                .delete("/{id}")
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }
}
