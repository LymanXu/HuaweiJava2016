package com.routesearch.route;

public class Test {

	public static void main(String[] args){
		/* 测试中间点数 <10  的用例 */
		String graphContent="0,0,1,1\n1,0,2,2\n2,0,3,1\n3,2,1,3\n4,3,1,1\n5,2,3,1\n6,3,2,1";
        String condition="0,1,2|3";
        
        /* 初始化该问题  
        RouteProblem.initRouteProblem();
        RouteProblem.getParams(graphContent,condition);
        
        HuiSu.getResultOfSimple();
        */
        
        /* 对于点数多的用例用智能算法 */
        RouteProblem.initRouteProblem();
        RouteProblem.getParams(graphContent,condition);
        RouteProblem.getDistanceAfterDij();
        
        /*  接下来要用智能算法，不过之前要转化为Tsp 的输入矩阵 */
        
        
	}
}
