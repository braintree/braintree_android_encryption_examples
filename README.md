# Braintree Android Encryption Integration Examples

This project contains examples of integrating with the [Braintree](http://www.braintreepaymentsolutions.com)
payment gateway using the [Braintree Android encryption library](https://github.com/braintree/braintree_android_encryption).

## Android

An example Android application that uses the Android encryption library to encrypt sensitive data and send it to a test server.
See android/README.md for more information

## Server

An example Ruby (Sinatra) server that receives requests with encrypted fields from the Android example application and forwards them to Braintree's payment gateway using the server-to-server API
See server/README.md for more information
