package com.example.nukarva_florist.ui.home

import Product
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.nukarva_florist.R
import com.example.nukarva_florist.data.AppPreferences
import com.example.nukarva_florist.data.model.Offer
import com.example.nukarva_florist.databinding.HomeLayoutBinding
import com.example.nukarva_florist.repository.ShimmerAdapter
import com.example.nukarva_florist.ui.menu.FavoriteActivity
import com.example.nukarva_florist.ui.product.ProductDetailActivity
import com.example.nukarva_florist.utils.GridSpacingItemDecorationHorizontalOnly
import com.example.nukarva_florist.utils.Resource
import com.example.nukarva_florist.viewmodel.CategoriesViewModel
import com.example.nukarva_florist.viewmodel.ProductsViewModel
import com.google.android.material.appbar.AppBarLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    lateinit var appPreferences: AppPreferences
    private var _binding: HomeLayoutBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CategoriesViewModel by viewModels()
    private val viewProductModel: ProductsViewModel by viewModels()
    private lateinit var plantsAdapter: PlantsAdapter
    private var categoryScrollTop = 0
    private var isNavigatingToDetail = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = HomeLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appPreferences = AppPreferences(requireContext())

        setUpToolbar()
        setupViewPager()
        setupCategoryChips()
        setupRecyclerView()
    }

    private fun setUpToolbar(){
        binding.favoritesIconToolbar.setOnClickListener {
            val intent = Intent(requireContext(), FavoriteActivity::class.java)
            startActivity(intent)
        }

        binding.favoritesIcon.setOnClickListener {
            val intent = Intent(requireContext(), FavoriteActivity::class.java)
            startActivity(intent)
        }

        binding.appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
            val isCollapsed = Math.abs(verticalOffset) >= binding.appBarLayout.totalScrollRange

            if (isCollapsed) {
                // Header is collapsed
                binding.toolbar.animate().alpha(1f).setDuration(200).start()
                // Optionally update title or show elements
                binding.toolbar.visibility = View.VISIBLE
                binding.categoryScrollSticky.translationY = binding.toolbar.height.toFloat()
            } else {
                // Header is expanded
                binding.toolbar.animate().alpha(1f).setDuration(200).start()
                // Optionally hide/show elements
                binding.toolbar.visibility = View.GONE
                binding.categoryScrollSticky.translationY = 0f
            }
        })

        binding.root.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                // Dapatkan posisi top dari category_scroll
                categoryScrollTop = binding.categoryScroll.top

                // Set listener untuk scroll
                binding.nestedScrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
                    if (scrollY >= categoryScrollTop) {
                        binding.categoryScrollSticky.visibility = View.VISIBLE
                    } else {
                        binding.categoryScrollSticky.visibility = View.GONE
                    }
                }

                // Hanya panggil sekali
                binding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    private fun setupViewPager() {
        binding.offersViewpager.apply {
            clipToPadding = false
            clipChildren = false
            offscreenPageLimit = 3
            setPadding(60, 0, 60, 0) //
        }

        val offers = listOf(
            Offer(R.drawable.img_offer_1, "30% Discount", "Get discount for every orders, only valid for today"),
            Offer(R.drawable.img_offer_2, "10% Discount", "Limited time offer on select items"),
            Offer(R.drawable.img_offer_2, "10% Discount", "Limited time offer on select items")
        )

        val offerAdapter = SpecialOfferAdapter(offers)
        binding.offersViewpager.adapter = offerAdapter

        // Hubungkan ke dots indicator jika pakai library seperti DotsIndicator
        binding.dotsIndicator.attachTo(binding.offersViewpager)
        binding.dotsIndicator.setViewPager2(binding.offersViewpager)
    }

    @SuppressLint("ResourceType")
    private fun setupCategoryChips() {
        viewModel.categories.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    // Tampilkan loading jika perlu
                }
                is Resource.Success -> {
                    val categories = resource.data ?: return@observe
                    binding.categoryRadioGroup.removeAllViews()
                    binding.stickyCategoryRadioGroup.removeAllViews()

                    categories.forEachIndexed { index, category ->
                        // Buat radio button utama
                        val radioButton = RadioButton(requireContext()).apply {
                            text = category.name
                            buttonDrawable = null
                            background = ContextCompat.getDrawable(context, R.drawable.chip_category_background)
                            setTextColor(ContextCompat.getColorStateList(context, R.drawable.chip_category_text_color))
                            setPadding(24, 12, 24, 12)
                            textSize = 16f
                            typeface = ResourcesCompat.getFont(context, R.font.urbanist_medium)
                            layoutParams = RadioGroup.LayoutParams(
                                RadioGroup.LayoutParams.WRAP_CONTENT,
                                RadioGroup.LayoutParams.WRAP_CONTENT
                            ).apply {
                                setMargins(0, 0, 20, 0)
                            }
                        }

                        binding.categoryRadioGroup.addView(radioButton)

                        // Buat copy-nya untuk sticky
                        val copy = RadioButton(requireContext()).apply {
                            text = category.name
                            buttonDrawable = null
                            background = ContextCompat.getDrawable(context, R.drawable.chip_category_background)
                            setTextColor(ContextCompat.getColorStateList(context, R.drawable.chip_category_text_color))
                            setPadding(24, 12, 24, 12)
                            textSize = 16f
                            typeface = ResourcesCompat.getFont(context, R.font.urbanist_medium)
                            layoutParams = RadioGroup.LayoutParams(
                                RadioGroup.LayoutParams.WRAP_CONTENT,
                                RadioGroup.LayoutParams.WRAP_CONTENT
                            ).apply {
                                setMargins(0, 0, 20, 0)
                            }

                            setOnClickListener {
                                radioButton.isChecked = true
                                radioButton.callOnClick()
                            }
                        }

                        binding.stickyCategoryRadioGroup.addView(copy)

                        // Pilih default (All)
                        if (index == 0) {
                            radioButton.isChecked = true
                            copy.isChecked = true
                            // filterProductsByCategory(category)
                        }

                        radioButton.setOnClickListener {
                            copy.isChecked = true
                            // filterProductsByCategory(category)
                        }
                    }
                }
                is Resource.Error -> {
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Trigger pengambilan data kategori
        viewModel.getCategories()
    }


    private fun setupRecyclerView() {
        binding.shimmerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.shimmerView.adapter = ShimmerAdapter()

        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.grid_spacing)
        binding.shimmerView.addItemDecoration(
            GridSpacingItemDecorationHorizontalOnly(2, spacingInPixels)
        )

        plantsAdapter = PlantsAdapter(mutableListOf())

        binding.plantsRecycler.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = plantsAdapter
            setHasFixedSize(true)
            addItemDecoration(GridSpacingItemDecorationHorizontalOnly(2, spacingInPixels))
        }

        plantsAdapter.setOnItemClickListener { product ->
            if (!isNavigatingToDetail) {
                isNavigatingToDetail = true

                val intent = Intent(requireContext(), ProductDetailActivity::class.java)
                intent.putExtra("product", product)
                startActivity(intent)

                // Reset flag setelah delay 500ms (bisa disesuaikan)
                Handler(Looper.getMainLooper()).postDelayed({
                    isNavigatingToDetail = false
                }, 500)
            }
        }


        plantsAdapter.setOnFavoriteClickListener { product, position ->
            product.isFavorite = !product.isFavorite
            handleFavoriteClick(product, position)
            plantsAdapter.notifyItemChanged(position)
        }

        // Observe data
        viewProductModel.products.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Resource.Loading -> {
                    binding.swipeRefreshLayout.isRefreshing = false
                    binding.shimmerLayout.startShimmer()
                    binding.shimmerLayout.visibility = View.VISIBLE
                    binding.plantsRecycler.visibility = View.GONE
                }

                is Resource.Success -> {
                    binding.swipeRefreshLayout.isRefreshing = false
                    binding.shimmerLayout.stopShimmer()
                    binding.shimmerLayout.visibility = View.GONE
                    binding.plantsRecycler.visibility = View.VISIBLE

                    result.data?.let {
                        it.forEach { product ->
                            product.isFavorite = appPreferences.isFavorite(product.productId)
                        }
                        plantsAdapter.updateProducts(it)
                    }
                }

                is Resource.Error -> {
                    binding.swipeRefreshLayout.isRefreshing = false
                    binding.shimmerLayout.stopShimmer()
                    binding.plantsRecycler.visibility = View.GONE
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Trigger initial load
        viewProductModel.getProducts()

        // Pull to refresh
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewProductModel.getProducts()
        }
    }


    private fun handleFavoriteClick(product: Product, position: Int) {
        // Save favorite status to local storage
        if (product.isFavorite) {
            appPreferences.addFavorite(product.productId)
            showMessage("${product.name} added to favorites")
        } else {
            appPreferences.removeFavorite(product.productId)
            showMessage("${product.name} removed from favorites")
        }
    }

    private fun showError(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    private fun showMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    @Override
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @Override
    override fun onResume() {
        super.onResume()
        plantsAdapter.updateFavoriteState { productId ->
            appPreferences.isFavorite(productId)
        }
    }

}
