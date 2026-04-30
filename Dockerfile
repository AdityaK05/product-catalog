FROM eclipse-temurin:11-jdk-jammy

WORKDIR /app

# Install curl (needed by setup.sh to download JARs)
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Copy source files
COPY setup.sh .
COPY ProductCatalog.java .

# Download Jetty JARs
RUN bash setup.sh

# Compile
RUN mkdir -p out && javac --release 8 -cp "lib/*" ProductCatalog.java -d out

# Render injects PORT at runtime
ENV PORT=8080
EXPOSE 8080

CMD ["java", "-cp", "out:lib/*", "ProductCatalog"]
