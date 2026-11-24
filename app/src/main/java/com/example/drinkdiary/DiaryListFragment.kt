package com.example.drinkdiary

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.drinkdiary.databinding.FragmentDiaryListBinding

class DiaryListFragment : Fragment(R.layout.fragment_diary_list) {

    private val viewModel: DrinkViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentDiaryListBinding.bind(view)

        // Open the details screen when a drink is selected
        val adapter = DrinkAdapter { drink ->

            val detailFragment = DrinkDetailFragment()

            val bundle = Bundle()
            bundle.putLong("drinkId", drink.id)
            detailFragment.arguments = bundle

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, detailFragment)
                .addToBackStack(null)
                .commit()
        }

        // Set up the RecyclerView
        binding.drinkRecyclerView.layoutManager =
            LinearLayoutManager(requireContext())

        binding.drinkRecyclerView.adapter = adapter

        // Observe saved drinks from the database
        viewModel.allDrinks.observe(viewLifecycleOwner) { drinks ->

            adapter.updateDrinks(drinks)

            binding.emptyMessage.visibility =
                if (drinks.isEmpty()) View.VISIBLE else View.GONE

        }
    }
}