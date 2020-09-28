package pixelvisu.data;

public class Vec2 {
	double x;
	double y;
	
	public Vec2(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public Vec2(Vec2 other) {
		this.x = other.x;
		this.y = other.y;
	}

	public Vec2 lineVec(Vec2 end) {
		return new Vec2(end.x-x,end.y-y);
	}
	
	public double getDistance(Vec2 v2) {
		// distance from self to v2 connection vector
		Vec2 ba = lineVec(v2);
		return Math.sqrt(ba.x*ba.x+ba.y*ba.y);
	}
	
	public double getLength() {
		return getDistance(new Vec2(0,0));
	}
	
	public String toString() {
		return "( "+x+" / "+y+" )";
	}
	
	public boolean equals(Object o) {
		Vec2 other =(Vec2)o;
		if(x==other.x&&y==other.y)return true;
		else return false;
	}
	
	public void make(Vec2 other) {
		x = other.x;
		y = other.y;
	}
}
