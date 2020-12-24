/*
 * Copyright (c) 2020 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 *
 * This project and source code may use libraries or frameworks that are
 * released under various Open-Source licenses. Use of those libraries and
 * frameworks are governed by their own individual licenses.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.raywenderlich.android.promoapp.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.raywenderlich.android.promoapp.R
import com.raywenderlich.android.promoapp.databinding.ActivityPromoBinding

class PromoActivity : AppCompatActivity() {
    private lateinit var activityPromoBinding: ActivityPromoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        FirebaseAnalytics.getInstance(this)
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        activityPromoBinding = ActivityPromoBinding.inflate(layoutInflater)
        setContentView(activityPromoBinding.root)

        handleIntent(intent)
        handleFirebaseDynamicLinks(intent)
    }

    private fun handleIntent(intent: Intent?) {
        val appLinkAction: String? = intent?.action
        val appLinkData: Uri? = intent?.data
        showDeepLinkOffer(appLinkAction, appLinkData)
    }

    private fun showDeepLinkOffer(appLinkAction: String?, appLinkData: Uri?) {
        // 1
        if (Intent.ACTION_VIEW == appLinkAction && appLinkData != null) {
            // 2
            val promotionCode = appLinkData.getQueryParameter("code")
            if (promotionCode.isNullOrBlank().not()) {
                activityPromoBinding.discountGroup.visibility = View.VISIBLE
                activityPromoBinding.tvPromoCode.text = promotionCode
                // 3
                activityPromoBinding.btnClaimOffer.setOnClickListener {
                    activityPromoBinding.tvOfferClaimed.visibility = View.VISIBLE
                }
            } else {
                activityPromoBinding.discountGroup.visibility = View.GONE
            }
        }
    }

    private fun handleFirebaseDynamicLinks(intent: Intent) {
        // 1
        Firebase.dynamicLinks
            .getDynamicLink(intent)
            .addOnSuccessListener { dynamicLinkData ->
                // 2
                if (dynamicLinkData != null) {
                    showDynamicLinkOffer(dynamicLinkData.link)
                }
            }
            // 3
            .addOnFailureListener(this) { e ->
                Log.d("DynamicLinkError", e.localizedMessage)
            }
    }

    private fun showDynamicLinkOffer(uri: Uri?) {
        val promotionCode = uri?.getQueryParameter("code")
        if (promotionCode.isNullOrBlank().not()) {
            activityPromoBinding.discountGroup.visibility = View.VISIBLE
            activityPromoBinding.tvPromoCode.text = promotionCode
            activityPromoBinding.btnClaimOffer.setOnClickListener {
                activityPromoBinding.tvOfferClaimed.visibility = View.VISIBLE
            }
        } else {
            activityPromoBinding.discountGroup.visibility = View.GONE
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let { newIntent ->
            handleIntent(newIntent)
            handleFirebaseDynamicLinks(intent)
        }
    }
}