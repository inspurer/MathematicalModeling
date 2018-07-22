/**
 * 原创声明:csu_xiaotao@163.com
 */
package 算法;

import java.util.ArrayList;

import 工具.util;
import 数据.DataProducer;
import 文件.FileUtil;
import 模型.Location;

/**
 * 新建类声明
 * 
 * @author csu_xiaotao <a href =
 *         "https://blog.csdn.net/cyxlzzs/article/details/7416491">算法详解</a>
 *         下午12:21:06 2018年5月4日
 */
public class KmeansTest {

	public static void main(String[] args) {
		long startTime = System.currentTimeMillis(); // 获取开始时间

		// 初始化一个Kmean对象，将k置为10
		Kmeans k = new Kmeans(84);

		/*
		 * dataSet.add(new float[] { 1, 2 }); dataSet.add(new float[] { 3, 3 });
		 * dataSet.add(new float[] { 3, 4 }); dataSet.add(new float[] { 5, 6 });
		 * dataSet.add(new float[] { 8, 9 }); dataSet.add(new float[] { 4, 5 });
		 * dataSet.add(new float[] { 6, 4 }); dataSet.add(new float[] { 3, 9 });
		 * dataSet.add(new float[] { 5, 9 }); dataSet.add(new float[] { 4, 2 });
		 * dataSet.add(new float[] { 1, 9 }); dataSet.add(new float[] { 7, 8 });
		 */
		DataProducer dp = new DataProducer();
		// 设置原始数据集
		k.setDataSet(dp.locDataSet);
		// long startTime=System.currentTimeMillis(); //获取开始时间
		// 执行算法
		k.execute();
		// long endTime=System.currentTimeMillis(); //获取结束时间
		// System.out.println("程序运行时间： "+(endTime-startTime)+"ms");
		// 得到聚类结果
		ArrayList<ArrayList<Location>> cluster = k.getCluster();
		ArrayList<Location> center = k.getCenter();
		// 查看结果
		for (int i = 0; i < cluster.size(); i++) {
			System.out.println("Center:(" + center.get(i).Longtitude + "," + center.get(i).Lattitude + ")");
			k.printDataArray(cluster.get(i), "cluster[" + i + "]");
		}
		// 输出每个簇最大距离
		for (int i = 0; i < cluster.size(); i++) {
			double max = util.getDistance(center.get(i), cluster.get(i).get(0));
			;
			for (int j = 1; j < cluster.get(i).size(); j++) {
				double curDistance = util.getDistance(center.get(i), cluster.get(i).get(j));
				if (max <= curDistance)
					max = curDistance;
			}
			System.out.println("第" + (i + 1) + "个簇最大距离为" + max);
		}
		// 聚类结果保存到文件
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < cluster.size(); i++) {
			/*
			 * sb.append("第"+(i+1)+"个簇中心:\r\n"+k.getCenter().get(i).Longtitude+","
			 * +k.getCenter().get(i).Lattitude+")\r\n"); sb.append("簇内点:\r\n");
			 */
			for (int j = 0; j < cluster.get(i).size(); j++) {
				sb.append(cluster.get(i).get(j).Longtitude + "," + cluster.get(i).get(j).Lattitude + "\r\n");
			}
		}
		try {
			FileUtil.writefile(sb.toString(), "data.txt");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long endTime = System.currentTimeMillis(); // 获取结束时间
		System.out.println("程序运行时间： " + (endTime - startTime) + "ms");
	}

}
