/**
 * 原创声明:csu_xiaotao@163.com
 */
package 算法;

import java.util.ArrayList;
import java.util.Random;

import 工具.Util;
import 模型.Station;

/**
 * 新建类声明
 * 
 * @author csu_xiaotao 
 */
public class Kmeans {
	private int k;// 分成多少簇
	private int m;// 迭代次数
	private int dataSetLength;// 数据集元素个数，即数据集的长度
	private ArrayList<Station> dataSet;// 数据集链表
	private ArrayList<Station> center;// 中心链表
	private ArrayList<ArrayList<Station>> cluster; // 簇
	private ArrayList<Float> jc;// 误差平方和，k越接近dataSetLength，误差越小
	private Random random;

	/**
	 * 设置需分组的原始数据集
	 * 
	 * @param dataSet
	 */
	public void setDataSet(ArrayList<Station> dataSet) {
		this.dataSet = dataSet;
	}

	/**
	 * 获取结果分组
	 * 
	 * @return 结果集
	 */

	public ArrayList<ArrayList<Station>> getCluster() {
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
	public ArrayList<Station> getCenter() {
		return this.center;
	}
	/**
	 * 如果调用者未初始化数据集，则采用内部测试数据集
	 */
	private void initDataSet() {
		dataSet = new ArrayList<Station>();

		Station[] dataSetArray = new Station[] { new Station(113.90652663861538, 28.15692405281289),
				new Station(113.90652663861538, 28.15692405281289) };

		for (int i = 0; i < dataSetArray.length; i++) {
			dataSet.add(dataSetArray[i]);
		}
	}

	/**
	 * 初始化中心数据链表，分成多少簇就有多少个中心点
	 * 
	 * @return 中心点集
	 */
	//生成的中心到后面肯定不属于簇了
	private ArrayList<Station> initCenters() {
		ArrayList<Station> center = new ArrayList<Station>();
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
	private ArrayList<ArrayList<Station>> initCluster() {
		ArrayList<ArrayList<Station>> cluster = new ArrayList<ArrayList<Station>>();
		for (int i = 0; i < k; i++) {
			cluster.add(new ArrayList<Station>());
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
	private double distance(Station start, Station end) {
		return Util.getDistance(start, end);
	}

	/**
	 * 获取距离集合中最小距离的位置
	 * 
	 * @param distance
	 *            距离数组
	 * @return 最小距离在距离数组中的位置
	 */
	private int minDistance(double[] distance) {
		double minDistance = 10000000000000d;//distance[0];
		int minLocation = -1;//0;
		
		for (int i = 0; i < distance.length; i++) {
			//如果最小距离的簇已满12
			/*if(ignore.contains(i))
				continue;
			*/
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
			//ArrayList<Integer> ignore = new ArrayList<Integer>();
			int minLocation = minDistance(distance);//,ignore);
			// 控制最多为12个为一簇(不包括自身)
			/*while (true) {
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
			}*/
			// System.out.println("test3:"+"dataSet["+i+"],minLocation="+minLocation);
			// System.out.println();

			cluster.get(minLocation).add(dataSet.get(i));// 核心，将当前元素放到最小距离中心相关的簇中

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
	private double errorSquare(Station element, Station center) {
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
				Station newCenter = new Station(0, 0);
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
	public void printDataArray(ArrayList<Station> dataArray, String dataArrayName) {
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
				if (jc.get(m) - jc.get(m - 1) ==0) {
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
		/*long startTime = System.currentTimeMillis();
		System.out.println("kmeans begins");*/
		kmeans();
	/*	long endTime = System.currentTimeMillis();
		System.out.println("kmeans running time=" + (endTime - startTime) + "ms");
		System.out.println("kmeans ends");*/
	
	}
}