package androidstudio.tools.missed.manager.resource

import com.intellij.DynamicBundle
import org.jetbrains.annotations.NonNls
import org.jetbrains.annotations.PropertyKey

@NonNls
const val BUNDLE = "string.String"

class ResourceManager : DynamicBundle(BUNDLE) {

    fun string(@PropertyKey(resourceBundle = BUNDLE) key: String, vararg params: Any) = getMessage(key, *params)
}
