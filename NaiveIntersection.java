//java -jar ../algtest.jar a2.props
import edu.gwu.algtest.*;
import java.lang.*;
import java.util.*;
import edu.gwu.util.*;
import edu.gwu.geometry.*;

public class NaiveIntersection implements LineSegmentIntersectionAlgorithm{

  public static void main(String[] args) {
  /*  Pointd p1 = new Pointd(1, 1);
    Pointd p2 = new Pointd(2, 2);
    Pointd p3 = new Pointd(2, 1);
    Pointd p4 = new Pointd(1, 2);
    LineSegment[] array = { new LineSegment(p1,p2), new LineSegment(p3,p4) };
    Pointd[] arr = new NaiveIntersection().findIntersections(array);
    System.out.println(Arrays.toString(arr));*/
  }

  public Pointd[] findIntersections(LineSegment[] segments){
    //1. For i = 0 to segments.length
    //  1.1 For j = i+1 to segments.length
    //    1.1.1 If intersection != null, push(intersection)
    Geometry g = new Geometry();
    Pointd intersection;
    Stack<Pointd> stack = new Stack<Pointd>();

    for(int i=0; i<segments.length-1; i++){
      for(int j=i+1; j<segments.length; j++){
        intersection = g.properIntersection(segments[i], segments[j]);
        if(intersection != null) stack.push(intersection);
      }
    }

    if(stack.size() == 0) return null;

    Pointd[] array = new Pointd[stack.size()];
    for(int i=0; i<stack.size(); i++){
      array[i] = stack.pop();
    }

    return array;

    //Part 2 Pseudocode:
    //1. Make a hashtable which depends on the number of elements
    //3. Compare only the segments that share the same hashes

    //MakeHashtable
    //1. Take the max Y and X coordinates and divide both by 4, making 16
    //    different hashes.
    //2. Go through each LineSegment and add their locations to their respecitve
          //hash values.

    //ComputeCellCoordinates(LineSegment seg)
    //1. Make stack for all points' hashes
    //2. Insert hash for first point
    //3. Insert hash for last point
    //4. Insert hashes for all squares in between

  }

  public String getName(){
    return "Capp's NaiveIntersection";
  }

  public void setPropertyExtractor(int algID, PropertyExtractor prop){

  }

}
