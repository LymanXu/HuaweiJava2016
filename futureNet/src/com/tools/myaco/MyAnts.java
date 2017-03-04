package com.tools.myaco;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Vector;



public class MyAnts {

	static class ant_struct {
		int[] tour;       /* 记录路径 */
		boolean[] visited;  /* 记录是否被访问过 */
		double tour_length;   /* 路径长度  */
	}

	public static final int MAX_ANTS = 1024;
	static ant_struct ant[];
	static ant_struct best_so_far_ant;
	static ant_struct restart_best_ant;

	static double total[][];
	static int n_ants=32; /* number of ants */

	static void allocate_ants()
	/*
	 * 配置蚂蚁群的记忆，best_so_far,初始化蚁群系统
	 */
	{
		int i;

		ant = new ant_struct[n_ants];

		for (i = 0; i < n_ants; i++) {
			ant[i] = new ant_struct();
			ant[i].tour = new int[MyTsp.N + 1];
			ant[i].visited = new boolean[MyTsp.N];
		}
		/* 用于记录全局最优的蚂蚁   和当前迭代最优的蚂蚁 */
		best_so_far_ant = new ant_struct();
		best_so_far_ant.tour = new int[MyTsp.N + 1];
		best_so_far_ant.visited = new boolean[MyTsp.N];

		restart_best_ant = new ant_struct();
		restart_best_ant.tour = new int[MyTsp.N + 1];
		restart_best_ant.visited = new boolean[MyTsp.N];

	}

	static void ant_empty_memory(ant_struct a)
	/*
	 * FUNCTION: empty the ants's memory regarding visited cities
	 * */
	{
		int i;

		for (i = 0; i < MyTsp.N; i++) {
			a.visited[i] = false;
			a.tour[i] = 0;
		}
		a.tour[MyTsp.N] = 0;
		a.tour_length = 0.0;

	}

	static void place_ant(ant_struct[] ants,int step)
	/*
	 * 将蚂蚁随机放到一个城市
	 */
	{
		int rnd;
		Random random = new Random(System.currentTimeMillis());
		for(int i=0;i<ants.length;i++)
		{
			rnd = (int) random.nextInt(MyTsp.N); /* random number between 0 .. n-1 */

			ants[i].tour[step] = rnd;
			ants[i].visited[rnd] = true;
		}


	}


	static void choose_and_move_to_next(ant_struct[] ants)
	/*
	 * 蚂蚁选择寻找下一个城市,直到蚂蚁群遍历所有的城市
	 */
	{
		Random random = new Random(System.currentTimeMillis());

		for(int i=0;i<MyAnts.n_ants;i++){

			/* 逐个蚂蚁选择路径 */
			for(int j=1;j<MyTsp.N;j++){
				/* 逐个城市选择路径  */

				List<Integer> allow_city = new ArrayList<Integer>();
				List<Double> P = new ArrayList<Double>();

				/* 寻找没有访问过的城市 */
				for(int k=0;k<MyTsp.N;k++){
					if(!ants[i].visited[k]){  
						allow_city.add(k);
					}
				}

				/* 计算城市之间的转移概率 */
				int tabuEnd = ants[i].tour[j-1];
				double alpha = MyTsp.instance.alpha;
				double beta = MyTsp.instance.beta;
				double sumP = 0.0;
				for(int k=0;k<allow_city.size();k++){

					double tempTau = MyTsp.instance.Tau[tabuEnd][allow_city.get(k)];
					tempTau = Math.pow(tempTau,alpha);

					double tempEta = MyTsp.instance.Eta[tabuEnd][allow_city.get(k)];
					tempEta = Math.pow(tempEta,beta);

					double tempPi = tempTau*tempEta;
					P.add(tempPi);
					sumP = sumP + tempPi;
				}

				/* 轮盘赌法选择下一个城市  */
				double randP = random.nextDouble();
				randP = randP * sumP;
				int target_city = 0;
				double tempP = 0.0;
				for(int k=0;k<P.size();k++){
					tempP = tempP + P.get(k);
					if(tempP>=randP){
						target_city = allow_city.get(k);
						break;
					}
				}
				ants[i].tour[j] = target_city;
				ants[i].visited[target_city] = true;
			}
		}
	}

	static int find_best()
	/*
	 * FUNCTION: find the best ant of the current iteration
	 * INPUT: none
	 * OUTPUT: index of struct containing the iteration best ant
	 * (SIDE)EFFECTS: none
	 */
	{
		double min;
		int k, k_min;

		min = ant[0].tour_length;
		k_min = 0;
		for (k = 1; k < n_ants; k++) {
			if (ant[k].tour_length < min) {
				min = ant[k].tour_length;
				k_min = k;
			}
		}
		return k_min;
	}

}
