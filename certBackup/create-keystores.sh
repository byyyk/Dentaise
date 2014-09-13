#!/bin/bash

rm -rf ./out
mkdir out
cd out

#### Generate key pair

openssl \
	req \
    -x509 \
	-newkey rsa:2048 \
    -keyout key.pem \
    -out cert.pem \
    -days 365

openssl \
	x509 \
    -outform der \
	-in cert.pem \
	-out cert.der

openssl \ 
	pkcs12 \
	-export \
	-out cert.p12 \
	-inkey key.pem \
	-in cert.pem

#### Create keystore for anroid (only trusted certificate will be contained)

keytool \
	-import \
    -v \
    -trustcacerts \
    -alias dentaiseTrusted \
    -file cert.der \
    -keystore keystoreAndroid.bks \
    -storetype BKS \
	-storepass changeit \
    -provider org.bouncycastle.jce.provider.BouncyCastleProvider \
    -providerpath ../bcprov-jdk15on-146.jar

#### Create keystore for play framework (trusted certificate and private key will be contained)

keytool \
	-importkeystore \
	-deststorepass changeit \
	-deststoretype jks \
	-destkeystore keystorePlay.jks \
	-srckeystore cert.p12 \
	-srcstoretype PKCS12 \
	-srcstorepass changeit \
	-alias dentaisePrivate

keytool \
	-import \
    -v \
    -trustcacerts \
    -alias dentaiseTrusted \
    -file cert.der \
    -keystore keystorePlay.jks \
    -storetype JKS \
	-storepass changeit

