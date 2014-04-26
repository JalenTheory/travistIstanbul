package fi.metropolia.lbs.travist.exchange;

import java.util.ArrayList;

import travist.pack.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ExchangeAdapter extends ArrayAdapter<ExchangeItem> {
	private final Context context;
	private final ArrayList<ExchangeItem> itemsArrayList;
	
	public ExchangeAdapter(Context context, ArrayList<ExchangeItem> itemsArrayList) {
		super(context, R.layout.exchange_rates_row, itemsArrayList);
		this.context = context;
		this.itemsArrayList = itemsArrayList;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		View rowView = inflater.inflate(R.layout.exchange_rates_row, parent, false);
		
		ImageView exchangeIcon = (ImageView) rowView.findViewById(R.id.exchange_rates_currency_sign);
		TextView exchangeCurrency = (TextView) rowView.findViewById(R.id.exchange_rates_currency_name);
		TextView exchangeRate = (TextView) rowView.findViewById(R.id.exchange_rates_currency_rate);
		
		exchangeIcon.setImageResource(itemsArrayList.get(position).getIcon());
		exchangeCurrency.setText(itemsArrayList.get(position).getCurrency());
		exchangeRate.setText(itemsArrayList.get(position).getRate());
		
		return rowView;
	}

}
