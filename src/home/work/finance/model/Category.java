package home.work.finance.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Category {
    private String userId;
    private String name;
    private double budgetLimit;

    @JsonCreator
    public Category(@JsonProperty("userId") String userId,
                    @JsonProperty("name") String name,
                    @JsonProperty("budgetLimit") double budgetLimit) {
        this.userId = userId;
        this.name = name;
        this.budgetLimit = budgetLimit;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getBudgetLimit() {
        return budgetLimit;
    }

    public void setBudgetLimit(double budgetLimit) {
        this.budgetLimit = budgetLimit;
    }

    public String toString() {
        return "Category{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", budgetLimit=" + budgetLimit +
                '}';
    }
}