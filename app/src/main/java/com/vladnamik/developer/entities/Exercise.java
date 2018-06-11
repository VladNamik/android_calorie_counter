package com.vladnamik.developer.entities;


public class Exercise {
    private String name;
    private int timeCoeff;
    private int quantityCoeff;

    public Exercise() {
        this.name = "";
        this.timeCoeff = 0;
        this.quantityCoeff = 0;
    }

    public Exercise(String name, int timeCoeff, int quantityCoeff) {
        this.name = name;
        this.timeCoeff = timeCoeff;
        this.quantityCoeff = quantityCoeff;
    }

    public String getName() {
        return name;
    }

    public int parseCaloriesFromFormula(int weight, int time, int quantity) {
        return weight * timeCoeff * time + weight * quantityCoeff * quantity;
    }

    public int getTimeCoefficient() {
        return timeCoeff;
    }

    public int getQuantityCoefficient() {
        return quantityCoeff;
    }

    public void setTimeCoefficient(int timeCoeff) {
        this.timeCoeff = timeCoeff;
    }

    public void setQuantityCoefficient(int quantityCoeff) {
        this.quantityCoeff = quantityCoeff;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Exercise)
            return name.equals(((Exercise) o).getName());
        else
            return false;
    }
}
