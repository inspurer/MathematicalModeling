/**
 * 原创声明:csu_xiaotao@163.com
 */
package 工具;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import 模型.Station;

/**
 * 新建类声明
 * 
 * @author csu_xiaotao <a href = "https://github.com/inspurer">月小水长</a>
 *         下午3:09:39 2018年5月16日
 */
public class Util {
	private static double EARTH_RADIUS = 6378.137;

	private static double rad(double d) {
		return d * Math.PI / 180.0;
	}

	// longitude 经度 ;“纬度”Latitude
	//求距离工具函数,传入两个Station类参数，返回距离(Km)
	public static double getDistance(Station start,Station end) {
		double radLng1 = rad(start.Longtitude);
		double radLng2 = rad(end.Longtitude);
		double deltaRadLng = radLng1 - radLng2;
		double radLat1 = rad(start.Lattitude);
		double radLat2 = rad(end.Lattitude);
		double deltaRadLat = radLat1 - radLat2;
		double s = (2 * Math.asin(Math.sqrt(Math.pow(Math.sin(deltaRadLat / 2), 2)
				+ Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(deltaRadLng / 2), 2))));
		s = s * EARTH_RADIUS;
		s = Math.round(s * 10000d) / 10000d;
		return s;
	}
	
	//写txt文件工具类函数
	public static void writefile(String content, String filename) throws Exception {
		FileOutputStream fos = new FileOutputStream(filename);
		OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
		BufferedWriter bw = new BufferedWriter(osw);
		bw.write(content);
		bw.close();
		osw.close();
		fos.close();
	}
}
