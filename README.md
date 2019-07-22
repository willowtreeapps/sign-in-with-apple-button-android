# Sign In with Apple Button for Android

A library for adding [Sign in With Apple](https://developer.apple.com/sign-in-with-apple/) to Android apps.

## What you get

This library includes a {TODO: name of button type} type, which you can include on your login screen. When tapped, the button presents a web login flow using the [Sign In with Apple JavaScript SDK](https://developer.apple.com/documentation/signinwithapplejs). After the user signs in, your code will receive a {TODO: name of token data model type} object. You can submit that object's details to your backend to validate and create a user session.

TODO: Diagram of styled buttons -> auth web flow -> token data -> your backend

TODO: Ask a designer to make the diagram extra nice 🙏

To fit both your UI and user expectations, you can style the button according to Apple's [Human Interface Guidelines](https://developer.apple.com/design/human-interface-guidelines/sign-in-with-apple/overview/).

TODO: Images of HIG-styled buttons, maybe animated to show touch effect

For Material Design apps, we also have a Material-themed button type, {TODO: name of material button type}.

TODO: Images of Material-themed buttons, maybe animated to show ripple

## Do I need this?

You will find this library useful if both of these statements are true:

1. Your service has both an Android app and an iOS app.
2. Your apps include third-party login, like signing in with Google, Facebook, or Twitter.

In June 2019, Apple announced Sign In with Apple, another third-party login provider. They also announced that if an iOS app offers any third-party login options, [it will be an App Store requirement](https://developer.apple.com/news/?id=06032019j) to offer Sign In with Apple. This rule would go into effect "later this year" (2019). That is, if you don't add the feature, at some point you won't be able to ship updates to your iOS app.

Obviously Apple does not control Android. But if you have to add a login method to your iOS app, your users will need it to be available on your Android app too. If it isn't, they won't be able to log in if they also use Android, or if they switch when they buy a new phone.

Therefore, we built this library to make that feature as painless as possible to add to your Android app.

## Installation and usage

TODO: How to include the app with Maven, directly from source, etc. … whatever the common methods are

## Example application

We've included an example Android app in {TODO: Folder with the example app in it}. This app mirrors [Apple's sample project](https://developer.apple.com/documentation/authenticationservices/adding_the_sign_in_with_apple_flow_to_your_app) for the [iOS Sign In with Apple button](https://developer.apple.com/documentation/authenticationservices/asauthorizationappleidbutton), so you can compare the two.

TODO: Where to look in the sample project for 1. the button's inclusion in layout, and what you might modify in order to style it differently, 2. the button's presentation of the web login flow, 3. where success or failure is reported back into the app, 4. display of success value details after login, with a note that in a real app you would take this data and submit it to your API's authentication endpoint.

## Similar projects

## Roadmap

- Apple HIG-themed Sign In with Apple button
- Material-themed Sign In with Apple button

## Contributing

Contributions are welcome. Please see the [Contributing guidelines](CONTRIBUTING.md).

This project has adopted a [code of conduct](CODE_OF_CONDUCT.md) defined by the [Contributor Covenant](http://contributor-covenant.org), the same used by the [Swift language](https://swift.org) and countless other open source software teams.
