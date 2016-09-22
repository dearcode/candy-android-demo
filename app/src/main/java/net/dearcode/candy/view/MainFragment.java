package net.dearcode.candy.view;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.dearcode.candy.R;
import net.dearcode.candy.model.Session;

import java.util.ArrayList;

/**
 *  * Created by c-wind on 2016/9/22 17:41
 *  * mail：root@codecn.org
 *  
 */
public class MainFragment extends Fragment {

    public static final String ARG_PAGE = "page_num";

    // 当前页
    private int currentPageNum;

    public MainFragment() {
    }

    public static MainFragment create(int pagerNum) {

        MainFragment myPageFrament = new MainFragment();
        Bundle arg = new Bundle();
        arg.putInt(ARG_PAGE, pagerNum);
        myPageFrament.setArguments(arg);

        return myPageFrament;

    }


    /* (non-Javadoc)
     * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
     *
     * Fragment创建的时候调用
     *
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentPageNum = getArguments().getInt(ARG_PAGE);
        Log.i("INFO", "onCreate:" + currentPageNum);
    }

    public static final String ANDROID_RESOURCE = "android.resource://";
    public static final String FORWARD_SLASH = "/";

    private static String resourceIdToPath(Context context, int resourceId) {
        return ANDROID_RESOURCE + context.getPackageName() + FORWARD_SLASH + resourceId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.i("INFO", "onCreateView:" + currentPageNum);

        View rootView = null;

        switch (currentPageNum) {
            case 0 :
                rootView = inflater.inflate(R.layout.fragment_candy, container, false);
                TextView textView = (TextView) rootView.findViewById(R.id.section_label);
                textView.setText("这里要显示联系人列表");
                return rootView;
            case 1:
                rootView = inflater.inflate(R.layout.fragment_session, container, false);
                RecyclerView rv = (RecyclerView) rootView.findViewById(R.id.fg_rv_session);
                rv.setLayoutManager(new LinearLayoutManager(this.getActivity()));
                rv.setItemAnimator(new DefaultItemAnimator());


                final ArrayList<Session> data = new ArrayList<Session>();
                for (int i = 0; i < 100; i++) {
                    switch (i % 2) {
                        case 0:
                            data.add(new Session(resourceIdToPath(this.getActivity(), R.mipmap.test_da), "测试标题" + i + "艺术大师疯狂拉升地方" + i, "09月22日", "测试消息" + i + "花木成畦手自栽客户打扫房间林城" + i));
                            break;
                        default:
                            data.add(new Session(resourceIdToPath(this.getActivity(), R.mipmap.test_xiao), "测试标题" + i + "阿斯利康的飞机疯狂拉升地方" + i, "09月22日", "测试消息" + i + "花木成畦手自栽客户打扫房间林城" + i));
                            break;
                    }
                }
                rv.setAdapter(new RecyclerView.Adapter() {
                    @Override
                    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                        final View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.session_item, null);
                        return new MyHolder(item);
                    }

                    @Override
                    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                        MyHolder h = (MyHolder) holder;
                        h.ivAvater.setImageURI(Uri.parse(data.get(position).getAvatar()));
                        h.tvTitle.setText(data.get(position).getTitle());
                        h.tvDate.setText(data.get(position).getDate());
                        h.tvMsg.setText(data.get(position).getMsg());

                    }

                    @Override
                    public int getItemCount() {
                        return data.size();
                    }

                    class MyHolder extends RecyclerView.ViewHolder {
                        ImageView ivAvater;
                        TextView tvTitle;
                        TextView tvDate;
                        TextView tvMsg;

                        public MyHolder(View root) {
                            super(root);
                            ivAvater = (ImageView) root.findViewById(R.id.si_iv_avatar);
                            tvTitle = (TextView) root.findViewById(R.id.si_tv_title);
                            tvDate = (TextView) root.findViewById(R.id.si_tv_date);
                            tvMsg = (TextView) root.findViewById(R.id.si_tv_msg);
                        }
                    }
                });
                return rootView;
        }
        rootView = inflater.inflate(R.layout.fragment_candy, container, false);
        TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        textView.setText("这里要显示朋友圈");

        return rootView;

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("INFO", "MyFragment Destroy...");
    }

}