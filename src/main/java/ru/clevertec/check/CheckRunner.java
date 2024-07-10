package ru.clevertec.check;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckRunner {

    private static String discountCardNumber;
    private static float debitCardValue;
    private static String pathToFile = null;
    private static String saveToFile = null;

    public static void main(String[] args) {
        try {
            Map<Integer, Integer> productsMap = new HashMap<>();

            parseArguments(args, productsMap);

            System.out.println("\nProducts Map:");
            for (Map.Entry<Integer, Integer> entry : productsMap.entrySet()) {
                System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
            }

//        System.out.println("All products");
//        List<Product> products = CSVReader.readProductsFromCSV("src/main/resources/products.csv");
//        for (Product product : products) {
//            System.out.println("ID: " + product.getId());
//            System.out.println("Description: " + product.getDescription());
//            System.out.println("Price: " + product.getPrice());
//            System.out.println("Quantity in Stock: " + product.getQuantityInStock());
//            System.out.println("Is Wholesale Product: " + product.isWholesaleProduct());
//            System.out.println();
//        }

//        List<DiscountCard> allDiscountCards = CSVReader.readDiscountCardsFromCSV("src/main/resources/discountCards.csv");
//        for (DiscountCard discountCard : allDiscountCards) {
//            System.out.println("ID: " + discountCard.getId());
//            System.out.println("Number: " + discountCard.getNumber());
//            System.out.println("Discount Amount: " + discountCard.getDiscountAmount());
//
//        }
            ReceiptGenerator receiptGenerator = new ReceiptGeneratorImpl();
            if (pathToFile == null || saveToFile == null) throw new IllegalArgumentException("BAD REQUEST");
            CSVWriter.writeToCSV(saveToFile, receiptGenerator.generateReceipt(productsMap, debitCardValue, discountCardNumber, pathToFile));
        } catch (IllegalArgumentException | NotEnoughMoneyException e) {
            CSVWriter.writeError(e.getMessage(), saveToFile);
        } catch (Exception e) {
            CSVWriter.writeError("INTERNAL SERVER ERROR", saveToFile);
        }
    }


    // javac -sourcepath ./src/main/java/ru/clevertec/check/* -d src ./src/main/java/ru/clevertec/check/CheckRunner.java
    // java -cp src ./src/main/java/ru/clevertec/check/CheckRunner.java 3-1 2-5 5-1 discountCard=1111 balanceDebitCard=100
    private static void parseArguments(String[] args, Map<Integer, Integer> productsMap) {
        for (String arg : args) {
            String[] parts = arg.split("=");
            if (parts.length == 2) {
                String key = parts[0];
                String value = parts[1];
                if (key.equals("balanceDebitCard")) {
                    debitCardValue = Float.parseFloat(value);
                } else if (key.equals("discountCard")) {
                    discountCardNumber = value;
                } else if (key.equals("pathToFile")) {
                    pathToFile = value;
                } else if (key.equals("saveToFile")) {
                    saveToFile = value;
                }
            } else {
                String[] productParts = arg.split("-");
                if (productParts.length == 2) {
                    int productId = Integer.parseInt(productParts[0]);
                    int quantity = Integer.parseInt(productParts[1]);
                    if (productsMap.containsKey(productId)) {
                        productsMap.put(productId, productsMap.get(productId) + quantity);
                    } else {
                        productsMap.put(productId, quantity);
                    }
                }
            }
        }
    }
}