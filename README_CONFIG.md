# Odoo Mobile Station Integration

## How to configure base URL

### For Emulator
When running on the Android Emulator, `localhost` (127.0.0.1) refers to the emulator itself. To access the Odoo server running on your host machine, use:
- **Base URL:** `http://10.0.2.2:8069`

### For Physical Device
If you are testing on a physical device, ensure the device is on the same Wi-Fi network as your server. Use your computer's local IP address:
- **Base URL:** `http://<your-computer-ip>:8069`
- Example: `http://192.168.1.5:8069`

### Configuration Location
The base URL is currently hardcoded in `AppContainer.kt`. For production, it is recommended to move this to a `buildConfigField` in `build.gradle.kts` or a configuration screen in the app.

```kotlin
// AppContainer.kt
private val retrofit = Retrofit.Builder()
    .baseUrl("http://10.0.2.2:8069/api/v1/mobile/")
    ...
```

## Security Note
The application uses `EncryptedSharedPreferences` to store the `access_token` and `station_session`. This ensures that sensitive credentials are encrypted at rest on the device.
