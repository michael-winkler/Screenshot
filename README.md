# Screenshot
A android library to take screenshots

[platform-badge]:     https://img.shields.io/badge/Platform-Android-009688.svg
[minsdk-badge]:       https://img.shields.io/badge/minSdkVersion-14-009688.svg
[license-badge]:      https://img.shields.io/badge/License-Apache_v2.0-009688.svg


## Usage
Add a dependency to your build.gradle file:
```java
dependencies {
    implementation 'com.nmd.android.support:Screenshot'
}
```

```java
import com.nmd.android.support.Screenshot;
```

| Public constructors |
| --- |
| `Screenshot(Context context)` |


## Needed app permissions
```java
android.permission.READ_EXTERNAL_STORAGE;
android.permission.WRITE_EXTERNAL_STORAGE;
```
AndroidMainfest.xml
```java
<manifest xlmns:android...>
  ...
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
  <application ...
</manifest>


```

## Callback
```java
.setCallback(new OnResultListener() {
    @Override
    public void result(boolean success, String result) {
      // your code here.        
    }
});
```


| type | function | description | default value | min api |
| --- | --- | --- | --- | --- |
| `void` | TakeScreenshot() | *Take a screenshot of the current visible screen.* | - |  14/16 |
| `void` | setFileName(String name) | *The filename for the taken screenshot.* | \"Screenshot.png\" |  14 |
| `string` | getFileName() | *Returns the given screenshot filename.* | - |  14 |
| `void` | ShowPreview(boolean enabled) | *If enabled you will see a short preview animation after the screenshot is taken.* | true |  14 |
| `boolean` | ShowPreview() | *Returns true/false if ShowPreview(...) is enabled/disabled.* | - |  14 |
| `void` | ShowNotification(boolean enabled) | *If enabled you will see a notification in the statusbar after the screenshot is taken.* | true |  14 |
| `boolean` | ShowNotification() | *Returns true/false if ShowNotification(...) is enabled/disabled.* | - |  14 |
| `void` | NotificationTitle(String name) | *The title text for the notification.* | \"Screenshot..\" |  14 |
| `string` | NotificationTitle() | *Returns the given notification title text.* | - |  14 |
| `void` | NotificationShareTitle(String name) | *The share button text for the notification if Show Notification(...) is enabled.* | "Share" |   16 |
| `string` | NotificationShareTitle() | *Returns the given notification share title text.* | - |  16 |
| `void` | NotificationBigStyle(boolean enabled) | *If enabled you will see a notification with \"big style\" in the statusbar after the screenshot is taken.* | true |  16 |
| `boolean` | NotificationBigStyle() | *Returns true/false if NotificationBigStyle(...) is enabled/disabled.* | - |  16 |
| `void` | AllowScreenshots(boolean enabled) | *This feature allows users of your app to make or ban screenshots of their app.<br>If disabled and a person tries to make a screenshot, they will receive then a default system message that this is not possible.* | - |  1 |


*You do not need to add SDK version checks by yourself. This does the library by itself.*

## Download(jar)
[Screenshot.jar](Screenshot.jar)

## Changelog
[Watch here the version changes](Changelog.md)

## License
```
Copyright 2018 Author @NMD [Next Mobile Development]

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
