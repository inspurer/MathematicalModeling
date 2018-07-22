/**
 * 原创声明:csu_xiaotao@163.com
 */
package 评价;

import java.util.ArrayList;

/**
 * 新建类声明
 * @author csu_xiaotao
 *<a href = "https://github.com/inspurer">月小水长</a>
 * 下午1:55:09
 * 2018年5月20日
 */
public class Judge {
	//挑战目标一
	public static double ChallengeOne(int hostNum, int slaveNum) {
		double price;
		int sateNum = (int) Math.ceil(hostNum/8.0);
		price = hostNum*10.0+slaveNum*5.0+sateNum*50.0;
		return price;
	}
	//挑战目标二
	public static double ChallengeTwo(ArrayList<Double> D) {
		double PL = 0.0;
		for(int i = 0; i<D.size(); i++) {
			if(D.get(i)>0) {//防止重点出现D.get(i)==0,而导致log无穷小
			//System.out.println("D.get(i)*900==="+D.get(i)*900+" ,log==="+20*Math.log10(D.get(i)*900));
			PL += 20*Math.log10(D.get(i)*900) + 32.5;
			}
		}
		return PL;
	}
}
