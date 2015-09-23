#include "opencv2/core.hpp"
#include "opencv2/highgui/highgui.hpp"
#include "opencv2/imgproc/imgproc.hpp"
#include "opencv2/core/utility.hpp"
#include "opencv2/core/core.hpp"
#include <iostream>
#include <string>

#include "com_vinci_dtp_ChamLib.h"

using namespace std;
using namespace cv;

jfloat chamferMatcher(Mat distImg, Mat tpl);
void getSlidingWindows(Mat& image,int winWidth,int winHeight,vector<Rect>& rects);
void cropBounding(Mat src, Mat& dst);
jfloat computeScore(Mat img, Mat tpl, Rect box);
static jfloat chamfer(jlong addDrawing, jlong addImage);


JNIEXPORT jfloat JNICALL Java_com_vinci_dtp_ChamLib_getScore
  (JNIEnv *env, jclass clazz, jlong addrDrawing, jlong addrImage){
	 return chamfer(addrDrawing,addrImage);
}

static float chamfer(jlong addDrawing, jlong addImage) {

	Mat& tpl = *(Mat*) addDrawing;
	Mat& img = *(Mat*) addImage;

	cvtColor(img,img,CV_BGR2GRAY);

	cvtColor(tpl,tpl,CV_BGR2GRAY);

	//get Bounding rect and crop excessive mat areas
	cropBounding(tpl,tpl);

	//create a binary image/edge with white background
	threshold(img,img,1,255,1);
	threshold(tpl,tpl,1,255,1);

	//perform distance transform. city block
	Mat distImg;
	distanceTransform(img,distImg,DIST_L1,DIST_MASK_3,CV_8UC1);

	vector<Rect> rects;
	getSlidingWindows(distImg,tpl.cols,tpl.rows,rects);

	Rect bestRect;
	jfloat bestScore = 1000;
	jfloat score;
	jint i;
	for(i = 0; i<rects.size(); i++){
		Mat roi = distImg(rects.at(i));
		score = chamferMatcher(roi,tpl);
		if(score < bestScore) {
			bestScore = score;
			bestRect = rects.at(i);
		}
	}

	//rectangle(img, bestRect, Scalar(0,0,0));
	jfloat myScore;


	imwrite("/storage/emulated/test.png", img);

	return myScore = computeScore(img, tpl, bestRect);

}



jfloat chamferMatcher(Mat distImg, Mat tpl) {

		//compute chamfer distance
		int distanceTotal = 0;
		int pixelOfInterest = 0;
		Scalar intensities;
		for (int i = 0;  i < tpl.rows; ++ i) {
			for (int j = 0; j < tpl.cols; ++ j) {
				if (tpl.at<uchar>(Point(j,i)) == 0) {
					pixelOfInterest++;
					intensities = distImg.at<uchar>(Point(j,i));
					distanceTotal += pow(intensities.val[0],2);
				}
			}
		}

		jfloat POIdevidedBy1,
		 POIxDistTotal,
		 sqrtOfPOIxDistTotal,
		 cdistance;

		 POIdevidedBy1 =  1.00/pixelOfInterest;
		 POIxDistTotal = POIdevidedBy1 * distanceTotal;
		 sqrtOfPOIxDistTotal = sqrt(POIxDistTotal);
		 cdistance = 0.33333 * sqrtOfPOIxDistTotal;

		return cdistance;
}

void getSlidingWindows(Mat& image,int winWidth,int winHeight,vector<Rect>& rects)
{
  int step = 3;
  for(int i=0;i<image.rows;i+=step)
  {
      if((i+winHeight)>image.rows){break;}
      for(int j=0;j< image.cols;j+=step)
      {
          if((j+winWidth)>image.cols){break;}
          Rect rect(j,i,winWidth,winHeight);
          rects.push_back(rect);
      }
  }
}

void cropBounding(Mat src, Mat& dst){

	Mat Points;
	findNonZero(src,Points);
	Rect boundingBox=boundingRect(Points);

	dst = src(boundingBox);

	//imshow("box", dst);

}

jfloat computeScore(Mat img, Mat tpl, Rect box){
	 jfloat score = 0.0;

	 jfloat TotalNumberOfPixels = img.rows * img.cols;
	 jfloat ZeroPixels = TotalNumberOfPixels - countNonZero(img);

	 jint i, j;
	for (i = 0;  i < tpl.rows; ++ i) {
		for (j = 0; j < tpl.cols; ++ j) {
			if (tpl.at<uchar>(Point(j,i)) == 0) {
				img.at<uchar>(Point(j+box.x,i+box.y)) = 100;
			}
		}
	}

	jfloat ZeroPixelsAfter = TotalNumberOfPixels - countNonZero(img);

	jfloat difference = ZeroPixels - ZeroPixelsAfter;
	jfloat d = difference / ZeroPixels;
	score = d * 100;


	return score;
}


