# ROOT
## Key
> openssl genrsa -out rootUpscalers.key 2048
## CERT
> openssl req -new -x509 -days 1826 -key rootUpscalers.key -out rootUpscalers.crt -extensions v3_ca

# Intermediate
## Key
> openssl genrsa -out jbeaucousin.key 4096
## CA CSR
> openssl req -new -key jbeaucousin.key -out jbeaucousin.csr -extensions v3_req
## Sign it
> openssl x509 -req -days 1000 -in jbeaucousin.csr -CA rootUpscalers.crt -CAkey rootUpscalers.key -out jbeaucousin.crt -extfile v3.ext -extensions v3_intermediate_ca

# Servers
## key
> openssl genrsa -out catalogsWebApp.key 2048
> openssl genrsa -out catalogsApi.key 2048
> openssl genrsa -out catalogsElasticsearch.key 2048
## CA CSR
> openssl req -new -key catalogsWebApp.key -out catalogsWebApp.csr -extensions v3_req
> openssl req -new -key catalogsApi.key -out catalogsApi.csr -extensions v3_req
> openssl req -new -key catalogsElasticsearch.key -out catalogsElasticsearch.csr -extensions v3_req
## Sign it
> openssl x509 -req -days 1000 -in catalogsWebApp.csr -CA jbeaucousin.crt -CAkey jbeaucousin.key -set_serial 0101  -out catalogsWebApp.crt -sha1 -extfile v3.ext
> openssl x509 -req -days 1000 -in catalogsApi.csr -CA jbeaucousin.crt -CAkey jbeaucousin.key -set_serial 0101  -out catalogsApi.crt -sha1 -extfile v3.ext
> openssl x509 -req -days 1000 -in catalogsElasticsearch.csr -CA jbeaucousin.crt -CAkey jbeaucousin.key -set_serial 0101  -out catalogsElasticsearch.crt -sha1 -extfile v3.ext

# Store
> openssl pkcs12 -export -in catalogsApi.crt -inkey catalogsApi.key -CAfile jbeaucousin.crt -name catalogsapi -out catalogsApi.pkcs12

# KeyTool
> keytool -importkeystore -srckeystore catalogsApi.pkcs12 -srcstoretype pkcs12 -alias catalogsapi -destalias catalogsapi -destkeystore catalogsapi.jks -deststoretype pkcs12
export 
> keytool -export -alias catalogsapi -file test.crt -keystore catalogsapi.jks

Import cert
> keytool -importcert -keystore catalogapi.jks -file rootJbe.crt -alias catalogsapi

Delete cert
> keytool -delete -keystore catalogapi.jks -alias catalogsapi