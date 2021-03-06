/**
 * 原创声明:csu_xiaotao@163.com
 */
package 数据;

import java.util.ArrayList;
import java.util.Random;

import 模型.Location;

/**
 * 新建类声明
 * @author csu_xiaotao
 *<a href = "https://github.com/inspurer">月小水长</a>
 * 下午4:25:07
 * 2018年5月16日
 */
public class DataProducer {
	public ArrayList<Location> locDataSet;
	public DataProducer() {
		Random random = new Random();
		locDataSet = new ArrayList<Location>();
		for(int i=0; i<1000; i++) {
			double x = random.nextDouble()+112.98;//经度112.98~113.98
			double y = random.nextDouble()*0.3+28.12;//纬度28.12~28.42
			Location item = new Location(x, y);
			System.out.println("经度:"+item.Longtitude+"  纬度:"+item.Lattitude);
			locDataSet.add(item);
		}
	}
}
