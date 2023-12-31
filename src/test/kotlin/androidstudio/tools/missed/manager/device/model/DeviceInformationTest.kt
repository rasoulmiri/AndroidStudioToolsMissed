package androidstudio.tools.missed.manager.device.model

import com.android.ddmlib.IDevice
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test

class DeviceInformationTest {

    @Test
    fun `toDeviceInformation should set correct values for emulator`() {
        val emulatorSerialNumber = "emulator_serial_number"
        val emulatorAvdName = "emulator_avd_name [emulator serial number abc123]"
        val emulatorDevice = mockk<IDevice>()

        every { emulatorDevice.isEmulator } returns true
        every { emulatorDevice.name } returns emulatorAvdName
        every { emulatorDevice.serialNumber } returns emulatorSerialNumber

        val deviceInformation = emulatorDevice.toDeviceInformation()

        assertEquals("emulator avd name [emulator serial number abc123]", deviceInformation.name)
        assertEquals(emulatorDevice, deviceInformation.iDevice)
    }

    @Test
    fun `toDeviceInformation should set correct values for real device`() {
        val realDeviceSerialNumber = "real_device_serial_number"
        val realDeviceBrand = "RealDeviceBrand"
        val realDeviceModel = "RealDeviceModel"
        val realDevice = mockk<IDevice>()

        every { realDevice.isEmulator } returns false
        every { realDevice.getProperty("ro.product.brand") } returns realDeviceBrand
        every { realDevice.getProperty("ro.product.model") } returns realDeviceModel
        every { realDevice.serialNumber } returns realDeviceSerialNumber

        val deviceInformation = realDevice.toDeviceInformation()

        assertEquals("REALDEVICEBRAND REALDEVICEMODEL [real_device_serial_number]", deviceInformation.name)
        assertEquals(realDevice, deviceInformation.iDevice)
    }
}
