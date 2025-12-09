# ProDVX Android Demo Application
A demo application that showcases the different uses of the APPC devices.\
Can also be used for a combined sdk of different features and functionalities.\
_Documentation in progress_

## Features
- Adaptive Light technology that adapts the LED ring to the screen content. (S-Series only, API required)
- LED Control: Manually change the color the the Full Led Bar LED's using either SDK or ProDVX API
- S-Series Led Demo: Demo Application that showcases the individual led control (S-Series only,  API required)
- NFC Demo: Test the full functionality of the integrated or external NFC capabilities

_To Come_:
- API Demo: See the full power of the ProDVX API from 
- Radar Demo: Test the functionalities of the Radar Motion Sensor
- Camera: Test some of the different ways to implement the camera on your APPC device
- Scanner: See what is possible with the barcode scanner

## Documentation
If looking to implement the features yourself, Search (in program/all files if possible) for the Feature with the following descriptors: \
_Please note that not all features have been fully documented yet. Please investigate the respective Activity files to find all required code._ \
_Some features have extensive functionality that is not essential to the functionality._
- API -> _API:_
- NFC -> _NFC:_
- Adaptive Lights -> _AdaptiveLight:_
- LED -> _LED:_
- S-Series Leds -> _SLED:_
- PoGo LED -> _PLED:_

## Installation
- Download the App from the releases tab.

- Either:
  - Transfer to USB stick
  - Insert USB into APPC device
  - Install using USB
- Or
  - Install using ADB with 
```
adb install ProDVX_Demo_Vx.x.apk // Make sure to check your version number
```
 
## Usage
### Without API:
Currently only the LED Demo and NFC is available.
- Open the app
- Tap "LED Demo" or "NFC"

### With API
- Ensure that the ProDVX API is running.
- When the app first starts no token is provided.
- Tap "Set new API Token". You can set it in two ways:
  - Manual entry:
    - Enter the token using virtual/on-screen keyboard or USB keyboard
    - Paste from another application
  - Configuration File:
    - Create a "configuration.json" file  (example below)
    - Place it on the device
    - When tapping above button, tap "Pick a file"
    - Pick the configuration file
- All other functionality becomes available now.

configuration.json
```
{
  "api_token": "<token>"
}
```

### LED Demo
The LED Demo demonstrates how the LEDs are controlled via either SDK or ProDVX API.
The SDK ius currently only implemented for the Type B and Type C Led Controllers. Please see the SDK documentation for your model [here](https://drive.google.com/file/d/1hjgXLdEUjeEcvE-VrGCZVuNLDNrolM9c/view?usp=sharing).
In this menu, the LED Demo for S-Series can als be found.

### Adaptive Light
This service enables the device to recognize specific datapoints from the display and sends the RGB data to the LEDs.\
_**Setting datapoints**_: You can set the datapoints by increasing/decreasing the offsets from the edge of the screen, indicated with red lines.\
_**Fuzzing values**_: Fuzzing the datapoints grabs pixels around the absolute pixel and averages the RGB data before sending it to the LEDs. It is also possible to turn this function off or increase/decrease the amount of pixels around the base point to be collected.\
_**Test Image**_: Set a test image that is applied to the background of the screen to test the offsets, incase of content with black bars.

## Issues
Please input any issue to the issue tracker.

## Maintenance
Since this is a project which is focused on internal testing and making simple SDK-like code examples, this codebase is only lightly maintained to develop new features or fix essential bugs. This project gives a complete package for potential customers or current partners and customers to test the possibilities with ProDVX Android Products.
