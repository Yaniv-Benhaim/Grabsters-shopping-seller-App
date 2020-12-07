package tech.gamedev.storemanager.data.models

data class Store(
    private var name: String = "",
    private var description: String = "",
    private var imgBackground: String = "",
    private var imgLogo: String = "",
    private var email: String = "",
    private var fullName: String = "",
    private var phoneNumber: String = "",
    private var storeName: String = "",
    private var country: String = "",
    private var city: String = "",
    private var category: String = "",
    private var addressLine1: String = "",
    private var addressLine2: String = "",
    private var sellerUid: String = ""

) {
    fun getSellerUid(): String{
        return sellerUid
    }

    fun getFullName(): String{
        return fullName
    }

    fun getPhoneNumber(): String{
        return phoneNumber
    }

    fun getStoreName(): String{
        return storeName
    }

    fun getCountry(): String{
        return country
    }

    fun getCity(): String{
        return city
    }

    fun getCategory(): String{
        return category
    }

    fun getAddressLine1(): String{
        return addressLine1
    }

    fun getAddressLine2(): String{
        return addressLine2
    }

    fun getEmail(): String{
        return email
    }

    fun getName(): String{
        return name
    }

    fun getDescription(): String{
        return description
    }

    fun getImgBackground(): String{
        return imgBackground
    }

    fun getImgLogo(): String{
        return imgLogo
    }
}