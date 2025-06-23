import android.os.Parcelable
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    @SerializedName("productId")
    val productId: Int,

    @SerializedName("categoryId")
    val categoryId: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("description")
    val description: String,

    @SerializedName("price")
    val price: Double,

    @SerializedName("discountPrice")
    val discountPrice: Double,

    @SerializedName("stockQuantity")
    val stockQuantity: Int,

    @SerializedName("imageUrls")
    val imageUrls: String, // still a JSON array string

    @SerializedName("createdAt")
    val createdAt: String,

    @SerializedName("updatedAt")
    val updatedAt: String,

    @SerializedName("productSize")
    val productSize: List<String>,

    @SerializedName("plantType")
    val plantType: String,

    @SerializedName("heightInches")
    val heightInches: Double,

    @SerializedName("humidityPercentage")
    val humidityPercentage: Int,

    @SerializedName("climate")
    val climate: String,

    var isFavorite: Boolean = false
) : Parcelable {

    // JSON parsing logic
    fun getImageUrlsList(): List<String> {
        return try {
            val gson = Gson()
            val listType = object : TypeToken<List<String>>() {}.type
            gson.fromJson(imageUrls, listType) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun getFirstImageUrl(): String {
        return getImageUrlsList().firstOrNull().orEmpty()
    }

    fun getFormattedPrice(): String = "Rp ${String.format("%,.0f", price)}"

    fun getFormattedDiscountPrice(): String =
        if (discountPrice > 0) "Rp ${String.format("%,.0f", discountPrice)}" else ""

    fun getFinalPrice(): Double = if (discountPrice > 0) price - discountPrice else price

    fun getFormattedFinalPrice(): String = "Rp ${String.format("%,.0f", getFinalPrice())}"

    fun hasDiscount(): Boolean = discountPrice > 0

    fun getCategoryName(): String {
        return when (categoryId) {
            1 -> "Indoor"
            2 -> "Outdoor"
            3 -> "Hanging"
            4 -> "Succulent"
            else -> "Plant"
        }
    }
}
