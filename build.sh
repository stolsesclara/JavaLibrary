#!/bin/bash
# JavaLibrary Build Script
mkdir -p out
find src -name "*.java" > sources.txt
javac -d out @sources.txt
if [ $? -eq 0 ]; then
    echo "✅ Compilação bem-sucedida!"
    cd out && jar cfe ../JavaLibrary.jar library.Main $(find . -name "*.class") && cd ..
    echo "✅ JavaLibrary.jar gerado!"
else
    echo "❌ Erro na compilação."
fi
