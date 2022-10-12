# Controller Remote Access Protocol (CRAP)

Kotlin multiplatform client library used to do remote procedure calls (RPC) to microcontrollers.

## Features
<img alt="Arduino logo" src="src/site/arduino-boobs.jpg" style="float:right"/>

- Arduino Uno support
    - USB serial communication
        - Minimal bytes are transferred
        - Fast sketch flow control. Uses array of lambdas instead of if-else-switch-cases.
        - Sketch uses 45 % of storage and 37 % of memory on Uno.
        - [jSerialComm](https://github.com/Fazecast/jSerialComm) JVM implementation.
- Arduino Relay Shield support
- 1-wire support with following devices:
    - Dallas Digital Temperature Sensor e.g. DS18B20
- Basic actuators: switch, relay

## Examples

Examples are located at `src/jvmTest/kotlin/arduino` directory.

### Blink

```kotlin
fun main() = Uno(1)                    // Connects Arduino Uno on 2nd serial.
        .setup { led.output() }        // Sets built-in LED pin to output mode.
        .loop { led.high = !led.high } // Changes its state on every second (by default).
```

### Switch

```kotlin
fun main() = Uno("COM3").setup {          // Uno is on COM3.
    switch(d3).onChange {                 // Attaches switch to pin 3.
        println("Switch changed to $it.") // Prints every state changes.
    }
}()                                       // Start's the loop
```

### Dallas Temperature Sensor
```kotlin
fun main() = Uno().setup {                         // Connects default port.
    dallasTemperature().onChange {                 // Set up 1-wire on first free pin (D2)
            old, new -> println("$old -> $new °C") // Prints when temperature changes.
    }
}()
```

### Relays
```kotlin
fun main() = object : Uno(1) {                       // Uno can also be extended.
    val relays = relayShield().relays                // Attaches 4 relay shield.
    override fun loop() { relays.random().change() } // Change state on random relay.
}()                                                  // Start's the loop
```

## Roadmap / Limitations

- Better logging.
- Packages aren't published to Maven Central yet.
- Interrupt Service Routines can't be used, because they leave the connection in asynchronous state (now) and procedure pool is quite full.
- Interrupts can't be disabled because they don't compile inside lambda.

## How to use

1. Clone this repository using `git clone https://github.com/jpink/crap.git`.
2. Publish it to your local Maven using `./gradlew publishToMavenLocal`.
3. Edit your project's `build.gradle.kts` file:
   1. Add `mavenLocal()` to your repositories.
   2. Add `implementation("fi.papinkivi:crap:2.1.0")` to your `main` or `jvmMain` dependencies.
4. Install  [Arduino IDE](https://www.arduino.cc/en/software).
   1. Ensure that [OneWire](https://www.pjrc.com/teensy/td_libs_OneWire.html) and [DallasTemperature](https://github.com/milesburton/Arduino-Temperature-Control-Library) libraries are installed.
   2. Open `src/assembly/Uno3/Uno3.ino` sketch and upload it to your microcontroller.