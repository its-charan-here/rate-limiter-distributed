#!/bin/bash

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m'

echo "Running Distributed Rate Limiter Tests..."
echo "----------------------------------------"

# Run Maven tests
mvn clean test

# Check if tests passed
if [ $? -eq 0 ]; then
    echo -e "${GREEN}All tests passed successfully!${NC}"
else
    echo -e "${RED}Tests failed. Please check the output above for details.${NC}"
    exit 1
fi 