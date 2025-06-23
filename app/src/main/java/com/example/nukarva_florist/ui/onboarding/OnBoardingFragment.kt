package com.example.nukarva_florist.ui.onboarding

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.example.nukarva_florist.R
import com.example.nukarva_florist.databinding.FragmentOnboardingBinding
import com.example.nukarva_florist.ui.auth.LoginActivity
import com.example.nukarva_florist.ui.auth.RegisterActivity

class OnBoardingFragment : Fragment() {
    private var _binding: FragmentOnboardingBinding? = null
    private val binding get() = _binding!!

    private val titles = listOf(
        "Welcome to Plantify: Your Digital Oasis for Plant Lovers!",
        "Unlock the World of Plants with Our App!",
        "Seamless Shopping Experience"
    )

    private val descriptions = listOf(
        "Welcome to Platify, your ultimate destination for all things plant-related. Get ready to embark on an exciting journey into the world of lush greenery and discover the joy of nurturing your own plants.",
        "Dive into a virtual garden brimming with a wide variety of plants. Swipe through our curated collection, from vibrant flowers to elegant succulents, and everything in between.",
        "With our user-friendly interface and secure payment options, shopping for your favorite plants has never been easier. Add plants to your cart, explore related products, and enjoy a hassle-free checkout process. Your plant companions will be on their way to you in no time."
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val images = listOf(
            R.drawable.img_onboarding1,
            R.drawable.img_onboarding2,
            R.drawable.img_onboarding3
        )

        val adapter = OnBoardingAdapter(images)
        binding.viewPager.adapter = adapter
        binding.dotsIndicator.attachTo(binding.viewPager)

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.tvTitle.text = titles[position]
                binding.tvDescription.text = descriptions[position]
            }
        })

        val buttonColor = ContextCompat.getColor(requireContext(), R.color.main_color_100)
        binding.btnSkip.backgroundTintList = ColorStateList.valueOf(buttonColor)
        binding.btnSkip.setOnClickListener {
        }

        val buttonColorMain = ContextCompat.getColor(requireContext(), R.color.main_color_500)
        binding.btnGetStarted.backgroundTintList = ColorStateList.valueOf(buttonColorMain)
        binding.btnGetStarted.setOnClickListener {
            navigateToLoginActivity()
        }
    }

    private fun navigateToLoginActivity() {
        val intent = Intent(requireContext(), RegisterActivity::class.java)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
