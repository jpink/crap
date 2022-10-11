# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [2.0.0] - 2022-10-11
### Added
- Dallas Temperature sensors support.
- Dallas example.
- 1-wire support.
- Module API.

### Changed
- Protocol procedures.
- Shield API.

## [1.0.0] - 2022-10-10
### Added
- Local Maven publish.
- [kotlin-logging](https://github.com/MicroUtils/kotlin-logging) with colored output.
- Unit tests.
- Protocol handshaking.
- Relay Shield support.
- Relay example.
- This changelog.

### Changed
- Stabilized Arduino Uno support.
- Fixed major bug in protocol.
- Refactored pin and protocol initialization.
- Blink example.

### Removed
- Text based protocol.

## [0.1.0] - 2022-10-08
### Added
- Unit tests.
- Arduino Uno API.
- Generator for binary based protocol.
- Text based protocol.
- [jSerialComm](https://fazecast.github.io/jSerialComm/) connection implementation (in JVM).
- Blink example.
