package ru.clevertec.check;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface ReceiptGenerator {

    void addBlankSpace(List<String[]> receipt);

    void addDateTimeHeader(List<String[]> receipt);

    void addDateTime(List<String[]> receipt);

    void addProductInfoHeader(List<String[]> receipt);

    void addProductsInfo(List<String[]> receipt, Map<Integer, Integer> productsMap, String discountCardNumber) throws IOException;

    void addDiscountCardInfoHeader(List<String[]> receipt);

    void addDiscountCardInfo(List<String[]> receipt, String discountCardNumber) throws IOException;

    void addOverallInfoHeader(List<String[]> receipt);

    void addOverallInfo(List<String[]> receipt, Map<Integer, Integer> productsMap) throws IOException;

    float getProductTotalPrice(Map<Integer, Integer> productsMap, int productId) throws IOException;

    float getReceiptTotalPrice(Map<Integer, Integer> productsMap) throws IOException;

    boolean checkProductsAvailability(Map<Integer, Integer> productsMap) throws IOException;

    void validate(Map<Integer, Integer> productsMap, float debitCardValue) throws IOException;

    List<String[]> generateReceipt(Map<Integer, Integer> productsMap, float debitCardValue, String discountCardNumber, String pathToFile) throws IOException;
}
