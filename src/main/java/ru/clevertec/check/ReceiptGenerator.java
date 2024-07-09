package ru.clevertec.check;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReceiptGenerator {

    DecimalFormat priceDF = new DecimalFormat("0.00");
    DecimalFormat discountDF = new DecimalFormat("0.#");
    private float totalDiscount;

    private void addBlankSpace(List<String[]> receipt) {
        receipt.add(new String[]{""});
    }

    private void addDateTimeHeader(List<String[]> receipt) {
        receipt.add(new String[]{"Date", "Time"});
    }

    private void addDateTime(List<String[]> receipt) {
        receipt.add(new String[]{
                DateTimeUtils.getDateTimeString().split(" ")[0],
                DateTimeUtils.getDateTimeString().split(" ")[1]
        });
    }

    private void addProductInfoHeader(List<String[]> receipt) {
        receipt.add(new String[]{"QTY", "DESCRIPTION", "PRICE", "DISCOUNT", "TOTAL"});
    }

    private void addProductsInfo(List<String[]> receipt, Map<Integer, Integer> productsMap, String discountCardNumber) throws IOException {
        List<Product> allProducts = CSVReader.readProductsFromCSV("src/main/resources/products.csv");
        List<DiscountCard> allDiscountCards = CSVReader.readDiscountCardsFromCSV("src/main/resources/discountCards.csv");

        float discount = 0;

        for (Map.Entry<Integer, Integer> entry : productsMap.entrySet()) {
            float totalProductPrice = getProductTotalPrice(productsMap, entry.getKey());
            Product product = allProducts.get(entry.getKey() - 1);

            if (entry.getValue() >= 5 && product.isWholesaleProduct()) {
                discount = totalProductPrice / 10;
                totalDiscount += discount;
            } else {
                discount = totalProductPrice / 100 * DiscountCard.getDiscountCardDiscountAmountByDiscountCardNumber(discountCardNumber);
                totalDiscount += discount;
            }
            receipt.add(new String[]{
                    entry.getValue().toString(),
                    product.getDescription(),
                    priceDF.format(product.getPrice()) + "$",
                    priceDF.format(discount) + "$",
                    priceDF.format(totalProductPrice) + "$",
            });
        }
    }

    private void addDiscountCardInfoHeader(List<String[]> receipt) {
        receipt.add(new String[]{"DISCOUNT CARD", "DISCOUNT PERCENTAGE"});
    }

    private void addDiscountCardInfo(List<String[]> receipt, String discountCardNumber) throws IOException {
        receipt.add(new String[]{discountCardNumber, discountDF.format(DiscountCard.getDiscountCardDiscountAmountByDiscountCardNumber(discountCardNumber)) + "%"});
    }

    private void addOverallInfoHeader(List<String[]> receipt) {
        receipt.add(new String[]{"TOTAL PRICE", "TOTAL DISCOUNT", "TOTAL WITH DISCOUNT"});
    }

    private void addOverallInfo(List<String[]> receipt, Map<Integer, Integer> productsMap) throws IOException {
        float totalPrice = getReceiptTotalPrice(productsMap);

        receipt.add(new String[]{priceDF.format(totalPrice) + "$",
                priceDF.format(totalDiscount) + "$",
                priceDF.format(totalPrice - totalDiscount) + "$"
        });
    }

    public float getProductTotalPrice(Map<Integer, Integer> productsMap, int productId) throws IOException {
        float totalProductPrice = 0;
        List<Product> allProducts = CSVReader.readProductsFromCSV("src/main/resources/products.csv");
        for (Map.Entry<Integer, Integer> entry : productsMap.entrySet()) {
            if (productId == entry.getKey()) {
                Product product = allProducts.get(entry.getKey() - 1);
                totalProductPrice += product.getPrice() * entry.getValue();
            }
        }
        return totalProductPrice;
    }

    private float getReceiptTotalPrice(Map<Integer, Integer> productsMap) throws IOException {
        float totalReceiptPrice = 0;
        List<Product> allProducts = CSVReader.readProductsFromCSV("src/main/resources/products.csv");
        for (Map.Entry<Integer, Integer> entry : productsMap.entrySet()) {
            Product product = allProducts.get(entry.getKey() - 1);

            totalReceiptPrice += product.getPrice() * entry.getValue();
        }
        return totalReceiptPrice;
    }

    private boolean checkProductsAvailability(Map<Integer, Integer> productsMap) throws IOException {
        List<Product> allProducts = CSVReader.readProductsFromCSV("src/main/resources/products.csv");
        try {
            for (Map.Entry<Integer, Integer> entry : productsMap.entrySet()) {
                allProducts.get(entry.getKey() - 1);
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private void validate(Map<Integer, Integer> productsMap, float debitCardValue) throws IOException {
        if (!checkProductsAvailability(productsMap)) {
            throw new IllegalArgumentException("BAD REQUEST");
        }
        if (getReceiptTotalPrice(productsMap) > debitCardValue) {
            throw new NotEnoughMoneyException("NOT ENOUGH MONEY");
        }
    }

    public List<String[]> generateReceipt(Map<Integer, Integer> productsMap, float debitCardValue, String discountCardNumber) throws IOException {
        totalDiscount = 0;
        validate(productsMap, debitCardValue);
        List<String[]> receipt = new ArrayList<>();
        addDateTimeHeader(receipt);
        addDateTime(receipt);
        addBlankSpace(receipt);
        addProductInfoHeader(receipt);
        addProductsInfo(receipt, productsMap, discountCardNumber);
        addBlankSpace(receipt);
        if (discountCardNumber != null) {
            addDiscountCardInfoHeader(receipt);
            addDiscountCardInfo(receipt, discountCardNumber);
            addBlankSpace(receipt);
        }
        addOverallInfoHeader(receipt);
        addOverallInfo(receipt, productsMap);

        return receipt;
    }

}