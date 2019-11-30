package com.happy.machine

import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.ref.WeakReference

class myInfo(p: Int, ans: Long) {
    var progress = 0
    var result = 0L

    init {
        this.progress = p
        this.result = ans
    }
}

class MainActivity : AppCompatActivity() {
    var myHandle: Handler? = WithoutLeakHandler(this)
    var initEnd = 12
    var isCalc: Boolean = false
    var sentence = arrayOf(
        "However long the night, the dawn will break.",
        "Sometimes,we are not waiting for somebody or sth. We are waiting to be changed as time goes by.",
        "For you, a thousand times over.",
        "In me the tiger sniffs the rose.",
        "The longest way must have its close; the gloomiest night will on to a morning.",
        "Let life be beautiful like summer flowers and death like autumn leaves."
    )

    external fun IdleOnce(m: Int, n: Int): Long

    external fun addint(m: Int, n: Int): Int
    //external fun getStringc(m: String): String
    external fun verify(m: String): Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Example of a call to a native method
        //sample_text.text = stringFromJNI()
//        sample_text.setHintTextColor(R.color.colorAccent)
        sample_text.setText("")
        Thread {
            //Toast.makeText(this, "Hell", Toast.LENGTH_SHORT).show()
        }.start()

        Thread(Runnable {
            try {
                for (i: Int in 0..initEnd) {
                    val msg = Message.obtain()
                    msg.what = 6734520
                    //.xor(arr[i - 2]).toChar()
                    msg.obj = myInfo(
                        i * 100 / initEnd,
                        IdleOnce(3, i)
                    )//arrayOf(i * 100 / initEnd as Int, IdleOnce(3, i))
                    //msg.obj = addint(i, 1107).toString()+getStringc("happy")
                    //msg.obj = ((IdleOnce(3, i).toString().toInt()).xor(arr[i - 2])).toChar()
                    myHandle!!.sendMessage(msg)
                }

            } catch (e: Exception) {

            }
        }).start()
        findViewById<Button>(R.id.button).setOnClickListener(View.OnClickListener {
            if (!isCalc) {
                var s: String = findViewById<EditText>(R.id.sample_text).text.toString()
                isCalc = true
                Thread {
                    var res = verify(s)
                    isCalc = false
                    val msg = Message.obtain()
                    msg.what = 2
                    when (res) {
                        1 -> {
                            msg.obj = "Congratulation! The flag is correct!"

                        }
                        5 -> {
                            msg.obj = "Ops, the flag is incorrect~   Almost there!"
                        }
                        2,3 -> {
                            msg.obj = "Your flag doesn`t fit the Machine!"
                        }
                        else -> {
                            msg.obj = "The machine rejects your flag!"
                        }
                    }
                    myHandle!!.sendMessage(msg)
                }.start()
                Thread {
                    var i = 0
                    while (isCalc) {
                        val msg = Message.obtain()
                        msg.what = 2
                        var str: String = "The Machine is Calculating your Flag"
                        for (t in 0..i) {
                            str += "."
                        }
                        i += 1
                        i %= 10
                        msg.obj = str//+"\n"+sentence[(0..sentence.size-1).random()]
                        Thread.sleep(600)
                        myHandle!!.sendMessage(msg)
                    }
                }.start()
            }
        })
    }


    companion object {

        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("native-lib")
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        myHandle!!.removeCallbacksAndMessages(null)

    }


}

private class WithoutLeakHandler(activity: MainActivity) : Handler() {
    private var mActivity: WeakReference<MainActivity> = WeakReference(activity)

    override fun handleMessage(msg: Message) {
        if (mActivity.get() == null) {
            return
        }
        val activity = mActivity.get()
        when (msg.what) {
            6734520 -> {
                //val tv = activity!!.findViewById<TextView>(R.id.sample_text)
                //tv.text = tv.text.toString() + "" + msg.obj.toString()
                val progress =
                    activity!!.findViewById<com.blackflagbin.semicircleprogressview.SemiCircleProgressView>(R.id.progress)
                val percent = (msg.obj as myInfo).progress
                progress.setProgress(percent)
                if (percent == 100) {
                    try {
                        Thread.sleep(500)
                    } catch (e: Exception) {
                    }

                    activity.findViewById<EditText>(R.id.sample_text).visibility = View.VISIBLE
                    activity.findViewById<Button>(R.id.button).visibility = View.VISIBLE
                    activity.findViewById<com.blackflagbin.semicircleprogressview.SemiCircleProgressView>(R.id.progress)
                        .visibility = View.GONE
                    activity.findViewById<SplashTextView>(R.id.hint_text).setText("The Machine is ready.")
                }

            }
            2 -> {
                activity!!.findViewById<SplashTextView>(R.id.hint_text).setText(msg.obj.toString())
            }
        }
    }
}


class SplashTextView : TextView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, style: Int) : super(context, attrs, style)

    private var mPaint: TextPaint? = null                                       // Painter
    private var mLinearGradient: LinearGradient? = null                         // LinearGradient
    private var mViewWidth = 0                                                  // saved textView width
    private var mGradientMatrix: Matrix? = null                                 // Matrix
    private var mTranslate: Int = 0                                             // Translate
    private var delay: Long = 150

    fun setDelay(delay: Long) {
        this.delay = delay
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (mGradientMatrix != null) {
            // increase in 100 ms with 1/5 width of textView
            mTranslate += mViewWidth / 5
            if (mTranslate > mViewWidth) mTranslate = -mViewWidth
            mGradientMatrix!!.setTranslate(mTranslate.toFloat(), 0f)         // !! suppose must not empty
            mLinearGradient?.setLocalMatrix(mGradientMatrix)                    // ? suppose might empty, do it if not empty
            postInvalidateDelayed(delay) // 100
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mViewWidth = measuredWidth                                              // get textView width and height
        if (mViewWidth > 0) {
            mPaint = paint                                                      // get textView Painter
            mLinearGradient = LinearGradient(
                0f,
                0f,
                mViewWidth.toFloat(),
                0f,
                intArrayOf(Color.BLUE, Color.RED, Color.BLUE),
                null,
                Shader.TileMode.CLAMP
            )// init LinearGradient
            mPaint?.shader = mLinearGradient                                    // set shader of textView's paint
            mGradientMatrix = Matrix()                                          // init Matrix
        }
    }
}