package com.example.mmp.model;

 

public enum ReportType {

    BLOOD("Blood Report", 500),
    ECG("ECG Report", 800),
    ULTRASOUND("Ultrasound Report", 1200);

    private final String label;
    private final int defaultFee;

    ReportType(String label, int defaultFee) {
        this.label = label;
        this.defaultFee = defaultFee;
    }

    public String getLabel() {
        return label;
    }

    public int getDefaultFee() {
        return defaultFee;
    }
}
