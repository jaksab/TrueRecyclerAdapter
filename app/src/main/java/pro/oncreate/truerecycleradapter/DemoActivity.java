package pro.oncreate.truerecycleradapter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

public class DemoActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DemoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        adapter = new DemoAdapter(this);
        recyclerView.setAdapter(adapter);

        for (int i = 0; i < 50; i++)
            adapter.add(new DemoModel("Simple Item " + i));

    }
}
