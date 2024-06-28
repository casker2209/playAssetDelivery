plugins {
    id("com.android.asset-pack")
}

assetPack {
    packName.set("assetPackFastFollow") // Directory name for the asset pack
    dynamicDelivery {
        deliveryType.set("fast-follow")
    }
}