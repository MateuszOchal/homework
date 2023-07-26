package com.nphase.service;

import com.nphase.entity.Product;
import com.nphase.entity.ShoppingCart;
import org.jetbrains.annotations.NotNull;

import javax.naming.OperationNotSupportedException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ShoppingCartService {

    public ConfigReader configReader;

    public ShoppingCartService(ConfigReader configReader) {
        this.configReader = configReader;
    }

    public BigDecimal calculateTotalPrice(@NotNull ShoppingCart shoppingCart) {
        return shoppingCart.getProducts()
                .stream()
                .map(product -> product.getPricePerUnit().multiply(BigDecimal.valueOf(product.getQuantity())))
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }

    public BigDecimal calculateTotalPriceWithDiscount(@NotNull ShoppingCart shoppingCart) {
        return shoppingCart.getProducts()
                .stream()
                .map(product -> {
                    int quantity = product.getQuantity();
                    BigDecimal pricePerUnit = product.getPricePerUnit();

                    if (quantity > 3) {
                        BigDecimal discountPercentage = BigDecimal.valueOf(0.10);
                        BigDecimal discountedPrice = pricePerUnit.multiply(BigDecimal.valueOf(quantity)).multiply(BigDecimal.ONE.subtract(discountPercentage));
                        return discountedPrice;
                    } else {
                        return pricePerUnit.multiply(BigDecimal.valueOf(quantity));
                    }
                })
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO);
    }

    public BigDecimal calculateTotalPriceWithDiscountAndCategories(ShoppingCart shoppingCart) {
        Map<String, List<Product>> productsByCategory = shoppingCart.getProducts()
                .stream()
                .collect(Collectors.groupingBy(Product::getCategory));

        BigDecimal totalPrice = BigDecimal.ZERO;
        for (List<Product> productsInCategory : productsByCategory.values()) {
            BigDecimal categoryTotalPrice = calculateCategoryTotalPrice(productsInCategory);
            int totalQuantityInCategory = productsInCategory.stream().mapToInt(Product::getQuantity).sum();

            if (totalQuantityInCategory > 3) {
                BigDecimal discountPercentage = BigDecimal.valueOf(0.10);
                BigDecimal discountAmount = categoryTotalPrice.multiply(discountPercentage);
                categoryTotalPrice = categoryTotalPrice.subtract(discountAmount).setScale(2, RoundingMode.HALF_UP);
            }

            totalPrice = totalPrice.add(categoryTotalPrice);
        }

        return totalPrice;
    }

    private BigDecimal calculateCategoryTotalPrice(List<Product> productsInCategory) {
        BigDecimal categoryTotalPrice = BigDecimal.ZERO;
        for (Product product : productsInCategory) {
            int quantity = product.getQuantity();
            BigDecimal pricePerUnit = product.getPricePerUnit();
            BigDecimal totalPriceForProduct = pricePerUnit.multiply(BigDecimal.valueOf(quantity));
            categoryTotalPrice = categoryTotalPrice.add(totalPriceForProduct);
        }
        return categoryTotalPrice;
    }


    public BigDecimal calculateTotalPriceWithDiscountAndCategoriesAndConfigurableDiscountValues(ShoppingCart shoppingCart, ConfigReader configReader) {
        Map<String, List<Product>> productsByCategory = shoppingCart.getProducts()
                .stream()
                .collect(Collectors.groupingBy(Product::getCategory));
        BigDecimal discountPercentage = BigDecimal.valueOf(configReader.getDiscountSizeInPercent());
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (List<Product> productsInCategory : productsByCategory.values()) {
            BigDecimal categoryTotalPrice = calculateCategoryTotalPrice(productsInCategory);
            int totalQuantityInCategory = productsInCategory.stream().mapToInt(Product::getQuantity).sum();

            if (totalQuantityInCategory > configReader.getNumberOfItemsNeededForDiscount()) {

                BigDecimal discountAmount = categoryTotalPrice.multiply(discountPercentage);
                categoryTotalPrice = categoryTotalPrice.subtract(discountAmount).setScale(2, RoundingMode.HALF_UP);
            }

            totalPrice = totalPrice.add(categoryTotalPrice);
        }

        return totalPrice;
    }
}


