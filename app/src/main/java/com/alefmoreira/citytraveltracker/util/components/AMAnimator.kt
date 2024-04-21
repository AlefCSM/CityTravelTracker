package com.alefmoreira.citytraveltracker.util.components

import android.animation.AnimatorInflater
import android.content.Context
import android.view.animation.DecelerateInterpolator
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.alefmoreira.citytraveltracker.R

class AMAnimator(private val context: Context) : SimpleItemAnimator() {

    override fun animateChange(
        oldHolder: RecyclerView.ViewHolder?,
        newHolder: RecyclerView.ViewHolder?,
        fromLeft: Int,
        fromTop: Int,
        toLeft: Int,
        toTop: Int
    ): Boolean {

        return false
    }

    override fun runPendingAnimations() {
    }

    override fun endAnimation(item: RecyclerView.ViewHolder) {
    }

    override fun endAnimations() {
    }

    override fun isRunning(): Boolean {
        return false
    }

    override fun animateRemove(holder: RecyclerView.ViewHolder?): Boolean {
        val animator = AnimatorInflater.loadAnimator(context, R.animator.fade_up)
        animator.interpolator = DecelerateInterpolator()
        animator.setTarget(holder)
        animator.start()
        return true
    }

    override fun animateAdd(holder: RecyclerView.ViewHolder?): Boolean {
        return false
    }

    override fun animateMove(
        holder: RecyclerView.ViewHolder?,
        fromX: Int,
        fromY: Int,
        toX: Int,
        toY: Int
    ): Boolean {
        return false
    }
}