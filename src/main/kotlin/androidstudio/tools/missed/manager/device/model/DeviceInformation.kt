package androidstudio.tools.missed.manager.device.model

import com.android.ddmlib.IDevice
import org.jetbrains.kotlin.util.capitalizeDecapitalize.toUpperCaseAsciiOnly

class DeviceInformation(var name: String, val iDevice: IDevice)

fun IDevice.toDeviceInformation(): DeviceInformation {
    val deviceInformation = DeviceInformation(name = "Unknow", iDevice = this)

    val name = if (this.isEmulator) {
        this.name?.replace("_", " ").toString()
    } else {
        val brand = deviceInformation.iDevice.getProperty("ro.product.brand").trim().toUpperCaseAsciiOnly()
        val model = deviceInformation.iDevice.getProperty("ro.product.model").trim().toUpperCaseAsciiOnly()
        "$brand $model [${this.serialNumber}]"
    }
    deviceInformation.name = name

    return deviceInformation
}
