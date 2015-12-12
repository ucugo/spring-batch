package playground.myspringbatch.model;

import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by Ugo on 12/12/2015.
 */
@Component
public class OutputItem {

    private String name;
    private String description;
    private String date;
    private String balance;
    private String price;
    private String interest;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getInterest() {
        return interest;
    }

    public void setInterest(String interest) {
        this.interest = interest;
    }
}
