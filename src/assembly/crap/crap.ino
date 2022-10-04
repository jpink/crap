// Arduino Uno is a microcontroller board based on the ATmega328P.
#define INVALID -1;
const long SPEED = 9600;
const float VERSION = 0.1;
const char ERROR = '!';
const char NL = '\n';
const char OK = 'O';
enum Func {
  AnalogRead              = 'a',  // Value of 0-1023
  AnalogReferenceDefault  = 'b',  // 5 V
  AnalogReferenceInternal = 'c',  // 2.56 V
  AnalogReferenceExternal = 'd',  // 0-5 V in AREF
  AnalogWrite             = 'e',
  AttachInterruptChange   = 'f',
  AttachInterruptFalling  = 'g',
  AttachInterruptLow      = 'h',
  AttachInterruptRising   = 'i',
  DigitalRead             = 'j',
  DigitalWriteHigh        = 'k',
  DigitalWriteLow         = 'l',
  DetachInterrupt         = 'm',
  Interrupts              = 'n',
  NoInterrupts            = 'o',
  NoTone                  = 'p',  // TODO
  PinModeInput            = 'q',
  PinModeInputPullUp      = 'r',
  PinModeOutput           = 's',
  PulseIn                 = 't',  // TODO
  PulseInTimeout          = 'u',  // TODO
  PulseLong               = 'v',  // TODO
  PulseLongTimeout        = 'w',  // TODO
  ShiftIn                 = 'x',  // TODO
  ShiftOut                = 'y',  // TODO
  Tone                    = 'z',  // TODO
  ToneDuration            = 'A'   // TODO
};
byte request[64];
int pin = INVALID;
int size = 0;
bool received = false;

void setup() {
  Serial.begin(SPEED);
  while (!Serial);
  Serial.print("CRAPv");
  Serial.print(VERSION);
  Serial.print('@');
  Serial.print(SPEED);
  println();
  Serial.flush();
}

void loop() {
  if (received) {
    received = false;
    char result = handleRequest(request[0]);
    pin = INVALID;
    size = 0;
    respondChar(result);
  }
}

void serialEvent() {
  int b = Serial.read();
  if (b > -1) {
    request[size++] = b;
    if (b == NL) received = true;
  }
}

char handleRequest(enum Func func) {
  switch (func) {
    case AnalogRead:
      // TODO check pin mode!
      pin = getAnalogPin();
      if (invalidPin()) return ERROR;
      writeShort(analogRead(pin));
      return OK;
    case DigitalRead:
      // TODO check pin mode!
      pin = getPin();
      if (validPin()) {
        switch(digitalRead(pin)) {
          case HIGH:  return 'H';
          case LOW:   return 'L';
        }
      }
      return ERROR;
    case DigitalWriteHigh:        return handleDigitalWrite(HIGH);
    case DigitalWriteLow:         return handleDigitalWrite(LOW);
    case AnalogWrite: // no need to call pinMode.
      pin = getPwmPin();
      if (invalidPin() || size != 3) return ERROR;
      analogWrite(pin, request[2]);
      return OK;
    case Interrupts:              interrupts();                       return OK;
    case NoInterrupts:            noInterrupts();                     return OK;
    case PinModeInput:            return handlePinMode(INPUT);
    case PinModeInputPullUp:      return handlePinMode(INPUT_PULLUP);
    case PinModeOutput:           return handlePinMode(OUTPUT);
    case AttachInterruptFalling:  return handleAttachInterrupt(FALLING);
    case AttachInterruptRising:   return handleAttachInterrupt(RISING);
    case AttachInterruptChange:   return handleAttachInterrupt(CHANGE);
    case AttachInterruptLow:      return handleAttachInterrupt(LOW);
    case DetachInterrupt:
      getInterruptPin();
      if (invalidPin()) return ERROR;
      detachInterrupt(pin);
      return OK;
    case AnalogReferenceDefault:  analogReference(DEFAULT);           return OK;
    case AnalogReferenceInternal: analogReference(INTERNAL);          return OK;
    case AnalogReferenceExternal: analogReference(EXTERNAL);          return OK;
    default:                      return '?';
  }
}

void respondChar(char c) {
  Serial.print(c);
  println();
  Serial.flush();
}

int getAnalogPin() {
  switch ((char) request[1]) {
    case 'A': return A0;
    case 'B': return A1;
    case 'C': return A2;
    case 'D': return A3;
    case 'E': return A4;
    case 'F': return A5;
    default: return -1;
  }
}

int getDigitalOutputPin() {
  switch ((char) request[1]) {
    case '2': return 2;
    case '4': return 4;
    case '7': return 7;
    case '8': return 8;
    case 'b': return 12;
    case 'c':
    case 'l':
    case 'L':
      return LED_BUILTIN;
    default: return getPwmPin();
  }
}

void getInterruptPin() {
  switch ((char) request[1]) {
    case '2': pin = digitalPinToInterrupt(2); return;
    case '3': pin = digitalPinToInterrupt(3); return;
    default:  pin = INVALID;                  return;
  }
}

int getPin() {
  pin = getDigitalOutputPin();
  if (invalidPin()) pin = getAnalogPin();
  return pin;
}

int getPwmPin() {
  switch ((char) request[1]) {
    case '3': return 3;   // 490 Hz
    case '5': return 5;   // 980 Hz
    case '6': return 6;   // 980 Hz
    case '9': return 9;   // 490 Hz
    case '0': return 10;  // 490 Hz
    case 'a': return 11;  // 490 Hz
    default: return INVALID;
  }
}

char handleAttachInterrupt(int mode) {
  getInterruptPin();
  if (invalidPin()) return ERROR;
  if (((char) request[1]) == '2') attachInterrupt(pin, isr2, mode);
  else                            attachInterrupt(pin, isr3, mode);
  return OK;
}

char handleDigitalWrite(int value) {
  pin = getDigitalOutputPin();
  if (invalidPin()) return ERROR;
  // TODO check pin mode!
  digitalWrite(pin, value);
  return OK;
}

char handlePinMode(int mode) {
  // TODO can pull-up be on analogs?
  if (mode == OUTPUT) pin = getDigitalOutputPin();
  else pin = getPin();
  if (invalidPin()) return ERROR;
  pinMode(pin, mode);
  return OK;
}

bool invalidPin() {
  return pin == INVALID
}

bool validPin() {
  return pin != INVALID
}

void isr2() {
  respondChar('2');
}

void isr3() {
  respondChar('3');
}

// https://forum.arduino.cc/t/sending-4-bytes-int-number-with-serial-write-to-serial/311669/7
void writeShort(short value) {
  Serial.write(value & 255);
  Serial.write((value >> 8) & 255);
}

void println() {
  Serial.print(NL);
}
