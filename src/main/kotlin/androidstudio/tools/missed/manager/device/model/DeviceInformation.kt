package androidstudio.tools.missed.manager.device.model

import com.android.ddmlib.IDevice
import org.jetbrains.kotlin.util.capitalizeDecapitalize.toUpperCaseAsciiOnly

class DeviceInformation(var title: String, var brand: String, var model: String, val iDevice: IDevice)

fun IDevice.toDeviceInformation(): DeviceInformation {
    val deviceInformation = DeviceInformation(title = "Unknow", brand = "", model = "", iDevice = this)

    var brand = ""
    var model = ""
    val title: String

    if (this.isEmulator) {
        brand = this.avdName?.replace("_", " ").orEmpty().trim().toUpperCaseAsciiOnly()
        model = ""
        title = "$brand [${this.serialNumber}]"
    } else {
        brand = deviceInformation.iDevice.getProperty("ro.product.brand").trim().toUpperCaseAsciiOnly()
        model = deviceInformation.iDevice.getProperty("ro.product.model").trim().toUpperCaseAsciiOnly()
        title = "$brand $model [${this.serialNumber}]"
    }
    deviceInformation.title = title
    deviceInformation.brand = brand
    deviceInformation.model = model

    return deviceInformation
}
