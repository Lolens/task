package ru.clevertec.check;

import java.io.IOException;
import java.util.List;

public class DiscountCard {
    private int id;
    private String number;
    private float discountAmount;

    public DiscountCard(int id, String number, float discountAmount) {
        this.id = id;
        this.number = number;
        this.discountAmount = discountAmount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public float getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(float discountAmount) {
        this.discountAmount = discountAmount;
    }

    public static float getDiscountCardDiscountAmountByDiscountCardNumber(String number) throws IOException {
        List<DiscountCard> allDiscountCards = CSVReader.readDiscountCardsFromCSV("src/main/resources/discountCards.csv");
        DiscountCard discountCard = allDiscountCards.stream()
                .filter(cardNumber -> number.equals(cardNumber.getNumber()))
                .findAny()
                .orElse(null);
        return discountCard != null ? discountCard.getDiscountAmount() : 2; // If found then returns discount amount else return default discount
    }
}

