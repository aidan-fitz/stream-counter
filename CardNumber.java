import java.math.BigInteger;

public class CardNumber {
    public final BigInteger issuerID;
    public final BigInteger accountID;
    public final int checkDigit;

    public CardNumber(BigInteger issuerID, BigInteger accountID) {
        this.issuerID = issuerID;
        this.accountID = accountID;
        this.checkDigit = makeCheckDigit(concat(issuerID, accountID));
    }

    public CardNumber(String digits) {
        BigInteger number = new BigInteger(digits);
        if (verifyCheckDigit(number)) {
            // The first 6 digits are the issuer identification number (IIN)
            this.issuerID = new BigInteger(digits.substring(0, 6));
            // The subsequent digits except the last digit are the individual account identifier
            this.accountID = new BigInteger(digits.substring(6, digits.length() - 1));
            // The last digit is the check digit
            this.checkDigit = Integer.parseInt(number.substring(digits.length() - 1));
        }
        else {
            throw new NumberFormatException("Check digit doesn't check out: " + digits);
        }
    }

    /**
     * Concatenates two BigIntegers.
     */
    private BigInteger concat(BigInteger first, BigInteger second) {
        String digits = first.toString() + second.toString(); // lol
        return new BigInteger(digits);
    }

    /**
     * Computes the check digit of an arbitrary integer using the Luhn algorithm.
     * @param digits The non-check digits we would like to checksum.
     */
    public static int makeCheckDigit(BigInteger digits) {
        digits = digits.abs();
        int sum = 0;
        // Compute the sum of the digits
        while (!digits.equals(BigInteger.ZERO)) {
            // Quotient and remainder
            BigInteger[] qr = digits.divideAndRemainder(BigInteger.TEN);
            // Add the remainder
            sum += qr[1].intValue();
            // Keep the quotient
            digits = qr[0];
        }
        return (sum * 9) % 10;
    }

    /**
     * Verifies an arbitrary integer along with its check digit.
     */
    public static boolean verifyCheckDigit(BigInteger digits) {
        digits = digits.abs();
        int sum = 0;
        boolean evenDigit = false;
        while (!digits.equals(BigInteger.ZERO)) {
            BigInteger[] qr = digits.divideAndRemainder(BigInteger.TEN);
            int remainder = qr[1].intValue();
            // Every second digit moving left, double it and take the sum of the digits
            if (evenDigit) {
                remainder *= 2;
                int remTensDigit = remainder / 10;
                int remOnesDigit = remainder % 10;
                remainder = remTensDigit + remOnesDigit;
            }
            sum += remainder;
            evenDigit = !evenDigit;
            // Keep the quotient
            digits = qr[0];
        }
        return sum % 10 == 0;
    }

    /**
     * Verifies this card number. Should always return true, as {@link CardNumber(String)} would throw
     * an exception if passed an invalid card number.
     */
    public boolean isValidCardNumber() {
        return verifyCheckDigit(new BigInteger(this.toString()));
    }

    @Override
    public String toString() {
        return issuerID.toString() + accountID.toString() + checkDigit;
    }

}