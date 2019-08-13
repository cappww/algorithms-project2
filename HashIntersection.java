//Capp Wiedenhoefer
//
import edu.gwu.algtest.*;
import edu.gwu.util.*;
import edu.gwu.debug.*;
import edu.gwu.geometry.*;
import java.util.*;

public class HashIntersection implements LineSegmentIntersectionAlgorithm {

  int algID;
  PropertyExtractor prop;

  int numIntervals;

  public String getName ()
  {
    return "HashInsersection";
  }

  public void setPropertyExtractor (int algID, PropertyExtractor prop)
  {
    try {
      this.algID = algID;
      this.prop = prop;
      numIntervals = prop.getIntProperty ("alg" + algID + ".numIntervals");
    }
    catch (UtilException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  public static void main(String[] args) {
    HashIntersection h = new HashIntersection();
    Pointd p1 = new Pointd(20, 16);
    Pointd p2 = new Pointd(2, 6);
    Pointd p3 = new Pointd(6, 18);
    Pointd p4 = new Pointd(18, 8);
    Pointd p5 = new Pointd(4, 9);
    Pointd p6 = new Pointd(20, 15);
    Pointd p7 = new Pointd(0, 0);
    Pointd p8 = new Pointd(20, 20);
    LineSegment[] array = { new LineSegment(p1,p2),
                            new LineSegment(p3,p4),
                            new LineSegment(p5,p6),
                            new LineSegment(p7,p8) };
    h.numIntervals = 4;
    h.findIntersections(array);
  }

  public Pointd[] findIntersections (LineSegment[] segments){
    // 1. Find the value at which the table scales to.
    double scale  = findScale(segments);
    // 2. Build the hash table.
    CappCell[][] cellTable = buildHashTable(scale, numIntervals);
    // 3. Insert segments.
    insertSegments(cellTable, segments);
    // 4. Scan segments and check for intersections, storing intersection points.
    ArrayList<Pointd> array = getIntersections(cellTable);
    // 5. Remove all duplicates.
    for(int i=0; i<array.size()-1; i++){
      for(int j=i+1; j<array.size(); j++){
        if(array.get(i).equals(array.get(j))){
          array.remove(j);
          i=0;
        }
      }
    }
    // 6. Put intersection points found in an array and return it.
    Pointd[] interArray = new Pointd[array.size()];
    for(int i=0; i<array.size(); i++){
      interArray[i] = array.get(i);
    }
    return interArray;
  }

  public double findScale(LineSegment[] segments){
    LineSegment line;
    Pointd A;
    Pointd B;
    double max = Double.MIN_VALUE;
    //1. For all lines in the segments array,
    for(int i=0; i<segments.length; i++){
      line = segments[i];
      A = line.getPoint1();
      B = line.getPoint2();
      //1.1 Check if any x or y coordinate is greater than the max.
      //1.2 If so, set max to that value.
      if(A.getx() > max) max = A.getx();
      if(A.gety() > max) max = A.gety();
      if(B.getx() > max) max = B.getx();
      if(B.gety() > max) max = B.gety();
    }
    return max;
  }

  public CappCell[][] buildHashTable(double scale, int numIntervals){
    CappCell[][] cellTable = new CappCell[numIntervals][numIntervals];
    int count = 0;
    for(int i=0; i<numIntervals; i++){
      for(int j=0; j<numIntervals; j++){
        cellTable[i][j] = new CappCell(i, j, scale, numIntervals);
      }
    }
    return cellTable;
  }

  public void insertSegments(CappCell[][] cellTable, LineSegment[] segments) {
    for(LineSegment line : segments){
      //1. Find the cell containing point A.
      Pointd A = line.getPoint1();
      int i = 0;
      int j = 0;

      while(A.getx() > cellTable[i][j].getRightBorder()) {
        i++;
      }
      while(A.gety() > cellTable[i][j].getTopBorder()) {
        j++;
      }

      CappCell currentCell = cellTable[i][j];
      int nextBorder;
      int prevBorder = -1;
      //2. Continue while loop until break.
      while(true){
        currentCell.insert(line);
        //2.1 Find the border of the cell that intersects the line.
        nextBorder = findBorderIntersection(currentCell, line, prevBorder);

        //2.2 Move to the next cell depending on which border the line crosses.
        if(nextBorder == 0){
          j++;
          prevBorder = 2;
        } else if(nextBorder == 1){
          i++;
          prevBorder = 3;
        } else if(nextBorder == 2){
          j--;
          prevBorder = 0;
        } else if(nextBorder == 3){
          i--;
          prevBorder = 1;
        } else if(nextBorder == -1){
          i++;
          j++;
        } else if(nextBorder == -2){
          i--;
          j--;
        } else if(nextBorder == -3){
          i++;
          j--;
        } else if(nextBorder == -4){
          i--;
          j++;
        } else break;

        //2.3 Set the current cell to be the adjacent one that the line goes through.
        currentCell = cellTable[i][j];
      }
    }
  }

  public int findBorderIntersection(CappCell cell, LineSegment line, int prev){
    //1. Find which point is farthest to the left and farthest to the right.
    double leftmostX = line.getPoint1().getx();
    double rightmostX = line.getPoint2().getx();
    if(leftmostX > rightmostX){
      double temp = leftmostX;
      leftmostX = rightmostX;
      rightmostX = temp;
    }
    //2. Find which point is higher and which is lower.
    double lowestY = line.getPoint1().gety();
    double highestY = line.getPoint2().gety();
    if(lowestY > highestY){
      double temp = lowestY;
      lowestY = highestY;
      highestY = temp;
    }

    //2. If the line crosses the top border while being within the left and
    //    right borders, and being within the length of the line, return 0.
    double x = (cell.getTopBorder() - line.getIntercept()) / line.getSlope();
    if(x > cell.getLeftBorder()  &&
       x < cell.getRightBorder() &&
       x > leftmostX             &&
       x < rightmostX            &&
       prev != 0) return 0;

    x = (cell.getBottomBorder() - line.getIntercept()) / line.getSlope();

    //3. Same as step 2 but for the bottom border.
    if(x > cell.getLeftBorder()  &&
       x < cell.getRightBorder() &&
       x > leftmostX             &&
       x < rightmostX            &&
       prev != 2) return 2;

    //4. If line crosses the rightBorder while being within the top and bottom
    //    borders, and while being within the height of the line, return 1;
    double y = (line.getSlope() * cell.getRightBorder()) + line.getIntercept();
    if(y > cell.getBottomBorder() &&
       y < cell.getTopBorder()    &&
       y > lowestY                &&
       y < highestY               &&
       prev != 1) return 1;

    y = (line.getSlope() * cell.getLeftBorder()) + line.getIntercept();
    //5. Same as step 4 but for the left border.
    if(y > cell.getBottomBorder() &&
       y < cell.getTopBorder()    &&
       y > lowestY                &&
       y < highestY               &&
       prev != 3) return 3;

    //6. Edge case: if the slope is an integer move to a cell diagonally from
    //    the current cell.
    if(line.getSlope() % 1 == 0){
      if(line.getSlope() > 0){
        if(line.getPoint1().getx() == leftmostX &&
           cell.getRightBorder() < rightmostX) return -1;
        else if(line.getPoint1().getx() == rightmostX &&
                cell.getLeftBorder() > leftmostX)  return -2;

      } else {
        if(line.getPoint1().getx() == leftmostX &&
           cell.getRightBorder() > rightmostX) return -3;
        else if(line.getPoint1().getx() == rightmostX &&
                cell.getLeftBorder() < leftmostX) return -4;
      }
    }

    //7. If no conditions are met, all cells have been reached, return -5
    return -5;
  }

  public ArrayList<Pointd> getIntersections(CappCell[][] cellTable){
    LinkedList<LineSegment> list;
    Geometry g = new Geometry();
    ArrayList<Pointd> intersections = new ArrayList<Pointd>();
    Pointd inter = null;

    //1. For every cell in the table,
    for(int i=0; i<cellTable.length; i++){
      for(int j=0; j<cellTable[0].length; j++){
        //1.1 And for every line in the cell,
        list = cellTable[i][j].list;
        for(int k=0; k<list.size()-1; k++){
          for(int l=k+1; l<list.size(); l++){
            //1.1.1 Insert if the two lines intersect.
            inter = g.properIntersection(list.get(k), list.get(l));
            if(inter != null){
              intersections.add(inter);
            }
          }
        }
      }
    }
    return intersections;
  }

}
