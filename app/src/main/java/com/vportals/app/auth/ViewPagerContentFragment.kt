package com.vportals.app.auth

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import com.vportals.app.R
import kotlinx.android.synthetic.main.help_item_page.*
import kotlinx.android.synthetic.main.help_item_page.view_pager_header as intro_head_text
import kotlinx.android.synthetic.main.help_item_page.view_pager_sub_text as intro_sub_text
import kotlinx.android.synthetic.main.help_item_page.view_pager_sub_text_sub as intro_sub_text_sub
import kotlinx.android.synthetic.main.help_item_page.view_pager_sub_text_sub_sub as intro_sub_text_sub_sub
import kotlinx.android.synthetic.main.help_item_page.imageView2 as base_layout

class ViewPagerContentFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.help_item_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        arguments?.takeIf { it.containsKey("INTRO_STRING_OBJECT") }?.apply {
            intro_head_text.text = getStringArray("INTRO_STRING_OBJECT")!![0]
            intro_sub_text.text = HtmlCompat.fromHtml(getStringArray("INTRO_STRING_OBJECT")!![1], 0)
            //changeColor(getStringArray("INTRO_STRING_OBJECT")!![2])
            when(getStringArray("INTRO_STRING_OBJECT")!![2])
            {
                "R" -> {
                    base_layout.setImageResource(R.drawable.help_one)
                    intro_sub_text_sub.text = ""
                    intro_sub_text_sub_sub.text = ""
                }
                "G" -> {
                    base_layout.setImageResource(R.drawable.help_two)
                    intro_sub_text_sub.text = HtmlCompat.fromHtml(getString(R.string.intro_sub_title_2_sub), 0)
                    intro_sub_text_sub_sub.text = HtmlCompat.fromHtml(getString(R.string.intro_sub_title_2_sub_sub), 0)
                }

            }
        }

    }
//
//    fun changeColor(color:String){
//        when(color)
//        {
//            "R" -> {
//                base_layout.setImageResource(R.drawable.help_one)
//                intro_sub_text_sub.text = getStringArray("INTRO_STRING_OBJECT")!![0]
//            }
//            "G" -> {
//                base_layout.setImageResource(R.drawable.help_two)
//            }
//
//        }
//    }
}