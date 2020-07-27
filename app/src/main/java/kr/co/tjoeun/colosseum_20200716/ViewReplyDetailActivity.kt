package kr.co.tjoeun.colosseum_20200716

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_view_reply_detail.*
import kr.co.tjoeun.colosseum_20200716.adapters.ReReplyAdapter
import kr.co.tjoeun.colosseum_20200716.datas.Reply
import kr.co.tjoeun.colosseum_20200716.utils.ServerUtil
import kr.co.tjoeun.colosseum_20200716.utils.TimeUtil
import org.json.JSONObject

class ViewReplyDetailActivity : BaseActivity() {

//    보려는 의견의 id는 여러 함수에서 공유할 것 같다.
//    그래서 멤버변수로 만들고 저장한다.
    var mReplyId = 0

//    이 화면에서 보여줘야할 의견의 정보를 가진 변수 => 멤버변수
    lateinit var mReply : Reply

//    의견에 달린 답글들을 저장할 목록
    val mReReplyList = ArrayList<Reply>()

    lateinit var mReReplyAdapter : ReReplyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_reply_detail)
        setupEvents()
        setValues()
    }

    override fun setupEvents() {

    }

    override fun setValues() {

//        의견 리스트뷰에서 보내준 id값을 멤버변수에 담아주자.
        mReplyId = intent.getIntExtra("replyId", 0)

//        해당 id값에 맞는 의견 정보를 (서버에서) 다시 불러오자

        getReplyFromServer()

        mReReplyAdapter = ReReplyAdapter(mContext, R.layout.re_reply_list_item, mReReplyList)
        reReplyListView.adapter = mReReplyAdapter

    }

//    서버에서 의견 정보 불러오기

    fun getReplyFromServer() {

        ServerUtil.getRequestReplyDetail(mContext, mReplyId, object : ServerUtil.JsonResponseHandler {
            override fun onResponse(json: JSONObject) {

                val data = json.getJSONObject("data")
                val replyObj = data.getJSONObject("reply")

//                replyObj 를 => Reply클래스로 변환 => mReply에 저장

                mReply = Reply.getReplyFromJson(replyObj)

//                replies JSONArray를 돌면서 => Reply로 변환해서 => mReReplyList에 추가

                val replies = replyObj.getJSONArray("replies")

                for (i in 0 until replies.length()) {

                    val reply = Reply.getReplyFromJson(replies.getJSONObject(i))

                    mReReplyList.add(reply)

                }

//                mReply 내부의 변수(정보) 들을 => 화면에 반영

                runOnUiThread {

                    writerNickNameTxt.text = mReply.writer.nickName

                    selectedSideTitleTxt.text = "(${mReply.selectedSide.title})"

                    writtenDateTimeTxt.text = TimeUtil.getTimeAgoFromCalendar(mReply.writtenDateTime)

                    replyContentTxt.text = mReply.content

//                    답글 목록이 모두 불러지면 새로 반영
                    mReReplyAdapter.notifyDataSetChanged()

                }

            }

        })

    }

}