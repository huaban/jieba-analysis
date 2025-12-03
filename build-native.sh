#!/bin/bash

# Build Native Image for jieba-analysis
# This script requires GraalVM to be installed

echo "Building jieba-analysis Native Image..."
echo "Using Java: $(java -version 2>&1 | head -n 1)"

# Set JAVA_HOME to GraalVM
export JAVA_HOME=~/.sdkman/candidates/java/21.0.8-graal
export PATH=$JAVA_HOME/bin:$PATH

# Clean and compile
echo "Step 1: Clean and compile..."
mvn clean compile -DskipTests

if [ $? -ne 0 ]; then
    echo "Compilation failed!"
    exit 1
fi

# Package
echo "Step 2: Package..."
mvn package -DskipTests

if [ $? -ne 0 ]; then
    echo "Package failed!"
    exit 1
fi

# Build Native Image
echo "Step 3: Build Native Image..."
mvn package -Pnative -DskipTests

if [ $? -ne 0 ]; then
    echo "Native Image build failed!"
    exit 1
fi

echo "Native Image build completed successfully!"
echo "Executable location: target/jieba-analysis"
