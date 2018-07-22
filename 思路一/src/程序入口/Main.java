/**
 * 原创声明:csu_xiaotao@163.com
 */
package 程序入口;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.csvreader.CsvWriter;

import 工具.Util;
import 数据.DataProducer;
import 模型.Station;
import 算法.Kmeans;
import 评价.Judge;

/**
 * 新建类声明
 * 
 */
public class Main {

	public static void main(String[] args) throws Exception {
		long startTime = System.currentTimeMillis(); // 获取开始时间
		/*System.out.println("请选择数据输入方式:\n1.计算机模拟产生符合条件的数据\n"
				+ "2.从文件dataresouce.");*/
		// 初始化一个Kmean对象，将k置为10
		Kmeans k = new Kmeans(84);

		ArrayList<Double> D = new ArrayList<Double>();

		DataProducer dp = new DataProducer();
		// 设置原始数据集
		k.setDataSet(dp.locDataSet);

		k.execute();

		ArrayList<Station> curCenter = new ArrayList<Station>();// 当前中心链表
		ArrayList<ArrayList<Station>> curCluster = new ArrayList<ArrayList<Station>>();

		// 得到聚类结果
		ArrayList<ArrayList<Station>> cluster = k.getCluster();
		for (int i = 0; i < cluster.size(); i++) {
			//System.out.println("i=====" + i);
			if (cluster.get(i).size() <= 12) {
				curCluster.add(cluster.get(i));
				curCenter.add(k.getCenter().get(i));

			} else {
				// 这个表达式基于统计平均
				int num = (cluster.get(i).size()) / 12 + 1;

				//System.out.println("num====" + num);

				Kmeans k2 = new Kmeans(num);
				k2.setDataSet(cluster.get(i));
				k2.execute();
				for (int u = 0; u < num; u++) {
					curCluster.add(k2.getCluster().get(u));
					curCenter.add(k2.getCenter().get(u));
				}

			}
		}
	/*	System.out.println("curCluster.size():" + curCluster.size());
		System.out.println("curCenter.size()" + curCenter.size());*/
		// 查看结果
		/*for (int i = 0; i < curCluster.size(); i++) {
			System.out.println("Center:(" + curCenter.get(i).Longtitude + "," + curCenter.get(i).Lattitude + ")");
			for (int j = 0; j < curCluster.get(i).size(); j++) {
				System.out.println("print:" + "[" + (j) + "]={" + curCluster.get(i).get(j).Longtitude + ","
						+ curCluster.get(i).get(j).Lattitude + "}");
			}
			System.out.println("===================================");
		}*/
		// 输出每个簇中最大距离
		for (int i = 0; i < curCluster.size(); i++) {
			double max = Util.getDistance(curCenter.get(i), curCluster.get(i).get(0));
			;
			for (int j = 1; j < curCluster.get(i).size(); j++) {
				double curDistance = Util.getDistance(curCenter.get(i), curCluster.get(i).get(j));
				if (max <= curDistance)
					max = curDistance;
			}
			//System.out.println("第" + (i + 1) + "个簇最大距离为" + max);
		}

		// 聚类结果(子站)保存到文件
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < curCluster.size(); i++) {
			for (int j = 0; j < curCluster.get(i).size(); j++) {
				sb.append(curCluster.get(i).get(j).Longtitude + "," + curCluster.get(i).get(j).Lattitude + "\r\n");
			}
		}
		Util.writefile(sb.toString(), "zizhan.txt");

		// 聚类结果(宿主站)保存到文件
		sb.delete(0, sb.length());
		for (int i = 0; i < curCenter.size(); i++) {
			sb.append(curCenter.get(i).Longtitude + "," + curCenter.get(i).Lattitude + "\r\n");
		}

		Util.writefile(sb.toString(), "suzhu.txt");

		// 建立连接,
		int size = 1000 + curCenter.size();
		int[][] connect = new int[size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				connect[i][j] = 0;
			}
		}

		
		sb.delete(0, sb.length());
		for (int i = 0; i < curCenter.size(); i++) {
			for (int j = i + 1; j < curCenter.size(); j++) {
				double distance = Util.getDistance(curCenter.get(i), curCenter.get(j));
				if (distance <= 50) {
					connect[i][j] = 2;
					connect[j][i] = 2;
				} else {
					sb.append("From " + i + " To " + j + "distance===" + distance + "\r\n");
				}
			}
		}
		Util.writefile(sb.toString(), "test.txt");

		
		for (int i = 0; i < curCenter.size(); i++) {
			int clustersize = curCluster.get(i).size();

			int count = curCenter.size();
			for (int j = 0; j < i; j++)
				count += curCluster.get(j).size();//
		
			if (clustersize <= 8) {

				for (int s = 0; s < clustersize; s++) {
					D.add(Util.getDistance(curCenter.get(i), curCluster.get(i).get(s)));
					connect[i][count + s] = 1;
					connect[count + s][i] = 1;
				}
			} else {
				// keymap
				HashMap<Double, Integer> hm = new HashMap<Double, Integer>();
				double[] distances = new double[clustersize];
				for (int j = 0; j < clustersize; j++) {

					distances[j] = Util.getDistance(curCenter.get(i), curCluster.get(i).get(j));

					hm.put(distances[j], j);
				}
				Arrays.sort(distances);

				for (int s = 0; s < 8; s++) {
					D.add(Util.getDistance(curCenter.get(i), curCluster.get(i).get(hm.get(distances[s]))));
					connect[i][count + hm.get(distances[s])] = 1;
					connect[count + hm.get(distances[s])][i] = 1;
				}

				for (int q = 8; q < clustersize; q++) {
					double min = 100000000;
					int minindex = 0;
					for (int p = 0; p < 8; p++) {
						double distance = Util.getDistance(curCluster.get(i).get(q), curCluster.get(i).get(p));
						if (distance < min) {
							min = distance;
							minindex = p;
						}
					}
					
					for (int s = 8; s < clustersize; s++) {
						connect[count + minindex][count + s] = 1;
						connect[count + s][count + minindex] = 1;
						D.add(Util.getDistance(curCluster.get(i).get(minindex), curCluster.get(i).get(s)));
					}
				}
			}
		}

		/*
		 * // 输出矩阵 sb.delete(0, sb.length()); for (int i = 0; i < size; i++) { for (int
		 * j = 0; j < size; j++) { sb.append(connect[i][j] + " "); } sb.append("\r\n");
		 * } Util.writefile(sb.toString(), "result.txt");
		 */

		// 输出csv文件
		// 定义一个CSV路径
		String graphCsvFilePath = "Graph.csv";
		// 创建CSV写对象 例如:CsvWriter(文件路径，分隔符，编码格式);
		CsvWriter csvWriter = new CsvWriter(graphCsvFilePath, ',', Charset.forName("GBK"));
		// 写表头
		ArrayList<String> csvHeaders = new ArrayList<String>();
		csvHeaders.add(" ");
		for (int i = 0; i < size; i++) {
			if (i < curCenter.size()) {
				csvHeaders.add("宿主站" + (i + 1));
			} else
				csvHeaders.add("子站" + (i + 1));
		}
		String[] headers = csvHeaders.toArray(new String[csvHeaders.size()]);
		csvWriter.writeRecord(headers);
		// 写内容
		for (int i = 0; i < size; i++) {
			ArrayList<String> csvContent = new ArrayList<String>();
			if (i < curCenter.size()) {
				csvContent.add("宿主站" + (i + 1));

			} else {
				csvContent.add("子站" + (i + 1 - curCenter.size()));
			}
			for (int j = 0; j < size; j++) {
				csvContent.add(connect[i][j] + "");
			}
			String[] content = csvContent.toArray(new String[csvContent.size()]);
			csvWriter.writeRecord(content);
		}

		csvWriter.close();
		System.out.println("--------Graph.csv文件已经写入--------");

		// 
		String posiCsvFilePath = "Posi.csv";
		// 创建CSV写对象 例如:CsvWriter(文件路径，分隔符，编码格式);
		CsvWriter csvWriter1 = new CsvWriter(posiCsvFilePath, ',', Charset.forName("GBK"));
		// 写表头
		ArrayList<String> csvHeaders1 = new ArrayList<String>();
		
		csvHeaders1.add("站点名" );
	    csvHeaders1.add("站点类型" );
		String[] headers1 = csvHeaders1.toArray(new String[csvHeaders1.size()]);
	    csvWriter1.writeRecord(headers1);
	    for(int i = 0; i<size; i++) {
	    	ArrayList<String> content = new ArrayList<String>();
	    	if(i<curCenter.size()) {
	    		content.add("宿主站"+(i+1));
	    		content.add(1+"");
	    	}
	    	else {
	    		content.add("子站"+(i-curCenter.size()+1));
	    		content.add(0+"");
	    	}
	    	String [] writecontent = content.toArray(new String[content.size()]);
	    	csvWriter1.writeRecord(writecontent);
	    }
	    csvWriter1.close();
	    System.out.println("--------Posi.csv文件已经写入 --------");
	    
		System.out.println("总体成本:" + Judge.ChallengeOne(curCenter.size(), 1000));
		System.out.println("平均成本:" + Judge.ChallengeOne(curCenter.size(), 1000) / (curCenter.size() + 1000));
		System.out.println("回传路径总体损耗:" + Judge.ChallengeTwo(D));
		System.out.println("回传路径平均损耗" + Judge.ChallengeTwo(D) / D.size());

		long endTime = System.currentTimeMillis(); // 获取结束时间
		System.out.println("程序运行时间： " + (endTime - startTime) + "ms");
	}
}
