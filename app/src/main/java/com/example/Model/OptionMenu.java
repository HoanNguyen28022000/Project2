package com.example.Model;

public class OptionMenu {
    private int icResource;
    private String optionName;

    public OptionMenu(int icResource, String optionName) {
        this.icResource = icResource;
        this.optionName = optionName;
    }

    public int getIcResource() {
        return icResource;
    }

    public void setIcResource(int icResource) {
        this.icResource = icResource;
    }

    public String getOptionName() {
        return optionName;
    }

    public void setOptionName(String optionName) {
        this.optionName = optionName;
    }
}
