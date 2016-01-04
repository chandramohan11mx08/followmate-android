package com.example.chandramohanr.followmate.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.chandramohanr.followmate.R;
import com.example.chandramohanr.followmate.app.models.ContactModel;

import java.util.List;

public class ContactListAdapter extends BaseAdapter {

    private Context mContext;
    private List<ContactModel> contactModelArrayList;

    public ContactListAdapter(Context c, List<ContactModel> contactModelList) {
        mContext = c;
        this.contactModelArrayList = contactModelList;
    }

    @Override
    public int getCount() {
        return contactModelArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return contactModelArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.contact_list_item, null);

        TextView vDisplayName = (TextView) view.findViewById(R.id.display_name);
        TextView vNumber = (TextView) view.findViewById(R.id.phone_number);

        vDisplayName.setText(contactModelArrayList.get(i).displayName);
        vNumber.setText(contactModelArrayList.get(i).number);

        return view;
    }
}
