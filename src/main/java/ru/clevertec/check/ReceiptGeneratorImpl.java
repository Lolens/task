package ru.clevertec.check;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReceiptGeneratorImpl implements ReceiptGenerator {
    DecimalFormat PRICE_DF = new DecimalFormat("0.00");
    DecimalFormat DISCOUNT_DF = new DecimalFormat("0.#");
    private float totalDiscount;

    @Override
    public void addBlankSpace(List<String[]> receipt) {
        receipt.add(new String[]{""});
    }

    @Override
    public void addDateTimeHeader(List<String[]> receipt) {
        receipt.add(new String[]{"Date", "Time"});
    }

    @Override
    public void addDateTime(List<String[]> receipt) {
        receipt.add(new String[]{
                DateTimeUtils.getDateTimeString().split(" ")[0],
                DateTimeUtils.getDateTimeString().split(" ")[1]
        });
    }

    @Override
    public void addProductInfoHeader(List<String[]> receipt) {
        receipt.add(new String[]{"QTY", "DESCRIPTION", "PRICE", "DISCOUNT", "TOTAL"});
    }

    @Override
    public void addProductsInfo(List<String[]> receipt, Map<Integer, Integer> productsMap, String discountCardNumber) throws IOException {
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
                    PRICE_DF.format(product.getPrice()) + "$",
                    PRICE_DF.format(discount) + "$",
                    PRICE_DF.format(totalProductPrice) + "$",
            });
        }
    }

    @Override
    public void addDiscountCardInfoHeader(List<String[]> receipt) {
        receipt.add(new String[]{"DISCOUNT CARD", "DISCOUNT PERCENTAGE"});
    }

    @Override
    public void addDiscountCardInfo(List<String[]> receipt, String discountCardNumber) throws IOException {
        receipt.add(new String[]{discountCardNumber, DISCOUNT_DF.format(DiscountCard.getDiscountCardDiscountAmountByDiscountCardNumber(discountCardNumber)) + "%"});
    }

    @Override
    public void addOverallInfoHeader(List<String[]> receipt) {
        receipt.add(new String[]{"TOTAL PRICE", "TOTAL DISCOUNT", "TOTAL WITH DISCOUNT"});
    }

    @Override
    public void addOverallInfo(List<String[]> receipt, Map<Integer, Integer> productsMap) throws IOException {
        float totalPrice = getReceiptTotalPrice(productsMap);

        receipt.add(new String[]{PRICE_DF.format(totalPrice) + "$",
                PRICE_DF.format(totalDiscount) + "$",
                PRICE_DF.format(totalPrice - totalDiscount) + "$"
        });
    }

    @Override
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

    @Override
    public float getReceiptTotalPrice(Map<Integer, Integer> productsMap) throws IOException {
        float totalReceiptPrice = 0;
        List<Product> allProducts = CSVReader.readProductsFromCSV("src/main/resources/products.csv");
        for (Map.Entry<Integer, Integer> entry : productsMap.entrySet()) {
            Product product = allProducts.get(entry.getKey() - 1);

            totalReceiptPrice += product.getPrice() * entry.getValue();
        }
        return totalReceiptPrice;
    }

    @Override
    public boolean checkProductsAvailability(Map<Integer, Integer> productsMap) throws IOException {
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

    @Override
    public void validate(Map<Integer, Integer> productsMap, float debitCardValue) throws IOException {
        if (!checkProductsAvailability(productsMap)) {
            throw new IllegalArgumentException("BAD REQUEST");
        }
        if (getReceiptTotalPrice(productsMap) > debitCardValue) {
            throw new NotEnoughMoneyException("NOT ENOUGH MONEY");
        }
    }

    @Override
    public List<String[]> generateReceipt(Map<Integer, Integer> productsMap, float debitCardValue, String discountCardNumber) throws IOException {
        totalDiscount = 0;
        validate(productsMap, debitCardValue);
        List<String[]> receipt = new ArrayList<>();
        ReceiptGeneratorImpl receiptGenerator = new ReceiptGeneratorImpl();
        receiptGenerator.addDateTimeHeader(receipt);
        receiptGenerator.addDateTime(receipt);
        receiptGenerator.addBlankSpace(receipt);
        receiptGenerator.addProductInfoHeader(receipt);
        addProductsInfo(receipt, productsMap, discountCardNumber);
        receiptGenerator.addBlankSpace(receipt);
        if (discountCardNumber != null) {
            receiptGenerator.addDiscountCardInfoHeader(receipt);
            addDiscountCardInfo(receipt, discountCardNumber);
            receiptGenerator.addBlankSpace(receipt);
        }
        receiptGenerator.addOverallInfoHeader(receipt);
        addOverallInfo(receipt, productsMap);

        return receipt;
    }

}
