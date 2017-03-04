package com.tools.myaco;



public class MyTsp {

	static class point {
		double x;
		double y;
	}
	static class problem {

		int optimum; /* 最优路径长度， otherwise a bound */
		point[] nodeptr; /* 点集的坐标 */
		double[][] Distance; /* 距离矩阵 */

		double alpha;
		double beta;  /* 启发函数因子 */
		double rho; /* 信息素挥发因子 */
		int Q; /* 常系数 */
		double[][] Eta;  /* 启发函数  1./D */
		double[][] Tau;  /* 信息素矩阵 */

		int iter;  /* 初始迭代次数   */
		int iter_max;  /* 最大迭代次数 */
		int[][] Route_best;  /* 各代最优路径 */
		double[] Length_best;  /* 各代最优路径长度 */
	}

	static int N; /* number of cities in the instance to be solved */

	static problem instance;

	static void initTspProblem(int cityNum,double[][] StanDistance)
	/* StanDistance 是城市之间的距离矩阵 */
	{
		N = cityNum;
		instance = new problem();
		instance.Distance = StanDistance;
		instance.alpha = 1;
		instance.beta = 5;
		instance.rho = 0.1;
		instance.Q = 1;
		instance.Eta = new double[N][N];
		instance.Tau = new double[N][N];

		for(int i=0;i<N;i++){
			for(int j=0;j<N;j++){
				instance.Eta[i][j] = 1.0/instance.Distance[i][j];
			}
		}
		for(int i=0;i<N;i++){
			for(int j=0;j<N;j++){
				instance.Tau[i][j] = 1;
			}
		}
		instance.iter = 0;
		instance.iter_max = 200;
		instance.Route_best = new int[instance.iter_max][N+1];
		instance.Length_best = new double[instance.iter_max];
	}

	static double compute_tour_length(int[] t)
	/*
	 * 计算旅行路径的长度
	 */
	{
		int i;
		double tour_length = 0;

		for (i = 0; i < N; i++) {
			tour_length += instance.Distance[t[i]][t[i + 1]];
		}
		return tour_length;
	}

	static boolean tsp_check_tour(int[] t)
	/*  
	 * 检查给定的一个路径是否是符合要求的巡回路径 
	 */
	{
		boolean error = false;

		int i;
		int[] used = new int[N];
		int size = N;

		if (t == null) {
			System.err.println("error: permutation is not initialized!");
			System.exit(1);
		}

		for (i = 0; i < size; i++) {
			if (used[t[i]] != 0) {
				System.err.println("error: solution vector has two times the value " + t[i] + "(last position: " + i
						+ ")");
				error = true;
			} else
				used[t[i]] = 1; // was true
		}

		if (!error)
			for (i = 0; i < size; i++) {
				if (used[i] == 0) {
					System.out.println("error: vector position " + i + " not occupied");
					error = true;
				}
			}

		if (!error)
			if (t[0] != t[size]) {
				System.err.println("error: permutation is not a closed tour.");
				error = true;
			}

		if (!error)    /* 所有的对巡回路径的检查要求已符合，返回 true */
			return true;

		// error: (ansi c mark)

		System.err.println("error: solution_vector:");
		for (i = 0; i < size; i++)
			System.err.println(t[i]);
		System.out.println();

		return false;
	}



	static void local_acs_pheromone_update(MyAnts.ant_struct a)
	/*
	 * 一只蚂蚁遍历过城市后，更新信息素矩阵
	 */
	{

		double[][] Delta_Tau = new double[N][N];
		for(int i=0;i<N;i++){
			Delta_Tau[a.tour[i]][a.tour[i+1]] = Delta_Tau[a.tour[i]][a.tour[i+1]] + 
					instance.Q/a.tour_length;
		}
		/* 用这只ant 更新信息素矩阵*/
		for(int i=0;i<N;i++){
			for(int j=0;j<N;j++){
				instance.Tau[i][j] = (1-instance.rho)*instance.Tau[i][j] +Delta_Tau[i][j];
			}
		}

	}

}
