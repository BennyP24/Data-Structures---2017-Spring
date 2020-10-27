
//Don't change the class name
public class Container {
    private Point data;//Don't delete or change this field;
    private Container nextX; //next link in x-axis linked list
    private Container nextY; //next link in y-axis linked list
    private Container prevX; //previous link in x-axis linked list
    private Container prevY; //previous link in y-axis linked list

    public Container(Point data) {
        this.data = data;
    }

    //Don't delete or change this function
    public Point getData() {
        return data;
    }

    public Container getNext(Boolean isX) {
        return isX ? nextX : nextY;
    }

    public void setNext(Boolean isX, Container next) {
        if (isX) {
            nextX = next;
        }
        else {
            nextY = next;
        }
    }

    public Container getPrev(Boolean isX) {
        return isX ? prevX : prevY;
    }

    public void setPrev(Boolean isX, Container prev) {
        if (isX) {
            prevX = prev;
        }
        else {
            prevY = prev;
        }
    }


    public String toString() {
        return data.toString();
    }
}
