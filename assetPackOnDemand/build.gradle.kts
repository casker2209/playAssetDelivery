plugins {
    id("com.android.asset-pack")
}

assetPack {
    packName.set("assetPackOnDemand") // Directory name for the asset pack
    dynamicDelivery {
        deliveryType.set("on-demand")
    }
}