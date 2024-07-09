package ru.clevertec.check;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVReader {
    public static List<Product> readProductsFromCSV(String csvFilePath) throws IOException {
        List<Product> products = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            br.readLine(); // Пропуск заголовка

            while ((line = br.readLine()) != null) {
                String[] fields = line.split(";");
                if (fields.length == 5) {
                    int id = Integer.parseInt(fields[0]);
                    String description = fields[1].trim();
                    float price = Float.parseFloat(fields[2]);
                    int quantityInStock = Integer.parseInt(fields[3]);
                    boolean isWholesaleProduct = Boolean.parseBoolean(fields[4]);

                    Product product = new Product(id, description, price, quantityInStock, isWholesaleProduct);
                    products.add(product);
                }
            }
        }
        return products;
    }

    public static List<DiscountCard> readDiscountCardsFromCSV(String csvFilePath) throws IOException {
        List<DiscountCard> discountCards = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            br.readLine(); // Пропуск заголовка

            while ((line = br.readLine()) != null) {
                String[] fields = line.split(";");
                if (fields.length == 3) {

                    int id = Integer.parseInt(fields[0]);
                    String cardNumber = fields[1].trim();
                    float discountAmount = Float.parseFloat(fields[2]);

                    DiscountCard discountCard = new DiscountCard(id, cardNumber, discountAmount);
                    discountCards.add(discountCard);
                }
            }
        }
        return discountCards;
    }
}
