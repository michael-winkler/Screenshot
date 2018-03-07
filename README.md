# Screenshot
A android library to take screenshots

[![API](https://img.shields.io/badge/API-14%2B-orange.svg?style=flat)](https://android-arsenal.com/api?level=14)
[![License Apache 2.0](https://img.shields.io/badge/License-Apache%202.0-blue.svg?style=true)](http://www.apache.org/licenses/LICENSE-2.0)
[![Total Downloads](https://img.shields.io/github/downloads/NmdOffical/Screenshot/total.svg)]()


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

Don't forget to set MIN and TARGET SDK values:
```java
<uses-sdk android:minSdkVersion="14" android:targetSdkVersion="22"/>
```

Android 8:
```
Do not set target sdk to 26.
If set to 26 you will see no notification function which the library you gives.
```

## Interface

| type | name |
| --- | --- |
| `interface` | OnResultListener<br>*Interface definition for a callback to be invoked when a screenshot was created.* |

Public methods

| type | function |
| --- | --- |
| `abstract void` | result(boolean success, String filePath, Bitmap bitmap)<br>*Called when a screenshot was created.* |

```java
.setCallback(new Screenshot.OnResultListener() {
    @Override
    public void result(boolean success, String filePath, Bitmap bitmap) {
      // your code here.        
    }
});
```

## Public methods

| type | function | description | default value | min api |
| --- | --- | --- | --- | --- |
| `void` | TakeScreenshot() | *Take a screenshot of the current visible screen.* | - |  14 |
| `void` | TakeScreenshotFromView() | *Take a screenshot of any visible view.* | - |  14 |
| `void` | setFileName(String name) | *The filename for the taken screenshot.* | \"Screenshot.png\" |  14 |
| `string` | getFileName() | *Returns the given screenshot filename.* | - |  14 |
| `void` | ShowPreview(boolean enabled) | *If enabled you will see a short preview animation after the screenshot is taken.* | true |  14 |
| `boolean` | ShowPreview() | *Returns true/false if* `ShowPreview(...)` *is enabled/disabled.* | - |  14 |
| `void` | ShowNotification(boolean enabled) | *If enabled you will see a notification in the statusbar after the screenshot is taken.* | true |  14 |
| `boolean` | ShowNotification() | *Returns true/false if* `ShowNotification(...)` *is enabled/disabled.* | - |  14 |
| `void` | NotificationTitle(String name) | *The title text for the notification.* | \"Screenshot..\" |  14 |
| `string` | NotificationTitle() | *Returns the given notification title text.* | - |  14 |
| `void` | NotificationShareTitle(String name) | *The share button text for the notification if* `Show Notification(...)` *is enabled.* | "Share" |   16 |
| `string` | NotificationShareTitle() | *Returns the given notification share title text.* | - |  16 |
| `void` | NotificationBigStyle(boolean enabled) | *If enabled you will see a notification with \"big style\" in the statusbar after the screenshot is taken.* | true |  16 |
| `boolean` | NotificationBigStyle() | *Returns true/false if* `NotificationBigStyle(...)` *is enabled/disabled.* | - |  16 |
| `void` | NotificationShareButton(boolean enabled) | *If enabled you will see a notification with a share button after the screenshot is taken.* | true |  16 |
| `boolean` | NotificationShareButton() | *Returns true/false if* `NotificationShareButton(...)` *is enabled/disabled.* | - |  16 |
| `void` | AllowScreenshots(boolean enabled) | *This feature allows users of your app to make or ban screenshots of their app.<br>If disabled and a person tries to make a screenshot, they will receive then a default system message that this is not possible.* | - |  1 |
| `void` | setDimAmount(float amount) | *Set the amount of dim behind the preview window if* `ShowPreview(...)` *is enabled. Use '0.0' for no dim and '1.0' for full dim.* | 0.5f |  14 |
| `float` | getDimAmount() | *Returns the amount from `setDimAmount(...)` .* | - |  14 |

*You do not need to add SDK version checks by yourself. This does the library for you.*

## Exampe code

```java
package com.YOURNAME.YOURNAME;

import android.app.Activity;
import android.content.Context;
import android.widget.TextView;
import android.graphics.Color;
import com.nmd.android.support.Screenshot;
...

public class YourClass extends Activity {
  private Context context;
  private TextView txt;
  private Screenshot screenshot;

    public YourClass (...) {
      this.context = getContext();
      Initialize();
    }
    
    private void Initialize() {
      txt = (TextView)findViewById(R.id.editText);
      screenshot = new Screenshot(this.context);
    }

    public void Take() {
      screenshot.NotificationTitle("My screenshot title");
      screenshot.setCallback(new Screenshot.OnResultListener() {
        @Override
        public void result(boolean success, String filePath, Bitmap bitmap) {
          txt.setText(filePath);
          txt.setTextColor(success ? Color.GREEN : Color.RED);
          // if success is true then set text color to green, else red
        }
      });
      //After you have done your settings let's take the screenshot
      screenshot.TakeScreenshot();
    }
}
```

## Download(jar)
Version 1.0<br>
[Screenshot.jar](Screenshot.jar)

## Known bugs/issues
You can find [here](https://github.com/NmdOfficial/Screenshot/issues) the known bugs/issues

## Changelog
Watch [here](Changelog.md) the version changes

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
