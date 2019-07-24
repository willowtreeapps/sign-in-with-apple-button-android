# Sign In with Apple Button for Android

A library for adding [Sign in With Apple](https://developer.apple.com/sign-in-with-apple/) to Android apps.

[![CircleCI](https://circleci.com/gh/willowtreeapps/sign-in-with-apple-button-android.svg?style=svg&circle-token=94aaaafd543585e19434a36498601ec291d29e62)](https://circleci.com/gh/willowtreeapps/sign-in-with-apple-button-android) [![License MIT](https://img.shields.io/badge/License-MIT-blue.svg?style=flat)]() [![Public Yes](https://img.shields.io/badge/Public-yes-green.svg?style=flat)]()

## What you get

This library includes a `SignInWithAppleButton` class, which you can include on your login screen. When tapped, the button presents a web login flow using the [Sign In with Apple JavaScript SDK](https://developer.apple.com/documentation/signinwithapplejs). After the user signs in, your code will receive an `AppleSignInSuccess` object with an authorization code string. You can then pass the authorization code to your backend's third party login endpoint.

TODO: Diagram of styled buttons -> auth web flow -> token data -> your backend

TODO: Ask a designer to make the diagram extra nice?

To fit both your UI and user expectations, you can style the button according to Apple's [Human Interface Guidelines](https://developer.apple.com/design/human-interface-guidelines/sign-in-with-apple/overview/).

TODO: Images of HIG-styled buttons, maybe animated to show touch effect

For Material Design apps, we also have a Material-themed button class, {TODO: name of material button class}.

TODO: Images of Material-themed buttons, maybe animated to show ripple

## Do I need this?

You will find this library useful if both of these statements are true:

1. Your service has both an Android app and an iOS app.
2. Your apps include third-party login, like signing in with Google, Facebook, or Twitter.

In June 2019, Apple announced Sign In with Apple, another third-party login provider. They also announced that if an iOS app offers any third-party login options, [it will be an App Store requirement](https://developer.apple.com/news/?id=06032019j) to offer Sign In with Apple. This rule would go into effect "later this year" (2019). That is, if you don't add the feature, at some point you won't be able to ship updates to your iOS app.

Obviously Apple does not control Android. But if you have to add a login method to your iOS app, your users will need it on your Android app too. If it isn't supported, your users won't be able to log in if they switch to Android.

We built this library to make it as painless as possible to add Sign In with Apple to your Android app.

## Instructions

### Service setup

First, follow Apple's instructions to set up Sign In with Apple [in your iOS app](https://help.apple.com/developer-account/#/devde676e696) and [for a web service](https://help.apple.com/developer-account/#/dev1c0e25352). It is the web service setup that you'll use from Android, but you need both.

More setup is necessary for backend operations, but the above is all you need to use this library.

For more detail, you can read Aaron Parecki's walkthrough, [What the Heck is Sign In with Apple?](https://developer.okta.com/blog/2019/06/04/what-the-heck-is-sign-in-with-apple)

You should have created:

- An App ID,
    - including the Sign In with Apple capability.
- A Service ID,
    - using the App ID as its primary,
    - mapped to a domain you control,
        - which Apple has verified,
    - including at least one Return URL.

From this setup, you will need two OAuth arguments to use this library:

- A client ID, which you entered as the Identifier field of the Service ID.
- A redirect URI, which you entered as the Return URL.

### Installation

TODO: How to include the app with Maven, directly from source, etc. … whatever the common methods are

### Configuration

Add a `SignInWithAppleButton` or {TODO: Material button class name} to your layout.

TODO: Explain that properties can be configured in layout XML or in code.

Configure the button's appearance properties:

- `buttonTheme` (optional): Supply `"white"`, `"black"`, or `"whiteWithOutline"`
- `signInText` (optional): Supply `"signInWithApple"` or `"continueWithApple"` to specify the text of the button.
- `cornerRadius` (optional): TODO

> These options are based on the style options from Apple's [Human Interface Guidelines](https://developer.apple.com/design/human-interface-guidelines/sign-in-with-apple/overview/).

Configure the button's service authentication properties:

- `clientId`: Use the client ID value from service setup.
- `redirectUri`: Use the redirect URI value from service setup.
- `scope` (optional): Supply a space-delimited string of OpenID scopes, like "name email".

Finally, configure the `callback` property with an instance of `AppleSignInCallback`.

TODO: Does the library consumer have to configure any security exceptions for the library to catch the request?

### Behavior

When the user taps the button, it will present a web view configured to authenticate for your service. After the user authenticates, Apple will forward to the redirect URI. The web view will automatically intercept this request and pull out the authorization code.

If the user completes authentication, your `AppleSignInCallback` object will receive an `AppleSignInSuccess` value in a call to `onSignInSuccess`. Your backend endpoint can then phone home to Apple to [exchange the authorization code for an access token](https://developer.apple.com/documentation/signinwithapplerestapi/generate_and_validate_tokens), completing login.

If instead there is a failure, your `AppleSignInCallback` object will receive that error in a call to `onSignInFailure`.

If the user dismisses the authentication screen intentionally, your `AppleSignInCallback` object won't receive any call.

## Example application

We've included an example Android app in the `sample` folder. This app mirrors [Apple's sample project](https://developer.apple.com/documentation/authenticationservices/adding_the_sign_in_with_apple_flow_to_your_app) for the [iOS Sign In with Apple button](https://developer.apple.com/documentation/authenticationservices/asauthorizationappleidbutton), so you can compare the two.

The example app demonstrates:

1. Adding the button to your layout, in `activity_sample.xml`
2. Configuring the button with service details, in {TODO: where?}
3. Configuring the button with success and failure callbacks, in `SampleActivity.kt`
4. Using the authorization code, in the success callback. In a real application, you would submit this code to your backend's third party login endpoint.

## Related projects

- Front end components offering a Sign In with Apple button
    - [Juice](https://developer.apple.com/documentation/authenticationservices/adding_the_sign_in_with_apple_flow_to_your_app), Apple's sample project, as seen in [WWDC 2019, Session 706 - Introducing Sign In with Apple](https://developer.apple.com/videos/play/wwdc19/706/)
    - [@react-native-community/apple-authentication](https://github.com/react-native-community/apple-authentication), a React Native library
    - [OmniAuth::Apple](https://github.com/nhosoya/omniauth-apple), an OmniAuth strategy for Rails web apps
- Backend components offering validaton of Sign In with Apple tokens
    - [apple-auth](https://github.com/ananay/apple-auth), an NPM package for JavaScript backends
    - [apple_id](https://github.com/nov/apple_id), a gem for Ruby backends

## Roadmap

- Apple HIG-themed Sign In with Apple button
- Material-themed Sign In with Apple button

## Contributing

Contributions are welcome. Please see the [Contributing guidelines](CONTRIBUTING.md).

This project has adopted a [code of conduct](CODE_OF_CONDUCT.md) defined by the [Contributor Covenant](http://contributor-covenant.org), the same used by the [Swift language](https://swift.org) and countless other open source software teams.
