package detection;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.img.util.ImgUtils;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class ObjectDetector {
	
   public static void main( String[] args ){

	   System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
	   
	   Mat img = Imgcodecs.imread(args[0]);
	   ImgUtils.imShow(img);
	   

   }
}
