package ru.clevertec.check;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReceiptGenerator {

    static DecimalFormat priceDF = new DecimalFormat("0.00");
    static DecimalFormat discountDF = new DecimalFormat("0.#");
    private static float totalDiscount;

    private static void addBlankSpace(List<String[]> receipt) {
        receipt.add(new String[]{""});
    }

    private static void addDateTimeHeader(List<String[]> receipt) {
        receipt.add(new String[]{"Date", "Time"});
    }

    private static void addDateTime(List<String[]> receipt) {
        receipt.add(new String[]{
                DateTimeUtils.getDateTimeString().split(" ")[0],
                DateTimeUtils.getDateTimeString().split(" ")[1]
        });
    }

    private static void addProductInfoHeader(List<String[]> receipt) {
        receipt.add(new String[]{"QTY", "DESCRIPTION", "PRICE", "DISCOUNT", "TOTAL"});
    }

    private static void addProductsInfo(List<String[]> receipt, Map<Integer, Integer> productsMap, String discountCardNumber) {
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

    private static void addDiscountCardInfoHeader(List<String[]> receipt) {
        receipt.add(new String[]{"DISCOUNT CARD", "DISCOUNT PERCENTAGE"});
    }

    private static void addDiscountCardInfo(List<String[]> receipt, String discountCardNumber) {
        receipt.add(new String[]{discountCardNumber, discountDF.format(DiscountCard.getDiscountCardDiscountAmountByDiscountCardNumber(discountCardNumber)) + "%"});
    }

    private static void addOverallInfoHeader(List<String[]> receipt) {
        receipt.add(new String[]{"TOTAL PRICE", "TOTAL DISCOUNT", "TOTAL WITH DISCOUNT"});
    }

    private static void addOverallInfo(List<String[]> receipt, Map<Integer, Integer> productsMap) {
        float totalPrice = getReceiptTotalPrice(productsMap);

        receipt.add(new String[]{priceDF.format(totalPrice) + "$",
                priceDF.format(totalDiscount) + "$",
                priceDF.format(totalPrice - totalDiscount) + "$"
        });
    }

    public static float getProductTotalPrice(Map<Integer, Integer> productsMap, int productId) {
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

    private static float getReceiptTotalPrice(Map<Integer, Integer> productsMap) {
        float totalReceiptPrice = 0;
        List<Product> allProducts = CSVReader.readProductsFromCSV("src/main/resources/products.csv");
        for (Map.Entry<Integer, Integer> entry : productsMap.entrySet()) {
            Product product = allProducts.get(entry.getKey() - 1);

            totalReceiptPrice += product.getPrice() * entry.getValue();
        }
        return totalReceiptPrice;
    }

    private static boolean checkProductsAvailability(Map<Integer, Integer> productsMap) {
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
    private void validate() {

    }
    public static List<String[]> generateReceipt(Map<Integer, Integer> productsMap, float debitCardValue, String discountCardNumber) {
        totalDiscount = 0;
        List<String[]> receipt = new ArrayList<>();
        if (!checkProductsAvailability(productsMap)) {
            System.out.println("BAD REQUEST");
            receipt.add(new String[]{"ERROR"});
            receipt.add(new String[]{"BAD REQUEST"});
            return receipt;
        }
        if (getReceiptTotalPrice(productsMap) > debitCardValue) {
            System.out.println("not enough money");
            receipt.add(new String[]{"ERROR"});
            receipt.add(new String[]{"NOT ENOUGH MONEY"});
            return receipt;
        }
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