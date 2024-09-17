package com.mahim.shopme.utils;

import com.mahim.shopme.setting.CurrencySettingBag;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class CurrencyUtils {

    public static String formatCurrency(float amount, CurrencySettingBag setting) {
        String symbol = setting.getSymbol();
        String symbolPosition = setting.getSymbolPosition();
        String decimalPointType = setting.getDecimalPointType();
        String thousandsPointType = setting.getThousandsPointType();
        int decimalDigits = setting.getDecimalDigits();

        String pattern = symbolPosition.equals("before") ?  symbol : "";
        pattern += "###,###";

        if (decimalDigits > 0) {
            pattern += ".";
            for (int i = 0; i < decimalDigits; i++) {
                pattern += "#";
            }
        }

        pattern += symbolPosition.equals("after") ? symbol : "";
        char thousandsSeparator = "POINT".equals(thousandsPointType) ? '.' : ',';
        char decimalSeparator = "POINT".equals(decimalPointType) ? '.' : ',';

        DecimalFormatSymbols decimalFormatSymbols = DecimalFormatSymbols.getInstance();
        decimalFormatSymbols.setDecimalSeparator(decimalSeparator);
        decimalFormatSymbols.setGroupingSeparator(thousandsSeparator);

        DecimalFormat formatter = new DecimalFormat(pattern, decimalFormatSymbols);
        return formatter.format(amount);
    }
}
