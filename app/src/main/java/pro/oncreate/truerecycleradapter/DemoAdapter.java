package pro.oncreate.truerecycleradapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import pro.oncreate.truerecycler.TrueRecyclerAdapter;

/**
 * Created by Andrii Konovalenko, 2014-2017 years.
 * Copyright © 2017 [Andrii Konovalenko]. All Rights Reserved.
 */
public class DemoAdapter extends TrueRecyclerAdapter<DemoModel, DemoAdapter.DemoViewHolder> {
    private Context context;

    public DemoAdapter(Context context) {
        this.context = context;
    }

    @Override
    protected RecyclerView.ViewHolder onCreateNormalHolder(ViewGroup parent) {
        return new DemoViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_recycler, parent, false));
    }

    @Override
    public void onBindNormalHolder(DemoAdapter.DemoViewHolder holder, int position, DemoModel model) {
        holder.textView.setText(model.getText());
    }

    static class DemoViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        DemoViewHolder(View view) {
            super(view);
            textView = (TextView) view.findViewById(R.id.text);
        }

    }
}