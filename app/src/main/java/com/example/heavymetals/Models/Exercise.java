package com.example.heavymetals.Models;

import java.util.ArrayList;
import java.util.List;

public class Exercise {
    private String name;
    private String type;
    private List<Set> sets;

    public Exercise(String name, String type) {
        this.name = name;
        this.type = type;
        this.sets = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public List<Set> getSets() {
        return sets;
    }
}

