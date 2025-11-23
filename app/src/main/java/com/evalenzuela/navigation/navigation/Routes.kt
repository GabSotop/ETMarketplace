package com.evalenzuela.navigation.navigation

object Routes {
    const val HOME = "home"
    const val PROFILE = "profile"

    const val POSTS = "posts"
    const val CART = "cart"
    const val DETAIL = "detail/{itemId}"
    fun detailRoute(itemId: Int) = "detail/$itemId"
}