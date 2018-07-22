/**
 * 原创声明:csu_xiaotao@163.com
 */
package 算法;

import java.util.ArrayList;
import java.util.Random;

import 工具.util;
import 模型.Location;

/**
 * 新建类声明
 * 
 * @author csu_xiaotao <a href =
 *         "https://blog.csdn.net/cyxlzzs/article/details/7416491">原文链接</a>
 *         <a href =
 *         "http://www.baidu.com/link?url=wDiWtargRHoiQ3mXlBAw-i4GZGSOaF4InKeNai9Je5F89mjRuD-dYdstIZdKmtLU-w4UVMzjdtU1Ffd22hqYpVm1zqU7bAOO_4-LpRNe2uK&wd=&eqid=df5c8d6f0001cdb4000000065aedd3f8">原理及实现</a>
 *         <a href
 *         ="http://www.baidu.com/link?url=jtVq35LEQ4KWlQ4cPQ7oi3ozoT3CYpNFlJVa-qGZ_5hYLbH0XyNYMw6BSkSB93P_c3mfUIN0RjrmTSZ-qFPrdQIh0G4t04tDZOMdMSe3pJq&wd=&eqid=df5c8d6f0001cdb4000000065aedd3f8">图像分割</a>
 *         下午12:19:06 2018年5月4日
 */
public class Kmeans {
	private int k;// 分成多少簇
	private int m;// 迭代次数
	private int dataSetLength;// 数据集元素个数，即数据集的长度
	private ArrayList<Location> dataSet;// 数据集链表
	private ArrayList<Location> center;// 中心链表
	private ArrayList<ArrayList<Location>> cluster; // 簇
	private ArrayList<Float> jc;// 误差平方和，k越接近dataSetLength，误差越小
	private Random random;

	/**
	 * 设置需分组的原始数据集
	 * 
	 * @param dataSet
	 */
	public void setDataSet(ArrayList<Location> dataSet) {
		this.dataSet = dataSet;
	}

	/**
	 * 获取结果分组
	 * 
	 * @return 结果集
	 */

	public ArrayList<ArrayList<Location>> getCluster() {
		return cluster;
	}

	/**
	 * 构造函数，传入需要分成的簇数量
	 * 
	 * @param k
	 *            簇数量,若k<=0时，设置为1，若k大于数据源的长度时，置为数据源的长度
	 */
	public Kmeans(int k) {
		if (k <= 0) {
			k = 1;
		}
		this.k = k;
	}

	/**
	 * 初始化
	 */
	private void init() {
		m = 0;
		random = new Random();
		if (dataSet == null || dataSet.size() == 0) {
			initDataSet();
		}
		dataSetLength = dataSet.size();
		if (k > dataSetLength) {
			k = dataSetLength;
		}
		center = initCenters();
		cluster = initCluster();
		jc = new ArrayList<Float>();
	}
	/**
	 * get center
	 */
	public ArrayList<Location> getCenter() {
		return this.center;
	}
	/**
	 * 如果调用者未初始化数据集，则采用内部测试数据集
	 */
	private void initDataSet() {
		dataSet = new ArrayList<Location>();

		Location[] dataSetArray = new Location[] { new Location(113.90652663861538, 28.15692405281289),
				new Location(113.90652663861538, 28.15692405281289) };

		for (int i = 0; i < dataSetArray.length; i++) {
			dataSet.add(dataSetArray[i]);
		}
	}

	/**
	 * 初始化中心数据链表，分成多少簇就有多少个中心点
	 * 
	 * @return 中心点集
	 */
	private ArrayList<Location> initCenters() {
		ArrayList<Location> center = new ArrayList<Location>();
		int[] randoms = new int[k];
		boolean flag;
		int temp = random.nextInt(dataSetLength);
		randoms[0] = temp;
		for (int i = 1; i < k; i++) {
			flag = true;
			while (flag) {
				temp = random.nextInt(dataSetLength);
				int j = 0;
				// 不清楚for循环导致j无法加1
				// for(j=0;j<i;++j)
				// {
				// if(temp==randoms[j]);
				// {
				// break;
				// }
				// }
				while (j < i) {
					if (temp == randoms[j]) {
						break;
					}
					j++;
				}
				if (j == i) {
					flag = false;
				}
			}
			randoms[i] = temp;
		}

		// 测试随机数生成情况
		// for(int i=0;i<k;i++)
		// {
		// System.out.println("test1:randoms["+i+"]="+randoms[i]);
		// }

		// System.out.println();
		for (int i = 0; i < k; i++) {
			center.add(dataSet.get(randoms[i]));// 生成初始化中心链表
		}
		return center;
	}

	/**
	 * 初始化簇集合
	 * 
	 * @return 一个分为k簇的空数据的簇集合
	 */
	private ArrayList<ArrayList<Location>> initCluster() {
		ArrayList<ArrayList<Location>> cluster = new ArrayList<ArrayList<Location>>();
		for (int i = 0; i < k; i++) {
			cluster.add(new ArrayList<Location>());
		}

		return cluster;
	}

	/**
	 * 计算两个点之间的距离
	 * 
	 * @param element
	 *            点1
	 * @param center
	 *            点2
	 * @return 距离
	 */
	/*
	 * private float distance(float[] element, float[] center) { float distance =
	 * 0.0f; float x = element[0] - center[0]; float y = element[1] - center[1];
	 * float z = x * x + y * y; distance = (float) Math.sqrt(z);
	 * 
	 * return distance; }
	 */
	private double distance(Location start, Location end) {
		return util.getDistance(start, end);
	}

	/**
	 * 获取距离集合中最小距离的位置
	 * 
	 * @param distance
	 *            距离数组
	 * @return 最小距离在距离数组中的位置
	 */
	private int minDistance(double[] distance,ArrayList<Integer> ignore) {
		double minDistance = 10000000000000d;//distance[0];
		int minLocation = -1;//0;
		
		for (int i = 0; i < distance.length; i++) {
			//如果最小距离的簇已满12
			if(ignore.contains(i))
				continue;
			
			if (distance[i] < minDistance) {
				minDistance = distance[i];
				minLocation = i;
			} else if (distance[i] == minDistance) // 如果相等，随机返回一个位置
			{
				if (random.nextInt(10) < 5) {
					minLocation = i;
				}
			}
		}

		return minLocation;
	}

	/**
	 * 核心，将当前元素放到最小距离中心相关的簇中
	 */
	private void clusterSet() {
		double[] distance = new double[k];
		for (int i = 0; i < dataSetLength; i++) {
			for (int j = 0; j < k; j++) {
				distance[j] = distance(dataSet.get(i), center.get(j));
				// System.out.println("test2:"+"dataSet["+i+"],center["+j+"],distance="+distance[j]);

			}
			ArrayList<Integer> ignore = new ArrayList<Integer>();
			int minLocation = minDistance(distance,ignore);
			// 控制最多为12个为一簇(不包括自身)
			while (true) {
				if (cluster.get(minLocation).size() < 12) {
					cluster.get(minLocation).add(dataSet.get(i));// 核心，将当前元素放到最小距离中心相关的簇中
					break;
				}
				
				else {
					ignore.add(minLocation);
					minLocation = minDistance(distance, ignore);
					System.out.println("minLoc"+minLocation);
				}
				System.out.println("i"+i);
			}
			// System.out.println("test3:"+"dataSet["+i+"],minLocation="+minLocation);
			// System.out.println();

			//cluster.get(minLocation).add(dataSet.get(i));// 核心，将当前元素放到最小距离中心相关的簇中

		}
	}

	/**
	 * 求两点误差平方的方法
	 * 
	 * @param element
	 *            点1
	 * @param center
	 *            点2
	 * @return 误差平方
	 */
	private double errorSquare(Location element, Location center) {
		double x = element.Longtitude - center.Longtitude;
		double y = element.Lattitude - center.Lattitude;

		double errSquare = x * x + y * y;

		return errSquare;
	}

	/**
	 * 计算误差平方和准则函数方法
	 */
	private void countRule() {
		float jcF = 0;
		for (int i = 0; i < cluster.size(); i++) {
			for (int j = 0; j < cluster.get(i).size(); j++) {
				jcF += errorSquare(cluster.get(i).get(j), center.get(i));
			}
		}
		jc.add(jcF);
	}

	/**
	 * 设置新的簇中心方法
	 */
	private void setNewCenter() {
		for (int i = 0; i < k; i++) {
			int n = cluster.get(i).size();
			if (n != 0) {
				Location newCenter = new Location(0, 0);
				for (int j = 0; j < n; j++) {
					newCenter.Longtitude += cluster.get(i).get(j).Longtitude;
					newCenter.Lattitude += cluster.get(i).get(j).Lattitude;
				}
				// 设置一个平均值
				newCenter.Longtitude = newCenter.Longtitude / n;
				newCenter.Lattitude = newCenter.Lattitude / n;
				center.set(i, newCenter);
			}
		}
	}

	/**
	 * 打印数据，测试用
	 * 
	 * @param dataArray
	 *            数据集
	 * @param dataArrayName
	 *            数据集名称
	 */
	public void printDataArray(ArrayList<Location> dataArray, String dataArrayName) {
		for (int i = 0; i < dataArray.size(); i++) {
			System.out.println("print:" + dataArrayName + "[" + i + "]={" + dataArray.get(i).Longtitude + ","
					+ dataArray.get(i).Lattitude + "}");
		}
		System.out.println("===================================");
	}

	/**
	 * Kmeans算法核心过程方法
	 */
	private void kmeans() {
		init();
		// printDataArray(dataSet,"initDataSet");
		// printDataArray(center,"initCenter");

		// 循环分组，直到误差不变为止
		while (true) {
			clusterSet();
			// for(int i=0;i<cluster.size();i++)
			// {
			// printDataArray(cluster.get(i),"cluster["+i+"]");
			// }

			countRule();
			// System.out.println("count:"+"jc["+m+"]="+jc.get(m));

			// System.out.println();
			// 误差不变了，分组完成
			
			if (m != 0) {
				if (jc.get(m) - jc.get(m - 1) < 0.0000000000001) {
					break;
				}
			}

			setNewCenter();
			// printDataArray(center,"newCenter");
			m++;
			cluster.clear();
			cluster = initCluster();
		}

		// System.out.println("note:the times of repeat:m="+m);//输出迭代次数
	}

	/**
	 * 执行算法
	 */
	public void execute() {
		long startTime = System.currentTimeMillis();
		System.out.println("kmeans begins");
		kmeans();
		long endTime = System.currentTimeMillis();
		System.out.println("kmeans running time=" + (endTime - startTime) + "ms");
		System.out.println("kmeans ends");
		System.out.println();
	}
}