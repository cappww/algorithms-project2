import edu.gwu.algtest.*;
import edu.gwu.util.*;
import edu.gwu.debug.*;
import edu.gwu.geometry.*;
import java.util.*;

public class CappCell {

  //The x and y coordinates of the cell, located in the cell table.
  private int x;
  private int y;

  //The values of the vertical and horizontal lines bordering the cell
  private double leftBorder;
  private double rightBorder;
  private double bottomBorder;
  private double topBorder;
  public double[] borderArray;
  public LinkedList<LineSegment> list;

  public CappCell(int x, int y, double scale, int numIntervals) {
    this.x = x;
    this.y = y;
    leftBorder = (scale / numIntervals) * x;
    rightBorder = (scale / numIntervals) * (x + 1);
    bottomBorder = (scale / numIntervals) * y;
    topBorder = (scale / numIntervals) * (y + 1);
    this.list = new LinkedList<LineSegment>();
  }

  public void insert(LineSegment line) {
    //System.out.println("Cell(" + x + ", " + y + ") inserted line: " + line.toString());
    list.add(line);
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public double getLeftBorder(){
    return leftBorder;
  }

  public double getRightBorder(){
    return rightBorder;
  }

  public double getBottomBorder(){
    return bottomBorder;
  }

  public double getTopBorder(){
    return topBorder;
  }

}
