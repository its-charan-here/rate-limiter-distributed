# Distributed Rate Limiter

A distributed rate limiter implementation using the Token Bucket algorithm with Redis as the backend storage. This implementation provides a scalable solution for rate limiting across multiple instances of your application.

## Features

- **Distributed Rate Limiting**: Works across multiple application instances
- **Token Bucket Algorithm**: Implements a fair and efficient rate limiting strategy
- **Redis Backend**: Provides persistence and atomic operations
- **Configurable Parameters**: 
  - Bucket capacity
  - Refill rate
  - Client-specific rate limits
- **Thread-Safe**: Uses Redis transactions for atomic operations
- **Comprehensive Testing**: Includes unit tests with TestContainers for Redis integration

## Why Redis?

Redis is chosen as the backend storage for several reasons:
- **Atomic Operations**: Redis provides atomic operations which are crucial for rate limiting
- **Persistence**: Rate limiting state persists across application restarts
- **Performance**: Redis is an in-memory data store with exceptional performance
- **Distributed**: Redis can be used in a distributed setup for high availability

## Test Scenarios

The implementation includes the following test scenarios:
1. **Basic Rate Limiting**: Tests the fundamental rate limiting functionality
2. **Token Refill**: Verifies token refill mechanism after waiting period
3. **Multiple Clients**: Tests rate limiting for different clients independently
4. **Partial Refill**: Validates partial token refill based on elapsed time

## Prerequisites

- JDK 21 or higher
- Redis server
- Docker (for running tests with TestContainers)

## Building and Running

### Windows

1. Build the project:
```batch
gradlew.bat build
```

2. Run tests:
```batch
gradlew.bat test
```

### Linux/MacOS

1. Build the project:
```bash
./gradlew build
```

2. Run tests:
```bash
./gradlew test
```

## Usage in Your Project

1. Add the dependency to your `build.gradle`:
```groovy
dependencies {
    implementation 'com.ratelimiter:distributed-rate-limiter:1.0-SNAPSHOT'
}
```

2. Initialize the rate limiter:
```java
Jedis jedis = new Jedis("localhost", 6379);
TokenBucketRateLimiter rateLimiter = new TokenBucketRateLimiter(jedis, 10, 5);
```

3. Use in your application:
```java
String clientId = "user123";
if (rateLimiter.isAllowed(clientId)) {
    // Process the request
} else {
    // Handle rate limit exceeded
}
```

## Implementation Details

The rate limiter uses the Token Bucket algorithm with the following components:
- **Bucket Capacity**: Maximum number of tokens available
- **Refill Rate**: Number of tokens added per second
- **Redis Keys**: 
  - `rate_limiter:{clientId}:tokens`: Current token count
  - `rate_limiter:{clientId}:last_update`: Last update timestamp

## Troubleshooting

### Common Issues

1. **Redis Connection Issues**
   - Ensure Redis server is running
   - Check Redis connection settings (host, port)
   - Verify Redis server is accessible from your application

2. **Test Failures**
   - Ensure Docker is running (required for TestContainers)
   - Check if Redis container can be started
   - Verify sufficient system resources are available

3. **Build Issues**
   - Ensure JDK 21 is properly installed and JAVA_HOME is set
   - Verify Gradle wrapper is executable (on Unix-like systems)
   - Check if all dependencies can be downloaded

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the MIT License - see the LICENSE file for details.