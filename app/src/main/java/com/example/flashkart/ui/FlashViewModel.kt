package com.example.flashkart.ui

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flashkart.data.InternetItems
import com.example.flashkart.network.FlashApi
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.prefs.Preferences

class FlashViewModel(application: Application):AndroidViewModel(application) {//ViewModel
    private val _uiState= MutableStateFlow(FlashUiState())
    val uiState:StateFlow<FlashUiState> = _uiState.asStateFlow()

    val _isvisible=MutableStateFlow<Boolean>(true)
    val isvisible=_isvisible

    var itemUiState:ItemUiState by mutableStateOf(ItemUiState.Loading)//when we open the app it should open laoding first.
        private set

    private val _user=MutableStateFlow<FirebaseUser?>(null)
    val user:MutableStateFlow<FirebaseUser?>get() = _user

    private val _phoneNumber= MutableStateFlow("")
    val phoneNumber:MutableStateFlow<String>get() = _phoneNumber

    private val _otp=MutableStateFlow("")
    val  otp:MutableStateFlow<String>get() = _otp

    private val _verificationId=MutableStateFlow("")
    val verificationId:MutableStateFlow<String>get() = _verificationId

    private val _ticks=MutableStateFlow(60L)
    val ticks :MutableStateFlow<Long>get() = _ticks

    private val _loading=MutableStateFlow(false)
    val loading:MutableStateFlow<Boolean>get()=_loading

    private val _logoutClicked=MutableStateFlow(false)
    val logoutClicked:MutableStateFlow<Boolean>get() = _logoutClicked

    val database = Firebase.database
    val myRef = database.getReference("users/${auth.currentUser?.uid}/cart")

    private lateinit var timerJob: Job

    //naming kotlin coroutines
    lateinit var internetJob: Job // for the screen in which data comes from the internet
    lateinit var screenJob: Job //for the offer screen

    private val _cartItems=MutableStateFlow<List<InternetItems>>(emptyList())
    val cartItems:StateFlow<List<InternetItems>>get() = _cartItems.asStateFlow()

    private val Context.datastore : DataStore<androidx.datastore.preferences.core.Preferences> by preferencesDataStore("cart")
    private val cartItemsKey= stringPreferencesKey("cart_items")
    private val context=application.applicationContext

    sealed interface ItemUiState{
        data class Success(val items:List<InternetItems>):ItemUiState
        object Loading:ItemUiState
        object Error: ItemUiState
    }

    fun setPhoneNumber(phoneNumber:String){
        _phoneNumber.value=phoneNumber
    }

    fun setOtp(otp:String){
        _otp.value=otp
    }

    fun setVerificationId(verificationId:String){
        _verificationId.value=verificationId
    }

    fun setUser(user: FirebaseUser){
        _user.value=user
    }

    fun clearData(){
        _user.value=null
        _phoneNumber.value=""
        _otp.value=""
        verificationId.value=""
        resetTimer()
    }

    //we will be making the timer in coroutines
    fun runTimer(){
        timerJob=viewModelScope.launch {
            while (_ticks.value>0){
                delay(1000)
                _ticks.value-=1
            }
        }
    }

    fun resetTimer(){
        try {
            timerJob.cancel()
        }catch (exception:Exception){

        }finally {
            _ticks.value=60
        }
    }

    fun setLoading(isLoading:Boolean){
        _loading.value=isLoading
    }

    fun setLogoutStatus(
        logoutStatus:Boolean
    ){
        _logoutClicked.value=logoutStatus
    }

    private suspend fun saveCartItemsToDataStore(){
        context.datastore.edit { preferences->
            preferences[cartItemsKey]= Json.encodeToString(_cartItems.value)
        }
    }

    private suspend fun loadCartItemsFromDataStore(){
        val fullData=context.datastore.data.first()
        val cartItemsJson=fullData[cartItemsKey]
        if (!cartItemsJson.isNullOrEmpty()){
            _cartItems.value=Json.decodeFromString(cartItemsJson)
        }
    }

    fun addToCart(item: InternetItems){
        _cartItems.value = _cartItems.value + item
        viewModelScope.launch {
            saveCartItemsToDataStore()
        }
    }

    fun addToDatabase(item: InternetItems){
        myRef.push().setValue(item)
    }

    fun fillCartItems(){
        // Read from the database
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                _cartItems.value= emptyList()
                for (childSnapshot in dataSnapshot.children){
                    val item=childSnapshot.getValue(InternetItems::class.java)
                    item?.let {
                        val newItem=it
                        addToCart(newItem)
                    }
                }
                setLoading(false)
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    fun removeFromCart(oldItem: InternetItems){
       /* _cartItems.value = _cartItems.value - item
        viewModelScope.launch {
            saveCartItemsToDataStore()
        }*/ //this code is for to remove item from the cart only not database
        myRef.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                _cartItems.value= emptyList()
                for (childSnapshot in dataSnapshot.children){
                    var itemRemoved=false
                    val item=childSnapshot.getValue(InternetItems::class.java)
                    item?.let {
                       if (oldItem.itemName ==it.itemName && oldItem.itemPrice == it.itemPrice){
                           childSnapshot.ref.removeValue()
                           itemRemoved=true
                       }
                    }
                    if(itemRemoved) break
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    fun updateClickText(updatedText:String){
        _uiState.update {
            it.copy(
                clickStatus = updatedText
            )
        }
    }

    fun updatedSelectedCategory(updatedCategory: Int){
        _uiState.update {
            it.copy(
                selectedCategory = updatedCategory
            )
        }
    }
    fun toggleVisibility(){
        isvisible.value=false
    }

    fun getFlashItems(){
        internetJob=viewModelScope.launch {
            try {
                val listResult=FlashApi.retrofitService.getItems()
                itemUiState=ItemUiState.Success(listResult)
                loadCartItemsFromDataStore()
            }
            catch (exception:Exception){
                itemUiState=ItemUiState.Error
                toggleVisibility()
                screenJob.cancel() // when there is no internet connectivity then splash screen should not come.
            }
        }
    }
    init {
        screenJob=viewModelScope.launch(Dispatchers.Default) {
            delay(3000)
            toggleVisibility()
        }
        getFlashItems()
        fillCartItems()
    }
}