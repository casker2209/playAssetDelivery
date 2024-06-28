plugins {
    id("com.android.asset-pack")
}

assetPack {
    packName.set("assetPackInstallTime") // Directory name for the asset pack
    dynamicDelivery {
        deliveryType.set("install-time")
    }
}