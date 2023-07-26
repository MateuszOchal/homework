package com.nphase.service;


import com.nphase.entity.Product;
import com.nphase.entity.ShoppingCart;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;

public class ShoppingCartServiceTest {


    @Test
    public void calculatesPrice()  {
         ConfigReader configReader = new ConfigReader("application.yml");
         ShoppingCartService shoppingCartService = new ShoppingCartService(configReader);
        ShoppingCart cart = new ShoppingCart(Arrays.asList(
                new Product("Tea", BigDecimal.valueOf(5.0), 2),
                new Product("Coffee", BigDecimal.valueOf(6.5), 1)
        ));

        BigDecimal result = shoppingCartService.calculateTotalPrice(cart);

        Assertions.assertEquals(result, BigDecimal.valueOf(16.5));
    }

    @Test
    public void calculatesPriceWithDiscount() {
        ConfigReader configReader = new ConfigReader("application.yml");
        ShoppingCartService shoppingCartService = new ShoppingCartService(configReader);
        ShoppingCart cart = new ShoppingCart(Arrays.asList(
                new Product("Tea", BigDecimal.valueOf(5.0), 5),
                new Product("Coffee", BigDecimal.valueOf(3.5), 3)
        ));

        BigDecimal result = shoppingCartService.calculateTotalPriceWithDiscount(cart);

        // Expected total is: 22.5 (for tea) + 10.5 (for coffee) = 33.0
        BigDecimal expectedTotal = BigDecimal.valueOf(33.00);
        Assertions.assertTrue(result.compareTo(expectedTotal) == 0);
    }

    @Test
    public void calculatesPriceWithCategoryDiscount() {
        ConfigReader configReader = new ConfigReader("application.yml");
        ShoppingCartService shoppingCartService = new ShoppingCartService(configReader);
        ShoppingCart cart = new ShoppingCart(Arrays.asList(
                new Product("Tea", BigDecimal.valueOf(5.3), 2, "drinks"),
                new Product("Coffee", BigDecimal.valueOf(3.5), 2, "drinks"),
                new Product("Cheese", BigDecimal.valueOf(8.0), 2, "food")
        ));

        BigDecimal result = shoppingCartService.calculateTotalPriceWithDiscountAndCategories(cart);

        // Expected total is: 9.54 (for tea) + 6.30 (for coffee) + 16 (for cheese) = 31.84
        Assertions.assertEquals(result, BigDecimal.valueOf(31.84));
    }
    @Test
    public void calculatesPriceWithDiscountAndEditableValues() {
        ConfigReader configReader = new ConfigReader("application.yml");
        ShoppingCartService shoppingCartService = new ShoppingCartService(configReader);
        // Create a ShoppingCart instance with items that qualify for the discount
        configReader = new ConfigReader("application.yml");
        ShoppingCart cart = new ShoppingCart(Arrays.asList(
                new Product("Tea", BigDecimal.valueOf(5.3), 2, "drinks"),
                new Product("Coffee", BigDecimal.valueOf(3.5), 2, "drinks"),
                new Product("Cheese", BigDecimal.valueOf(8.0), 2, "food")
        ));

        BigDecimal result = shoppingCartService.calculateTotalPriceWithDiscountAndCategories(cart);

        // Expected total is: 9.54 (for tea) + 6.30 (for coffee) + 16.0 (for cheese) = 31.84
        BigDecimal expectedTotal = BigDecimal.valueOf(31.84);
        Assertions.assertEquals(result, BigDecimal.valueOf(31.84));
    }

    @Test
    public void calculatesPriceWithDiscountAndEditableValuesDifferentYML() {
        // Create a ShoppingCart instance with items that qualify for the discount
        ConfigReader configReader = new ConfigReader("application-custom.yml");
        ShoppingCartService shoppingCartService = new ShoppingCartService(configReader);
        ShoppingCart cart = new ShoppingCart(Arrays.asList(
                new Product("Tea", BigDecimal.valueOf(5.3), 2, "drinks"),
                new Product("Coffee", BigDecimal.valueOf(3.5), 2, "drinks"),
                new Product("Cheese", BigDecimal.valueOf(8.0), 2, "food")
        ));

        BigDecimal result = shoppingCartService.calculateTotalPriceWithDiscountAndCategoriesAndConfigurableDiscountValues(cart, configReader);

        // Expected total is: 5.3 (for tea) + 3.5 (for coffee) + 16.0 (for cheese) = 31.84
        BigDecimal expectedTotal = BigDecimal.valueOf(24.8);
        Assertions.assertTrue(result.compareTo(expectedTotal) == 0);
    }

}