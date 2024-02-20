package androidstudio.tools.missed.manager.adb

import androidstudio.tools.missed.manager.adb.command.AdbCommand
import androidstudio.tools.missed.manager.adb.command.SuccessResultEnum
import androidstudio.tools.missed.manager.adb.logger.AdbLogger
import androidstudio.tools.missed.manager.device.model.DeviceInformation
import androidstudio.tools.missed.manager.device.model.toDeviceInformation
import androidstudio.tools.missed.manager.resource.ResourceManager
import androidstudio.tools.missed.utils.DELAY_MEDIUM
import com.android.ddmlib.AndroidDebugBridge
import com.android.ddmlib.IDevice
import com.android.ddmlib.IShellOutputReceiver
import com.android.ddmlib.SyncException
import com.android.ddmlib.SyncService
import com.android.ddmlib.TimeoutException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.callbackFlow
import java.io.ByteArrayOutputStream
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AdbManagerImpl(
    private val resourceManager: ResourceManager,
    private val androidDebugBridge: AndroidDebugBridge,
    private val adbLogger: AdbLogger
) : AdbManager {

    companion object {
        private const val MAX_TRIALS = 10
        private const val INITIAL_ADB_DELAY_MS = 100L
        private const val INITIAL_ADB_RETRY_DELAY_MS = 1000L
    }

    private var adbIsConnect: Boolean = false

    @Suppress("TooGenericExceptionCaught")
    override suspend fun initialAdb(): Result<Boolean> {
        try {
            var trials = MAX_TRIALS
            while (trials > 0) {
                delay(INITIAL_ADB_DELAY_MS)
                if (androidDebugBridge.isConnected) {
                    break
                }
                trials--
            }

            if (!androidDebugBridge.isConnected) {
                println(resourceManager.string("adbConnectionIssue"))
                return Result.failure(Throwable(resourceManager.string("adbConnectionIssue")))
            }

            trials = MAX_TRIALS
            while (trials > 0) {
                delay(INITIAL_ADB_DELAY_MS)
                if (androidDebugBridge.hasInitialDeviceList()) {
                    break
                }
                trials--
            }
            delay(DELAY_MEDIUM)
            if (!androidDebugBridge.hasInitialDeviceList()) {
                println(resourceManager.string("getDevicesFromAdbError"))
                return Result.failure(Throwable(resourceManager.string("getDevicesFromAdbError")))
            }

            this.adbIsConnect = true
            return Result.success(true)
        } catch (e: Exception) {
            @Suppress("PrintStackTrace")
            e.printStackTrace()
            return Result.failure(e)
        }
    }

    override suspend fun getDevices(): Result<List<DeviceInformation>> {
        if (!adbIsConnect) {
            delay(INITIAL_ADB_RETRY_DELAY_MS)
            initialAdb()
        }

        return if (adbIsConnect) {
            val iDevices = androidDebugBridge.devices ?: arrayOf()
            val devices = iDevices.filter {
                it.isOnline
            }.map {
                it.toDeviceInformation()
            }

            Result.success(devices)
        } else {
            Result.failure(Throwable(resourceManager.string("adbIsNotInitialize")))
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun deviceChangeListener() = callbackFlow {
        val listener = object : AndroidDebugBridge.IDeviceChangeListener {
            override fun deviceConnected(device: IDevice) {
                println("deviceConnected -> " + device.serialNumber)
                trySend(device)
            }

            override fun deviceDisconnected(device: IDevice?) {
                println("deviceDisconnected -> " + device?.serialNumber)
                trySend(device)
            }

            @Suppress("EmptyFunctionBlock")
            override fun deviceChanged(device: IDevice?, changeMask: Int) {
            }
        }

        AndroidDebugBridge.addDeviceChangeListener(listener)

        awaitClose {
            AndroidDebugBridge.addDeviceChangeListener(null)
        }
    }

    override suspend fun executeADBShellCommand(device: DeviceInformation?, command: AdbCommand): Result<String> {
        return suspendCoroutine { continuation ->
            device?.iDevice?.executeShellCommand(
                command.command,
                object : IShellOutputReceiver {
                    val bos: ByteArrayOutputStream = ByteArrayOutputStream()

                    override fun addOutput(data: ByteArray?, offset: Int, length: Int) {
                        bos.write(data, offset, length)
                    }

                    override fun flush() {
                        try {
                            bos.flush()
                        } catch (e: IOException) {
                            adbLogger.printResult(device, command, "fail", e)
                        }
                        val msg = String(bos.toByteArray()).trim()
                        adbLogger.printResult(device, command, msg, null)
                        return if (command.successResult == SuccessResultEnum.EMPTY && msg.isEmpty()) {
                            continuation.resume(Result.success(msg))
                        } else if (command.successResult == SuccessResultEnum.NOT_EMPTY && msg.isNotEmpty()) {
                            continuation.resume(Result.success(msg))
                        } else if (command.successResult == SuccessResultEnum.EMPTY_OR_NOT_EMPTY) {
                            continuation.resume(Result.success(msg))
                        } else {
                            continuation.resume(Result.failure(Throwable(msg)))
                        }
                    }

                    override fun isCancelled(): Boolean {
                        return false
                    }
                }
            )
        }
    }

    @Suppress("TooGenericExceptionCaught")
    override suspend fun installApk(device: DeviceInformation?, packageFilePath: String): Result<String> {
        return suspendCoroutine { continuation ->
            device?.iDevice?.let {
                try {
                    device.iDevice.installPackage(packageFilePath, true)
                    continuation.resume(Result.success(""))
                } catch (e: Exception) {
                    continuation.resume(Result.failure(Throwable(e.message ?: resourceManager.string("errorGeneral"))))
                }
            } ?: run {
                continuation.resume(Result.failure(Throwable(resourceManager.string("selectADevice"))))
            }
        }
    }

    override suspend fun pullFile(
        device: DeviceInformation?,
        remoteFilepath: String,
        localFilePath: String
    ): Result<String> {
        return suspendCoroutine { continuation ->
            device?.iDevice?.let {
                val service: SyncService = device.iDevice.syncService
                try {
                    service.pullFile(
                        remoteFilepath,
                        localFilePath,
                        SyncService.getNullProgressMonitor()
                    )
                    continuation.resume(Result.success(""))
                } catch (e: SyncException) {
                    continuation.resume(Result.failure(Throwable(e.message ?: resourceManager.string("errorGeneral"))))
                } catch (e: TimeoutException) {
                    continuation.resume(Result.failure(Throwable(e.message ?: resourceManager.string("errorGeneral"))))
                } catch (e: IOException) {
                    continuation.resume(Result.failure(Throwable(e.message ?: resourceManager.string("errorGeneral"))))
                }
            } ?: run {
                continuation.resume(Result.failure(Throwable(resourceManager.string("selectADevice"))))
            }
        }
    }

    override suspend fun pushFile(
        device: DeviceInformation?,
        localFilePath: String,
        remoteFilepath: String
    ): Result<String> {
        return suspendCoroutine { continuation ->
            device?.iDevice?.let {
                val service: SyncService = device.iDevice.syncService
                try {
                    service.pushFile(
                        localFilePath,
                        remoteFilepath,
                        SyncService.getNullProgressMonitor()
                    )
                    continuation.resume(Result.success(""))
                } catch (e: SyncException) {
                    continuation.resume(Result.failure(Throwable(e.message ?: resourceManager.string("errorGeneral"))))
                } catch (e: TimeoutException) {
                    continuation.resume(Result.failure(Throwable(e.message ?: resourceManager.string("errorGeneral"))))
                } catch (e: IOException) {
                    continuation.resume(Result.failure(Throwable(e.message ?: resourceManager.string("errorGeneral"))))
                }
            }
        }
    }
}
