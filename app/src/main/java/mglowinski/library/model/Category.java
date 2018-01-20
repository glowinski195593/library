package mglowinski.library.model;

import java.io.Serializable;

/**
 * Created by mck00 on 19.01.2018.
 */

public enum Category implements Serializable {
    Dla_dzieci("Dla dzieci"),
    Dla_młodzieży("Dla młodzieży"),
    Fantasy("Fantasy"),
    Horror("Horror"),
    Klasyka("Klasyka"),
    Obyczajowe("Obyczajowe"),
    Powieść("Powieść"),
    Romans("Romans"),
    Tragedia("Tragedia");

    private String displayName;

    Category(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }
}
