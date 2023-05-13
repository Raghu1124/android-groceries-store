package com.hieuwu.groceriesstore.presentation.orderhistory

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@Composable
fun OrderHistoryScreen(
//    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: OrderListViewModel = hiltViewModel()
) {
    val orderList = viewModel.orderList.collectAsState()
    Column(modifier = modifier) {
        Text("This his order history screen")
        LazyColumn {
            items(orderList.value) { it ->
                Text(text = it.total.toString())
            }
        }
    }

}