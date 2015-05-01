package im.mz.EmailAlarm.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Created by mzhua_000 on 2015/2/2.
 */
public class PullToAddMoreListView extends ListView implements AbsListView.OnScrollListener {
    Context context;
    private int scrollState;
    private boolean isLastItemVisible = false;

    public PullToAddMoreListView(Context context) {
        super(context);
        init(context);
    }

    public PullToAddMoreListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PullToAddMoreListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        this.setOnScrollListener(this);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        this.scrollState = scrollState;
        if (scrollState == SCROLL_STATE_IDLE && isLastItemVisible) {
            Toast.makeText(context, "last", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        boolean mLastItemVisible = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount - 1);
        if (view != null)
            if (mLastItemVisible && view.getBottom() <= view.getChildAt(totalItemCount - 1).getBottom()) {
                isLastItemVisible = true;
            }
    }
}
