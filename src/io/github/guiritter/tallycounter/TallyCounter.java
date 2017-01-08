package io.github.guiritter.tallycounter;

import java.util.Arrays;

/**
 * Implements a counter as an array of numbers, like a
 * <a href="http://en.wikipedia.org/wiki/Tally_counter" target="_top">
 * mechanical counter</a>.
 * Every number can go from zero to {@link java.lang.Long#MAX_VALUE}.
 * Can operate in one of three modes: normal, unique numbers
 * and unique combinations. The normal mode works like mechanical counters:
 * actuating it increments the first (rightmost) number and when a number
 * rolls over, the next number increments. For example, the following code
 * <p><blockquote><pre> TallyCounter tallyCounter = new TallyCounter(2, Type.NORMAL, 1);
 * while (!tallyCounter.overflowFlag) {
 *     System.out.println(tallyCounter.toReadableString());
 *     tallyCounter.increment();
 * }</pre></blockquote>produces the following output<p><blockquote><pre> [0, 0]
 * [0, 1]
 * [1, 0]
 * [1, 1]</pre></blockquote>The unique numbers mode does not allow numbers
 * with the same value. This is useful, for example, to generate
 * all combinations that can be made where every value
 * represents an unique object. For example, the following code
 * <p><blockquote><pre> TallyCounter tallyCounter = new TallyCounter(2, Type.UNIQUE_NUMBERS, 1);
 * while (!tallyCounter.overflowFlag) {
 *     System.out.println(tallyCounter.toReadableString());
 *     tallyCounter.increment();
 * }</pre></blockquote>produces the following output<p><blockquote><pre> [0, 1]
 * [1, 0]</pre></blockquote>The unique combination mode does not allow values
 * that appeared together to appear together again. This is useful, for example,
 * to generate all combination of unique values, regardless of order.
 * For example, the following code
 * <p><blockquote><pre> TallyCounter tallyCounter = new TallyCounter(2, Type.UNIQUE_COMBINATION, 1);
 * while (!tallyCounter.overflowFlag) {
 *     System.out.println(tallyCounter.toReadableString());
 *     tallyCounter.increment();
 * }</pre></blockquote>produces the following output<p><blockquote><pre> [0, 1]</pre></blockquote>
 * This class is also useful to replace a set of nested <code>for</code> loops,
 * where every <code>for</code> command is replaced by a number, and every value
 * is the value that would be iterated. For example, the following code<p><blockquote><pre> for (String c : new String[]{"a", "b"}) {
 *     for (String b : new String[]{"0", "1"}) {
 *         for (String a : new String[]{"&", "|"}) {
 *             System.out.format("[%s, %s, %s]\n", a, b, c);
 *         }
 *     }
 * }</pre></blockquote>produces the same output as the following code<p><blockquote><pre> Map&lt;Long, String> mapA = new HashMap&lt;>();
 * mapA.put(0l, "&");
 * mapA.put(1l, "|");
 * Map&lt;Long, String> mapB = new HashMap&lt;>();
 * mapB.put(0l, "0");
 * mapB.put(1l, "1");
 * Map&lt;Long, String> mapC = new HashMap&lt;>();
 * mapC.put(0l, "a");
 * mapC.put(1l, "b");
 * TallyCounter tallyCounter = new TallyCounter(3, Type.NORMAL, 1);
 * long array[];
 * while (!tallyCounter.overflowFlag) {
 *     array = tallyCounter.getArray();
 *         System.out.format("[%s, %s, %s]\n",
 *          mapA.get(array[0]),
 *          mapB.get(array[1]),
 *          mapC.get(array[2]));
 *     tallyCounter.increment();
 * }</pre></blockquote>which is<p><blockquote><pre> [&, 0, a]
 * [|, 0, a]
 * [&, 1, a]
 * [|, 1, a]
 * [&, 0, b]
 * [|, 0, b]
 * [&, 1, b]
 * [|, 1, b]</pre></blockquote>It's more verbose, but the amount
 * of nested loops can be altered programatically, which would require
 * recursive methods otherwise. It makes it easier to implement
 * <a href="http://en.wikipedia.org/wiki/Brute-force_search" target="_top">
 * brute force algorithms</a>.
 * @author guir
 */
public final class TallyCounter {

    /**
     * Holds the numbers (least significant number first).
     */
    private long array[] = {};

    /**
     * Holds the numbers in human readable order (as they appear
     * on mechanical tally counters, most significant number first).
     */
    private long arrayReadable[] = {};

    /**
     * Loop control.
     */
    private int i = 0;

    /**
     * Loop control.
     */
    private int j = 0;

    /**
     * Holds the maximum values for each number. The values must be positive
     * integers. The values must be equal to be used in the non normal modes.
     */
    public final long maximumValues[];

    /**
     * Indicates whether an overflow occurred.
     */
    public boolean overflowFlag = false;

    /**
     * Defines the type of counter.
     */
    private final Type type;

    /**
     * Defines the three different ways the counter can behave.
     */
    public enum Type {

        /**
         * Normal counter. All combinations are accepted.
         */
        NORMAL,

        /**
         * Does not allow repeated combination of numbers.
         * For example, a two numbers counter can equal 01, but not 10.
         */
        UNIQUE_COMBINATION,

        /**
         * Does not allow numbers with the same value.
         */
        UNIQUE_NUMBERS
    };

    /**
     * Returns a copy of the array backing this tally counter
     * (least significant number first).
     * @return an array containing the numbers
     * with the least significant number first
     */
    public long[] getArray () {
        return array;
    }

    /**
     * Returns a copy of the array backing this tally counter
     * in human readable order (as they appear on mechanical tally counters,
     * most significant number first).
     * @return an array containing the numbers
     * with the most significant number first
     */
    public long[] getReadableArray() {
        for (i = 0; i < array.length; i++) {
            arrayReadable[array.length - i - 1] = array[i];
        }
        return arrayReadable;
    }

    /**
     * Returns whether the counter's combination is a repeated one.
     * For example: 01 is the first occurrence; 10 is the repeated one.
     * @return <code>true</code> if the counter's combination is repeated
     * <code>false</code> if it's the first
     * occurrence of the counter's combination
     */
    public boolean haveRepeatedCombination () {
        for (i = 0; i < array.length - 1; i++) {
            if (array[i] <= array[i + 1]) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns whether the counter's combination
     * contains numbers with the same value.
     * @return <code>true</code> if the counter's
     * combination contains numbers with the same value;
     * <code>false</code> if all numbers are unique
     */
    public boolean haveRepeatedDigits () {
        for (j = 0; j < array.length - 1; j++) {
            for (i = j + 1; i < array.length; i++) {
                if (array[i] == array[j]) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Repeating code inside <code>increment()</code>.
     */
    private void incrementLoop () {
        array[0]++;
        for (i = 0; i < array.length; i++) {
            if (array[i] == maximumValues[i] + 1) {
                if (i == array.length - 1) {
                    reset(true);
                    break;
                } else {
                    array[i] = 0;
                    array[i + 1]++;
                }
            }
        }
    }

    /**
     * Increments the counter.
     * If the counter is not normal,
     * checks to see if the combination complies with the type set;
     * if it's not, keeps incrementing until it does.
     * If the counter overflows, it is reset.
     */
    public void increment () {
        switch (type) {
            case NORMAL:
                incrementLoop();
                break;
            case UNIQUE_COMBINATION:
                do {
                    incrementLoop();
                } while (haveRepeatedCombination());
                break;
            case UNIQUE_NUMBERS:
                do {
                    incrementLoop();
                } while (haveRepeatedDigits());
                break;
        }
    }

    /**
     * If the counter is normal, set all numbers to zero; else,
     * set the last number to zero, the previous number to one, and so on.
     * Sets the overflow flag value.
     * @param overflow new overflow flag value
     */
    public void reset (boolean overflow) {
        if (type == Type.NORMAL) {
            for (i = 0; i < array.length; i++) {
                array[i] = 0;
            }
        } else {
            for (i = 0; i < array.length; i++) {
                array[i] = array.length - i - 1;
            }
        }
        overflowFlag = overflow;
    }

    /**
     * Returns a string representation of the tally counter, with the numbers
     * in human readable order (as they appear on mechanical tally counters,
     * with the most significant number first).
     * @return a string representation of the numbers
     * with the most significant number first
     */
    public String toReadableString() {
        return Arrays.toString(getReadableArray());
    }

    /**
     * Returns a string representation of the tally counter,
     * with the least significant number first.
     * @return a string representation of the numbers
     * with the least significant number first
     */
    @Override
    public String toString() {
        return Arrays.toString(array);
    }

    /**
     * Constructs a new counter. Uses an array of maximum values for each number
     * and can only operate in normal mode. Each maximum value must be at least
     * one.
     * @param maximumValues the maximum values for each number
     */
    public TallyCounter (long maximumValues[]) {
        if ((maximumValues == null) || (maximumValues.length < 1)) {
            this.maximumValues = new long[] {1};
        } else {
            this.maximumValues = new long[maximumValues.length];
            for (i = 0; i < maximumValues.length; i++) {
                if (maximumValues[i] < 1) {
                    this.maximumValues[i] = 1;
                } else {
                    this.maximumValues[i] = maximumValues[i];
                }
            }
        }
        type = Type.NORMAL;
        array = new long[this.maximumValues.length];
        arrayReadable = new long[this.maximumValues.length];
        for (i = 0; i < this.maximumValues.length; i++) {
            array[i] = 0;
        }
        overflowFlag = false;
    }

    /**
     * Constructs a new counter. The numbers' maximum value must be at least
     * one. The amount must be at least one. For non normal modes, the amount
     * must be less or equal than the numbers' maximum value plus one.
     * Initializes the numbers to zero, or the first unique combination.
     * @param amount the amount of numbers
     * @param type the counter's behaviour
     * @param maximumValue the maximum value for each number
     */
    public TallyCounter (int amount, Type type, long maximumValue) {
        if (maximumValue < 1) {
            maximumValue = 1;
        }
        if (type == Type.NORMAL) {
            if (amount > 0) {
                array = new long[amount];
            } else {
                array = new long[1];
            }
        } else {
            if (amount <= maximumValue + 1) {
                array = new long[amount];
            } else {
                if ((maximumValue + 1) > Integer.MAX_VALUE) {
                    array = new long[Integer.MAX_VALUE];
                } else {
                    array = new long[(int) (maximumValue + 1)];
                }
            }
        }
        arrayReadable = new long[array.length];
        maximumValues = new long[array.length];
        this.type = type;
        if (type == Type.NORMAL) {
            for (i = 0; i < array.length; i++) {
                array[i] = 0;
            }
        } else {
            for (i = 0; i < array.length; i++) {
                array[i] = array.length - i - 1;
            }
        }
        for (i = 0; i < array.length; i++) {
            maximumValues[i] = maximumValue;
        }
        overflowFlag = false;
    }
}
