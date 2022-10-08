// Controller Remote Access Protocol (CRAP) sketch for Arduino Uno rev. 3

const int ID2 = 172;
const int ID4 = 190;
const int ID6 = 227;

byte buffer[4];             // Byte buffer to store read bytes.
int bytesNeeded = 0;        // How many bytes procedure needs to read the arguments.
boolean callable = false;   // Can the procedure be called.
int procedureIndex = -1;    // The procedure index in lambda array.
long speed = 9600L;         // Serial speed in bauds.

byte readByte() {           // Receive byte argument.
  return Serial.read();
}

int readInt() {             // Receive integer argument.
  Serial.readBytes(buffer, 2);
  long value = buffer[0] << 8;
  value += buffer[1];
  return value;
}

long readLong() {           // Receive long integer argument.
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
  send4(speed);
}

void serialEvent() {        // Read procedure and ensure enough bytes in buffer when data is available.
  if (procedureIndex < 0) {
    procedureIndex = Serial.read();
    if (-1 < procedureIndex) {
      if (procedureIndex < ID2) bytesNeeded = 0;
      else if (procedureIndex < ID4) bytesNeeded = 2;
      else if (procedureIndex < ID6) bytesNeeded = 4;
      else bytesNeeded = 6;
    }
  }
  if (-1 < procedureIndex && Serial.available() >= bytesNeeded) callable = true;
}

void (*procedures[])() = {                           // index, argument bytes
  [] { send4(millis())                          ; }, // #0   , 0 b
  [] { analogReference(DEFAULT)                 ; }, // #1   , 0 b
  [] { analogReference(EXTERNAL)                ; }, // #2   , 0 b
  [] { analogReference(INTERNAL)                ; }, // #3   , 0 b
  [] { send1(digitalRead(2))                    ; }, // #4   , 0 b
  [] { digitalWrite(2, HIGH)                    ; }, // #5   , 0 b
  [] { digitalWrite(2, LOW)                     ; }, // #6   , 0 b
  [] { noTone(2)                                ; }, // #7   , 0 b
  [] { send4(pulseIn(2, HIGH))                  ; }, // #8   , 0 b
  [] { send4(pulseIn(2, LOW))                   ; }, // #9   , 0 b
  [] { pinMode(2, INPUT)                        ; }, // #10  , 0 b
  [] { pinMode(2, INPUT_PULLUP)                 ; }, // #11  , 0 b
  [] { pinMode(2, OUTPUT)                       ; }, // #12  , 0 b
  [] { send1(digitalRead(3))                    ; }, // #13  , 0 b
  [] { digitalWrite(3, HIGH)                    ; }, // #14  , 0 b
  [] { digitalWrite(3, LOW)                     ; }, // #15  , 0 b
  [] { noTone(3)                                ; }, // #16  , 0 b
  [] { send4(pulseIn(3, HIGH))                  ; }, // #17  , 0 b
  [] { send4(pulseIn(3, LOW))                   ; }, // #18  , 0 b
  [] { pinMode(3, INPUT)                        ; }, // #19  , 0 b
  [] { pinMode(3, INPUT_PULLUP)                 ; }, // #20  , 0 b
  [] { pinMode(3, OUTPUT)                       ; }, // #21  , 0 b
  [] { send1(digitalRead(4))                    ; }, // #22  , 0 b
  [] { digitalWrite(4, HIGH)                    ; }, // #23  , 0 b
  [] { digitalWrite(4, LOW)                     ; }, // #24  , 0 b
  [] { noTone(4)                                ; }, // #25  , 0 b
  [] { send4(pulseIn(4, HIGH))                  ; }, // #26  , 0 b
  [] { send4(pulseIn(4, LOW))                   ; }, // #27  , 0 b
  [] { pinMode(4, INPUT)                        ; }, // #28  , 0 b
  [] { pinMode(4, INPUT_PULLUP)                 ; }, // #29  , 0 b
  [] { pinMode(4, OUTPUT)                       ; }, // #30  , 0 b
  [] { send1(digitalRead(5))                    ; }, // #31  , 0 b
  [] { digitalWrite(5, HIGH)                    ; }, // #32  , 0 b
  [] { digitalWrite(5, LOW)                     ; }, // #33  , 0 b
  [] { noTone(5)                                ; }, // #34  , 0 b
  [] { send4(pulseIn(5, HIGH))                  ; }, // #35  , 0 b
  [] { send4(pulseIn(5, LOW))                   ; }, // #36  , 0 b
  [] { pinMode(5, INPUT)                        ; }, // #37  , 0 b
  [] { pinMode(5, INPUT_PULLUP)                 ; }, // #38  , 0 b
  [] { pinMode(5, OUTPUT)                       ; }, // #39  , 0 b
  [] { send1(digitalRead(6))                    ; }, // #40  , 0 b
  [] { digitalWrite(6, HIGH)                    ; }, // #41  , 0 b
  [] { digitalWrite(6, LOW)                     ; }, // #42  , 0 b
  [] { noTone(6)                                ; }, // #43  , 0 b
  [] { send4(pulseIn(6, HIGH))                  ; }, // #44  , 0 b
  [] { send4(pulseIn(6, LOW))                   ; }, // #45  , 0 b
  [] { pinMode(6, INPUT)                        ; }, // #46  , 0 b
  [] { pinMode(6, INPUT_PULLUP)                 ; }, // #47  , 0 b
  [] { pinMode(6, OUTPUT)                       ; }, // #48  , 0 b
  [] { send1(digitalRead(7))                    ; }, // #49  , 0 b
  [] { digitalWrite(7, HIGH)                    ; }, // #50  , 0 b
  [] { digitalWrite(7, LOW)                     ; }, // #51  , 0 b
  [] { noTone(7)                                ; }, // #52  , 0 b
  [] { send4(pulseIn(7, HIGH))                  ; }, // #53  , 0 b
  [] { send4(pulseIn(7, LOW))                   ; }, // #54  , 0 b
  [] { pinMode(7, INPUT)                        ; }, // #55  , 0 b
  [] { pinMode(7, INPUT_PULLUP)                 ; }, // #56  , 0 b
  [] { pinMode(7, OUTPUT)                       ; }, // #57  , 0 b
  [] { send1(digitalRead(8))                    ; }, // #58  , 0 b
  [] { digitalWrite(8, HIGH)                    ; }, // #59  , 0 b
  [] { digitalWrite(8, LOW)                     ; }, // #60  , 0 b
  [] { noTone(8)                                ; }, // #61  , 0 b
  [] { send4(pulseIn(8, HIGH))                  ; }, // #62  , 0 b
  [] { send4(pulseIn(8, LOW))                   ; }, // #63  , 0 b
  [] { pinMode(8, INPUT)                        ; }, // #64  , 0 b
  [] { pinMode(8, INPUT_PULLUP)                 ; }, // #65  , 0 b
  [] { pinMode(8, OUTPUT)                       ; }, // #66  , 0 b
  [] { send1(digitalRead(9))                    ; }, // #67  , 0 b
  [] { digitalWrite(9, HIGH)                    ; }, // #68  , 0 b
  [] { digitalWrite(9, LOW)                     ; }, // #69  , 0 b
  [] { noTone(9)                                ; }, // #70  , 0 b
  [] { send4(pulseIn(9, HIGH))                  ; }, // #71  , 0 b
  [] { send4(pulseIn(9, LOW))                   ; }, // #72  , 0 b
  [] { pinMode(9, INPUT)                        ; }, // #73  , 0 b
  [] { pinMode(9, INPUT_PULLUP)                 ; }, // #74  , 0 b
  [] { pinMode(9, OUTPUT)                       ; }, // #75  , 0 b
  [] { send1(digitalRead(10))                   ; }, // #76  , 0 b
  [] { digitalWrite(10, HIGH)                   ; }, // #77  , 0 b
  [] { digitalWrite(10, LOW)                    ; }, // #78  , 0 b
  [] { noTone(10)                               ; }, // #79  , 0 b
  [] { send4(pulseIn(10, HIGH))                 ; }, // #80  , 0 b
  [] { send4(pulseIn(10, LOW))                  ; }, // #81  , 0 b
  [] { pinMode(10, INPUT)                       ; }, // #82  , 0 b
  [] { pinMode(10, INPUT_PULLUP)                ; }, // #83  , 0 b
  [] { pinMode(10, OUTPUT)                      ; }, // #84  , 0 b
  [] { send1(digitalRead(11))                   ; }, // #85  , 0 b
  [] { digitalWrite(11, HIGH)                   ; }, // #86  , 0 b
  [] { digitalWrite(11, LOW)                    ; }, // #87  , 0 b
  [] { noTone(11)                               ; }, // #88  , 0 b
  [] { send4(pulseIn(11, HIGH))                 ; }, // #89  , 0 b
  [] { send4(pulseIn(11, LOW))                  ; }, // #90  , 0 b
  [] { pinMode(11, INPUT)                       ; }, // #91  , 0 b
  [] { pinMode(11, INPUT_PULLUP)                ; }, // #92  , 0 b
  [] { pinMode(11, OUTPUT)                      ; }, // #93  , 0 b
  [] { send1(digitalRead(12))                   ; }, // #94  , 0 b
  [] { digitalWrite(12, HIGH)                   ; }, // #95  , 0 b
  [] { digitalWrite(12, LOW)                    ; }, // #96  , 0 b
  [] { noTone(12)                               ; }, // #97  , 0 b
  [] { send4(pulseIn(12, HIGH))                 ; }, // #98  , 0 b
  [] { send4(pulseIn(12, LOW))                  ; }, // #99  , 0 b
  [] { pinMode(12, INPUT)                       ; }, // #100 , 0 b
  [] { pinMode(12, INPUT_PULLUP)                ; }, // #101 , 0 b
  [] { pinMode(12, OUTPUT)                      ; }, // #102 , 0 b
  [] { send1(digitalRead(13))                   ; }, // #103 , 0 b
  [] { digitalWrite(13, HIGH)                   ; }, // #104 , 0 b
  [] { digitalWrite(13, LOW)                    ; }, // #105 , 0 b
  [] { noTone(13)                               ; }, // #106 , 0 b
  [] { send4(pulseIn(13, HIGH))                 ; }, // #107 , 0 b
  [] { send4(pulseIn(13, LOW))                  ; }, // #108 , 0 b
  [] { pinMode(13, INPUT)                       ; }, // #109 , 0 b
  [] { pinMode(13, INPUT_PULLUP)                ; }, // #110 , 0 b
  [] { pinMode(13, OUTPUT)                      ; }, // #111 , 0 b
  [] { send1(digitalRead(A0))                   ; }, // #112 , 0 b
  [] { digitalWrite(A0, HIGH)                   ; }, // #113 , 0 b
  [] { digitalWrite(A0, LOW)                    ; }, // #114 , 0 b
  [] { noTone(A0)                               ; }, // #115 , 0 b
  [] { send4(pulseIn(A0, HIGH))                 ; }, // #116 , 0 b
  [] { send4(pulseIn(A0, LOW))                  ; }, // #117 , 0 b
  [] { pinMode(A0, INPUT)                       ; }, // #118 , 0 b
  [] { pinMode(A0, INPUT_PULLUP)                ; }, // #119 , 0 b
  [] { pinMode(A0, OUTPUT)                      ; }, // #120 , 0 b
  [] { send2(analogRead(A0))                    ; }, // #121 , 0 b
  [] { send1(digitalRead(A1))                   ; }, // #122 , 0 b
  [] { digitalWrite(A1, HIGH)                   ; }, // #123 , 0 b
  [] { digitalWrite(A1, LOW)                    ; }, // #124 , 0 b
  [] { noTone(A1)                               ; }, // #125 , 0 b
  [] { send4(pulseIn(A1, HIGH))                 ; }, // #126 , 0 b
  [] { send4(pulseIn(A1, LOW))                  ; }, // #127 , 0 b
  [] { pinMode(A1, INPUT)                       ; }, // #128 , 0 b
  [] { pinMode(A1, INPUT_PULLUP)                ; }, // #129 , 0 b
  [] { pinMode(A1, OUTPUT)                      ; }, // #130 , 0 b
  [] { send2(analogRead(A1))                    ; }, // #131 , 0 b
  [] { send1(digitalRead(A2))                   ; }, // #132 , 0 b
  [] { digitalWrite(A2, HIGH)                   ; }, // #133 , 0 b
  [] { digitalWrite(A2, LOW)                    ; }, // #134 , 0 b
  [] { noTone(A2)                               ; }, // #135 , 0 b
  [] { send4(pulseIn(A2, HIGH))                 ; }, // #136 , 0 b
  [] { send4(pulseIn(A2, LOW))                  ; }, // #137 , 0 b
  [] { pinMode(A2, INPUT)                       ; }, // #138 , 0 b
  [] { pinMode(A2, INPUT_PULLUP)                ; }, // #139 , 0 b
  [] { pinMode(A2, OUTPUT)                      ; }, // #140 , 0 b
  [] { send2(analogRead(A2))                    ; }, // #141 , 0 b
  [] { send1(digitalRead(A3))                   ; }, // #142 , 0 b
  [] { digitalWrite(A3, HIGH)                   ; }, // #143 , 0 b
  [] { digitalWrite(A3, LOW)                    ; }, // #144 , 0 b
  [] { noTone(A3)                               ; }, // #145 , 0 b
  [] { send4(pulseIn(A3, HIGH))                 ; }, // #146 , 0 b
  [] { send4(pulseIn(A3, LOW))                  ; }, // #147 , 0 b
  [] { pinMode(A3, INPUT)                       ; }, // #148 , 0 b
  [] { pinMode(A3, INPUT_PULLUP)                ; }, // #149 , 0 b
  [] { pinMode(A3, OUTPUT)                      ; }, // #150 , 0 b
  [] { send2(analogRead(A3))                    ; }, // #151 , 0 b
  [] { send1(digitalRead(A4))                   ; }, // #152 , 0 b
  [] { digitalWrite(A4, HIGH)                   ; }, // #153 , 0 b
  [] { digitalWrite(A4, LOW)                    ; }, // #154 , 0 b
  [] { noTone(A4)                               ; }, // #155 , 0 b
  [] { send4(pulseIn(A4, HIGH))                 ; }, // #156 , 0 b
  [] { send4(pulseIn(A4, LOW))                  ; }, // #157 , 0 b
  [] { pinMode(A4, INPUT)                       ; }, // #158 , 0 b
  [] { pinMode(A4, INPUT_PULLUP)                ; }, // #159 , 0 b
  [] { pinMode(A4, OUTPUT)                      ; }, // #160 , 0 b
  [] { send2(analogRead(A4))                    ; }, // #161 , 0 b
  [] { send1(digitalRead(A5))                   ; }, // #162 , 0 b
  [] { digitalWrite(A5, HIGH)                   ; }, // #163 , 0 b
  [] { digitalWrite(A5, LOW)                    ; }, // #164 , 0 b
  [] { noTone(A5)                               ; }, // #165 , 0 b
  [] { send4(pulseIn(A5, HIGH))                 ; }, // #166 , 0 b
  [] { send4(pulseIn(A5, LOW))                  ; }, // #167 , 0 b
  [] { pinMode(A5, INPUT)                       ; }, // #168 , 0 b
  [] { pinMode(A5, INPUT_PULLUP)                ; }, // #169 , 0 b
  [] { pinMode(A5, OUTPUT)                      ; }, // #170 , 0 b
  [] { send2(analogRead(A5))                    ; }, // #171 , 0 b
  [] { tone(2, readInt())                       ; }, // #172 , 2 b
  [] { tone(3, readInt())                       ; }, // #173 , 2 b
  [] { tone(4, readInt())                       ; }, // #174 , 2 b
  [] { tone(5, readInt())                       ; }, // #175 , 2 b
  [] { tone(6, readInt())                       ; }, // #176 , 2 b
  [] { tone(7, readInt())                       ; }, // #177 , 2 b
  [] { tone(8, readInt())                       ; }, // #178 , 2 b
  [] { tone(9, readInt())                       ; }, // #179 , 2 b
  [] { tone(10, readInt())                      ; }, // #180 , 2 b
  [] { tone(11, readInt())                      ; }, // #181 , 2 b
  [] { tone(12, readInt())                      ; }, // #182 , 2 b
  [] { tone(13, readInt())                      ; }, // #183 , 2 b
  [] { tone(A0, readInt())                      ; }, // #184 , 2 b
  [] { tone(A1, readInt())                      ; }, // #185 , 2 b
  [] { tone(A2, readInt())                      ; }, // #186 , 2 b
  [] { tone(A3, readInt())                      ; }, // #187 , 2 b
  [] { tone(A4, readInt())                      ; }, // #188 , 2 b
  [] { tone(A5, readInt())                      ; }, // #189 , 2 b
  [] { speed = readLong(); Serial.end(); setup(); }, // #190 , 4 b
  [] { send4(pulseIn(2, HIGH, readLong()))      ; }, // #191 , 4 b
  [] { send4(pulseIn(2, LOW, readLong()))       ; }, // #192 , 4 b
  [] { send4(pulseIn(3, HIGH, readLong()))      ; }, // #193 , 4 b
  [] { send4(pulseIn(3, LOW, readLong()))       ; }, // #194 , 4 b
  [] { send4(pulseIn(4, HIGH, readLong()))      ; }, // #195 , 4 b
  [] { send4(pulseIn(4, LOW, readLong()))       ; }, // #196 , 4 b
  [] { send4(pulseIn(5, HIGH, readLong()))      ; }, // #197 , 4 b
  [] { send4(pulseIn(5, LOW, readLong()))       ; }, // #198 , 4 b
  [] { send4(pulseIn(6, HIGH, readLong()))      ; }, // #199 , 4 b
  [] { send4(pulseIn(6, LOW, readLong()))       ; }, // #200 , 4 b
  [] { send4(pulseIn(7, HIGH, readLong()))      ; }, // #201 , 4 b
  [] { send4(pulseIn(7, LOW, readLong()))       ; }, // #202 , 4 b
  [] { send4(pulseIn(8, HIGH, readLong()))      ; }, // #203 , 4 b
  [] { send4(pulseIn(8, LOW, readLong()))       ; }, // #204 , 4 b
  [] { send4(pulseIn(9, HIGH, readLong()))      ; }, // #205 , 4 b
  [] { send4(pulseIn(9, LOW, readLong()))       ; }, // #206 , 4 b
  [] { send4(pulseIn(10, HIGH, readLong()))     ; }, // #207 , 4 b
  [] { send4(pulseIn(10, LOW, readLong()))      ; }, // #208 , 4 b
  [] { send4(pulseIn(11, HIGH, readLong()))     ; }, // #209 , 4 b
  [] { send4(pulseIn(11, LOW, readLong()))      ; }, // #210 , 4 b
  [] { send4(pulseIn(12, HIGH, readLong()))     ; }, // #211 , 4 b
  [] { send4(pulseIn(12, LOW, readLong()))      ; }, // #212 , 4 b
  [] { send4(pulseIn(13, HIGH, readLong()))     ; }, // #213 , 4 b
  [] { send4(pulseIn(13, LOW, readLong()))      ; }, // #214 , 4 b
  [] { send4(pulseIn(A0, HIGH, readLong()))     ; }, // #215 , 4 b
  [] { send4(pulseIn(A0, LOW, readLong()))      ; }, // #216 , 4 b
  [] { send4(pulseIn(A1, HIGH, readLong()))     ; }, // #217 , 4 b
  [] { send4(pulseIn(A1, LOW, readLong()))      ; }, // #218 , 4 b
  [] { send4(pulseIn(A2, HIGH, readLong()))     ; }, // #219 , 4 b
  [] { send4(pulseIn(A2, LOW, readLong()))      ; }, // #220 , 4 b
  [] { send4(pulseIn(A3, HIGH, readLong()))     ; }, // #221 , 4 b
  [] { send4(pulseIn(A3, LOW, readLong()))      ; }, // #222 , 4 b
  [] { send4(pulseIn(A4, HIGH, readLong()))     ; }, // #223 , 4 b
  [] { send4(pulseIn(A4, LOW, readLong()))      ; }, // #224 , 4 b
  [] { send4(pulseIn(A5, HIGH, readLong()))     ; }, // #225 , 4 b
  [] { send4(pulseIn(A5, LOW, readLong()))      ; }, // #226 , 4 b
  [] { tone(2, readInt(), readLong())           ; }, // #227 , 6 b
  [] { tone(3, readInt(), readLong())           ; }, // #228 , 6 b
  [] { tone(4, readInt(), readLong())           ; }, // #229 , 6 b
  [] { tone(5, readInt(), readLong())           ; }, // #230 , 6 b
  [] { tone(6, readInt(), readLong())           ; }, // #231 , 6 b
  [] { tone(7, readInt(), readLong())           ; }, // #232 , 6 b
  [] { tone(8, readInt(), readLong())           ; }, // #233 , 6 b
  [] { tone(9, readInt(), readLong())           ; }, // #234 , 6 b
  [] { tone(10, readInt(), readLong())          ; }, // #235 , 6 b
  [] { tone(11, readInt(), readLong())          ; }, // #236 , 6 b
  [] { tone(12, readInt(), readLong())          ; }, // #237 , 6 b
  [] { tone(13, readInt(), readLong())          ; }, // #238 , 6 b
  [] { tone(A0, readInt(), readLong())          ; }, // #239 , 6 b
  [] { tone(A1, readInt(), readLong())          ; }, // #240 , 6 b
  [] { tone(A2, readInt(), readLong())          ; }, // #241 , 6 b
  [] { tone(A3, readInt(), readLong())          ; }, // #242 , 6 b
  [] { tone(A4, readInt(), readLong())          ; }, // #243 , 6 b
  [] { tone(A5, readInt(), readLong())          ; }, // #244 , 6 b
};

void loop() {               // Procedure call loop.
  if (callable) {
    procedures[procedureIndex]();
    callable = false;
  }
}