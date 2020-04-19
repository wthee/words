package cn.wthee.words.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.TextWatcher
import android.view.*
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import cn.wthee.words.R
import cn.wthee.words.databinding.FragmentMainBinding
import cn.wthee.words.util.ColorSeekBar
import cn.wthee.words.util.CommonUtil
import cn.wthee.words.util.FlodAnim
import cn.wthee.words.util.TianZiGeTextView
import java.security.spec.EllipticCurve


class MainFragment : Fragment() {

    companion object {
        lateinit var sharedPreferences: SharedPreferences
        var wordSize = CommonUtil.wordSize
        var wordColor = CommonUtil.wordColor
        private var wordColorProgress1 = CommonUtil.wordColorProgress1
        private var wordColorProgress2 = CommonUtil.wordColorProgress2
        var lineWidth = CommonUtil.lineWidth
        var lineSize = CommonUtil.lineSize
        var tzgColor = CommonUtil.tzgColor
        private var tzgColorProgress1 = CommonUtil.tzgColorProgress1
        private var tzgColorProgress2 = CommonUtil.tzgColorProgress2
        var word: String? = CommonUtil.word
        var wordEx: String? = CommonUtil.wordEx
        //视图
        lateinit var wordView : TianZiGeTextView
    }

    private lateinit var binding: FragmentMainBinding
    private var layoutLineHeight = 200

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        //加载本地设置
        init()
        //输入框监听事件
        setInputListener()
        //线框设置监听
        setLineListener()
        //操作栏按钮监听
        setButtonListener()
        //线框设置折叠
        val lineFlod = FlodAnim(binding.root.context,binding.layoutLine, 500)
        binding.titleLine.setOnClickListener {
            lineFlod.exec()
        }
        //线框设置折叠
        val wordFlow = FlodAnim(binding.root.context,binding.layoutWord, 500)
        binding.titleWord.setOnClickListener {
            wordFlow.exec()
        }
        //字体监听
        setWordListener()
        //获取高度
        binding.layoutLine.post {
            layoutLineHeight = binding.layoutLine.height
        }
        return binding.root
    }

    //加载本地设置，初始化
    private fun init() {
        sharedPreferences = activity?.getSharedPreferences("customize", Context.MODE_PRIVATE)!!
        wordSize = sharedPreferences.getFloat("wordSize", wordSize)
        wordColor = sharedPreferences.getInt("wordColor", wordColor)
        wordColorProgress1 = sharedPreferences.getInt("wordColorProgress1", wordColorProgress1)
        wordColorProgress2 = sharedPreferences.getInt("wordColorProgress2", wordColorProgress2)
        lineWidth = sharedPreferences.getFloat("lineWidth", lineWidth)
        lineSize = sharedPreferences.getFloat("lineSize", lineSize)
        tzgColor = sharedPreferences.getInt("tzgColor", tzgColor)
        tzgColorProgress1 = sharedPreferences.getInt("tzgColorProgress1", tzgColorProgress1)
        tzgColorProgress2 = sharedPreferences.getInt("tzgColorProgress2", tzgColorProgress2)
        word = sharedPreferences.getString("word", word)
        wordEx = sharedPreferences.getString("wordEx", wordEx)
        //颜色选择
        binding.colorSeekBar1.progress = tzgColorProgress1
        binding.colorSeekBar2.progress = tzgColorProgress2
        //线框粗细
        binding.lineWidthSeekbar.progress = (lineWidth - CommonUtil.lineWidth).toInt()
        //线框大小
        binding.lineSizeSeekbar.progress = (lineSize - CommonUtil.lineSize).toInt()
        //字体大小
        binding.wordSizeSeekbar.progress = (wordSize - CommonUtil.wordSize).toInt()
        //字体颜色选择
        binding.wordColorSeekBar1.progress = wordColorProgress1
        binding.wordColorSeekBar2.progress = wordColorProgress2
        //输入框文字
        binding.wordInput.text = SpannableStringBuilder(word)
        binding.wordExInput.text = SpannableStringBuilder(wordEx)
        binding.wordExText.text = SpannableStringBuilder(wordEx)
        wordView = binding.wordText
        updateView()
    }

    //输入框监听
    private fun setInputListener() {
        //词语输入框监听
        binding.wordInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                sharedPreferences.edit {
                    putString(
                        "word",
                        s.toString().replace("\r\n|\r|\n".toRegex(), "").replace(" ", "")
                    )
                }
                updateView()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //回车焦点变更
                if (s == null) return
                val str = s.toString().substring(start, start + count)
                if (str == "\n") {
                    binding.wordInput.setText(s.toString().replaceFirst("\n", ""))
                    binding.wordExInput.requestFocus()
                    return
                }
            }
        })
        //解释输入框监听
        binding.wordExInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                binding.wordExText.text = s
                sharedPreferences.edit {
                    putString("wordEx", s.toString())
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
    }

    //线框设置监听
    private fun setLineListener() {
        //边框颜色选择滑动条监听
        binding.colorSeekBar1.setBackgroundGradientColors(ColorSeekBar.DEFAULT_COLORS)
        binding.colorSeekBar1.setOnDrawListener(object : ColorSeekBar.OnDrawListener {
            override fun onDrawFinish(seekBar: ColorSeekBar) {
                //设置渐变颜色
                binding.colorSeekBar2.setBackgroundGradientColors(
                    arrayListOf(
                        Color.BLACK,
                        seekBar.getThumbColor(),
                        Color.WHITE
                    )
                )
                //更新第二个进度条
                binding.colorSeekBar2.postInvalidate()
                tzgColor = binding.colorSeekBar2.getThumbColor()
                sharedPreferences.edit {
                    putInt("tzgColorProgress1", seekBar.progress)
                }
                updateView()
            }
        })
        //边框颜色选择滑动条监听
        binding.colorSeekBar2.setOnDrawListener(object : ColorSeekBar.OnDrawListener {
            override fun onDrawFinish(seekBar: ColorSeekBar) {
                tzgColor = seekBar.getThumbColor()
                sharedPreferences.edit {
                    putInt("tzgColorProgress2", seekBar.progress)
                    putInt("tzgColor", tzgColor)
                }
                updateView()
            }
        })
        //线框粗细监听
        binding.lineWidthSeekbar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                lineWidth = seekBar!!.progress + CommonUtil.lineWidth
                sharedPreferences.edit {
                    putFloat("lineWidth", lineWidth)
                }
                updateView()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })
        //线框大小监听
        binding.lineSizeSeekbar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                lineSize = seekBar!!.progress + CommonUtil.lineSize
                sharedPreferences.edit {
                    putFloat("lineSize", lineSize)
                }
                updateView()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })
    }

    //字体监听
    private fun setWordListener() {
        //字体颜色选择滑动条监听
        binding.wordColorSeekBar1.setBackgroundGradientColors(ColorSeekBar.DEFAULT_COLORS)
        binding.wordColorSeekBar1.setOnDrawListener(object : ColorSeekBar.OnDrawListener {
            override fun onDrawFinish(seekBar: ColorSeekBar) {
                //设置渐变颜色
                binding.wordColorSeekBar2.setBackgroundGradientColors(
                    arrayListOf(
                        Color.BLACK,
                        seekBar.getThumbColor(),
                        Color.WHITE
                    )
                )
                //更新第二个进度条
                binding.wordColorSeekBar2.postInvalidate()
                wordColor = binding.wordColorSeekBar2.getThumbColor()
                sharedPreferences.edit {
                    putInt("wordColorProgress1", seekBar.progress)
                }
                updateView()
            }
        })
        //字体颜色选择滑动条监听
        binding.wordColorSeekBar2.setOnDrawListener(object : ColorSeekBar.OnDrawListener {
            override fun onDrawFinish(seekBar: ColorSeekBar) {
                wordColor = seekBar.getThumbColor()
                sharedPreferences.edit {
                    putInt("wordColorProgress2", seekBar.progress)
                    putInt("wordColor", wordColor)
                }
                updateView()
            }
        })
        //字体大小监听
        binding.wordSizeSeekbar.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                wordSize = seekBar!!.progress + CommonUtil.wordSize
                sharedPreferences.edit {
                    putFloat("wordSize", wordSize)
                }
                updateView()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })
    }

    //操作栏按钮监听
    @SuppressLint("ClickableViewAccessibility")
    private fun setButtonListener() {
        //颜色选择按钮监听 （输入框布局 /  自定义设置布局）
        binding.customize.setOnClickListener {
            //更改淡入淡出顺序
            if(binding.customizeLayout.isShown){
                showHiddenCustomize()
                showHiddenInput()
            }else{
                showHiddenInput()
                showHiddenCustomize()
            }
        }
        //保存按钮监听
        binding.save.setOnClickListener {
            binding.customize.visibility = View.GONE
            binding.save.visibility = View.GONE
            binding.word.visibility = View.GONE
            binding.wordEx.visibility = View.GONE
            binding.customizeLayout.visibility = View.GONE
            binding.wordExText.maxLines = 999
            binding.saveLayout.visibility = View.VISIBLE
            binding.layoutMain.gravity = Gravity.CENTER
        }
    }

    //更新田字格
    private fun updateView() {
        val text = binding.wordInput.text.toString()
        binding.wordText.setWord(text)
        //线框设置
        binding.wordText.setLineColor(tzgColor)
        binding.wordText.setLineWidth(lineWidth)
        binding.wordText.setLineSize(lineSize)
        //字体设置
        binding.wordText.setWordSpace(50)
        binding.wordText.setWordSize(wordSize)
        binding.wordText.setWordColor(wordColor)
        binding.wordText.updateView()
    }

    //显示隐藏输入框布局
    private fun showHiddenInput(){
        binding.word.visibility = if (binding.word.isShown) View.GONE else View.VISIBLE
        binding.wordEx.visibility = if (binding.wordEx.isShown) View.GONE else View.VISIBLE
    }

    //显示隐藏自定义布局
    private fun showHiddenCustomize() {
        binding.customizeLayout.visibility = if (binding.customizeLayout.isShown) {
            //切换到输入框布局
            binding.customize.background = resources.getDrawable(R.drawable.customize_btn, null)
            CommonUtil.showInput(activity!!, binding.wordInput)
            CommonUtil.showInput(activity!!, binding.wordExInput)
            binding.wordInput.requestFocus()
            View.GONE
        } else {
            //切换到自定义设置布局
            binding.customize.background = resources.getDrawable(R.drawable.input_btn, null)
            CommonUtil.hideInput(activity!!, binding.wordInput)
            CommonUtil.hideInput(activity!!, binding.wordExInput)
            View.VISIBLE
        }
    }
}
