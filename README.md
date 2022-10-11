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

## Examples

### Blink

```kotlin
fun main() = with (Uno("COM3")) {           // Connect COM3 serial port and expects it to be Arduino Uno.
    led.mode = Mode.Output                  // Call `pinMode(13, OUTPUT)`.
    while (connected) {
        led.high = !led.high                // Change the LED state by calling `digitalWrite(HIGH)` or `LOW`.
        TimeUnit.SECONDS.sleep(1)           // Caller is waiting for a second.
    }
}
```

### Dallas Temperature Sensor
```kotlin
fun main() = with (Uno("COM3")) {
    val device = attachDallasTemperature()  // Set up 1-wire on first available pin (D2) and assumes DS18B20 on index 0.
    var previous = 0f
    while (connected) {
        previous = device.celsius.apply {   // Read temperature using DallasTemperature library about 2 times a second.
            if (previous != this)
                println("Changed to $this °C.")
        }
    }
}
```

### Relays
```kotlin
fun main() = with (Uno()) {                 // Connect default serial port.
  with(attach4RelayShield()) {              // Set up pin modes for 4 Relay Shield.
    while (connected) {
      relays.shuffle()                      // Choose random relay.
      relays.first().change()               // Activate or deactivate it.
      TimeUnit.SECONDS.sleep(1)
    }
  }
}
```

## Roadmap / Limitations

- Packages aren't published to Maven Central yet.
- Interrupt Service Routines can't be used, because they leave the connection in asynchronous state (now) and procedure pool is quite full.
- Interrupts can't be disabled because they don't compile inside lambda.

## How to use

1. Clone this repository using `git clone https://github.com/jpink/crap.git`.
2. Publish it to your local Maven using `./gradlew publishToMavenLocal`.
3. Edit your project's `build.gradle.kts` file:
   1. Add `mavenLocal()` to your repositories.
   2. Add `implementation("fi.papinkivi:crap:1.0.0")` to your `main` or `jvmMain` dependencies.
4. Install  [Arduino IDE](https://www.arduino.cc/en/software).
   1. Ensure that [OneWire](https://www.pjrc.com/teensy/td_libs_OneWire.html) and [DallasTemperature](https://github.com/milesburton/Arduino-Temperature-Control-Library) libraries are installed.
   2. Open `src/assembly/Uno3/Uno3.ino` sketch and upload it to your microcontroller.