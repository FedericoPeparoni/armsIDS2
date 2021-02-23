## 1.5.6 (2018-09-04)

### Fixed (1 change)

- Fix `CacheableExceptionProvider.handleException` when exclude is true.

## 1.5.5 (2018-09-04)

### Fixed (1 change)

- Fix `CacheableExceptionManager` null pointer exception issue when exclude is null.

## 1.5.4 (2018-09-04)

### Added (3 changes)

- Add `CacheableOnException.exclude` property to inverse `exceptions` list for method.
- Add `CacheOnExceptionConfig.exclude` property to inverse `exceptions` list for class.
- Add `CacheableExceptionProvider.handleException` interface method with default implementation (backwards compatible).

## 1.5.3 (2018-05-25)

### Fixed (1 change)

- Fix support for undeclared checked exceptions, `UndeclaredThrowableException`.

## 1.5.2 (2018-05-25)

### Added (1 change)

- Add support for undeclared checked exceptions, `UndeclaredThrowableException`.

## 1.5.1 (2018-05-10)

### Added (3 changes)

- Add `isLocked()` to `TriggerIntervalProvider` interface **[Breaking Change]**.
- Add `setLock(boolean isLocked)` to `TriggerIntervalProvider` interface **[Breaking Change]**.
- Add `UpdateCycleLockException` to `CacheUpdateManager` thrown when lock set.

### Removed (3 changes)

- Remove `CacheableExceptionMetadata` deprecated class **[Breaking Change]**.
- Remove `CacheableExceptionMetadataAction` deprecated enumerator **[Breaking Change]**.
- Remove `CacheableExceptionMetadataType` deprecated enumerator **[Breaking Change]**.

## 1.4.2 (2018-02-07)

### Added (3 changes)

- Add `jacoco` code coverage report.
- Add `sonarqube` support to GitLab CI.
- Add `url` and `scm.url` metadata to `pom.xml` file.

### Modified (1 change)

- Modify `CacheableRuntimeException` to require `metadata` parameters that implement `java.io.Serializable`.

## 1.4.1 (2018-02-05)

### Fixed (1 change)

- Fixed `CacheableExceptionResult` constructor back to public and added code inspection warning suppression.

## 1.4.0 (2018-02-05)

### Deprecated (1 change)

- Deprecated `CacheableExceptionMetadata`, `CacheableExceptionMetadataType`, and `CacheableExceptionMetadataAction` as they are no longer supported.

### Removed (1 change)

- Removed `CacheableExceptionMetadata` from `CacheableRuntimeException` and `CacheableException` metadata property type. Now you can pass any `Object` that you would like.

## 1.3.0 (2018-01-30)

### Added (2 changes)

- Add `CacheExceptionManager` retry cycle lock property and `RetryCycleLockException` throwable that must be handled.
- Add `GET` and `SELECT` value to `CacheableExceptionMetadataAction` enumerator.

## 1.2.1 (2018-01-26)

### Added (2 changes)

- Add ability to leave `@CacheableOnException` and `@CacheOnExceptionConfig` `exceptions` property empty to cache all exceptions.
- Add ability to loop threw throwable `causes` for matches to `@CacheableOnException` and `@CacheOnExceptionConfig` `exceptions` property values.

## 1.2.0 (2018-01-19)

### Added (2 changes)

- Add `CacheableExceptionMetadata` for actionable and dynamic information.
- Add `CacheableRuntimeException` for passing additional dynamic metadata at runtime.

## 1.1.0 (2018-01-09)

### Added (1 change)

- Add `TriggerIntervalProvider` interface with default implementation.

## 1.0.0 (2017-12-07)

### Added (6 changes)

- Add `@CacheableOnException` annotation to define cacheable methods.
- Add `@CacheOnExceptionConfig` annotation to config class global settings.
- Add `CacheRetryException` runtime exception to handle cached retries of methods expecting a return value.
- Add `CacheExceptionManager`config to handle cached exception retry cycles.
- Add `CacheUpdateManager` config to handle cache update cycles.
- Add `CacheableExceptionProvider` interface with default implementation.
