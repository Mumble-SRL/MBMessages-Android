# MBMessages Android

MBMessages is a plugin library for [MBurger](https://mburger.cloud), lets you display in app messages and manage push notifications in your app. The minimum deployment target for the library is 4.2 (API 17).

Using this library you can display the messages that you set up in the MBurger dashboard in your app. You can also setup and manage push notifications connected to your MBurger project.

## Installation

### Installation with gradle

This plugin <u>only works with the latest Kotlin version of MBurger Client SDK</u> so make sure to ad Kotlin Android Studio plugin and Kotlin dependencies to your Android project.

Add this repository to your project level `build.gradle` file under allprojects:

```
maven { url "https://dl.bintray.com/mumbleideas/MBurger-Android/" }
```

Then add **MBurger Kotlin** dependency to your `app build.gradle` file:

```
implementation 'mumble.mburger:android_kt:0.5.0'
```

Lastly add `MBMessages` library:

```
implementation 'mumble.mburger:mbmessages-android:0.3.0'
```



### Initialization

To initialize MBMessages you need to add `MBMessages` to the array of plugins of `MBurger`.

```kotlin
val plugins = ArrayList<MBPlugin>()
val plugin = MBMessages()
plugins.add(plugin)

MBurger.initialize(applicationContext, "MBURGER_KEY", false, plugins)
```

Then you need to say MBurger to initialize the plugins with the in initPlugins method from MBurger class. If you wish you can add a listener to know when the plugin have been initialized.

```kotlin
val listener: MBMessagesPluginInitialized
plugin.initListener = listener

override fun onInitialized() {
   //Plugin has been initialized correctly, can be started
}

override fun onInitializedError(error: String?) {
   //Error during initialization of MBMessages plugin
}
```

Once you've done this ask MBurger to start plugins in your main activity (be aware that it has to be an `AppCompatActivity`), then, in app messages will be fetched automatically and showed, if they need to be showed.

```kotlin
MBurger.startPlugins(activity)
```



# Stylization and parameters

You can set a couples of parameters when creating the plugin object:

- **order**: which order to be initialized this plugin, if not set plugin will be initialized according to the plugin array order
- **delayInSeconds**: it's the time after which the messages will be displayed once fetched

The `MBMessagesManager` is the main class which shows the messages and handles the stylization and if messages need to be shown or not. There are many possible parameters you can set to change message appaerances:

- **forceMessageStyle**: change the style for the messages:
  - **MBIAMConstants**.IAM_STYLE_BANNER_TOP
  - **MBIAMConstants**.IAM_STYLE_BANNER_BOTTOM
  - **MBIAMConstants**.IAM_STYLE_BANNER_CENTER
  - **MBIAMConstants**.IAM_STYLE_FULL_SCREEN_IMAGE
- **debug**: show messages even if they have been already shown
- **backgroundColor**: the color of the background
- **titleColor**: the text color for the title of the message
- **bodyColor**: the text color for the body
- **closeButtonColor**: the color of the close button
- **button1BackgroundColor**: the background color of the first action button
- **button1TitleColor**: the text color of the first action button
- **button2BackgroundColor**: the background color of the second action button
- **button2TitleColor**: the text color of the second action button
- **titleFontRes**: the font resource of the title
- **bodyFontRes**: the font resource  of the body
- **buttonsTextFontRes**: the font resource of the buttons titles
- **titleSizeRes**: the font size resource of the title
- **bodySizeRes**: the font size resource of the body
- **buttonsSizeRes**: the font size resource of the buttons titles



All web links will be automatically handled, the in-app messages must be handled adding the `MNBIAMClickListener` which calls `onCTAClicked` with the CTA object in order to be handled.