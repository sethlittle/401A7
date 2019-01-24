package a7;

public class RegionImpl implements Region {

	/*
	 * The region class uses two coordinates to create a region of points, with
	 * access to the sides and top and bottom of the region along with the
	 * upperleft corner and lowerright corner
	 */

	private Coordinate _upperLeft;
	private Coordinate _lowerRight;
	private int _left;
	private int _right;
	private int _top;
	private int _bottom;

	public RegionImpl(Coordinate a, Coordinate b) {
		if (a.getX() < 0 || a.getY() < 0 || b.getX() < 0 || b.getY() < 0) {
			throw new IllegalArgumentException("The Coordinate(s) are out of bounds, they must be greater than 0");
		}

		_left = a.getX() < b.getX() ? a.getX() : b.getX();
		_right = a.getX() < b.getX() ? b.getX() : a.getX();
		_bottom = a.getY() < b.getY() ? b.getY() : a.getY();
		_top = a.getY() < b.getY() ? a.getY() : b.getY();

		_upperLeft = new Coordinate(_left, _top);
		_lowerRight = new Coordinate(_right, _bottom);
	}

	@Override
	public Coordinate getUpperLeft() {
		return _upperLeft;
	}

	@Override
	public Coordinate getLowerRight() {
		return _lowerRight;
	}

	@Override
	public int getTop() {
		return _top;
	}

	@Override
	public int getBottom() {
		return _bottom;
	}

	@Override
	public int getLeft() {
		return _left;
	}

	@Override
	public int getRight() {
		return _right;
	}

	/*
	 * The intersect method determines if two regions have any points that are
	 * in both regions, the one the method is called on and the one passed as a
	 * parameter
	 */

	@Override
	public Region intersect(Region other) throws NoIntersectionException {
		if (other == null) {
			throw new NoIntersectionException();
		}

		int newLeft = _left;
		int newRight = _right;
		int newTop = _top;
		int newBottom = _bottom;

		/*
		 * If the left and right of the parameter region are outside the left or
		 * right of this object, then there is no intersection, same thing for
		 * the top and bottom
		 */

		if ((other.getLeft() > _right && other.getRight() > _right)
				|| (other.getLeft() < _left && other.getRight() < _left)
				|| (other.getTop() < _top && other.getBottom() < _top)
				|| (other.getTop() > _bottom && other.getBottom() > _bottom)) {
			throw new NoIntersectionException();
		}

		/*
		 * For intersection, we must use whichever value is smaller for each
		 * characteristic (top, bottom, left, right)
		 */

		if (other.getLeft() >= _left) {
			newLeft = other.getLeft();
		}

		if (other.getRight() <= _right) {
			newRight = other.getRight();
		}

		if (other.getTop() >= _top) {
			newTop = other.getTop();
		}

		if (other.getBottom() <= _bottom) {
			newBottom = other.getBottom();
		}

		Coordinate topLeft = new Coordinate(newLeft, newTop);
		Coordinate bottomRight = new Coordinate(newRight, newBottom);

		/*
		 * This method returns another Region with new Coordinates
		 */
		return new RegionImpl(topLeft, bottomRight);
	}

	/*
	 * The union method includes every point the regions encompass
	 */

	@Override
	public Region union(Region other) {
		if (other == null) {
			return this;
		}

		int newLeft = _left;
		int newRight = _right;
		int newTop = _top;
		int newBottom = _bottom;

		/*
		 * The union method uses whichever characteristic is larger to form two
		 * new Coordinates to create a new Region to return
		 * 
		 */

		if (other.getLeft() <= _left) {
			newLeft = other.getLeft();
		}

		if (other.getRight() >= _right) {
			newRight = other.getRight();
		}

		if (other.getTop() <= _top) {
			newTop = other.getTop();
		}

		if (other.getBottom() >= _bottom) {
			newBottom = other.getBottom();
		}

		Coordinate topLeft = new Coordinate(newLeft, newTop);
		Coordinate bottomRight = new Coordinate(newRight, newBottom);

		return new RegionImpl(topLeft, bottomRight);
	}

}
