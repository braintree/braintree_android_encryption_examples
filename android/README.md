# Android

## Overview

This code is a demonstration of creating one time transaction using Braintree's server-to-server API in conjunction with requests with fields encrypted with Braintree's Android encryption library.

## Getting Started

* Install Android SDK and developer tools from [Android Developer website](http://developer.android.com/index.html).
* Configure your client side encryption public key in `src/com/braintree/example/ui/BraintreeActivity.java`.
* Initialize Android projects:

```
android update project --path .
```

* Start an Android Virtual Device from Android Virtual Device Manager.
* Build and install release package.

```
ant debug install
```

* The example app should now be loaded in your AVD!
* When running the example app, go to Menu > Settings, and enter the URL of your example merchant server `https://10.0.2.2:8443` (for localhost).
