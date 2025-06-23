package com.example.nukarva_florist.ui.menu

import Product
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.nukarva_florist.data.AppPreferences
import com.example.nukarva_florist.databinding.ActivityFavoriteBinding
import com.example.nukarva_florist.ui.bottomsheet.DynamicSortBottomSheet
import com.example.nukarva_florist.ui.home.PlantsAdapter
import com.example.nukarva_florist.utils.Resource
import com.example.nukarva_florist.viewmodel.ProductsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoriteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoriteBinding
    private lateinit var adapter: PlantsAdapter
    private lateinit var appPreferences: AppPreferences
    private val productViewModel: ProductsViewModel by viewModels()
    private var currentSortOption: String = "Latest Saved"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarRegular.toolbarTitle.setText("Favorites")
        setSupportActionBar(binding.toolbarRegular.toolbar)

        appPreferences = AppPreferences(this)
        setupRecyclerView()
        loadFavoriteProducts()
        setUpSortFilter()
    }

    private fun sortFavoriteProducts(products: List<Product>, sortOption: String): List<Product> {
        return when (sortOption) {
            "Latest Saved" -> products.sortedByDescending { appPreferences.getFavoriteSavedTimestamp(it.productId) }
            "Longest Saved" -> products.sortedBy { appPreferences.getFavoriteSavedTimestamp(it.productId) }
            "Highest Price" -> products.sortedByDescending { it.price }
            "Lowest Price" -> products.sortedBy { it.price }
            else -> products
        } }

    private fun setUpSortFilter(){
        val sortOptions = listOf("Latest Saved", "Longest Saved", "Most Reviews", "Highest Price", "Lowest Price")

        binding.tvSortFilter.setOnClickListener {
            val sortSheet = DynamicSortBottomSheet(
                title = "Sort Notifications",
                options = sortOptions,
                selectedOption = currentSortOption
            ) { selected ->
                currentSortOption = selected
                Toast.makeText(this, "Selected: $selected", Toast.LENGTH_SHORT).show()
                loadFavoriteProducts()
            }

            sortSheet.show(supportFragmentManager, "SortBottomSheet")
        }
    }

    private fun setupRecyclerView() {
        adapter = PlantsAdapter(mutableListOf())

        binding.plantsRecycler.apply {
            layoutManager = GridLayoutManager(this@FavoriteActivity, 2)
            adapter = this@FavoriteActivity.adapter
        }

        adapter.setOnItemClickListener { product ->
            Toast.makeText(this, "Clicked: ${product.name}", Toast.LENGTH_SHORT).show()
        }

        adapter.setOnFavoriteClickListener { product, position ->
            product.isFavorite = !product.isFavorite
            handleFavoriteClick(product)
            loadFavoriteProducts()
        }
    }

    private fun loadFavoriteProducts() {
        productViewModel.products.observe(this) { result ->
            when (result) {
                is Resource.Success -> {
                    val favoritesId = appPreferences.getFavorites()
                    val favoriteProducts = result.data?.filter { product ->
                        favoritesId.contains(product.productId)
                    } ?: emptyList()

                    val sortedProducts = sortFavoriteProducts(favoriteProducts, currentSortOption)
                    sortedProducts.forEach { it.isFavorite = true }
                    adapter.updateProducts(sortedProducts)
                    Log.d("TimestampCheck", "productId: ${sortedProducts[0].productId}, timestamp: ${appPreferences.getFavoriteSavedTimestamp(
                        sortedProducts[0].productId)}")
                    binding.tvTotalFavorite.text = "${sortedProducts.size} Plant"
                }

                is Resource.Error -> {
                    Toast.makeText(this, result.message, Toast.LENGTH_SHORT).show()
                }

                is Resource.Loading -> {
                    // Optional loading UI
                }
            }
        }

        productViewModel.getProducts()
    }

    private fun handleFavoriteClick(product: Product) {
        if (product.isFavorite) {
            appPreferences.addFavorite(product.productId)
            showMessage("${product.name} added to favorites")
        } else {
            appPreferences.removeFavorite(product.productId)
            showMessage("${product.name} remove from favorites")
        }
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}