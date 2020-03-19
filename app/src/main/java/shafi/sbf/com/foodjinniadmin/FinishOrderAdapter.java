package shafi.sbf.com.foodjinniadmin;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import shafi.sbf.com.foodjinniadmin.pojo.ConfirmOrder;


public class FinishOrderAdapter extends RecyclerView.Adapter<FinishOrderAdapter.MyViewHolder>{


    private Context context;
    private List<ConfirmOrder> confirmOrders;

    private OnFinishyListener onFinish;

    public FinishOrderAdapter(Context context, List<ConfirmOrder> confirmOrders) {
        this.context = context;
        this.confirmOrders = confirmOrders;
        onFinish = (OnFinishyListener) context;
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView location, type;

        LinearLayout getDetailsLayout,TypeBack;


        public MyViewHolder(View view) {
            super(view);

            getDetailsLayout = itemView.findViewById(R.id.card);
            TypeBack = itemView.findViewById(R.id.type_bac);

            location = (TextView) view.findViewById(R.id.location_show);
            type = (TextView) view.findViewById(R.id.typeText);


        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.active_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        final ConfirmOrder confirmOrder = confirmOrders.get(position);

        holder.location.setText(confirmOrder.getUser().getUserName());
        holder.type.setText(confirmOrder.getIncludeVatTotalPrice()+" TK");

        if (confirmOrder.getUser().getUserGender().equals("Male")){
            holder.TypeBack.setBackgroundColor(Color.parseColor("#00ACC1"));
        }
        else {
            holder.TypeBack.setBackgroundColor(Color.parseColor("#43A047"));
        }

        holder.getDetailsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onFinish.onFinish(confirmOrder,position+1);

            }
        });


    }

    @Override
    public int getItemCount() {
        return confirmOrders.size();
    }


    public interface OnFinishyListener{

        void onFinish(ConfirmOrder confirmOrder, int position);
    }


}
