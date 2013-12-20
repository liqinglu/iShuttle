package nandgate.ishuttle.selector;

import java.util.List;

public class AlphaWheelAdapter implements WheelAdapter{

	private int length=0;
	private List<String> alpha=null;
	private Boolean isLong=false;
	
	AlphaWheelAdapter(List<String> alpha){
		this.alpha=alpha;
		length=this.alpha.size();
	}
	
	public AlphaWheelAdapter setLongString(){
		this.isLong=true;
		return this;
	}
	@Override
	public int getItemsCount() {
		// TODO Auto-generated method stub
		return length;
	}

	@Override
	public String getItem(int index) {
		// TODO Auto-generated method stub
		if(index>=alpha.size())
			index=0;
		return this.alpha.get(index);
	}

	@Override
	public int getMaximumLength() {
		// TODO Auto-generated method stub
		if(isLong)
			return 12;
		
		return 1;
	}

}
