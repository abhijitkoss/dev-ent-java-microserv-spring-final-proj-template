package com.hinkmond.finalproj;

public class Validator {
    public static boolean isDateValid(int year, int month, int day) {
        if ((month < 1) || (month > 12) || (day < 1))
            return false;
        switch (month) {
            case 1:         // Jan
            case 3:         // March
            case 5:         // May
            case 7:         // July
            case 8:         // Aug
            case 10:        // Oct
            case 12:        // Dec
                if (day > 31)
                    return false;
                break;
            case 2:         // Feb
                if ( ((year % 4 == 0) && (year % 100!= 0))
                        || (year%400 == 0)) {
                    // Leap year
                    if (day > 29)
                        return false;
                } else {
                    // Regular year
                    if (day > 28)
                        return false;
                }
                break;
            default:        // Other months - Apr, Jun, Sept, Nov
                if (day > 30)
                    return false;

        }
        return true;
    }
}
