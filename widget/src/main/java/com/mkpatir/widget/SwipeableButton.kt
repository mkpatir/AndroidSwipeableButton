package com.mkpatir.widget

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.mkpatir.widget.databinding.SwipeableButtonBinding

class SwipeableButton: FrameLayout {

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context,attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
            context,
            attrs,
            defStyleAttr
    ) {
        init(context,attrs)
    }

    private lateinit var binding: SwipeableButtonBinding

    /**
     *   Swipe Listener
     * */
    var onSwipeCompleted:(() -> Unit)? = null

    /**
     *   View attributes
     * */
    private var isButtonSwiped = false

    var isSwipeEnabled = true

    var buttonBackground = ContextCompat.getDrawable(context, R.drawable.bg_button_layout)
        set(value) {
            if (isBindingInitialized()){
                binding.root.background = value ?: ContextCompat.getDrawable(
                        context,
                        R.drawable.bg_button_layout
                )
                field = value
            }
        }

    var buttonForeground = ContextCompat.getDrawable(context, R.drawable.bg_button)
        set(value) {
            if (isBindingInitialized()){
                binding.swipeButton.background = value ?: ContextCompat.getDrawable(
                        context,
                        R.drawable.bg_button
                )
                field = value
            }
        }

    var swipeFinishProgress = 0.9
        set(value) {
            if (value > 0 && value < 1){
                field = value
            }
        }

    var swipeButtonBackAnimationDuration = 300L
        set(value) {
            if (value > 0){
                field = value
            }
        }

    var swipeText = ""
        set(value) {
            if (isBindingInitialized()){
                binding.swipeText.text = value
                field = value
            }
        }

    private fun init(context: Context, attrs: AttributeSet? = null){
        binding = SwipeableButtonBinding.inflate(LayoutInflater.from(context), this, true)
        attrs?.let {
            context.theme.obtainStyledAttributes(it, R.styleable.SwipeableButton,0,0).apply {
                buttonBackground = getDrawable(R.styleable.SwipeableButton_btnBackground)
                buttonForeground = getDrawable(R.styleable.SwipeableButton_btnForeground)
                isSwipeEnabled = getBoolean(R.styleable.SwipeableButton_isSwipeEnabled,true)
                swipeFinishProgress = getFloat(R.styleable.SwipeableButton_swipeFinishProgress,0.9f).toDouble()
                swipeButtonBackAnimationDuration = getFloat(R.styleable.SwipeableButton_btnBackAnimDuration,300f).toLong()
                swipeText = getString(R.styleable.SwipeableButton_swipeText).orEmpty()
                recycle()
            }
        }
        initSwipeButton()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initSwipeButton(){
        binding.swipeButton.setOnTouchListener { v, event ->
            if (isSwipeEnabled){
                when(event.action){
                    MotionEvent.ACTION_DOWN -> true
                    MotionEvent.ACTION_UP -> {
                        buttonUp()
                        true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        buttonMove(event)
                        true
                    }
                    else -> false
                }
            }
            else
                false
        }
    }

    private fun buttonMove(event: MotionEvent){
        if (event.rawX > binding.swipeButton.width / 2 && event.rawX + binding.swipeButton.width / 2 < width){
            binding.swipeButton.x = event.rawX - binding.swipeButton.width / 2
            binding.swipeText.alpha = 1 - ((event.rawX + binding.swipeButton.width / 2) / width)
        }
    }

    private fun buttonUp(){
        if (binding.swipeButton.x + binding.swipeButton.width > width * swipeFinishProgress){
            onSwipeCompleted?.invoke()
            isButtonSwiped = true
            isSwipeEnabled = false
            binding.swipeButton.x = width.toFloat() - binding.swipeButton.width
            binding.swipeText.alpha = 0f
        }
        else {
            moveToBack()
        }
    }

    private fun moveToBack(){
        startButtonAnimation(binding.swipeButton.x,0f,swipeButtonBackAnimationDuration)
        startTextAlphaAnimation(binding.swipeText.alpha,1f,swipeButtonBackAnimationDuration)
    }

    private fun startButtonAnimation(from: Float,to: Float,durationValue: Long){
        ValueAnimator.ofFloat(from, to).apply {
            duration = durationValue
            addUpdateListener {
                binding.swipeButton.x = (it.animatedValue as Float)
            }
            start()
        }
    }

    private fun startTextAlphaAnimation(from: Float,to: Float,durationValue: Long){
        ValueAnimator.ofFloat(from, to).apply {
            duration = durationValue
            addUpdateListener {
                binding.swipeText.alpha = (it.animatedValue as Float)
            }
            start()
        }
    }

    private fun isBindingInitialized() = this::binding.isInitialized

    fun isSwiped() = isButtonSwiped

    fun swipeButton(){
        if (isSwiped().not()){
            isButtonSwiped = true
            isSwipeEnabled = false
            startButtonAnimation(0f,width.toFloat() - binding.swipeButton.width,300L)
            startTextAlphaAnimation(1f,0f,300L)
        }
    }

    fun clearSwipe(){
        if (isSwiped()){
            isButtonSwiped = false
            isSwipeEnabled = true
            moveToBack()
        }
    }
}