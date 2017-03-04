package com.routesearch.route;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


public class RouteProblem {

	/* 寻找路径的基本类，包括问题用到的数据 */

	static class problem{

		List<Integer> Map_of_vindex;
		double[][] Distance_of_vindex;
		int[][] LineIndex;

		/* 运算均基于Map_of_vindex 上对应的索引  */

		double[][] Distance_afterdij;
		int[][] Prev;

		List<Integer> Map_of_vpindex;
		double[][] Distance_of_vpindex;
		int num_ofcity;

		int StartIndex,EndIndex;
		List<Integer> Needpoint;

		/* 用智能算法的输入 Tsp矩阵  */
		double[][] Distance_ofTsp;
		/* Tsp矩阵的索引对应关系 ,点集为 V'+ start + end */
		List<Integer> StandmapPoint;
		
		
	}

	static int MaxNum_ofcity = 600;
	public static int Max_ofnum = 9999;
	static problem instance;

	static void initRouteProblem()
	/* 
	 * 初始化寻找路径问题
	 */
	{
		instance = new problem();
		instance.Distance_of_vindex = new double[MaxNum_ofcity][MaxNum_ofcity];
		instance.Map_of_vindex = new ArrayList<Integer>();
		instance.LineIndex = new int[MaxNum_ofcity][MaxNum_ofcity];

		instance.Needpoint = new ArrayList<Integer>();


	}

	static void getParams(String graphContent, String condition)
	/* 
	 * 根据读入的文件，初始化点集的对应，距离矩阵，V'点集
	 */
	{
		/* 距离矩阵的初始化 */
		for(int i=0;i<RouteProblem.MaxNum_ofcity;i++){
			for(int j=0;j<RouteProblem.MaxNum_ofcity;j++){
				if(i!=j){
					instance.Distance_of_vindex[i][j] = Max_ofnum;
				}else{
					instance.Distance_of_vindex[i][j] = 0.001;
				}

				instance.LineIndex[i][j] = -1;  

			}
		}

		int LineNum=0;
		String[] Source;
		String[] target = new String[4];
		Source = graphContent.split("\n");
		/* 不连续的点的索引序列，映射为从0开始连续的顶点索引序列 */

		while(LineNum<Source.length){
			/* 记录一条边 */
			String sline = Source[LineNum];
			target = sline.split(",");
			LineNum += 1;

			/* 将每条边的数据存到数组 */
			int Pstart = Integer.parseInt(target[1]);
			int Pend = Integer.parseInt(target[2]);

			/* 加入点的映射关系,此时邻接矩阵和边的索引都是基于映射后的点的索引 */
			if(!instance.Map_of_vindex.contains(Pstart)){
				instance.Map_of_vindex.add(Pstart);
			}
			if(!instance.Map_of_vindex.contains(Pend)){
				instance.Map_of_vindex.add(Pend);
			}

			int lindex = Integer.parseInt(target[0]);
			int lvalue = Integer.parseInt(target[3]);
			/* 基于索引  */
			instance.LineIndex[instance.Map_of_vindex.indexOf(Pstart)][instance.Map_of_vindex.indexOf(Pend)] = lindex;

			// test
			//System.out.println("mapPoint.indexOf(Pstart)"+mapPoint.indexOf(Pstart));
			instance.Distance_of_vindex[instance.Map_of_vindex.indexOf(Pstart)][instance.Map_of_vindex.indexOf(Pend)] = lvalue;

			//System.out.println("mapPoint:"+mapPoint);
		} 

		/* 城市的个数 */
		instance.num_ofcity = instance.Map_of_vindex.size();
		/* 读取condition 参数 */
		Source = condition.split(",");

		instance.StartIndex = instance.Map_of_vindex.indexOf(Integer.parseInt(Source[0]));
		instance.EndIndex = instance.Map_of_vindex.indexOf(Integer.parseInt(Source[1]));

		/* 记录要过的点集V' */

		Source[2] = Source[2].substring(0, Source[2].length()-1);
		String[] tempString;
		tempString = Source[2].split("\\|");

		int temp=0;
		while(temp<tempString.length){
			instance.Needpoint.add(instance.Map_of_vindex.indexOf(Integer.parseInt(tempString[temp])));
			temp+=1;
		}

		//test
		//System.out.println("Map_of_vindex:"+instance.Map_of_vindex);
		//printDistance();
		//System.out.println("NeedPoint:"+instance.Needpoint);
	}

	static void getDistanceAfterDij()
	/*
	 * 通过迪杰斯塔拉算法更新V'中顶点的距离和前端点
	 */
	{

		instance.Distance_afterdij = new double[instance.num_ofcity][instance.num_ofcity];

		/* 初始化该迪杰斯塔拉矩阵 */
		for(int i=0;i<instance.num_ofcity;i++){
			for(int j=0;j<instance.num_ofcity;j++){
				instance.Distance_afterdij[i][j] = instance.Distance_of_vindex[i][j];
			}
		}

		/* 求V'中的点到其它点的最短路径，这里修改为求所有点之间的最短距离  */
		int onePoint;
		int PointNum = instance.Map_of_vindex.size();

		instance.Prev = new int[PointNum][PointNum];

		onePoint = instance.StartIndex;
		dijkstra(onePoint,PointNum);
		onePoint = instance.EndIndex;
		dijkstra(onePoint,PointNum);

		for(int i=0;i<instance.Needpoint.size();i++){
			onePoint = instance.Needpoint.get(i);
			dijkstra(onePoint,PointNum);
		}
		
		/* 这里修改为求所有点之间的最短距离  
		int onePoint;
		int PointNum = instance.Map_of_vindex.size();

		instance.Prev = new int[PointNum][PointNum];
		
		for(int i=0;i<PointNum;i++){
			//onePoint = instance.Needpoint.get(i);
			onePoint = i;
			dijkstra(onePoint,PointNum);
		}*/

		// test
		//printPrev();
	}



	public static void dijkstra(int v0,int pointNum)
	/*
	 * 迪杰斯塔拉算法,在映射后的矩阵上进行计算
	 */
	{
		int MaxInt = Max_ofnum;
		double[] dist = new double[pointNum];
		int[] prev = new int[pointNum];

		/* 记录点是否被经过 */
		boolean[] visited = new boolean[pointNum];

		int n = pointNum;

		for(int i=0;i<n;i++){
			dist[i] = instance.Distance_afterdij[v0][i];
			visited[i] = false;
			if(dist[i]==MaxInt){
				prev[i] = -1;
			}else{
				prev[i] = v0;
			}
		}

		dist[v0] = 0;
		visited[v0] = true;
		int u;
		double mindist;

		for(int i=1;i<n;i++){
			/* 每次在可达点集中加入一个点 */
			mindist = MaxInt;
			u = v0;
			/* 找出没使用点到该点的最近点  */
			for(int j=0;j<n;j++){
				if((!visited[j]) && dist[j]<mindist){
					u = j;
					mindist = dist[j];
				}
			}
			/* 将最近的点加入  */
			visited[u] = true;
			/* 更新距离  */
			for(int j=0;j<n;j++){
				if((!visited[j]) && instance.Distance_of_vindex[u][j]<MaxInt){
					if(dist[u] + instance.Distance_of_vindex[u][j] < dist[j]){
						dist[j] = dist[u] + instance.Distance_of_vindex[u][j];
						prev[j]=u;
					}
				}
			}
		}
		/* 将Distance 更新  */
		for(int i=0;i<n;i++){
			instance.Distance_afterdij[v0][i] = dist[i];
			instance.Prev[v0][i] = prev[i];
		}

	}

	static List<Integer> adjustpath_bydij(int city_start,int city_end,Set<Integer> keys)
	/*
	 * 寻找从城市 start 到 城市 end 避免一些点的最短路径
	 * 如何可以调整就返回路径，否则返回 null
	 */
	{
		List<Integer> route_temp = new ArrayList<Integer>();

		int MaxInt = Max_ofnum;
		int pointNum = RouteProblem.instance.num_ofcity;
		double[] dist = new double[pointNum];
		int[] prev = new int[pointNum];

		/* 记录点是否被经过 */
		boolean[] visited = new boolean[pointNum];

		int n = pointNum;

		for(int i=0;i<n;i++){
			dist[i] = instance.Distance_of_vindex[city_start][i];
			visited[i] = false;
			if(dist[i]==MaxInt){
				prev[i] = -1;
			}else{
				prev[i] = city_start;
			}
		}

		/* 将避免点 设置为访问过  */
		Iterator<Integer> iter = keys.iterator();
		while(iter.hasNext()){
			int tempcity = iter.next();
			/* 该城市不能再进行访问  */
			visited[tempcity] = true;
		}

		//dist[city_start] = 0;
		visited[city_start] = true;
		visited[city_end] = false;
		
		int u;
		double mindist;

		for(int i=1;i<n;i++){
			/* 每次在可达点集中加入一个点 */
			mindist = MaxInt;
			u = city_start;
			/* 找出没使用点到该点的最近点  */
			for(int j=0;j<n;j++){
				if((!visited[j]) && dist[j]<mindist){
					u = j;
					mindist = dist[j];
				}
			}
			/* 将最近的点加入  */
			visited[u] = true;
			/* 更新距离  */
			for(int j=0;j<n;j++){
				if((!visited[j]) && instance.Distance_of_vindex[u][j]<MaxInt){
					if(dist[u] + instance.Distance_of_vindex[u][j] < dist[j]){
						dist[j] = dist[u] + instance.Distance_of_vindex[u][j];
						prev[j]=u;
					}
				}
			}
		}

		/* 得到新的路线  */
		int end,start;
		List<Integer> indexPath = new ArrayList<Integer>();
		end = city_end;
		start = city_start;

		while(prev[end]!=-1 && prev[end]!=start){
			indexPath.add(end);
			end = prev[end];
		}
	
		if(prev[end]==start){
			/* 有解 ,返回值不包括，起点和 终点,方向 起点 ---》终点 */
			indexPath.add(end);
			for(int i=indexPath.size()-1;i>0;i--){
				route_temp.add(indexPath.get(i));
			}
		}else{
			route_temp = null;
		}

		return route_temp;
	}
	
	/* test */ 
	public static void printDistance(){
		/* test 1  */
		System.out.println("Distance:");
		for(int i=0;i<instance.Map_of_vindex.size();i++){
			for(int j=0;j<instance.Map_of_vindex.size();j++){
				System.out.print(instance.Distance_of_vindex[i][j]+"  ");
			}
			System.out.println();
		}
	}

	public static void printPrev(){
		// test 1
		System.out.println("Prev:");
		for(int i=0;i<instance.Map_of_vindex.size();i++){
			for(int j=0;j<instance.Map_of_vindex.size();j++){
				System.out.print(instance.Prev[i][j]+"  ");
			}
			System.out.println();
		}
	}

	static void get_Distance_ofTsp(int sindex,int eindex)
	/*
	 * 得到用智能算法的  Tsp矩阵，将起点和终点虚拟为一个节点
	 * 回溯法的输入矩阵，对Distance 变化，转化为  index=0对应开始顶点，index=n 对应终点
	 */
	{
		double[][] StandDistance;

		int pointNum = RouteProblem.instance.Map_of_vindex.size();
		//StandDistance = new double[pointNum][pointNum];
		instance.StandmapPoint = new ArrayList<Integer>();

		/* 添加Vr 点集的映射关系 */
		if(!instance.StandmapPoint.contains(sindex))
			instance.StandmapPoint.add(sindex);
		int tempvri;
		for(int i=0;i<instance.Needpoint.size();i++){
			tempvri = instance.Needpoint.get(i);
			if(!instance.StandmapPoint.contains(tempvri) && tempvri!=eindex && tempvri!=sindex){
				instance.StandmapPoint.add(tempvri);
			}
		}  	
		if(!instance.StandmapPoint.contains(eindex))
			instance.StandmapPoint.add(eindex);

		/* 初始化StandDistance 矩阵，点集V'+start + end */
		int num_ofStandPoint = instance.StandmapPoint.size();
		StandDistance = new double[num_ofStandPoint][num_ofStandPoint];
		/* test 
    	System.out.println("StandmapPoint:"+StandmapPoint);*/

		for(int i=0;i<num_ofStandPoint;i++){
			for(int j=0;j<num_ofStandPoint;j++){
				StandDistance[i][j] = RouteProblem.instance.Distance_afterdij[instance.StandmapPoint.get(i)][instance.StandmapPoint.get(j)];
			}
		}


		instance.Distance_ofTsp = new double[num_ofStandPoint-1][num_ofStandPoint-1];

		/* 为虚拟Tsp 矩阵赋值 */
		for(int i=0;i<num_ofStandPoint-1;i++){
			instance.Distance_ofTsp[0][i] = StandDistance[0][i];
		}
		for(int i=1;i<num_ofStandPoint-1;i++){
			instance.Distance_ofTsp[i][0] = StandDistance[i][num_ofStandPoint-1];
		}
		for(int i=1;i<num_ofStandPoint-1;i++){
			for(int j=1;j<num_ofStandPoint-1;j++){
				instance.Distance_ofTsp[i][j] = StandDistance[i][j];
			}
		}

		/* test 输入Distance_ofTsp  查看 */
		/*
		System.out.println("input智能算法的Tsp矩阵： ");
		for(int i=0;i<num_ofStandPoint-1;i++){
			for(int j=0;j<num_ofStandPoint-1;j++){
				System.out.print(instance.Distance_ofTsp[i][j]+" ");
			}
			System.out.println();
		}
		 */
	}
	
	public static String getLineIndex()
	/*
	 * Fuction: 由路径得到边的索引
	 * input： n-所有城市的个数
	 */
	{

		/* 最终的路线 */
		List<Integer> presult = Path.path_end;
		//List<Integer> presult = Path.truePath;
		String result="";
		int index;
		int start =0,end=0;
		if(presult!=null && presult.size()>1){

			for(int i=0;i<presult.size()-2;i++){
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
