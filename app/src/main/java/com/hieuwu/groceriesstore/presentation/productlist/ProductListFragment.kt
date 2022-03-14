package com.hieuwu.groceriesstore.presentation.productlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.hieuwu.groceriesstore.R
import com.hieuwu.groceriesstore.databinding.FragmentProductListBinding
import com.hieuwu.groceriesstore.domain.usecases.GetProductListUseCase
import com.hieuwu.groceriesstore.presentation.adapters.GridListItemAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProductListFragment : Fragment() {
    lateinit var binding: FragmentProductListBinding

    @Inject
    lateinit var getProductListUseCase: GetProductListUseCase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate<FragmentProductListBinding>(
            inflater, R.layout.fragment_product_list, container, false
        )

        val args = ProductListFragmentArgs.fromBundle(
            arguments as Bundle
        )

        val categoryName = args.categoryName
        var categoryId = args.categoryId
        val viewModelFactory =
            ProductListViewModelFactory(categoryId, getProductListUseCase)
        binding.toolbar.title = categoryName

        val viewModel = ViewModelProvider(this, viewModelFactory)
            .get(ProductListViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        setUpRecyclerView(viewModel)
        viewModel.navigateToSelectedProperty.observe(this.viewLifecycleOwner, {
            if (null != it) {
                val direction =
                    ProductListFragmentDirections.actionProductListFragmentToProductDetailFragment(
                        it.id
                    )
                findNavController().navigate(direction)
                viewModel.displayProductDetailComplete()
            }
        })

        viewModel.currentCart.observe(viewLifecycleOwner, {})

        viewModel.productList.observe(viewLifecycleOwner, {
            if (it.isEmpty()) {
                binding.productRecyclerview.visibility = View.GONE
                binding.emptyLayout.visibility = View.VISIBLE
            }
        })

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_filter -> {
                    showFilterDialog()
                    val bottomSheetDialogFragment = FilterFragment()
                    bottomSheetDialogFragment.show(
                        activity?.supportFragmentManager!!,
                        bottomSheetDialogFragment.tag
                    )
                    true
                }
                else -> false
            }
        }


        return binding.root
    }

    private fun showFilterDialog() {

    }

    private fun setUpRecyclerView(viewModel: ProductListViewModel) {
        binding.productRecyclerview.adapter =
            GridListItemAdapter(
                GridListItemAdapter.OnClickListener(
                    clickListener = {
                        viewModel.displayProductDetail(it)
                    },
                    addToCartListener = {
                        viewModel.addToCart(it)
                        showSnackBar(it.name)
                    },
                )
            )
    }

    private fun showSnackBar(productName: String?) {
        Snackbar.make(
            requireActivity().findViewById(android.R.id.content),
            "Added $productName",
            Snackbar.LENGTH_SHORT
        ).show()
    }
}