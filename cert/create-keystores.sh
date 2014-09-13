#!/bin/bash
PASS="changeit"
DENTAISE_SERVER_HOSTNAME="192.168.0.5"

sudo rm -rf ./out
mkdir out
cd out

#### Generate key pair

sudo openssl req \
    -x509 \
	-newkey rsa:2048 \
    -keyout key.pem \
	-passout pass:$PASS \
    -out cert.pem \
    -days 365 \
	-subj "/C=PL/ST=Malopolskie/L=Krakow/O=AGH/CN=$DENTAISE_SERVER_HOSTNAME"

sudo openssl	x509 \
    -outform der \
	-in cert.pem \
	-passin pass:$PASS \
	-out cert.der

sudo openssl	pkcs12 \
	-export \
	-out cert.p12 \
	-passout pass:$PASS \
	-inkey key.pem \
	-passin pass:$PASS \
	-in cert.pem \
	-name dentaisePrivate \
	-caname dentaisePrivate

#### Create keystore for anroid (only trusted certificate will be contained)

keytool \
	-import \
    -v \
	-noprompt \
    -trustcacerts \
    -alias dentaiseTrusted \
    -file cert.der \
    -keystore android.bks \
    -storetype BKS \
	-storepass $PASS \
    -provider org.bouncycastle.jce.provider.BouncyCastleProvider \
    -providerpath ../bcprov-jdk15on-146.jar

#### Create keystore for play framework (trusted certificate and private key will be contained)

keytool \
	-importkeystore \
	-noprompt \
	-deststorepass $PASS \
	-deststoretype jks \
	-destkeystore play.jks \
	-srckeystore cert.p12 \
	-srcstoretype PKCS12 \
	-srcstorepass $PASS \
	-alias dentaisePrivate

keytool \
	-import \
    -v \
	-noprompt \
    -trustcacerts \
    -alias dentaiseTrusted \
    -file cert.der \
    -keystore play.jks \
    -storetype JKS \
	-storepass $PASS

### Copying keystores to modules

cp play.jks ../../dentaise-web/cert/
cp android.bks ../../dentaise-mobile/app/src/main/res/raw/


