package mglowinski.library.fragments;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import mglowinski.library.R;
import mglowinski.library.model.Book;

public class PagerFragment extends android.support.v4.app.Fragment{

    private List<Book> data;
    private int currentPosition;
    private String userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.view_pager, container, false);
        ViewPager mViewPager = (ViewPager) v.findViewById(R.id.pager_view);
        currentPosition = getArguments().getInt("CURRENT_POSITION");
        data = (List<Book>) getArguments().getSerializable("books");
        userId = getArguments().getString("userId");

        FragmentItemPagerAdapter fragmentItemPagerAdapter = new FragmentItemPagerAdapter(getFragmentManager(), data, userId);
        mViewPager.setAdapter(fragmentItemPagerAdapter);
        mViewPager.setCurrentItem(currentPosition);
        return v;
    }
}