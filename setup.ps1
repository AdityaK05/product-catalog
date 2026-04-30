# setup.ps1 – Downloads Jetty 9.4 JARs (no Maven/Docker needed)
$v = "9.4.54.v20240208"
$base = "https://repo1.maven.org/maven2"
New-Item -ItemType Directory -Force -Path lib | Out-Null

$jars = @(
    "org/eclipse/jetty/jetty-server/$v/jetty-server-$v.jar",
    "org/eclipse/jetty/jetty-servlet/$v/jetty-servlet-$v.jar",
    "org/eclipse/jetty/jetty-util/$v/jetty-util-$v.jar",
    "org/eclipse/jetty/jetty-http/$v/jetty-http-$v.jar",
    "org/eclipse/jetty/jetty-io/$v/jetty-io-$v.jar",
    "org/eclipse/jetty/jetty-security/$v/jetty-security-$v.jar",
    "javax/servlet/javax.servlet-api/3.1.0/javax.servlet-api-3.1.0.jar"
)

foreach ($jar in $jars) {
    $file = Split-Path $jar -Leaf
    $dest = "lib\$file"
    if (Test-Path $dest) { Write-Host "  [ok] $file"; continue }
    Write-Host "Downloading $file ..."
    Invoke-WebRequest -Uri "$base/$jar" -OutFile $dest -UseBasicParsing
}
Write-Host "`nAll dependencies downloaded to lib/"
