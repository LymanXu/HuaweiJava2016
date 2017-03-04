package com.routesearch.route;

import java.util.ArrayList;
import java.util.List;

public class HuiSu {

	/*
	 * 回溯法：求最短哈密顿通路，给定一个图，求哈密顿通路，起点为0，终点为最后n
	 */
	static boolean[] visited = null;
	static int[] path = null;
	static int[] bestpath = null;
	static double pbestlength=Integer.MAX_VALUE,plength = 0;
	static int Max_Num = RouteProblem.Max_ofnum;
	static double[][] StandDistance;
	static List<Integer> StandmapPoint;

	static String getResultOfSimple()
	/*
	 * 对于V' 节点<7的话用这种回溯法求解
	 */
	{
		/* 2 得到标准的距离矩阵 StandDistance  */
		getStandDistance(RouteProblem.instance.StartIndex,RouteProblem.instance.EndIndex);

		/* 3 回溯法求得最优的路径，和距离   */
		int pointNum = RouteProblem.instance.Map_of_vindex.size();
		trackBack(pointNum);

		String result;
		result = getLineIndex(pointNum);
		//System.out.println("result: "+result);
		return result;
	}


	public static void  getStandDistance(int sindex,int eindex)
	/*
	 * 回溯法的输入矩阵，对Distance 变化，转化为  index=0对应开始顶点，index=n 对应终点
	 */
	{
		int pointNum = RouteProblem.instance.Map_of_vindex.size();
		StandDistance = new double[pointNum][pointNum];
		StandmapPoint = new ArrayList<Integer>();

		/* 添加Vr 点集的映射关系 */
		if(!StandmapPoint.contains(sindex))
			StandmapPoint.add(sindex);
		int tempvri;
		for(int i=0;i<pointNum;i++){
			tempvri = i;
			if(!StandmapPoint.contains(tempvri) && tempvri!=eindex && tempvri!=sindex){
				StandmapPoint.add(tempvri);
			}
		}  	
		if(!StandmapPoint.contains(eindex))
			StandmapPoint.add(eindex);

		/* test 
    	System.out.println("StandmapPoint:"+StandmapPoint);
    	*/

		for(int i=0;i<pointNum;i++){
			for(int j=0;j<pointNum;j++){
				StandDistance[i][j] = RouteProblem.instance.Distance_of_vindex[StandmapPoint.get(i)][StandmapPoint.get(j)];
			}
		}


		//printStandDistance(); 
	}

	/* test  */
	public static void printStandDistance(){
		// test 1
		System.out.println("StandDistance:");
		for(int i=0;i<RouteProblem.instance.Map_of_vindex.size();i++){
			for(int j=0;j<RouteProblem.instance.Map_of_vindex.size();j++){
				System.out.print(StandDistance[i][j]+"  ");
			}
			System.out.println();
		}
	}


	public static void trackBack(int Pn)
	/*
	 * 回溯法，通过标准距离矩阵，求  0--？--？--?--n的路径，使路径包含V'点集，长度最短
	 */
	{

		int n = Pn - 1; /* n 是解空间的层数  */
		visited = new boolean[n];
		path = new int[n];
		bestpath = new int[n+1];

		/* 初始化数组  */
		for(int i=0;i<n;i++){
			path[i] = 0;
			visited[i] = false;
		}

		/* 指定第一个位置为 0  */
		path[0] = 0;
		visited[0] = true;

		int k = 1;   /* 求解到达第 k 层状态的选择 */
		while(k>=1){
			path[k] = path[k] + 1;

			/* 减枝  */
			while(path[k]<=n-1){  /* != n-1 时 ，因为n-1为结束点，输出结果哪里处理 */
				if(check(k)){
					plength = plength + StandDistance[path[k-1]][path[k]];
					visited[path[k]] = true;
					break;  /* 如果 k 位置可以填写，path[k] */
				}
				else {
					path[k] = path[k] + 1;
				}
			}   		

			/* 记录结果  */
			if(writeAnswer(k,n) && path[k]<=n-1 && k<=n-1){
				/* 更新最优路径 */
				//System.out.println("更新结果：");
				double temp_distance = 0;
				for(int tdi=0;tdi<k;tdi++){
					temp_distance += StandDistance[path[tdi]][path[tdi+1]];
				}
				//int tdi = k;
				temp_distance += StandDistance[path[k]][n];
				
				if((plength+StandDistance[path[k]][n]) <pbestlength){
				//if((temp_distance) <pbestlength){
					/* 将bestpath 清空 */
					for(int i=0;i<n+1;i++){
						bestpath[i] = -1;
					}
					for(int i=0;i<=k;i++){
						bestpath[i] = path[i];
					}
					/*
					 * 这里添加 return  求得一个解就返回
					 */
					pbestlength = temp_distance;
				}
			}

			/* 回溯 */
			if(path[k]>n-1 || k>n-1){ 
				visited[path[k-1]] = false;
				path[k] = 0;
				k = k-1;

			}else{
				/* 得到结果就回溯，没得到就更近一步  */
				if(writeAnswer(k,n)){  
					/* 如果找到是结果也要回溯  */
					plength = plength - StandDistance[path[k-1]][path[k]];
					visited[path[k]] = false;
				}else{
					k = k + 1;
				}
			}				    	
		} 
	}


	public static boolean check(int k)
	/*
	 * 如果 k 位置可以填写，path[k]
	 */
	{
		int prev = path[k-1];
		int next = path[k];

		if(visited[next]){
			return false;
		}
		if(StandDistance[prev][next]>=Max_Num){
			return false;
		}
		return true;
	}

	static List<Integer> pathList;

	public static boolean writeAnswer(int k,int sn)
	/*
	 * 判断是否需要更新结果
	 */
	{
		/* 现在填写到第 k 层  */
		int n = sn+1;
		/* n 是点集中的点数  */
		int needPassNum = RouteProblem.instance.Needpoint.size() + 2;
		if(k<needPassNum-2){
			return false;
		}
		/* k 的下一个点是不是 end */
		if(StandDistance[path[k]][n-1]>=Max_Num){
			return false;
		}
		pathList = new ArrayList<Integer>();

		for(int i=0;i<=k;i++){
			pathList.add(StandmapPoint.get(path[i]));
		}
		if(!pathList.containsAll(RouteProblem.instance.Needpoint)){

			return false;
		}   	
		return true;
	}


	public static String getLineIndex(int n)
	/*
	 * Fuction: 由路径得到边的索引
	 * input： n-所有城市的个数
	 */
	{

		int i=0;
		int tempS,temp;
		List<Integer> presult = new ArrayList<Integer>();
		String result="";
		while(bestpath[i]>-1){
			tempS = StandmapPoint.get(bestpath[i]);
			presult.add(tempS);
			i = i+1;
		}
		/* 加入终点 */
		tempS = StandmapPoint.get(n-1);
		presult.add(tempS);

		int index;
		int start =0,end=0;
		if(presult.size()>1){

			for(i=0;i<presult.size()-2;i++){
				start = presult.get(i);
				end = presult.get(i+1);
				index = RouteProblem.instance.LineIndex[start][end];
				result = result + Integer.toString(index)+"|";
			}
			start = presult.get(presult.size()-2);
			end = presult.get(presult.size()-1);
			index = RouteProblem.instance.LineIndex[start][end];
			result = result + Integer.toString(index);
			return result;
		}else{
			return "NA";
		}
	}

}
