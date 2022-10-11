// Controller Remote Access Protocol (CRAP) version 1 sketch for Arduino UNO R3

#include <OneWire.h> 
#include <DallasTemperature.h>

int bytesNeeded = 0;        // How many bytes procedure needs to read the arguments.
boolean callable = false;   // Can the procedure be called.
DallasTemperature dallas;   // Dallas digital temperature sensors.
OneWire one;                // 1-wire communication bus.
int procedureIndex = -1;    // The procedure index in lambda array.
long speed = 9600L;         // Serial speed in bauds.

int readInt() {             // Receive integer argument.
  int value = Serial.read() << 8;
  value += Serial.read();
  return value;
}

long readLong() {           // Receive long integer argument.
  byte buffer[4];
  Serial.readBytes(buffer, 4);
  long value = buffer[0] << 24;
  value += buffer[1] << 16;
  value += buffer[2] << 8;
  value += buffer[3];
  return value;
}

void send1(byte value) {    // Transmit one byte back.
  Serial.write(value);
  Serial.flush();
}

void send2(int value) {     // Transmit two bytes back.
  Serial.write(value & 255);
  Serial.write(value >> 8 & 255);
  Serial.flush();
}

void send4(long value) {    // Transmit four bytes back.
  Serial.write(value & 255);
  Serial.write(value >> 8 & 255);
  Serial.write(value >> 16 & 255);
  Serial.write(value >> 24 & 255);
  Serial.flush();
}

void setup() {              // Begin serial connection. Used also for changing the speed.
  Serial.begin(speed);
  while (!Serial);
  Serial.print("Uno3 crap1@");
  Serial.println(speed);
}

void serialEvent() {        // Read procedure and ensure enough bytes in buffer when data is available.
  if (procedureIndex < 0) {
    procedureIndex = Serial.read();
    if (-1 < procedureIndex) {
      if (procedureIndex < 172) bytesNeeded = 0;
      else if (procedureIndex < 180) bytesNeeded = 1;
      else if (procedureIndex < 198) bytesNeeded = 2;
      else if (procedureIndex < 235) bytesNeeded = 4;
      else bytesNeeded = 6;
    }
  }
  if (-1 < procedureIndex && Serial.available() >= bytesNeeded) callable = true;
}

void (*procedures[])() = {                                                                    // index, hex , arguments
  [] { analogReference(DEFAULT)                                                          ; }, //  #  0, 0x00, 0 b
  [] { analogReference(EXTERNAL)                                                         ; }, //  #  1, 0x01, 0 b
  [] { analogReference(INTERNAL)                                                         ; }, //  #  2, 0x02, 0 b
  [] { digitalWrite( 2, HIGH)                                                            ; }, //  #  3, 0x03, 0 b
  [] { digitalWrite( 2, LOW )                                                            ; }, //  #  4, 0x04, 0 b
  [] { digitalWrite( 3, HIGH)                                                            ; }, //  #  5, 0x05, 0 b
  [] { digitalWrite( 3, LOW )                                                            ; }, //  #  6, 0x06, 0 b
  [] { digitalWrite( 4, HIGH)                                                            ; }, //  #  7, 0x07, 0 b
  [] { digitalWrite( 4, LOW )                                                            ; }, //  #  8, 0x08, 0 b
  [] { digitalWrite( 5, HIGH)                                                            ; }, //  #  9, 0x09, 0 b
  [] { digitalWrite( 5, LOW )                                                            ; }, //  # 10, 0x0a, 0 b
  [] { digitalWrite( 6, HIGH)                                                            ; }, //  # 11, 0x0b, 0 b
  [] { digitalWrite( 6, LOW )                                                            ; }, //  # 12, 0x0c, 0 b
  [] { digitalWrite( 7, HIGH)                                                            ; }, //  # 13, 0x0d, 0 b
  [] { digitalWrite( 7, LOW )                                                            ; }, //  # 14, 0x0e, 0 b
  [] { digitalWrite( 8, HIGH)                                                            ; }, //  # 15, 0x0f, 0 b
  [] { digitalWrite( 8, LOW )                                                            ; }, //  # 16, 0x10, 0 b
  [] { digitalWrite( 9, HIGH)                                                            ; }, //  # 17, 0x11, 0 b
  [] { digitalWrite( 9, LOW )                                                            ; }, //  # 18, 0x12, 0 b
  [] { digitalWrite(10, HIGH)                                                            ; }, //  # 19, 0x13, 0 b
  [] { digitalWrite(10, LOW )                                                            ; }, //  # 20, 0x14, 0 b
  [] { digitalWrite(11, HIGH)                                                            ; }, //  # 21, 0x15, 0 b
  [] { digitalWrite(11, LOW )                                                            ; }, //  # 22, 0x16, 0 b
  [] { digitalWrite(12, HIGH)                                                            ; }, //  # 23, 0x17, 0 b
  [] { digitalWrite(12, LOW )                                                            ; }, //  # 24, 0x18, 0 b
  [] { digitalWrite(13, HIGH)                                                            ; }, //  # 25, 0x19, 0 b
  [] { digitalWrite(13, LOW )                                                            ; }, //  # 26, 0x1a, 0 b
  [] { digitalWrite(A0, HIGH)                                                            ; }, //  # 27, 0x1b, 0 b
  [] { digitalWrite(A0, LOW )                                                            ; }, //  # 28, 0x1c, 0 b
  [] { digitalWrite(A1, HIGH)                                                            ; }, //  # 29, 0x1d, 0 b
  [] { digitalWrite(A1, LOW )                                                            ; }, //  # 30, 0x1e, 0 b
  [] { digitalWrite(A2, HIGH)                                                            ; }, //  # 31, 0x1f, 0 b
  [] { digitalWrite(A2, LOW )                                                            ; }, //  # 32, 0x20, 0 b
  [] { digitalWrite(A3, HIGH)                                                            ; }, //  # 33, 0x21, 0 b
  [] { digitalWrite(A3, LOW )                                                            ; }, //  # 34, 0x22, 0 b
  [] { digitalWrite(A4, HIGH)                                                            ; }, //  # 35, 0x23, 0 b
  [] { digitalWrite(A4, LOW )                                                            ; }, //  # 36, 0x24, 0 b
  [] { digitalWrite(A5, HIGH)                                                            ; }, //  # 37, 0x25, 0 b
  [] { digitalWrite(A5, LOW )                                                            ; }, //  # 38, 0x26, 0 b
  [] { noTone( 2)                                                                        ; }, //  # 39, 0x27, 0 b
  [] { noTone( 3)                                                                        ; }, //  # 40, 0x28, 0 b
  [] { noTone( 4)                                                                        ; }, //  # 41, 0x29, 0 b
  [] { noTone( 5)                                                                        ; }, //  # 42, 0x2a, 0 b
  [] { noTone( 6)                                                                        ; }, //  # 43, 0x2b, 0 b
  [] { noTone( 7)                                                                        ; }, //  # 44, 0x2c, 0 b
  [] { noTone( 8)                                                                        ; }, //  # 45, 0x2d, 0 b
  [] { noTone( 9)                                                                        ; }, //  # 46, 0x2e, 0 b
  [] { noTone(10)                                                                        ; }, //  # 47, 0x2f, 0 b
  [] { noTone(11)                                                                        ; }, //  # 48, 0x30, 0 b
  [] { noTone(12)                                                                        ; }, //  # 49, 0x31, 0 b
  [] { noTone(13)                                                                        ; }, //  # 50, 0x32, 0 b
  [] { noTone(A0)                                                                        ; }, //  # 51, 0x33, 0 b
  [] { noTone(A1)                                                                        ; }, //  # 52, 0x34, 0 b
  [] { noTone(A2)                                                                        ; }, //  # 53, 0x35, 0 b
  [] { noTone(A3)                                                                        ; }, //  # 54, 0x36, 0 b
  [] { noTone(A4)                                                                        ; }, //  # 55, 0x37, 0 b
  [] { noTone(A5)                                                                        ; }, //  # 56, 0x38, 0 b
  [] { pinMode( 2, INPUT       )                                                         ; }, //  # 57, 0x39, 0 b
  [] { pinMode( 2, INPUT_PULLUP)                                                         ; }, //  # 58, 0x3a, 0 b
  [] { pinMode( 2, OUTPUT      )                                                         ; }, //  # 59, 0x3b, 0 b
  [] { pinMode( 3, INPUT       )                                                         ; }, //  # 60, 0x3c, 0 b
  [] { pinMode( 3, INPUT_PULLUP)                                                         ; }, //  # 61, 0x3d, 0 b
  [] { pinMode( 3, OUTPUT      )                                                         ; }, //  # 62, 0x3e, 0 b
  [] { pinMode( 4, INPUT       )                                                         ; }, //  # 63, 0x3f, 0 b
  [] { pinMode( 4, INPUT_PULLUP)                                                         ; }, //  # 64, 0x40, 0 b
  [] { pinMode( 4, OUTPUT      )                                                         ; }, //  # 65, 0x41, 0 b
  [] { pinMode( 5, INPUT       )                                                         ; }, //  # 66, 0x42, 0 b
  [] { pinMode( 5, INPUT_PULLUP)                                                         ; }, //  # 67, 0x43, 0 b
  [] { pinMode( 5, OUTPUT      )                                                         ; }, //  # 68, 0x44, 0 b
  [] { pinMode( 6, INPUT       )                                                         ; }, //  # 69, 0x45, 0 b
  [] { pinMode( 6, INPUT_PULLUP)                                                         ; }, //  # 70, 0x46, 0 b
  [] { pinMode( 6, OUTPUT      )                                                         ; }, //  # 71, 0x47, 0 b
  [] { pinMode( 7, INPUT       )                                                         ; }, //  # 72, 0x48, 0 b
  [] { pinMode( 7, INPUT_PULLUP)                                                         ; }, //  # 73, 0x49, 0 b
  [] { pinMode( 7, OUTPUT      )                                                         ; }, //  # 74, 0x4a, 0 b
  [] { pinMode( 8, INPUT       )                                                         ; }, //  # 75, 0x4b, 0 b
  [] { pinMode( 8, INPUT_PULLUP)                                                         ; }, //  # 76, 0x4c, 0 b
  [] { pinMode( 8, OUTPUT      )                                                         ; }, //  # 77, 0x4d, 0 b
  [] { pinMode( 9, INPUT       )                                                         ; }, //  # 78, 0x4e, 0 b
  [] { pinMode( 9, INPUT_PULLUP)                                                         ; }, //  # 79, 0x4f, 0 b
  [] { pinMode( 9, OUTPUT      )                                                         ; }, //  # 80, 0x50, 0 b
  [] { pinMode(10, INPUT       )                                                         ; }, //  # 81, 0x51, 0 b
  [] { pinMode(10, INPUT_PULLUP)                                                         ; }, //  # 82, 0x52, 0 b
  [] { pinMode(10, OUTPUT      )                                                         ; }, //  # 83, 0x53, 0 b
  [] { pinMode(11, INPUT       )                                                         ; }, //  # 84, 0x54, 0 b
  [] { pinMode(11, INPUT_PULLUP)                                                         ; }, //  # 85, 0x55, 0 b
  [] { pinMode(11, OUTPUT      )                                                         ; }, //  # 86, 0x56, 0 b
  [] { pinMode(12, INPUT       )                                                         ; }, //  # 87, 0x57, 0 b
  [] { pinMode(12, INPUT_PULLUP)                                                         ; }, //  # 88, 0x58, 0 b
  [] { pinMode(12, OUTPUT      )                                                         ; }, //  # 89, 0x59, 0 b
  [] { pinMode(13, INPUT       )                                                         ; }, //  # 90, 0x5a, 0 b
  [] { pinMode(13, INPUT_PULLUP)                                                         ; }, //  # 91, 0x5b, 0 b
  [] { pinMode(13, OUTPUT      )                                                         ; }, //  # 92, 0x5c, 0 b
  [] { pinMode(A0, INPUT       )                                                         ; }, //  # 93, 0x5d, 0 b
  [] { pinMode(A0, INPUT_PULLUP)                                                         ; }, //  # 94, 0x5e, 0 b
  [] { pinMode(A0, OUTPUT      )                                                         ; }, //  # 95, 0x5f, 0 b
  [] { pinMode(A1, INPUT       )                                                         ; }, //  # 96, 0x60, 0 b
  [] { pinMode(A1, INPUT_PULLUP)                                                         ; }, //  # 97, 0x61, 0 b
  [] { pinMode(A1, OUTPUT      )                                                         ; }, //  # 98, 0x62, 0 b
  [] { pinMode(A2, INPUT       )                                                         ; }, //  # 99, 0x63, 0 b
  [] { pinMode(A2, INPUT_PULLUP)                                                         ; }, //  #100, 0x64, 0 b
  [] { pinMode(A2, OUTPUT      )                                                         ; }, //  #101, 0x65, 0 b
  [] { pinMode(A3, INPUT       )                                                         ; }, //  #102, 0x66, 0 b
  [] { pinMode(A3, INPUT_PULLUP)                                                         ; }, //  #103, 0x67, 0 b
  [] { pinMode(A3, OUTPUT      )                                                         ; }, //  #104, 0x68, 0 b
  [] { pinMode(A4, INPUT       )                                                         ; }, //  #105, 0x69, 0 b
  [] { pinMode(A4, INPUT_PULLUP)                                                         ; }, //  #106, 0x6a, 0 b
  [] { pinMode(A4, OUTPUT      )                                                         ; }, //  #107, 0x6b, 0 b
  [] { pinMode(A5, INPUT       )                                                         ; }, //  #108, 0x6c, 0 b
  [] { pinMode(A5, INPUT_PULLUP)                                                         ; }, //  #109, 0x6d, 0 b
  [] { pinMode(A5, OUTPUT      )                                                         ; }, //  #110, 0x6e, 0 b
  [] { send1(digitalRead( 2))                                                            ; }, //  #111, 0x6f, 0 b
  [] { send1(digitalRead( 3))                                                            ; }, //  #112, 0x70, 0 b
  [] { send1(digitalRead( 4))                                                            ; }, //  #113, 0x71, 0 b
  [] { send1(digitalRead( 5))                                                            ; }, //  #114, 0x72, 0 b
  [] { send1(digitalRead( 6))                                                            ; }, //  #115, 0x73, 0 b
  [] { send1(digitalRead( 7))                                                            ; }, //  #116, 0x74, 0 b
  [] { send1(digitalRead( 8))                                                            ; }, //  #117, 0x75, 0 b
  [] { send1(digitalRead( 9))                                                            ; }, //  #118, 0x76, 0 b
  [] { send1(digitalRead(10))                                                            ; }, //  #119, 0x77, 0 b
  [] { send1(digitalRead(11))                                                            ; }, //  #120, 0x78, 0 b
  [] { send1(digitalRead(12))                                                            ; }, //  #121, 0x79, 0 b
  [] { send1(digitalRead(13))                                                            ; }, //  #122, 0x7a, 0 b
  [] { send1(digitalRead(A0))                                                            ; }, //  #123, 0x7b, 0 b
  [] { send1(digitalRead(A1))                                                            ; }, //  #124, 0x7c, 0 b
  [] { send1(digitalRead(A2))                                                            ; }, //  #125, 0x7d, 0 b
  [] { send1(digitalRead(A3))                                                            ; }, //  #126, 0x7e, 0 b
  [] { send1(digitalRead(A4))                                                            ; }, //  #127, 0x7f, 0 b
  [] { send1(digitalRead(A5))                                                            ; }, //  #128, 0x80, 0 b
  [] { send2(analogRead(A0))                                                             ; }, //  #129, 0x81, 0 b
  [] { send2(analogRead(A1))                                                             ; }, //  #130, 0x82, 0 b
  [] { send2(analogRead(A2))                                                             ; }, //  #131, 0x83, 0 b
  [] { send2(analogRead(A3))                                                             ; }, //  #132, 0x84, 0 b
  [] { send2(analogRead(A4))                                                             ; }, //  #133, 0x85, 0 b
  [] { send2(analogRead(A5))                                                             ; }, //  #134, 0x86, 0 b
  [] { send4(millis())                                                                   ; }, //  #135, 0x87, 0 b
  [] { send4(pulseIn( 2, HIGH))                                                          ; }, //  #136, 0x88, 0 b
  [] { send4(pulseIn( 2, LOW ))                                                          ; }, //  #137, 0x89, 0 b
  [] { send4(pulseIn( 3, HIGH))                                                          ; }, //  #138, 0x8a, 0 b
  [] { send4(pulseIn( 3, LOW ))                                                          ; }, //  #139, 0x8b, 0 b
  [] { send4(pulseIn( 4, HIGH))                                                          ; }, //  #140, 0x8c, 0 b
  [] { send4(pulseIn( 4, LOW ))                                                          ; }, //  #141, 0x8d, 0 b
  [] { send4(pulseIn( 5, HIGH))                                                          ; }, //  #142, 0x8e, 0 b
  [] { send4(pulseIn( 5, LOW ))                                                          ; }, //  #143, 0x8f, 0 b
  [] { send4(pulseIn( 6, HIGH))                                                          ; }, //  #144, 0x90, 0 b
  [] { send4(pulseIn( 6, LOW ))                                                          ; }, //  #145, 0x91, 0 b
  [] { send4(pulseIn( 7, HIGH))                                                          ; }, //  #146, 0x92, 0 b
  [] { send4(pulseIn( 7, LOW ))                                                          ; }, //  #147, 0x93, 0 b
  [] { send4(pulseIn( 8, HIGH))                                                          ; }, //  #148, 0x94, 0 b
  [] { send4(pulseIn( 8, LOW ))                                                          ; }, //  #149, 0x95, 0 b
  [] { send4(pulseIn( 9, HIGH))                                                          ; }, //  #150, 0x96, 0 b
  [] { send4(pulseIn( 9, LOW ))                                                          ; }, //  #151, 0x97, 0 b
  [] { send4(pulseIn(10, HIGH))                                                          ; }, //  #152, 0x98, 0 b
  [] { send4(pulseIn(10, LOW ))                                                          ; }, //  #153, 0x99, 0 b
  [] { send4(pulseIn(11, HIGH))                                                          ; }, //  #154, 0x9a, 0 b
  [] { send4(pulseIn(11, LOW ))                                                          ; }, //  #155, 0x9b, 0 b
  [] { send4(pulseIn(12, HIGH))                                                          ; }, //  #156, 0x9c, 0 b
  [] { send4(pulseIn(12, LOW ))                                                          ; }, //  #157, 0x9d, 0 b
  [] { send4(pulseIn(13, HIGH))                                                          ; }, //  #158, 0x9e, 0 b
  [] { send4(pulseIn(13, LOW ))                                                          ; }, //  #159, 0x9f, 0 b
  [] { send4(pulseIn(A0, HIGH))                                                          ; }, //  #160, 0xa0, 0 b
  [] { send4(pulseIn(A0, LOW ))                                                          ; }, //  #161, 0xa1, 0 b
  [] { send4(pulseIn(A1, HIGH))                                                          ; }, //  #162, 0xa2, 0 b
  [] { send4(pulseIn(A1, LOW ))                                                          ; }, //  #163, 0xa3, 0 b
  [] { send4(pulseIn(A2, HIGH))                                                          ; }, //  #164, 0xa4, 0 b
  [] { send4(pulseIn(A2, LOW ))                                                          ; }, //  #165, 0xa5, 0 b
  [] { send4(pulseIn(A3, HIGH))                                                          ; }, //  #166, 0xa6, 0 b
  [] { send4(pulseIn(A3, LOW ))                                                          ; }, //  #167, 0xa7, 0 b
  [] { send4(pulseIn(A4, HIGH))                                                          ; }, //  #168, 0xa8, 0 b
  [] { send4(pulseIn(A4, LOW ))                                                          ; }, //  #169, 0xa9, 0 b
  [] { send4(pulseIn(A5, HIGH))                                                          ; }, //  #170, 0xaa, 0 b
  [] { send4(pulseIn(A5, LOW ))                                                          ; }, //  #171, 0xab, 0 b
  [] { analogWrite( 3, Serial.read())                                                    ; }, //  #172, 0xac, 1 b
  [] { analogWrite( 5, Serial.read())                                                    ; }, //  #173, 0xad, 1 b
  [] { analogWrite( 6, Serial.read())                                                    ; }, //  #174, 0xae, 1 b
  [] { analogWrite( 9, Serial.read())                                                    ; }, //  #175, 0xaf, 1 b
  [] { analogWrite(10, Serial.read())                                                    ; }, //  #176, 0xb0, 1 b
  [] { analogWrite(11, Serial.read())                                                    ; }, //  #177, 0xb1, 1 b
  [] { dallas.requestTemperatures(); send2(dallas.getTempCByIndex(Serial.read()) * 100.0); }, //  #178, 0xb2, 1 b
  [] { one = OneWire(Serial.read()); dallas = DallasTemperature(&one); dallas.begin()    ; }, //  #179, 0xb3, 1 b
  [] { tone( 2, readInt())                                                               ; }, //  #180, 0xb4, 2 b
  [] { tone( 3, readInt())                                                               ; }, //  #181, 0xb5, 2 b
  [] { tone( 4, readInt())                                                               ; }, //  #182, 0xb6, 2 b
  [] { tone( 5, readInt())                                                               ; }, //  #183, 0xb7, 2 b
  [] { tone( 6, readInt())                                                               ; }, //  #184, 0xb8, 2 b
  [] { tone( 7, readInt())                                                               ; }, //  #185, 0xb9, 2 b
  [] { tone( 8, readInt())                                                               ; }, //  #186, 0xba, 2 b
  [] { tone( 9, readInt())                                                               ; }, //  #187, 0xbb, 2 b
  [] { tone(10, readInt())                                                               ; }, //  #188, 0xbc, 2 b
  [] { tone(11, readInt())                                                               ; }, //  #189, 0xbd, 2 b
  [] { tone(12, readInt())                                                               ; }, //  #190, 0xbe, 2 b
  [] { tone(13, readInt())                                                               ; }, //  #191, 0xbf, 2 b
  [] { tone(A0, readInt())                                                               ; }, //  #192, 0xc0, 2 b
  [] { tone(A1, readInt())                                                               ; }, //  #193, 0xc1, 2 b
  [] { tone(A2, readInt())                                                               ; }, //  #194, 0xc2, 2 b
  [] { tone(A3, readInt())                                                               ; }, //  #195, 0xc3, 2 b
  [] { tone(A4, readInt())                                                               ; }, //  #196, 0xc4, 2 b
  [] { tone(A5, readInt())                                                               ; }, //  #197, 0xc5, 2 b
  [] { send4(pulseIn( 2, HIGH, readLong()))                                              ; }, //  #198, 0xc6, 4 b
  [] { send4(pulseIn( 2, LOW , readLong()))                                              ; }, //  #199, 0xc7, 4 b
  [] { send4(pulseIn( 3, HIGH, readLong()))                                              ; }, //  #200, 0xc8, 4 b
  [] { send4(pulseIn( 3, LOW , readLong()))                                              ; }, //  #201, 0xc9, 4 b
  [] { send4(pulseIn( 4, HIGH, readLong()))                                              ; }, //  #202, 0xca, 4 b
  [] { send4(pulseIn( 4, LOW , readLong()))                                              ; }, //  #203, 0xcb, 4 b
  [] { send4(pulseIn( 5, HIGH, readLong()))                                              ; }, //  #204, 0xcc, 4 b
  [] { send4(pulseIn( 5, LOW , readLong()))                                              ; }, //  #205, 0xcd, 4 b
  [] { send4(pulseIn( 6, HIGH, readLong()))                                              ; }, //  #206, 0xce, 4 b
  [] { send4(pulseIn( 6, LOW , readLong()))                                              ; }, //  #207, 0xcf, 4 b
  [] { send4(pulseIn( 7, HIGH, readLong()))                                              ; }, //  #208, 0xd0, 4 b
  [] { send4(pulseIn( 7, LOW , readLong()))                                              ; }, //  #209, 0xd1, 4 b
  [] { send4(pulseIn( 8, HIGH, readLong()))                                              ; }, //  #210, 0xd2, 4 b
  [] { send4(pulseIn( 8, LOW , readLong()))                                              ; }, //  #211, 0xd3, 4 b
  [] { send4(pulseIn( 9, HIGH, readLong()))                                              ; }, //  #212, 0xd4, 4 b
  [] { send4(pulseIn( 9, LOW , readLong()))                                              ; }, //  #213, 0xd5, 4 b
  [] { send4(pulseIn(10, HIGH, readLong()))                                              ; }, //  #214, 0xd6, 4 b
  [] { send4(pulseIn(10, LOW , readLong()))                                              ; }, //  #215, 0xd7, 4 b
  [] { send4(pulseIn(11, HIGH, readLong()))                                              ; }, //  #216, 0xd8, 4 b
  [] { send4(pulseIn(11, LOW , readLong()))                                              ; }, //  #217, 0xd9, 4 b
  [] { send4(pulseIn(12, HIGH, readLong()))                                              ; }, //  #218, 0xda, 4 b
  [] { send4(pulseIn(12, LOW , readLong()))                                              ; }, //  #219, 0xdb, 4 b
  [] { send4(pulseIn(13, HIGH, readLong()))                                              ; }, //  #220, 0xdc, 4 b
  [] { send4(pulseIn(13, LOW , readLong()))                                              ; }, //  #221, 0xdd, 4 b
  [] { send4(pulseIn(A0, HIGH, readLong()))                                              ; }, //  #222, 0xde, 4 b
  [] { send4(pulseIn(A0, LOW , readLong()))                                              ; }, //  #223, 0xdf, 4 b
  [] { send4(pulseIn(A1, HIGH, readLong()))                                              ; }, //  #224, 0xe0, 4 b
  [] { send4(pulseIn(A1, LOW , readLong()))                                              ; }, //  #225, 0xe1, 4 b
  [] { send4(pulseIn(A2, HIGH, readLong()))                                              ; }, //  #226, 0xe2, 4 b
  [] { send4(pulseIn(A2, LOW , readLong()))                                              ; }, //  #227, 0xe3, 4 b
  [] { send4(pulseIn(A3, HIGH, readLong()))                                              ; }, //  #228, 0xe4, 4 b
  [] { send4(pulseIn(A3, LOW , readLong()))                                              ; }, //  #229, 0xe5, 4 b
  [] { send4(pulseIn(A4, HIGH, readLong()))                                              ; }, //  #230, 0xe6, 4 b
  [] { send4(pulseIn(A4, LOW , readLong()))                                              ; }, //  #231, 0xe7, 4 b
  [] { send4(pulseIn(A5, HIGH, readLong()))                                              ; }, //  #232, 0xe8, 4 b
  [] { send4(pulseIn(A5, LOW , readLong()))                                              ; }, //  #233, 0xe9, 4 b
  [] { speed = readLong(); Serial.end(); setup()                                         ; }, //  #234, 0xea, 4 b
  [] { tone( 2, readInt(), readLong())                                                   ; }, //  #235, 0xeb, 6 b
  [] { tone( 3, readInt(), readLong())                                                   ; }, //  #236, 0xec, 6 b
  [] { tone( 4, readInt(), readLong())                                                   ; }, //  #237, 0xed, 6 b
  [] { tone( 5, readInt(), readLong())                                                   ; }, //  #238, 0xee, 6 b
  [] { tone( 6, readInt(), readLong())                                                   ; }, //  #239, 0xef, 6 b
  [] { tone( 7, readInt(), readLong())                                                   ; }, //  #240, 0xf0, 6 b
  [] { tone( 8, readInt(), readLong())                                                   ; }, //  #241, 0xf1, 6 b
  [] { tone( 9, readInt(), readLong())                                                   ; }, //  #242, 0xf2, 6 b
  [] { tone(10, readInt(), readLong())                                                   ; }, //  #243, 0xf3, 6 b
  [] { tone(11, readInt(), readLong())                                                   ; }, //  #244, 0xf4, 6 b
  [] { tone(12, readInt(), readLong())                                                   ; }, //  #245, 0xf5, 6 b
  [] { tone(13, readInt(), readLong())                                                   ; }, //  #246, 0xf6, 6 b
  [] { tone(A0, readInt(), readLong())                                                   ; }, //  #247, 0xf7, 6 b
  [] { tone(A1, readInt(), readLong())                                                   ; }, //  #248, 0xf8, 6 b
  [] { tone(A2, readInt(), readLong())                                                   ; }, //  #249, 0xf9, 6 b
  [] { tone(A3, readInt(), readLong())                                                   ; }, //  #250, 0xfa, 6 b
  [] { tone(A4, readInt(), readLong())                                                   ; }, //  #251, 0xfb, 6 b
  [] { tone(A5, readInt(), readLong())                                                   ; }, //  #252, 0xfc, 6 b
};

void loop() {               // Procedure call loop.
  if (callable) {
    procedures[procedureIndex]();
    callable = false;
    procedureIndex = -1;
  }
}