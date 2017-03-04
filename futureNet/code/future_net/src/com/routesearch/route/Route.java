package com.routesearch.route;


public class Route {

	public static String searchRoute(String graphContent, String condition)
	{

		/* 初始化该问题  */
		String result="";
		String[] Source;
		RouteTest.initRouteProblem();
		
		RouteTest.getParams(graphContent,condition);
		//System.out.println("NeedPoint: "+RouteTest.problem.Needpoint);
		//System.out.println("StartPoint: "+RouteTest.problem.StartIndex);
		//System.out.println("EndPoint: "+RouteTest.problem.EndIndex);
		//BDPV.initdjs();
		//result=BDPV.dijkstra(0,1);
		//System.out.println(result);
		//result=BDPV.dijkstra(3,2);
		//System.out.println(result);
		result=BDPV.getResult();
//		instance.StartIndex = instance.Map_of_vindex.indexOf(Integer.parseInt(Source[0]));
//		instance.EndIndex = instance.Map_of_vindex.indexOf(Integer.parseInt(Source[1]));
		//System.out.println("result: "+result);
		//System.out.println("result: "+BDPV.roadlibrary.get(BDPV.roadlibrary.size()-1));
		//System.out.println("result: "+result);
		return result;
	}
}
