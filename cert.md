# Generate root certificat 
## Root
Root key
> openssl genrsa -des3 -out rootJbe.key 4096
root certificat
> openssl req -x509 -new -nodes -key rootJbe.key -sha256 -days 2048 -out rootJbe.crt

## Domain
Domain Key
> openssl genrsa -out CatalogsApi.key 2048
Domain cert
> openssl req -new -sha256 -key CatalogsApi.key -subj "/C=US/ST=CA/O=Scalian/CN=catalogsapi.com" -out CatalogsApi.csr


## Certificate 
certificate
> openssl x509 -req -in CatalogsApi.csr -CA rootJbe.crt -CAkey rootJbe.key -CAcreateserial -sha256 -out CatalogsApi.crt
Verification 
> openssl x509 -in CatalogsApi.crt -text -noout

## Merge
> cat CatalogsApi.crt rootJbe.crt > CatalogsApiBundle.crt

## Create store with root cert
> openssl pkcs12 -export -in CatalogsApiBundle.crt -inkey CatalogsApi.key -CAfile CatalogsApi.crt -name catalogapi -out CatalogsApi.pkcs12

# Java Keytool
## generate keystore with certificate
> keytool -genkeypair -v -alias catalogapi -dname "CN=catalogsapi.com, OU=Upscalers, O=Scalian, L=Nantes, ST=France, C=FR" -keystore toolboxapi.jks -keypass jbeaucou -storepass jbeaucou -keyalg RSA -keysize 4096 -ext KeyUsage:critical="keyCertSign" -ext BasicConstraints:critical="ca:true" -validity 9999
export certificate 
> keytool -export -v -alias toolboxapi -file toolboxapi.crt -keypass jbeaucou -storepass jbeaucou -keystore toolboxapi.jks -rfc

## generate keystore with previous cetificate
> keytool -importkeystore -srckeystore CatalogsApi.pkcs12 -srcstoretype pkcs12 -alias catalogapi -destalias toolboxapi -destkeystore catalogapi.jks -deststoretype pkcs12
check 
> keytool -list -keystore catalogapi.jks
export 
> keytool -export -alias toolboxapi -file test.crt -keystore catalogapi.jks

## Import cert
keytool -importcert -keystore catalogapi.jks -file rootJbe.crt -alias jbeaucousin

## Delete cert
keytool -delete -keystore catalogapi.jks -alias jbeaucousin