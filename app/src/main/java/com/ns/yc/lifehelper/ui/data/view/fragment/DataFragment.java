package com.ns.yc.lifehelper.ui.data.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.SizeUtils;
import com.ns.yc.lifehelper.R;
import com.ns.yc.lifehelper.base.adapter.BaseViewPagerRollAdapter;
import com.ns.yc.lifehelper.bean.ImageIconBean;
import com.ns.yc.lifehelper.ui.data.contract.DataFragmentContract;
import com.ns.yc.lifehelper.ui.data.presenter.DataFragmentPresenter;
import com.ns.yc.lifehelper.ui.data.view.adapter.DataToolAdapter;
import com.ns.yc.lifehelper.ui.data.view.adapter.NarrowImageAdapter;
import com.ns.yc.lifehelper.ui.data.view.adapter.ViewPagerGridAdapter;
import com.ns.yc.lifehelper.ui.main.view.MainActivity;
import com.pedaily.yc.ycdialoglib.toast.ToastUtils;
import com.yc.cn.ycbannerlib.snap.ScrollPageHelper;
import com.yc.configlayer.arounter.ARouterUtils;
import com.yc.configlayer.arounter.RouterConfig;
import com.yc.configlayer.constant.Constant;
import com.yc.customwidget.MyGridView;
import com.yc.toollayer.FastClickUtils;
import com.yc.toollayer.GoToScoreUtils;
import com.yc.toollayer.calendar.CalendarReminderUtils;
import com.yc.toollib.crash.CrashToolUtils;
import com.yc.zxingserver.demo.CodeActivity;
import com.ycbjie.library.base.mvp.BaseFragment;

import org.yczbj.ycrefreshviewlib.YCRefreshView;
import org.yczbj.ycrefreshviewlib.item.SpaceViewItemLine;

import java.util.ArrayList;
import java.util.List;


/**
 * <pre>
 *     @author yangchong
 *     blog  : https://github.com/yangchong211
 *     time  : 2016/03/22
 *     desc  :
 *     revise: v1.4 17年6月8日
 *             v1.5 17年10月3日修改
 * </pre>
 */
public class DataFragment extends BaseFragment<DataFragmentPresenter> implements
        View.OnClickListener, DataFragmentContract.View {

    private ViewPager mVpPager;
    private LinearLayout mLlPoints;
    private TextView mTvNoteEdit;
    private TextView mTvNewsZhiHu;
    private YCRefreshView mRecyclerView;
    private MyGridView mGridView;
    private MainActivity activity;
    private NarrowImageAdapter adapter;
    private DataFragmentContract.Presenter presenter = new DataFragmentPresenter(this);

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (activity != null) {
            activity = null;
        }
    }

    @Override
    public int getContentView() {
        return R.layout.fragment_data;
    }

    @Override
    public void initView(View view) {
        mVpPager = view.findViewById(R.id.vp_pager);
        mLlPoints = view.findViewById(R.id.ll_points);
        mTvNoteEdit = view.findViewById(R.id.tv_note_edit);
        mTvNewsZhiHu = view.findViewById(R.id.tv_news_zhi_hu);
        mRecyclerView = view.findViewById(R.id.recyclerView);
        mGridView = view.findViewById(R.id.gridView);

        iniVpData();
    }

    @Override
    public void initListener() {
        mTvNoteEdit.setOnClickListener(this);
        mTvNewsZhiHu.setOnClickListener(this);
    }


    @Override
    public void initData() {
        presenter.initGridViewData();
        presenter.initRecycleViewData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_note_edit:
                break;
            case R.id.tv_news_zhi_hu:
                break;
            default:
                break;
        }
    }

    private void iniVpData() {
        List<ImageIconBean> listData = presenter.getVpData();
        //每页显示的最大的数量
        final int mPageSize = 8;
        //总的页数向上取整
        final int totalPage = (int) Math.ceil(listData.size() * 1.0 / mPageSize);
        List<View> viewPagerList = new ArrayList<>();
        for (int i = 0; i < totalPage; i++) {
            //每个页面都是inflate出一个新实例
            final GridView gridView = (GridView) View.inflate(activity,
                    R.layout.item_vp_grid_view, null);
            gridView.setAdapter(new ViewPagerGridAdapter(activity, listData, i, mPageSize));
            //添加item点击监听
            gridView.setOnItemClickListener((adapterView, view, position, l) -> {
                Object obj = gridView.getItemAtPosition(position);
                //int pos = position + totalPage * mPageSize;
                if (obj instanceof ImageIconBean) {
                    int pos = ((ImageIconBean) obj).getId();
                    toPage(pos);
                }
            });
            //每一个GridView作为一个View对象添加到ViewPager集合中
            viewPagerList.add(gridView);
        }
        //设置ViewPager适配器
        mVpPager.setAdapter(new BaseViewPagerRollAdapter(viewPagerList));

        //添加小圆点
        final ImageView[] ivPoints = new ImageView[totalPage];
        for (int i = 0; i < totalPage; i++) {
            //循坏加入点点图片组
            ivPoints[i] = new ImageView(activity);
            if (i == 0) {
                ivPoints[i].setImageResource(R.drawable.ic_page_focuese);
            } else {
                ivPoints[i].setImageResource(R.drawable.ic_page_unfocused);
            }
            ivPoints[i].setPadding(8, 8, 8, 8);
            mLlPoints.addView(ivPoints[i]);
        }
        mVpPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < totalPage; i++) {
                    if (i == position) {
                        ivPoints[i].setImageResource(R.drawable.ic_page_focuese);
                    } else {
                        ivPoints[i].setImageResource(R.drawable.ic_page_unfocused);
                    }
                }
            }
        });
    }

    private void toPage(int pos) {
        switch (pos) {
            case 0:
                ARouterUtils.navigation(RouterConfig.Video.ACTIVITY_VIDEO_VIDEO);
                break;
            case 1:
                Bundle bundle1 = new Bundle();
                bundle1.putString(Constant.URL,"https://github.com/yangchong211/YCMeiZiTu");
                bundle1.putString(Constant.TITLE,"爬妹子图");
                ARouterUtils.navigation(RouterConfig.Library.ACTIVITY_LIBRARY_WEB_VIEW,bundle1);
                break;
            case 2:
                ARouterUtils.navigation(RouterConfig.Note.ACTIVITY_MARKDOWN_ACTIVITY);
                break;
            case 3:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!Settings.canDrawOverlays(activity)) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        activity.startActivity(intent);
                    }
                }
                break;
            case 4:

                break;
            case 5:
                ARouterUtils.navigation(RouterConfig.Demo.ACTIVITY_OTHER_BANNER_ACTIVITY);
                break;
            case 6:
                Bundle bundle6 = new Bundle();
                bundle6.putString(Constant.URL,Constant.FLUTTER);
                bundle6.putString(Constant.TITLE,"flutter极致体验的WanAndroid客户端");
                ARouterUtils.navigation(RouterConfig.Library.ACTIVITY_LIBRARY_WEB_VIEW,bundle6);
                break;
            case 7:
                Bundle bundle7 = new Bundle();
                bundle7.putString(Constant.URL,"https://github.com/yangchong211/YCStateLayout");
                bundle7.putString(Constant.TITLE,"状态切换，View状态的切换和Activity彻底分离开");
                ARouterUtils.navigation(RouterConfig.Library.ACTIVITY_LIBRARY_WEB_VIEW,bundle7);
                break;
            case 8:
                ARouterUtils.navigation(RouterConfig.Demo.ACTIVITY_OTHER_PROGRESS2_ACTIVITY);
                break;
            case 9:
                ARouterUtils.navigation(RouterConfig.Game.ACTIVITY_OTHER_PIN_TU_ACTIVITY);
                break;
            default:
                Toast.makeText(activity, pos+ "---", Toast.LENGTH_SHORT).show();
                break;
        }
    }


    @Override
    public void setGridView(String[] toolName, ArrayList<Integer> logoList) {
        DataToolAdapter adapter = new DataToolAdapter(activity, toolName, logoList);
        mGridView.setAdapter(adapter);
        mGridView.setOnItemClickListener((parent, view, position, id) -> {
            if (FastClickUtils.isFastDoubleClick()){
                return;
            }
            switch (position) {
                case 0:
                    ARouterUtils.navigation(RouterConfig.Demo.ACTIVITY_LARGE_IMAGE_ACTIVITY);
                    break;
                case 1:
                    ARouterUtils.navigation(RouterConfig.Demo.ACTIVITY_OTHER_BANNER_ACTIVITY);
                    break;
                case 2:
                    ARouterUtils.navigation(RouterConfig.Love.ACTIVITY_LOVE_ACTIVITY);
                    break;
                case 3:
                    ARouterUtils.navigation(RouterConfig.Demo.ACTIVITY_OTHER_PROGRESS1_ACTIVITY);
                    break;
                    //nfc测试
                case 4:
                    ARouterUtils.navigation(RouterConfig.Nfc.ACTIVITY_NFC_MAIN);
                    break;
                case 5:
                    ARouterUtils.navigation(RouterConfig.Game.ACTIVITY_OTHER_AIR_ACTIVITY);
                    break;
                case 6:
                    ARouterUtils.navigation(RouterConfig.Game.ACTIVITY_OTHER_MONKEY_ACTIVITY);
                    break;
                case 7:
                    ARouterUtils.navigation(RouterConfig.DouBan.ACTIVITY_DOU_TOP_ACTIVITY);
                    break;
                case 8:
                    Bundle bundle8 = new Bundle();
                    bundle8.putString(Constant.URL,"https://github.com/yangchong211/YCThreadPool");
                    bundle8.putString(Constant.TITLE,"轻量级线程池封装库");
                    ARouterUtils.navigation(RouterConfig.Library.ACTIVITY_LIBRARY_WEB_VIEW,bundle8);
                    break;
                case 9:
                    ARouterUtils.navigation(RouterConfig.Video.ACTIVITY_VIDEO_VIDEO);
                    break;
                case 10:
                    ARouterUtils.navigation(RouterConfig.Demo.ACTIVITY_OTHER_PROGRESS3_ACTIVITY);
                    break;
                case 11:
                    ARouterUtils.navigation(RouterConfig.Game.ACTIVITY_BOOK_DOODLE_ACTIVITY);
                    break;
                    //去评分
                case 12:
                    if(GoToScoreUtils.isPkgInstalled(activity,"com.tencent.mm")){
                        GoToScoreUtils.startMarket(activity,"com.tencent.mm");
                    } else {
                        ToastUtils.showRoundRectToast("请先安装应用宝");
                    }
                    break;
                    //向系统日历插入事件
                case 13:
                    CalendarReminderUtils.test(activity);
                    break;
                    //测试崩溃
                case 14:
                    CrashToolUtils.startCrashTestActivity(activity);
                    break;
                    //跳转崩溃记录页面
                case 15:
                    CrashToolUtils.startCrashListActivity(activity);
                    break;
                    //生产条形码
                case 16:
                    Intent intent16 = new Intent(activity, CodeActivity.class);
                    intent16.putExtra(CodeActivity.KEY_IS_QR_CODE,false);
                    startActivity(intent16);
                    break;
                    //生产二维码
                case 17:
                    Intent intent17 = new Intent(activity, CodeActivity.class);
                    intent17.putExtra(CodeActivity.KEY_IS_QR_CODE,true);
                    startActivity(intent17);
                    break;
                default:
                    break;
            }
        });
    }


    @Override
    public void setRecycleView(final ArrayList<Integer> list) {
        mRecyclerView.setAdapter(adapter = new NarrowImageAdapter(activity));
        mRecyclerView.setHorizontalScrollBarEnabled(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(
                activity, LinearLayoutManager.HORIZONTAL, false));
        ScrollPageHelper snapHelper = new ScrollPageHelper(Gravity.START,false);
        try {
            //attachToRecyclerView源码上的方法可能会抛出IllegalStateException异常，这里手动捕获一下
            RecyclerView.OnFlingListener onFlingListener = mRecyclerView.getRecyclerView().getOnFlingListener();
            //源码中判断了，如果onFlingListener已经存在的话，再次设置就直接抛出异常，那么这里可以判断一下
            if (onFlingListener==null){
                snapHelper.attachToRecyclerView(mRecyclerView.getRecyclerView());
            }
        } catch (IllegalStateException e){
            e.printStackTrace();
        }
        mRecyclerView.addItemDecoration(new SpaceViewItemLine(SizeUtils.dp2px(8)));
        mRecyclerView.setRefreshListener(() -> {
            adapter.clear();
            adapter.addAll(list);
        });
        adapter.addAll(list);
        adapter.setOnItemClickListener(position -> {
            ARouterUtils.navigation(RouterConfig.Demo.ACTIVITY_OTHER_GALLERY_ACTIVITY);
        });
    }

}
