package com.keego.playassetdelivery

import android.content.res.AssetManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.play.core.assetpacks.AssetPackManager
import com.google.android.play.core.assetpacks.AssetPackManagerFactory
import com.google.android.play.core.assetpacks.AssetPackState
import com.google.android.play.core.assetpacks.model.AssetPackStatus
import com.google.android.play.core.ktx.status
import com.keego.playassetdelivery.databinding.ActivityMainBinding
import com.keego.playassetdelivery.databinding.LayoutItemBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    var binding: ActivityMainBinding? = null
    val manager by lazy{
        AssetPackManagerFactory.getInstance(this@MainActivity)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setupView()
        setContentView(binding!!.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    fun setupView() {
        binding?.apply {
            loadInstallTime()
            getPackStates()

            viewFastFollow(manager)
            viewOnDemand(manager)

            tvInstallTime.setOnClickListener {
                loadInstallTime()
            }
            tvFastFollow.setOnClickListener {
                getPackStates()
                loadFastFollow()
            }
            tvOnDemand.setOnClickListener {
                getPackStates()
                loadOnDemand()
            }
            manager.registerListener {
                binding?.apply {
                    if (it.name() == "assetPackOnDemand" && it.status() == AssetPackStatus.COMPLETED) {
                        viewOnDemand(manager)
                    }
                }
            }

            manager.registerListener {
                binding?.apply {
                    if (it.name() == "assetPackFastFollow" && it.status() == AssetPackStatus.COMPLETED) {
                        println("Completed:" + it.name())
                        println("Path:" + manager.getPackLocation(it.name())?.assetsPath())
                        viewFastFollow(manager)
                    }
                }
            }
        }
    }

    fun loadInstallTime() {
        binding?.apply {
            tvAssetInstallTime.text = "Asset:" + this@MainActivity.assets.list("")?.map {
                it + "\n"
            }
        }
    }

    fun loadFastFollow() {
        lifecycleScope.launch(Dispatchers.IO) {
            manager.fetch(listOf("assetPackFastFollow"))
                .addOnSuccessListener {
                    println("Success")
                    println( manager.getPackLocation("assetPackFastFollow")?.assetsPath())
                    println(manager.packLocations["assetPackFastFollow"]?.assetsPath())

                }
                .addOnFailureListener(this@MainActivity) { p0 -> println("Error:" + p0.message) }
        }
    }

    private fun ActivityMainBinding.viewFastFollow(manager: AssetPackManager) {
        var path = manager.getPackLocation("assetPackFastFollow")?.assetsPath()
        tvFastFollow.text = "Fast Follow path:$path"
        path?.let { path ->
            var file = File(path)
            if (file.exists()) {
                tvAssetFastFollow.text = "Asset:" + file.listFiles().filter { it.isFile }.map {
                    it.name + "\n"
                }
            }
        }
    }

    fun loadOnDemand() {
        lifecycleScope.launch(Dispatchers.IO) {
            manager.fetch(listOf("assetPackOnDemand"))
                .addOnSuccessListener {
                    println("Success")
                    println("Path:" + manager.packLocations["assetPackOnDemand"]?.assetsPath())
                }
                .addOnFailureListener(this@MainActivity) { p0 -> println("Error:" + p0.message) }


        }
    }

    private fun ActivityMainBinding.viewOnDemand(manager: AssetPackManager) {
        var path = manager.getPackLocation("assetPackOnDemand")?.assetsPath()
        tvOnDemand.text = "On Demand path:$path"
        path?.let { path ->
            var file = File(path)
            if (file.exists()) {
                tvAssetOnDemand.text = "Asset:" + file.listFiles().filter { it.isFile }.map {
                    it.name + "\n"
                }
            }
        }
    }

    fun getPackStates() {
        lifecycleScope.launch(Dispatchers.IO) {
            manager.getPackStates(listOf("assetPackFastFollow", "assetPackOnDemand"))
                .addOnSuccessListener {
                    it.packStates().values.forEach {
                        println("State of ${it.name()}:${it.status()}")
                        if (it.status == AssetPackStatus.COMPLETED) {
                            manager.packLocations.forEach { (packName, location) ->
                                if (packName == it.name()) {
                                    println("Location of $packName:${location.assetsPath()}")
                                }
                            }
                        }
                    }
                }
                .addOnFailureListener(this@MainActivity) { p0 -> println("Error:" + p0.message) }
        }
    }
}

class ItemAdapter(var items: List<String> = listOf()) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {
    inner class ItemViewHolder(val binding: LayoutItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = LayoutItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

    }

}