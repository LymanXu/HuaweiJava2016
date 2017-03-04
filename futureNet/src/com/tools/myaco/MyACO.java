package com.tools.myaco;

import com.routesearch.route.RouteProblem;

public class MyACO {

	//蚁群算法总的框架

	public static int[] startACO(int cityNum,double[][] stanDistance){

		/* 初始化 TSP 问题 ，input：城市个数，距离矩阵  */
		MyTsp.initTspProblem(cityNum, stanDistance);

		/* 初始化蚁群  */
		MyAnts.allocate_ants();

		/* 进行迭代求解  */
		for(MyTsp.instance.iter=0;MyTsp.instance.iter<MyTsp.instance.iter_max;MyTsp.instance.iter++){
			construct_solutions(MyTsp.instance.iter);
		}

		/* 返回结果，路线和路径长度*/
		
		/*System.out.println("智能算法路径——index_stand：");
		for(int i=0;i<=MyTsp.N;i++){
			int index_ofstand = MyTsp.instance.Route_best[MyTsp.instance.iter_max-1][i];
			System.out.print(index_ofstand+" ");
		}
		*/
		//System.out.println("路径距离： "+MyTsp.instance.Length_best[MyTsp.instance.iter_max-1]);
		
		 //test
		//System.out.println("智能算法得到的解的城市之间的距离：");
		for(int i=0;i<MyTsp.N;i++){
			int index_ofstand_start = MyTsp.instance.Route_best[MyTsp.instance.iter_max-1][i];
			int index_ofstand_end = MyTsp.instance.Route_best[MyTsp.instance.iter_max-1][i+1];
			double distance_oftwo = stanDistance[index_ofstand_start][index_ofstand_end];
			//System.out.print(stanDistance[index_ofstand_start][index_ofstand_end]+" ");
			
			if(distance_oftwo==RouteProblem.Max_ofnum){
				/* 存在两个城市不可达    */
				//System.out.println("智能算法无解（存在城市间不可达）");
				MyTsp.instance.Route_best[MyTsp.instance.iter_max-1] = null;
				break;
			}
		}
		
		
		return MyTsp.instance.Route_best[MyTsp.instance.iter_max-1];
		/* 输出所有迭代的最优 
		for(int j=0;j<MyAnts.n_ants;j++){
			for(int i=0;i<=MyTsp.N;i++){
				System.out.print(MyAnts.ant[j].tour[i]+" ");
			}
			System.out.println("路径距离： "+MyAnts.ant[j].tour_length);
		}
		 */
	}

	static void construct_solutions(int iter)
	/*   
	 *  管理解决方案施工阶段，所有蚂蚁的殖民地已经构建了一个解决方案
	 */
	{
		int k; /* counter variable */
		int step; /* counter of the number of construction steps */

		/* 初始化所有的蚂蚁 */
		for (k = 0; k < MyAnts.n_ants; k++) {
			MyAnts.ant_empty_memory(MyAnts.ant[k]);
		}

		/*  随机将蚂蚁放到一个城市 */
		step = 0;
		MyAnts.place_ant(MyAnts.ant, step);

		/* 使所有蚂蚁遍历所有城市 */
		MyAnts.choose_and_move_to_next(MyAnts.ant);

		/* 记录本次迭代的最优蚂蚁路径  */
		step = MyTsp.N;
		for(k=0;k<MyAnts.n_ants;k++){
			MyAnts.ant[k].tour[MyTsp.N] = MyAnts.ant[k].tour[0];
			/* 计算所有蚂蚁路径长度 */
			MyAnts.ant[k].tour_length = MyTsp.compute_tour_length(MyAnts.ant[k].tour);
		}

		int ant_index =MyAnts.find_best();
		if(iter==0){
			for(k=0;k<=MyTsp.N;k++){
				MyTsp.instance.Route_best[iter][k] = MyAnts.ant[ant_index].tour[k];
			}
			MyTsp.instance.Length_best[iter] = MyAnts.ant[ant_index].tour_length;
		}else{
			if(MyTsp.instance.Length_best[iter-1]>MyAnts.ant[ant_index].tour_length){
				for(k=0;k<=MyTsp.N;k++){
					MyTsp.instance.Route_best[iter][k] = MyAnts.ant[ant_index].tour[k];
				}
				MyTsp.instance.Length_best[iter] = MyAnts.ant[ant_index].tour_length;
			}else{
				for(k=0;k<=MyTsp.N;k++){
					MyTsp.instance.Route_best[iter][k] = MyTsp.instance.Route_best[iter-1][k];
				}
				MyTsp.instance.Length_best[iter] = MyTsp.instance.Length_best[iter-1];
			}

		}

		/* 更新信息素*/

		for(k=0;k<MyAnts.ant.length;k++){
			MyTsp.local_acs_pheromone_update(MyAnts.ant[k]);
		}


	}

}
