Dear Developers,

This package provides the Android Java SDK for the PTS-2 device.
You can use Android Studio to build both the library and the sample application.

Please refer to the documents "Android SDK for PTS-2 Controller" and "jsonPTS Protocol for PTS-2 Controller", which describe the low-level protocol used by the PTS-2 device. This implementation closely follows that specification.

In the current Java implementation, some optional fields may not be returned in response to a request. To verify their availability, call the corresponding check method. For example, before calling getProductDensity(), you can check whether the field is set by calling isProductDensitySet().

If needed, you can also review the C# and C++ implementations for reference.

Kind regards,
PTS-2 Team