package jam.mbarakat.com.myshares.helpers;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by MBARAKAT on 2/2/2016.
 */
public abstract class EndlessRecyclerView extends RecyclerView.OnScrollListener {
    private int previousTotal = 0;
    private int visibleThreshold = 5;
    private boolean loading = true;
    private int current_page = 1;
    private LinearLayoutManager mLinearLayoutManager;
    int firstVisibleItem, visibleItemsCount, totalItemsCount;

    EndlessRecyclerView(LinearLayoutManager mLinearLayoutManager){
        this.mLinearLayoutManager = mLinearLayoutManager;
    }




    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        visibleItemsCount = recyclerView.getChildCount();
        totalItemsCount = mLinearLayoutManager.getItemCount();
        firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();
        if(loading){
            if(totalItemsCount > previousTotal){
                loading =false;
                previousTotal = totalItemsCount;
            }
        }
        if(!loading && (totalItemsCount - visibleItemsCount) <= (firstVisibleItem + visibleThreshold)){
            current_page++;
            onLoadMore(current_page);
            loading = true;
        }
    }

    public abstract void onLoadMore(int current_page);
}
