package ru.netology.web.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.web.data.DataHelper;
import ru.netology.web.page.DashboardPage;
import ru.netology.web.page.LoginPageV1;
import ru.netology.web.page.LoginPage;
import ru.netology.web.page.LoginPageV3;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MoneyTransferTest {
    DashboardPage dashboardPage;
    DataHelper.CardInfo firstCardInfo;
    DataHelper.CardInfo secondCardInfo;
    int balanceFirstCard;
    int balanceSecondCard;


    @BeforeEach
    void setup() {
        open("http:localhost:9999");
    }
        DashboardPage login() {
            var loginPage = new LoginPage();
            var authInfo = DataHelper.getAuthInfo();
            var verificationPage = loginPage.validLogin(authInfo);
            var verificationCode = DataHelper.getVerificationCodeFor(authInfo);
            firstCardInfo = DataHelper.getFirstCardInfo();
            secondCardInfo = DataHelper.getSecondCardInfo();
            balanceFirstCard = dashboardPage.getCardBalance(0);
            balanceSecondCard = dashboardPage.getCardBalance(1);
            return verificationPage.validVerify(verificationCode);
        }

    @Test
    void shouldTransferMoneyBetweenCards() {
        var amount = DataHelper.generateValidAmount(balanceFirstCard);
        var expectedBalanceFirstCard = balanceFirstCard - amount;
        var expectedBalanceSecondCard = balanceSecondCard + amount;
        var transferPage = dashboardPage.selectCardToTransfer(secondCardInfo);
        dashboardPage = transferPage.makeValidTransfer(String.valueOf(amount), firstCardInfo);
        dashboardPage.reloadDashboardPage();
        var actualBalanceFirstCard = dashboardPage.getCardBalance(0);
        var actualBalanceSecondCard = dashboardPage.getCardBalance(1);
        assertAll(() -> assertEquals(expectedBalanceFirstCard, actualBalanceFirstCard),
                () -> assertEquals(expectedBalanceSecondCard, actualBalanceSecondCard));
    }

    @Test
    void shouldErrorWhenMoreBalance() {
        var amount = DataHelper.generateInvalidAmount(balanceSecondCard);
        var transferPage = dashboardPage.selectCardToTransfer(firstCardInfo);
        transferPage.makeTransfer(String.valueOf(amount), secondCardInfo);
        transferPage.error("Выполняется попытка перевода суммы, превыщающей баланс карты");
    }
}