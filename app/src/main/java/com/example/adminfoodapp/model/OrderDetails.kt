package com.example.adminfoodapp.model


import java.io.Serializable


class OrderDetails() : Serializable {
    var userUid: String? = null
    var userName : String ?= null
    var foodNames : MutableList<String>? = null
    var foodImages : MutableList<String>? = null
    var foodPrices : MutableList<String>? = null
    var foodQuantities : MutableList<Int>? = null
    var address : String ?= null
    var totalPrice : String ?= null
    var phoneNumber : String ?= null
    var orderAccepted : Boolean ?= null
    var paymentReceived : Boolean ?= null
    var itemPushKey : String ?= null
    var currentTime : Long = 0

}