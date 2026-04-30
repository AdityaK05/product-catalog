#!/bin/bash
# setup.sh – Downloads Jetty 9.4 JARs (Linux/macOS/Render)
V="9.4.54.v20240208"
BASE="https://repo1.maven.org/maven2"
mkdir -p lib

download() {
  FILE=$(basename "$1")
  DEST="lib/$FILE"
  if [ -f "$DEST" ]; then
    echo "  [ok] $FILE"
  else
    echo "Downloading $FILE ..."
    curl -fsSL "$BASE/$1" -o "$DEST"
  fi
}

download "org/eclipse/jetty/jetty-server/$V/jetty-server-$V.jar"
download "org/eclipse/jetty/jetty-servlet/$V/jetty-servlet-$V.jar"
download "org/eclipse/jetty/jetty-util/$V/jetty-util-$V.jar"
download "org/eclipse/jetty/jetty-http/$V/jetty-http-$V.jar"
download "org/eclipse/jetty/jetty-io/$V/jetty-io-$V.jar"
download "org/eclipse/jetty/jetty-security/$V/jetty-security-$V.jar"
download "javax/servlet/javax.servlet-api/3.1.0/javax.servlet-api-3.1.0.jar"

echo ""
echo "All dependencies downloaded to lib/"
