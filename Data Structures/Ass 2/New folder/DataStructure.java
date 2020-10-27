import sun.awt.image.ImageWatched;

import java.util.LinkedList;
import java.util.List;
import java.util.Arrays;

public class DataStructure implements DT {

    private Container firstX; //first link in x-axis list
    private Container firstY; //first link in y-axis list
    private Container lastX;  //last link in x-axis list
    private Container lastY;  //last link in y-axis list
    private int size;   //number of containers in the list

    //////////////// DON'T DELETE THIS CONSTRUCTOR ////////////////

    public DataStructure() {
        firstX = null;
        firstY = null;
        lastX = null;
        lastY = null;
        size = 0;
    }

    @Override
    public void addPoint(Point point) {
                Container newContainer = new Container(point);
                if (size == 0) {
                    firstX = newContainer;
            firstY = newContainer;
            lastX = newContainer;
            lastY = newContainer;
        }
        else if (size == 1) { //insert second container to list
            // insert to x-axis list
            insert2ndByAxis(true, point, newContainer);
            // insert to y-axis list
            insert2ndByAxis(false, point, newContainer);
        }
        else { //size >= 2
            //insert to x-axis list
            insertToList(true, newContainer);
            //insert to y-axis list
            insertToList(false, newContainer);
        }
        size++;
    }

    @Override
    public Point[] getPointsInRangeRegAxis(int min, int max, Boolean axis) {
        int b = 0;
        Container curr = getFirstByAxis(axis);
        while (curr != null) { // counts how many points we have
            if (getByAxis(axis, curr.getData()) >= min && getByAxis(axis, curr.getData()) <= max) {
                b++;
            }
            curr = curr.getNext(axis);
        }

        Point[] array = new Point[b];
        curr = getFirstByAxis(axis);
        int i = 0;
        while (curr != null) { // checks if the points are in range, by the given axis
            if (getByAxis(axis, curr.getData()) >= min && getByAxis(axis, curr.getData()) <= max) {
                array[i] = curr.getData();
                i++;
            }
            curr = curr.getNext(axis);
        }
        return array;
    }


    @Override
    public Point[] getPointsInRangeOppAxis(int min, int max, Boolean axis) {
        int b = 0;
        Container curr = getFirstByAxis(!axis);
        while (curr != null) { // counts how many points do we have
            if (getByAxis(axis, curr.getData()) >= min && getByAxis(axis, curr.getData()) <= max) {
                b++;
            }
            curr = curr.getNext(!axis);
        }

        Point[] array = new Point[b];
        curr = getFirstByAxis(!axis);
        int i = 0;
        while (curr != null) { // checks if the points are in range by the opposite axis of the given axis
            if (getByAxis(axis, curr.getData()) >= min && getByAxis(axis, curr.getData()) <= max) {
                array[i] = curr.getData();
                i++;
            }
            curr = curr.getNext(!axis);
        }
        return array;
    }

    @Override
    public double getDensity() { // calculate density by formula
        if (size == 0)
            return 0;
        int maxX = lastX.getData().getX();
        int minX = firstX.getData().getX();
        int maxY = lastY.getData().getY();
        int minY = firstY.getData().getY();
        double density = size / ((maxX - minX) * (maxY - minY));
        return density;
    }


    @Override
    public void narrowRange(int min, int max, Boolean axis) {
        if (size == 0) {
            return;
        }
        removeByMin(min, axis); // remove the points with smaller value of x or y
        removeByMax(max, axis); // remove the points with larger value of x or y
    }

    @Override
    public Boolean getLargestAxis() {  // checks which axis is larger
        int maxX = lastX.getData().getX();
        int minX = firstX.getData().getX();
        int maxY = lastY.getData().getY();
        int minY = firstY.getData().getY();
        return (maxX - minX) > (maxY - minY);
    }

    @Override
    public Container getMedian(Boolean axis) {  // move on the data structure and find the median container
        int i = 0;
        Container tmp = getFirstByAxis(axis);
        while (i < (size / 2)) {
            tmp = tmp.getNext(axis);
            i++;
        }
        return tmp;
    }
    public Point[] nearestPairInStrip(Container container, double width,
                                      Boolean axis) {
        int B = 1;
        Container firstRange;
        Container lastRange;
        Container Median = container;
        Point[] closest = new Point[2];
        // find the points with Zp-(width/2) value (by axis)
        // start from median value and go backwards
        while (container.getPrev(axis)!= null && getByAxis(axis, container.getPrev(axis).getData()) >= getByAxis(axis, Median.getData()) - (width / 2)) {
            container = container.getPrev(axis);
            B++;  // count the points is in the range
        }
        firstRange = container;
        container = Median;
        // find the points with Zp+(width/2) value (by axis)
        // start from median value and go forward
        while (container.getNext(axis) !=null && getByAxis(axis, container.getNext(axis).getData()) <= getByAxis(axis, Median.getData()) + (width / 2)) {
            container = container.getNext(axis);
            B++;  // count the points in the range
        }
        lastRange = container;
        // if there are less than 2 points in the strip, return null
        if (B < 2) {
            return null;
        }
        else {
            // check which complexity is shorter (according the amount of the points)
            Double Check = B * (Math.log(B) / Math.log(2));
            Point[] array;
            if (Check > size) {
                // if O(n) is smaller, solve by it
                array = SolveByN(firstRange, lastRange, axis);
            }
            else {
                //solve by O(b(log b))
                array = SolveByBlogB(firstRange, axis, B);
            }

            // initialize minimum distance in order to compare
            double minimumDistance = DistanceCalculator(array[0], array[1]);
            // initialize the array with the first and second points (arbitrarily)
            closest[0] = array[0];
            closest[1] = array[1];
            // compare every value in the array with the next 7 values
            // according the known algorithm of finding 2 closest points
            for (int i = 0; i < array.length; i++) {
                    int j = i + 1;
                    while (j <= i + 7 && j<array.length){
                        double distance = DistanceCalculator(array[i], array[j]);
                        if(distance < minimumDistance){
                            minimumDistance = distance;
                            closest[0] = array[i];
                            closest[1] = array[j];
                        }
                        j = j +1;
                    }
            }
        }
        return closest;
    }


    @Override
    public Point[] nearestPair() {
        Point[] ClosestPair = new Point[2];
        Point[] stripPair ;
        Point[] left ;
        Point[] right ;
        boolean axis = getLargestAxis();
        double leftdistance ;
        double rightdistance ;
        double mindist ;
        double stripdist ;
        if (size < 2){
            return null;
        }
        if (size == 2) {
            ClosestPair[0] = getFirstByAxis(axis).getData();
            ClosestPair[1] = getLastByAxis(axis).getData();
            return ClosestPair;
        }
        else {
            Container median = getMedian(axis);
            DataStructure leftDT = copyStructure(getFirstByAxis(axis), median.getPrev(axis), axis); // copy the left part of the data structure to a new DS
            DataStructure rightDT = copyStructure(median, getLastByAxis(axis), axis); // copy the right part of the data structure to a new DS
            left = leftDT.nearestPair();
            right = rightDT.nearestPair();
            // choose the minimum distance between the two points of the left and right part
            if(left != null && right != null) {
                leftdistance = DistanceCalculator(left[0], left[1]);
                rightdistance = DistanceCalculator(right[0], right[1]);
                mindist = Math.min(leftdistance, rightdistance);
                if (leftdistance < rightdistance) {
                    ClosestPair[0] = left[0];
                    ClosestPair[1] = left[1];
                }
                else {
                    ClosestPair[0] = right[0];
                    ClosestPair[1] = right[1];
                }
            } // if one of the arrays return null, set the minimum distance to be the distance of the two points of the other array
            else if (left == null){
                rightdistance = DistanceCalculator(right[0], right[1]);
                mindist = rightdistance;
                ClosestPair[0] = right[0];
                ClosestPair[1] = right[1];
            }
            else {
                leftdistance = DistanceCalculator(left[0], left[1]);
                mindist = leftdistance;
                ClosestPair[0] = left[0];
                ClosestPair[1] = left[1];
            }
            //check if the minimum distance is between two points that one is on the left part and the other is on the right part
            stripPair = nearestPairInStrip(median, 2 * mindist, axis);
            // choose if the minimum is between 2 points from the same part or from different parts of the data structure
            if (stripPair!=null)  {
                stripdist = DistanceCalculator(stripPair[0], stripPair[1]);
                if (stripdist < mindist) {
                    return stripPair;
                }
                else {
                    return ClosestPair;
                }
            }else{
                return ClosestPair;
            }

        }

    }



    /////////////////// HELPER METHODS ///////////////////
    private double DistanceCalculator (Point p1, Point p2) {
        int x1 = p1.getX();
        int x2 = p2.getX();
        int y1 = p1.getY();
        int y2 = p2.getY();
        return Math.sqrt(Math.pow(x1 - x2,2) + Math.pow(y1 - y2,2));

    }

    private DataStructure copyStructure(Container firstRange, Container lastRange, boolean axis) {
        DataStructure CopyDt = new DataStructure();
        Container pointer = firstRange;
        while (pointer != lastRange.getNext(axis)) {
            Point p = new Point(pointer.getData());
            CopyDt.addPoint(p);
            pointer = pointer.getNext(axis);
        }
        return CopyDt;
    }


    private Point[] SolveByN(Container firstRange, Container lastRange, boolean axis){
        Point[] array ;
        array = getPointsInRangeOppAxis(getByAxis(axis, firstRange.getData()), getByAxis(axis, lastRange.getData()), axis);

        return array;
    }

    private Point[] SolveByBlogB(Container firstRange, boolean axis, int B){
        Point[] array = new Point[B];
        Container pointer = firstRange;
        for(int i = 0; i< B; i++){
            array[i]= pointer.getData();
            pointer = pointer.getNext(axis);
        }
        PointComparator comparator = new PointComparator(!axis);
        Arrays.sort(array, comparator);
        return array;
    }

    public int getByAxis(Boolean isX, Point point) {
        return isX ? point.getX() : point.getY();
    }

    private void insertToList(Boolean isX, Container newContainer) {
        Container current = getFirstByAxis(isX);
        while (current != null) {
            if (getByAxis(isX, current.getData()) > getByAxis(isX, newContainer.getData())) {
                if(current == getFirstByAxis(isX)){
                    current.setPrev(isX, newContainer);
                    newContainer.setNext(isX, current);
                    setFirstByAxis(isX, newContainer);
                    return;
                }
                else {
                    newContainer.setPrev(isX, current.getPrev(isX));
                    newContainer.setNext(isX, current);
                    current.getPrev(isX).setNext(isX, newContainer);
                    current.setPrev(isX, newContainer);
                    return;
                }
                }
            current = current.getNext(isX);
        }
        //need to insert last
        getLastByAxis(isX).setNext(isX, newContainer);
        newContainer.setPrev(isX, getLastByAxis(isX));
        setLastByAxis(isX, newContainer);

    }

    public void setFirstByAxis(Boolean isX, Container container) {
        if (isX) {
            firstX = container;
        }
        else {
            firstY = container;
        }
    }

    public void setLastByAxis(Boolean isX, Container container) {
        if (isX) {
            lastX = container;
        }
        else {
            lastY = container;
        }
    }

    public Container getFirstByAxis(Boolean isX) {
        return isX ? firstX : firstY;
    }

    public Container getLastByAxis(Boolean isX) {
        return isX ? lastX : lastY;
    }


    private void insert2ndByAxis(Boolean isX, Point point, Container newContainer) {
        if (getByAxis(isX, getFirstByAxis(isX).getData()) < getByAxis(isX, point)) { //put after first
            newContainer.setPrev(isX, getFirstByAxis(isX));
            getLastByAxis(isX).setNext(isX, newContainer);
            setLastByAxis(isX, newContainer);
        }
        else { //put before first
            getFirstByAxis(isX).setPrev(isX, newContainer);
            newContainer.setNext(isX, getFirstByAxis(isX));
            setFirstByAxis(isX, newContainer);
        }

    }
    private void removeByMin(int min, Boolean axis) {
        Container current = getFirstByAxis(axis);
        while (current != null && getByAxis(axis, current.getData()) < min) {

            if(current.getNext(axis) == null && current.getPrev(axis) == null){
                setFirstByAxis(axis, null);
                setLastByAxis(axis, null);
                setFirstByAxis(!axis, null);
                setLastByAxis(!axis, null);
                current = null; // to exit the while
                size = 0;
            } else {

                current.getNext(axis).setPrev(axis, null);
                setFirstByAxis(axis, current.getNext(axis));

                // handle opposite axis list
                if (current.getNext(!axis) == null && current.getPrev(!axis) != null) { //if current is last in opp. axis
                    current.getPrev(!axis).setNext(!axis, null);
                    setLastByAxis(!axis, current.getPrev(!axis));
                }else if (current.getPrev(!axis) == null && current.getNext(!axis) != null) { //if current is first in opp. axis
                    current.getNext(!axis).setPrev(!axis, null);
                    setFirstByAxis(!axis, current.getNext(!axis));
                } else { // if current is not first or last in opp.axis
                    current.getNext(!axis).setPrev(!axis, current.getPrev(!axis));
                    current.getPrev(!axis).setNext(!axis, current.getNext(!axis));
                }

                // advance current in axis list
                current = current.getNext(axis);
                size = size - 1;

            }
        }

    }
    private void removeByMax(int max, Boolean axis) {
        Container current = getLastByAxis(axis);
        while (current != null && getByAxis(axis, current.getData()) > max) {
            if(current.getNext(axis) == null && current.getPrev(axis) == null){
                setFirstByAxis(axis, null);
                setLastByAxis(axis, null);
                setFirstByAxis(!axis, null);
                setLastByAxis(!axis, null);
                current = null; // to exit the while
                size = 0;
            }else{
                    current.getPrev(axis).setNext(axis, null);
                     setLastByAxis(axis, current.getPrev(axis));

                    // handle opposite axis list
                    if (current.getNext(!axis) == null && current.getPrev(!axis) != null) { //if current is last in opp. axis
                        current.getPrev(!axis).setNext(!axis, null);
                        setLastByAxis(!axis, current.getPrev(!axis));
                    }
                    else if (current.getPrev(!axis) == null && current.getNext(!axis) != null) { //if current is first in opp. axis
                        current.getNext(!axis).setPrev(!axis, null);
                        setFirstByAxis(!axis, current.getNext(!axis));
                    }
                    else { // if current is not first or last in opp.axis
                        current.getNext(!axis).setPrev(!axis, current.getPrev(!axis));
                        current.getPrev(!axis).setNext(!axis, current.getNext(!axis));
                    }

                // advance current in axis list
                current = current.getPrev(axis);
                size = size - 1;
            }

        }
    }

}

