package com.example.okta.githubusersearch.activty;

import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.okta.githubusersearch.R;
import com.example.okta.githubusersearch.adapter.RecycleviewAdapterSearch;
import com.example.okta.githubusersearch.adapter.RecycleviewAdapterSearchUser;
import com.example.okta.githubusersearch.model.ResponseSearch;
import com.example.okta.githubusersearch.service.APIClient;
import com.example.okta.githubusersearch.service.APISearch;
import com.example.okta.githubusersearch.widget.EndlessRecyclerOnScrollListener;

import java.lang.reflect.Field;
import java.security.NoSuchAlgorithmException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.SWToolBar)
    SearchView SWToolBar;
    @BindView(R.id.RVResultSearch)
    RecyclerView mRecyclerView;
    LinearLayoutManager mLayoutManager;
    RecycleviewAdapterSearchUser mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setStatusBarColor();
        setSearchView();
    }

    private void setSearchView() {
        SWToolBar.setQueryHint("Username");
        SWToolBar.onActionViewExpanded();
        SWToolBar.setIconified(false);
        SWToolBar.clearFocus();
        SWToolBar.setActivated(true);
        SWToolBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                consumeAPIusername(query, "1");
                SWToolBar.clearFocus();
                if (getCurrentFocus() != null) {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        try {
            Field mDrawable = SearchView.class.getDeclaredField("mSearchHintIcon");
            mDrawable.setAccessible(true);
            Drawable drawable = (Drawable) mDrawable.get(SWToolBar);
            drawable.setAlpha(0);
            drawable.setBounds(0, 0, 0, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int searchId = getResources().getIdentifier("android:id/search_plate", null, null);
        View v = SWToolBar.findViewById(searchId);
        v.setBackgroundResource(android.R.color.transparent);
    }

    private void consumeAPIusername(final String query, final String page) {
        final ProgressDialog pd = new ProgressDialog(this, R.style.MyTheme);
        pd.setCancelable(false);
        pd.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        pd.show();

        APISearch client = APIClient.createService(APISearch.class);

        Call<ResponseSearch> call = client.GetUsers(query, page);
        call.enqueue(new Callback<ResponseSearch>() {
            @Override
            public void onResponse(Call<ResponseSearch> call, final Response<ResponseSearch> response) {
                if (response.body().getTotalCount() > 0) {
                    mRecyclerView.setVisibility(View.VISIBLE);
                    mAdapter = new RecycleviewAdapterSearchUser();
                    mAdapter.setItems(response.body().getItems());
                    mAdapter.notifyDataSetChanged();
                    mLayoutManager = new LinearLayoutManager(MainActivity.this);
                    mRecyclerView.setLayoutManager(mLayoutManager);
                    mRecyclerView.setAdapter(mAdapter);
                    mLayoutManager.setSmoothScrollbarEnabled(true);
                    mRecyclerView.setHasFixedSize(true);
                    mRecyclerView.setOnScrollListener(new EndlessRecyclerOnScrollListener(mLayoutManager) {
                        @Override
                        public void onLoadMore(int current_page) {
                            if (response.body().getTotalCount() > 30) {
                                if (mAdapter.getItemCount() < 1000) {
                                    response.body().getItems().add(null);
                                    mAdapter.notifyItemInserted(response.body().getItems().size() - 1);
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            response.body().getItems().remove(response.body().getItems().size() - 1);
                                            mAdapter.notifyItemRemoved(response.body().getItems().size());
                                            if (mAdapter.getItemCount() < 1000 && mAdapter.getItemCount() != response.body().getTotalCount()) {
                                                int pagebytotal = mAdapter.getItemCount();
                                                consumeAPIusernamenextpage(query, String.valueOf(pagebytotal / 30 + 1));
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Tidak ada Data Lainnya", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    }, 2000);
                                }
                            }
                        }
                    });
                } else {
                    mRecyclerView.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "User Tidak Di temukan", Toast.LENGTH_LONG).show();
                }
                pd.dismiss();
            }

            @Override
            public void onFailure(Call<ResponseSearch> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                pd.dismiss();
            }
        });
    }

    private void consumeAPIusernamenextpage(String query, String page) {

        APISearch client = APIClient.createService(APISearch.class);

        Call<ResponseSearch> call = client.GetUsers(query, page);
        call.enqueue(new Callback<ResponseSearch>() {
            @Override
            public void onResponse(Call<ResponseSearch> call, Response<ResponseSearch> response) {
                if (mAdapter.getItemCount() != response.body().getTotalCount()) {
                    for (int i = 0; i < response.body().getItems().size(); i++) {
                        mAdapter.addItem(response.body().getItems().get(i), mAdapter.getItemCount());
                    }
                }else {
                    Toast.makeText(getApplicationContext(),"Tidak ada Data lainnya",Toast.LENGTH_LONG).show();
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<ResponseSearch> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void setStatusBarColor() {
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
        }
    }
}
