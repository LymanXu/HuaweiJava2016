package com.routesearch.route;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class RouteTest {

	/* 寻找路径的基本类，包括问题用到的数据 */

	static class roadproblem{

		List<Integer> Map_of_vindex;
		double[][] Distance_of_vindex;
		int line_index[][];
		//int[][] LineIndex;
		int num_ofcity;
		int StartIndex,EndIndex;
		List<Integer> Needpoint;
		LinkedList<Integer>[] queue;
		int[][] Prev;
		public int[][] prev;
	}
	
	static int MaxNum_ofcity = 600;
	static roadproblem problem;


	@SuppressWarnings("unchecked")
	static void initRouteProblem()
	/* 
	 * 初始化寻找路径问题
	 */
	{
		problem = new roadproblem();
		problem.line_index=new int[MaxNum_ofcity][MaxNum_ofcity];
		problem.Distance_of_vindex = new double[MaxNum_ofcity][MaxNum_ofcity];
		problem.queue=new LinkedList[MaxNum_ofcity];
		for(int i=0;i<MaxNum_ofcity;i++)
		{problem.queue[i]=new LinkedList<Integer>();}
		//problem.queue=new LinkedList[MaxNum_ofcity];
		problem.Map_of_vindex = new ArrayList<Integer>();
		//instance.LineIndex = new int[MaxNum_ofcity][MaxNum_ofcity];
		problem.Needpoint = new ArrayList<Integer>();


	}
	@SuppressWarnings("unused")
	static void getParams(String graphContent, String condition)
	/* 
	 * 根据读入的文件，初始化点集的对应，距离矩阵，V'点集
	 */
	{
		/* 距离矩阵的初始化 */
		for(int i=0;i<RouteTest.MaxNum_ofcity;i++){
			for(int j=0;j<RouteTest.MaxNum_ofcity;j++){
				if(i!=j){
					problem.Distance_of_vindex[i][j] = 99999;
				}else{
					problem.Distance_of_vindex[i][j] = 0;
				}

				//instance.LineIndex[i][j] = -1;  

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
			int lindex = Integer.parseInt(target[0]);
			int lvalue = Integer.parseInt(target[3]);
			problem.line_index[Pstart][Pend]=lindex;
			if(problem.Distance_of_vindex[Pstart][Pend]>lvalue)
			{
				problem.Distance_of_vindex[Pstart][Pend] = lvalue;
			}
			/* 加入点的映射关系,此时邻接矩阵和边的索引都是基于映射后的点的索引 */
			if(!problem.Map_of_vindex.contains(Pstart)){
				problem.Map_of_vindex.add(Pstart);
			}
			if(!problem.Map_of_vindex.contains(Pend)){
				problem.Map_of_vindex.add(Pend);
			}

			
			/* 基于索引  */
			//instance.LineIndex[instance.Map_of_vindex.indexOf(Pstart)][instance.Map_of_vindex.indexOf(Pend)] = lindex;
			problem.prev = new int[problem.Map_of_vindex.size()][problem.Map_of_vindex.size()];
			// test
			//System.out.println("mapPoint.indexOf(Pstart)"+mapPoint.indexOf(Pstart));
			//problem.Distance_of_vindex[problem.Map_of_vindex.indexOf(Pstart)][problem.Map_of_vindex.indexOf(Pend)] = lvalue;

			//System.out.println("mapPoint:"+mapPoint);
		} 

		/* 城市的个数 */
		problem.num_ofcity = problem.Map_of_vindex.size();
		//System.out.println("problem.num_ofcity:"+problem.num_ofcity);
		/* 读取condition 参数 */
		Source = condition.split(",");

		problem.StartIndex = problem.Map_of_vindex.indexOf(Integer.parseInt(Source[0]));
		problem.EndIndex = problem.Map_of_vindex.indexOf(Integer.parseInt(Source[1]));

		/* 记录要过的点集V' */

		Source[2] = Source[2].substring(0, Source[2].length()-1);
		String[] tempString;
		tempString = Source[2].split("\\|");

		int temp=0;
		while(temp<tempString.length){
			problem.Needpoint.add(problem.Map_of_vindex.indexOf(Integer.parseInt(tempString[temp])));
			temp+=1;
		}
		initconnectqueue();

		//test
		//System.out.println("Map_of_vindex:"+problem.Map_of_vindex);
		//printDistance();
		//System.out.println("NeedPoint:"+problem.Needpoint);
	}
	public static void printDistance(){
		/* test 1  */
		System.out.println("Distance:");
		for(int i=0;i<problem.Map_of_vindex.size();i++){
			for(int j=0;j<problem.Map_of_vindex.size();j++){
				System.out.print(problem.Distance_of_vindex[i][j]+"  ");
			}
			System.out.println();
		}
	}
	public static void initconnectqueue(){
		for(int i=0;i<problem.Map_of_vindex.size();i++)
		{
			
			for(int j=0;j<problem.Map_of_vindex.size();j++)
			{
				if(problem.Distance_of_vindex[i][j]>0&&problem.Distance_of_vindex[i][j]<99999)
					problem.queue[i].add(j);
			}
		}
	}
}
