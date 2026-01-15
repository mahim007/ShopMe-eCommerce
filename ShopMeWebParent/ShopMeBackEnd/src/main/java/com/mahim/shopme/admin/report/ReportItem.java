package com.mahim.shopme.admin.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportItem {
    private String identifier;
    private float grossSales;
    private float netSales;
    private int  orderCount;
    private int productCount;

    public ReportItem(String identifier) {
        this.identifier = identifier;
    }

    public ReportItem(String identifier, float grossSales, float netSales) {
        this.identifier = identifier;
        this.grossSales = grossSales;
        this.netSales = netSales;
    }

    public ReportItem(String identifier, float grossSales, float netSales, int productCount) {
        this.identifier = identifier;
        this.grossSales = grossSales;
        this.netSales = netSales;
        this.productCount = productCount;
    }

    public void addGrossSales(float amount) {
        this.grossSales += amount;
    }

    public void addNetSales(float amount) {
        this.netSales += amount;
    }

    public void increaseOrdersCount() {
        this.orderCount++;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof ReportItem that)) return false;

        return identifier.equals(that.identifier);
    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }

    public void increaseProductCount(int quantity) {
        this.productCount += quantity;
    }
}
