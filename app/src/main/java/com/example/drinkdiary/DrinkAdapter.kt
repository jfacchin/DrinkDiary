package com.example.drinkdiary

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.drinkdiary.data.Drink
import com.example.drinkdiary.databinding.ItemDrinkBinding
import java.util.Locale

class DrinkAdapter(
    private val onDrinkClick: (Drink) -> Unit
) : RecyclerView.Adapter<DrinkAdapter.DrinkViewHolder>() {

    private var drinks: List<Drink> = emptyList()

    fun updateDrinks(newDrinks: List<Drink>) {
        drinks = newDrinks
        notifyDataSetChanged()
    }

    class DrinkViewHolder(
        val binding: ItemDrinkBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DrinkViewHolder {
        val binding = ItemDrinkBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DrinkViewHolder(binding)
    }

    override fun getItemCount(): Int = drinks.size

    override fun onBindViewHolder(
        holder: DrinkViewHolder,
        position: Int
    ) {
        val drink = drinks[position]

        holder.binding.drinkName.text = drink.name
        holder.binding.drinkCafe.text = drink.cafe
        holder.binding.drinkType.text = drink.type

        holder.binding.drinkPrice.text =
            String.format(Locale.US, "$%.2f", drink.price)

        val rating = drink.rating.coerceIn(0, 5)

        holder.binding.drinkRating.text =
            "★".repeat(rating) + "☆".repeat(5 - rating)

        holder.binding.root.setOnClickListener {
            onDrinkClick(drink)
        }
    }
}