package mumble.mbmessages.iam

import mumble.mburger.sdk.kt.MBPlugins.MBPlugin

class MBMessages : MBPlugin() {

    override var id: String? = "MBMessages"

    override var order: Int
        get() = super.order
        set(value) {}

    override fun init() {
        super.init()
    }
}