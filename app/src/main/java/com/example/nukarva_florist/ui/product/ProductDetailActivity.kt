package com.example.nukarva_florist.ui.product

import Product
import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.TextView
import android.widget.TextView.BufferType
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.nukarva_florist.R
import com.example.nukarva_florist.data.AppPreferences
import com.example.nukarva_florist.databinding.ActivityPlantDetailBinding
import com.example.nukarva_florist.ui.bottomsheet.ProductOrderBottomSheet
import com.example.nukarva_florist.utils.AppUtil
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONArray

@AndroidEntryPoint
class ProductDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlantDetailBinding
    private lateinit var appPreferences: AppPreferences
    private lateinit var appUtil:AppUtil
    private var isExpanded = false

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlantDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        appPreferences = AppPreferences(this)

        binding.toolbar.btnFavorite.setColorFilter(ContextCompat.getColor(this, R.color.font_black))

        val product = intent.getParcelableExtra<Product>("product")?.apply {
            isFavorite = appPreferences.isFavorite(productId)
            if (isFavorite) {
                binding.toolbar.btnFavorite.setImageResource(R.drawable.ic_heart_filled)
                binding.toolbar.btnFavorite.clearColorFilter()
            }
        }

        product?.let {
            binding.tvProductTitle.text = it.name
            setupReadMore(it.description)
            binding.tvPrice.text = it.getFormattedPrice()
            binding.tvClimate.text = it.climate
            binding.tvType.text = it.plantType
            binding.tvHeight.text = "${it.heightInches}"
            binding.tvHumidity.text = "${it.humidityPercentage}%"

            binding.toolbar.toolbarTitle.text = it.name

            val urls = JSONArray(it.imageUrls)
            val imageUrl = urls.getString(0)
            val fixedUrl = imageUrl.replace("localhost", "10.0.2.2")

            Glide.with(this)
                .load(fixedUrl)
                .placeholder(R.drawable.img_example_plants)
                .into(binding.imgProduct)

            setUpBtnAddToCart(it)
        }

        binding.toolbar.btnFavorite.setOnClickListener {
            product?.let {
                val isFavorite = appPreferences.isFavorite(it.productId)

                if (isFavorite) {
                    appPreferences.removeFavorite(it.productId)
                    updateFavoriteIcon(isFavorite = false, animate = true)
                    Toast.makeText(this, "${it.name} removed from favorites", Toast.LENGTH_SHORT).show()
                } else {
                    appPreferences.addFavorite(it.productId)
                    updateFavoriteIcon(isFavorite = true, animate = true)
                    Toast.makeText(this, "${it.name} added to favorites", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    private fun updateFavoriteIcon(isFavorite: Boolean, animate: Boolean) {
        val heartView = binding.toolbar.btnFavorite

        if (isFavorite) {
            heartView.setImageResource(R.drawable.ic_heart_filled)
            heartView.clearColorFilter()
            if (animate) AppUtil.animateHeartFill(heartView)
        } else {
            heartView.setImageResource(R.drawable.ic_heart_outline)
            heartView.setColorFilter(ContextCompat.getColor(this, R.color.font_black))
            if (animate) AppUtil.animateHeartUnfill(heartView)
        }
    }

    private fun setupReadMore(description: String) {
        binding.tvDescription.text = description
        binding.tvDescription.maxLines = 3
        binding.tvDescription.ellipsize = TextUtils.TruncateAt.END

        if (description.length > 200) {
            binding.tvReadMore.visibility = View.VISIBLE
        } else {
            binding.tvReadMore.visibility = View.GONE
        }

        binding.tvReadMore.setOnClickListener {
            isExpanded = !isExpanded
            TransitionManager.beginDelayedTransition(binding.root as ViewGroup) // smooth animation

            if (isExpanded) {
                binding.tvDescription.maxLines = Integer.MAX_VALUE
                binding.tvDescription.ellipsize = null
                binding.tvReadMore.text = "Tampilkan Lebih Sedikit"
            } else {
                binding.tvDescription.maxLines = 3
                binding.tvDescription.ellipsize = TextUtils.TruncateAt.END
                binding.tvReadMore.text = "Baca Selengkapnya"
            }
        }
    }

    private fun setUpBtnAddToCart(product: Product) {
        val buttonColor = ContextCompat.getColor(this, R.color.main_color_500)
        binding.btnAddToCart.backgroundTintList = ColorStateList.valueOf(buttonColor)

        binding.btnAddToCart.setOnClickListener {
            val bottomSheet = ProductOrderBottomSheet(product)
            bottomSheet.show(supportFragmentManager, "ProductOrderBottomSheet")
        }
    }
}
