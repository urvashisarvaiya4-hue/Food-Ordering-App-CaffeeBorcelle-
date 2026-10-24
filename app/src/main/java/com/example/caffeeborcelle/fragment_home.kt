package com.example.caffeeborcelle

import android.os.Bundle
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.example.caffeeborcelle.MyAdapter.BestsellersAdapter
import com.example.caffeeborcelle.MyAdapter.CategoryAdapter
import com.example.caffeeborcelle.MyAdapter.QuickPickAdapter
import com.example.caffeeborcelle.R
import com.example.caffeeborcelle.databinding.FragmentHomeBinding
import com.example.caffeeborcelle.model.CategoryModel
import com.example.caffeeborcelle.viewmodel.ProductViewModel
import com.google.android.material.snackbar.Snackbar


class fragment_home : Fragment() {


    private lateinit var binding: FragmentHomeBinding
    private lateinit var bestSellerAdapter: BestsellersAdapter
    //private lateinit var rvQuick: RecyclerView


    private val homeViewModel: ProductViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.BestSellers.setOnClickListener {
            val blinkAnim =
                AnimationUtils.loadAnimation(requireContext(), R.anim.animation)
            binding.BestSellers.startAnimation(blinkAnim)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupImageSlider()
        setupRecyclerView()
        setupCategoryRecyclerView()
        observeProducts()

        val quickList = listOf(
            "Best Seller",
            "Coffee Lover",
            "Healthy",
            "Cheese Special"
        )

        binding.rvQuick.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        binding.rvQuick.adapter = QuickPickAdapter(quickList)}
    // ================= IMAGE SLIDER =================
    private fun setupImageSlider() {

        val imageList = arrayListOf(
            SlideModel(R.drawable.img_5, ScaleTypes.FIT),
            SlideModel(R.drawable.img_6, ScaleTypes.FIT),
            SlideModel(R.drawable.img_9, ScaleTypes.FIT)
        )

        binding.ImageSlider.setImageList(imageList, ScaleTypes.FIT)

        binding.ImageSlider.setItemClickListener(object : ItemClickListener {

            override fun doubleClick(position: Int) {}

            override fun onItemSelected(position: Int) {

                Snackbar.make(
                    binding.root,
                    "Coffee $position selected",
                    Snackbar.LENGTH_SHORT
                ).show()

                val blinkAnim =
                    AnimationUtils.loadAnimation(requireContext(), R.anim.animation)
                binding.BestSellers.startAnimation(blinkAnim)
            }
        })
    }

    // ================= BEST SELLER =================
    private fun setupRecyclerView() {

        bestSellerAdapter = BestsellersAdapter(requireContext())

        binding.BestSellerRecyclerView.layoutManager =
            GridLayoutManager(requireContext(), 2)

        binding.BestSellerRecyclerView.adapter = bestSellerAdapter
    }


    // ================= CATEGORY =================
    private fun setupCategoryRecyclerView() {

        val categoryList = listOf(
            CategoryModel("Pizza", R.drawable.download),
            CategoryModel("Burger", R.drawable.download3),
            CategoryModel("Dessert", R.drawable.download1),
            CategoryModel("Thikshake", R.drawable.download2),
            CategoryModel("Tea-Coffee", R.drawable.download4)
        )

        val adapter = CategoryAdapter(categoryList) { categoryName ->

            val bundle = Bundle()
            bundle.putString("category", categoryName)

            findNavController().navigate(
                R.id.categoryProductsFragment,
                bundle
            )
        }

        binding.rvCategories.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        binding.rvCategories.adapter = adapter
    }


    override fun onResume() {
        super.onResume()

        requireActivity()
            .findViewById<TextView>(R.id.toolbar_title)
            ?.text = "CoffeeBorcelle"
    }

    // ================= FIREBASE =================
    private fun observeProducts() {

        homeViewModel.allCoffee.observe(viewLifecycleOwner) { list ->
            bestSellerAdapter.submitList(list)
        }

    }}