/*
 * GNU GPL v3 License
 *
 * Copyright 2015 AboutHydrology (Riccardo Rigon)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.wordpress.growworkinghard.riverNe3.composite.key;

import net.jcip.annotations.GuardedBy;

public class Key {

    @GuardedBy("this") private String hexKey;

    public Key (final double doubleKey) {
 
        if (this.hexKey == null) {
            synchronized(this) {
                if (this.hexKey == null) {
                    validateDoubleKey(doubleKey);
                    this.hexKey = decimalToHex(doubleKey);
                }
            }
        }

    }

    public Key (final String hexKey) {

        if (this.hexKey == null) {
            synchronized(this) {
                if (this.hexKey == null) {
                    validateStringKey(hexKey);
                    this.hexKey = hexKey;
                }
            }
        }

    }

    public Key (final Key key) {

        if (this.hexKey == null) {
            synchronized(this) {
                if (this.hexKey == null) {
                    validateKey(key);
                    this.hexKey = key.getString();
                }
            }
        }

    }

    public synchronized String getString() {
        return hexKey;
    }

    public synchronized Double getDouble() {
        return hexToDecimal();
    }

    private String decimalToHex(double d) {

        String digits = "0123456789abcdef";
        if (d == 0.0) return "0";
        String hex = "";
        while (Math.floor(d) > 0) {
            int digit = (int) d % 16;
            hex = digits.charAt(digit) + hex;
            d = d / 16;
        }
        return hex;

    }

    /**
     * @brief from
     * http://introcs.cs.princeton.edu/java/31datatype/Hex2Decimal.java.html
     *
     * @return
     */
    private double hexToDecimal() {

        String digits = "0123456789ABCDEF";
        String tmpHex = hexKey.toUpperCase();
        long val = 0;

        for (int i = 0; i < tmpHex.length(); i++) {
            char c = tmpHex.charAt(i);
            int d = digits.indexOf(c);
            val = 16*val + d;
        }

        return val;

    }

    private void validateDoubleKey(final double doubleKey) {

        if (doubleKey < 0)
            throw new IllegalArgumentException("Key less than 0 is not accepted");

    }

    private void validateStringKey(final String hexKey) {

        String digits = "0123456789ABCDEF";
        String tmpHexKey = hexKey.toUpperCase();

        for (int i = 0; i < tmpHexKey.length(); i++) {
            char c = tmpHexKey.charAt(i);
            int d = digits.indexOf(c);
            if (d < 0)
                throw new IllegalArgumentException("String '" + hexKey + "' cannot be converted in hexadecimal format");
        }

    }

    private void validateKey(final Key key) {

        if (key == null)
            throw new NullPointerException("The input key is null");

    }

}
