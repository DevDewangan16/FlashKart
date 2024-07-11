package com.example.flashkart.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.flashkart.R
import com.example.flashkart.data.InternetItemWithQuantity
import com.example.flashkart.data.InternetItems
import com.example.flashkart.data.Item
import java.lang.reflect.Modifier

@Composable
fun CartScreen(flashViewModel: FlashViewModel,
               onHomeButtonClicked:()->Unit){
    val loading by flashViewModel.loading.collectAsState()
    val cartItems by flashViewModel.cartItems.collectAsState()
    val cartItemWithQuantity=cartItems.groupBy { it }
        .map {
            (item,cartItems)->InternetItemWithQuantity(
                item,
                cartItems.size
            )
        }
    if (cartItems.isNotEmpty()){
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)) {
            item {
                Image(painter = painterResource(id = R.drawable.categorybanner), contentDescription = "Offer")
            }
            item {
                Text(
                    text = "Review Items",
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
            }
            items(cartItemWithQuantity) {
                CartCard(it.item,flashViewModel,it.quantity)
            }
            item{
                Text(text = "Bill Details",
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp)
            }

            val totalPrice=cartItems.sumOf {
                it.itemPrice*75/100
            }
            val handlingCharge=totalPrice * 1/100
            val deliveryFee=30
            val grandTotal=totalPrice+handlingCharge+deliveryFee
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(236,236,236,255)
                    ),
                    modifier = androidx.compose.ui.Modifier.fillMaxWidth()
                ) {
                    Column(modifier = androidx.compose.ui.Modifier.padding(10.dp)) {
                        BillRows(itemName = "Item Total", ItemPrice =totalPrice , fontWeight = FontWeight.Normal )
                        BillRows(itemName = "Handling Charge", ItemPrice = handlingCharge, fontWeight = FontWeight.Light)
                        BillRows(itemName = "Delivery Fee", ItemPrice = deliveryFee, fontWeight = FontWeight.Light)
                        Divider(thickness = 1.dp, modifier = androidx.compose.ui.Modifier.padding(
                            vertical = 5.dp
                        ), color = Color.LightGray)
                        BillRows(itemName = "To Pay", ItemPrice = grandTotal, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
        }
    }
    else if(loading){
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = androidx.compose.ui.Modifier
                .fillMaxSize()
                .background(color = Color(255, 255, 255, 190)),

            ) {
            Box {
                LinearProgressIndicator()
            }
            Text(text = "Loading")
        }
    }
    else{
        Column(
            modifier = androidx.compose.ui.Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(painter = painterResource(id = R.drawable.cart_empty), contentDescription = "app icon", modifier = androidx.compose.ui.Modifier.size(
                150.dp
            ))
            Text(text = "Your cart is empty", fontWeight = FontWeight.Bold, modifier = androidx.compose.ui.Modifier.padding(
                20.dp
            ))
            FilledTonalButton(onClick = { onHomeButtonClicked()}) {
                Text(text ="Browse Products" )
            }
        }
    }
}

@Composable
fun CartCard(cartItem:InternetItems,
             flashViewModel: FlashViewModel,
             cartItemQuantity:Int){
    Row(
        modifier = androidx.compose.ui.Modifier
            .fillMaxWidth()
            .height(80.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = cartItem.imageUrl, contentDescription = "Item Image",
            modifier = androidx.compose.ui.Modifier
                .fillMaxHeight()
                .padding(start = 5.dp)
                .weight(4f))
        Column(
            modifier = androidx.compose.ui.Modifier
                .padding(horizontal = 5.dp)
                .fillMaxHeight()
                .weight(4f),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(text = cartItem.itemName, fontSize = 16.sp, maxLines = 1)
            Text(text = cartItem.itemQuantity, fontSize = 14.sp, maxLines = 1)
        }
        Column(
            modifier = androidx.compose.ui.Modifier
                .padding(horizontal = 5.dp)
                .fillMaxHeight()
                .weight(3f),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = "Rs. ${cartItem.itemPrice}",
                fontSize = 16.sp,
                maxLines = 1,
                color = Color.Gray,
                textDecoration = TextDecoration.LineThrough
            )
            Text(text = "Rs. ${cartItem.itemPrice * 75/100}", fontSize = 14.sp, maxLines = 1, color = Color(254,116,105,255))
        }
        Column(
            modifier = androidx.compose.ui.Modifier
                .padding(horizontal = 5.dp)
                .fillMaxHeight()
                .weight(3f),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(text = "Quantity:$cartItemQuantity",
                fontSize = 11.sp,
                textAlign = TextAlign.Left,
                modifier = androidx.compose.ui.Modifier.fillMaxWidth())

            Card(
                modifier = androidx.compose.ui.Modifier
                    .clickable {
                        flashViewModel.removeFromCart(oldItem = cartItem)
                        flashViewModel.setLoading(true)
                    }
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(254,116,105,255)
                )
            ) {
                Text(text = "Remove",
                    color = Color.White,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                    modifier = androidx.compose.ui.Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
fun BillRows(
    itemName:String,
    ItemPrice:Int,
    fontWeight: FontWeight
){
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = androidx.compose.ui.Modifier.fillMaxWidth()
    ){
        Text(text = itemName, fontWeight = fontWeight)
        Text(text = "Rs. $ItemPrice", fontWeight = fontWeight)
    }
}
