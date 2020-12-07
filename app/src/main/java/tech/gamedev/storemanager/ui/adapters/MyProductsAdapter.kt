package tech.gamedev.storemanager.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_product_list.view.*
import tech.gamedev.storemanager.R
import tech.gamedev.storemanager.data.models.Product

class MyProductsAdapter(private var products: ArrayList<Product>, private val context: Context): RecyclerView.Adapter<MyProductsAdapter.StoreViewHolder>() {

    private var productsList = products

    inner class StoreViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreViewHolder {
        return StoreViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_product_list,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: StoreViewHolder, position: Int) {
        val product = productsList[position]

        //SET PRODUCT IMAGE


       /* Glide.with(context)
            .load(product.getProductImg())
            .into(holder.itemView.ivProductListImg)*/

       holder.itemView.apply {
           tvProductPrice.text = product.getPrice().toString()
           tvProductName.text = product.getName()
           tvStock.text = product.getStock().toString()
           tvProductIdNumber.text = product.getProductId()
           }
       }


    override fun getItemCount(): Int {
       return productsList.size
    }

    fun dataChanged(newProducts: ArrayList<Product>){
        productsList.clear()
        productsList = newProducts
        notifyDataSetChanged()
    }

}




