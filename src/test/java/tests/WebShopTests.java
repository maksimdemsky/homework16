package tests;

import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Cookie;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class WebShopTests {

    @Test
    void addItemToCardNewUserTest() {
        given()
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .body("addtocart_43.EnteredQuantity=1")
                .when()
                .post("http://demowebshop.tricentis.com/addproducttocart/details/43/1")
                .then()
                .statusCode(200)
                .body("message", is("The product has been added to your <a href=\"/cart\">shopping cart</a>"))
                .body("updatetopcartsectionhtml", is("(1)"));
    }

    @Test
    void addItemToCardAsExistUserTest() {
        String cookie =
        given()
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .body("addtocart_43.EnteredQuantity=5")
                .when()
                .post("http://demowebshop.tricentis.com/addproducttocart/details/43/1")
                .then()
                .statusCode(200)
                .log().body().extract().cookie("Nop.customer");
        given()
                .cookie(cookie)
                .when()
                .get("http://demowebshop.tricentis.com/cart")
                .then()
                .statusCode(200)
                .log().body();
        assertThat("You have no items in your shopping cart.", is("You have no items in your shopping cart."));

        given()
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .body("addtocart_43.EnteredQuantity=5")
                .cookie(cookie)
                .when()
                .post("http://demowebshop.tricentis.com/addproducttocart/details/43/1")
                .then()
                .statusCode(200)
                .body("success", is(true))
                .body("updatetopcartsectionhtml", is("(5)"));
        open("http://demowebshop.tricentis.com/cart");
        getWebDriver().manage().addCookie(new Cookie("Nop.customer", cookie));
        Selenide.refresh();
        $(".cart-qty").shouldHave(text("(5)"));
    }

}

