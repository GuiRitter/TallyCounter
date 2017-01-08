# TallyCounter
Implements a counter as an array of numbers, like a [mechanical counter](http://en.wikipedia.org/wiki/Tally_counter). Every number can go from zero to `Long.MAX_VALUE`. Can operate in one of three modes: normal, unique numbers and unique combinations. The normal mode works like mechanical counters: actuating it increments the first (rightmost) number and when a number rolls over, the next number increments. For example, the following code

```java
TallyCounter tallyCounter = new TallyCounter(2, Type.NORMAL, 1);
while (!tallyCounter.overflowFlag) {
    System.out.println(tallyCounter.toReadableString());
    tallyCounter.increment();
}
```

produces the following output

```
[0, 0]
[0, 1]
[1, 0]
[1, 1]
```

The unique numbers mode does not allow numbers with the same value. This is useful, for example, to generate all combinations that can be made where every value represents an unique object. For example, the following code

```java
TallyCounter tallyCounter = new TallyCounter(2, Type.UNIQUE_NUMBERS, 1);
while (!tallyCounter.overflowFlag) {
    System.out.println(tallyCounter.toReadableString());
    tallyCounter.increment();
}
```

produces the following output

```
[0, 1]
[1, 0]
```

The unique combination mode does not allow values that appeared together to appear together again. This is useful, for example, to generate all combination of unique values, regardless of order. For example, the following code

```java
TallyCounter tallyCounter = new TallyCounter(2, Type.UNIQUE_COMBINATION, 1);
while (!tallyCounter.overflowFlag) {
    System.out.println(tallyCounter.toReadableString());
    tallyCounter.increment();
}
```

produces the following output

```
[0, 1]
```

This class is also useful to replace a set of nested `for` loops, where every `for` command is replaced by a number, and every value
is the value that would be iterated. For example, the following code

```java
for (String c : new String[]{"a", "b"}) {
    for (String b : new String[]{"0", "1"}) {
        for (String a : new String[]{"&", "|"}) {
            System.out.format("[%s, %s, %s]\n", a, b, c);
        }
    }
}
```

produces the same output as the following code

```java
Map<Long, String> mapA = new HashMap<>();
mapA.put(0l, "&");
mapA.put(1l, "|");
Map<Long, String> mapB = new HashMap<>();
mapB.put(0l, "0");
mapB.put(1l, "1");
Map<Long, String> mapC = new HashMap<>();
mapC.put(0l, "a");
mapC.put(1l, "b");
TallyCounter tallyCounter = new TallyCounter(3, Type.NORMAL, 1);
long array[];
while (!tallyCounter.overflowFlag) {
    array = tallyCounter.getArray();
        System.out.format("[%s, %s, %s]\n",
         mapA.get(array[0]),
         mapB.get(array[1]),
         mapC.get(array[2]));
    tallyCounter.increment();
}
```

which is

```
[&, 0, a]
[|, 0, a]
[&, 1, a]
[|, 1, a]
[&, 0, b]
[|, 0, b]
[&, 1, b]
[|, 1, b]
```

It's more verbose, but the amount of nested loops can be altered programatically, which would require recursive methods otherwise. It makes it easier to implement [brute force algorithms](http://en.wikipedia.org/wiki/Brute-force_search) and to skip combinations according to the modes mentioned above.
