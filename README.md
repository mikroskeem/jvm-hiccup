# jvm-hiccup

JVM GC hiccup metering utility

Based on:
- [jHiccup]
- [jvm-hiccup-meter]

## License
See [jHiccup]'s

## Use it in your project

### Example
```java
import java.util.function.LongConsumer;
import eu.mikroskeem.jvmhiccup.HiccupMeterThread;

public final class MyApp {
    private static HiccupMeterThread hiccupMeter;

    public static void main(String... args) {
        LongConsumer consumer = lastHiccupNs -> {
            // Report to Prometheus etc.
        };
        hiccupMeter = new HiccupMeterThread(consumer);

        // Start your business logic here
        // TODO
        hiccupMeter.start();

        // Wait until things shut down
        // TODO
        hiccupMeter.interrupt();
    }
}
```

### Gradle/Maven

Gradle:
```kotlin
val jvmHiccupVersion = "1.0.0"

repositories {
    maven("https://repo.wut.ee/repository/mikroskeem-repo")
}

dependencies {
    implementation("eu.mikroskeem:jvm-hiccup:$jvmHiccupVersion")
}
```

Maven:
```xml
<repositories>
    <repository>
        <id>mikroskeem-repo</id>
        <url>https://repo.wut.ee/repository/mikroskeem-repo</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>eu.mikroskeem</groupId>
        <artifactId>jvm-hiccup</artifactId>
        <version>1.0.0</version>
        <scope>compile</scope>
    </dependency>
</dependencies>
```

[jHiccup]: https://github.com/giltene/jHiccup
[jvm-hiccup-meter]: https://github.com/clojure-goes-fast/jvm-hiccup-meter