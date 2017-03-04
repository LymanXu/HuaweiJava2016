package com.routesearch.route;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;




public class BDPV {

	/*
	 * 基于迪杰斯特拉的抖动算法
	 */
	public static Map<Integer,Vertex> vertexMap = new HashMap<Integer,Vertex>();
	public static int INFINITY = 99999;
	static ArrayList<String> roadlibrary=new ArrayList<String>();
	static double[][] deldistance=new double[RouteTest.problem.num_ofcity][RouteTest.problem.num_ofcity];//=RouteTest.problem.Distance_of_vindex;
	static String fin=null;
	static double findis=Double.MAX_VALUE;
	 static class classpath
	{
		String completepath;
		ArrayList<String> midpoint;
		double distance;
		public classpath()
		{
            this.midpoint = new ArrayList<String>();
            this.completepath=null;
            this.distance = 99999;
        }
	}
	static void initdjs()
	{
		
		for(int a=0;a<RouteTest.problem.num_ofcity;a++)
			for(int b=0;b<RouteTest.problem.num_ofcity;b++)
			{
				deldistance[a][b]=RouteTest.problem.Distance_of_vindex[a][b];
			}
		int m=RouteTest.problem.num_ofcity;
		Vertex v[]=new Vertex[m];
		@SuppressWarnings("unchecked")
		List<Edge> e[]=new List[m];
		for(int i=0;i<m;i++)
		{
			 v[i]= new Vertex(i);
			 e[i]=v[i].adj;
		}
		for(int i=0;i<m;i++)
		{
			for(int j=0;j<m;j++)
			{
				double temp;
				Edge eij;
				if(i==j)
					continue;	
				else
				{
					temp=RouteTest.problem.Distance_of_vindex[i][j];
					eij=new Edge(v[j],temp);
					e[i].add(eij);
				}
			}
		}
		for(int i=0;i<m;i++)
		{
			vertexMap.put(i, v[i]);
		}
	}
	
	
	
	
	
	
	
	//-------------------------------------------------------------------------------------------

	static String getResult()
	{
		while(roadlibrary.size()<1000)
		//while(roadlibrary.size()<RouteTest.problem.num_ofcity)
		{
			classpath path = new classpath();
			if(roadlibrary.isEmpty())
			{
				needpointrand();
				path.midpoint.add(dijkstra(RouteTest.problem.StartIndex,RouteTest.problem.Needpoint.get(0)));
				path.completepath=dijkstra(RouteTest.problem.StartIndex,RouteTest.problem.Needpoint.get(0));
				
				for(int i=0;i<RouteTest.problem.Needpoint.size()-1;i++)
				{
					path.midpoint.add(dijkstra(RouteTest.problem.Needpoint.get(i),RouteTest.problem.Needpoint.get(i+1)));
					path.completepath=path.completepath+dijkstra(RouteTest.problem.Needpoint.get(i),RouteTest.problem.Needpoint.get(i+1)).substring(0);
				}
				path.midpoint.add(dijkstra(RouteTest.problem.Needpoint.get(RouteTest.problem.Needpoint.size()-1),RouteTest.problem.EndIndex));
				path.completepath=path.completepath+dijkstra(RouteTest.problem.Needpoint.get(RouteTest.problem.Needpoint.size()-1),RouteTest.problem.EndIndex).substring(0);
			}
			else
			{
				if(resetrand())
				{
					needpointrand();
					path.midpoint.add(dijkstra(RouteTest.problem.StartIndex,RouteTest.problem.Needpoint.get(0)));
					path.completepath=dijkstra(RouteTest.problem.StartIndex,RouteTest.problem.Needpoint.get(0));
					for(int i=0;i<RouteTest.problem.Needpoint.size()-1;i++)
					{
						path.midpoint.add(dijkstra(RouteTest.problem.Needpoint.get(i),RouteTest.problem.Needpoint.get(i+1)));
						path.completepath=path.completepath+dijkstra(RouteTest.problem.Needpoint.get(i),RouteTest.problem.Needpoint.get(i+1)).substring(0);
					}
					path.midpoint.add(dijkstra(RouteTest.problem.Needpoint.get(RouteTest.problem.Needpoint.size()-1),RouteTest.problem.EndIndex));
					path.completepath=path.completepath+dijkstra(RouteTest.problem.Needpoint.get(RouteTest.problem.Needpoint.size()-1),RouteTest.problem.EndIndex).substring(0);
				}	
				else
				{
					path.completepath=pathpickrand(roadlibrary);
					
				}
			}
			changeroad(path);
		}
		removeDuplicate(roadlibrary);
		String linelist = null;
		int m=0;
		String s=fin;
		System.out.println(s);
		String point[]=convertStrToArray(s);
//		for(int i=1;i<point.length-1;i++)
//		{
//			if(Integer.parseInt(point[i])>RouteTest.problem.num_ofcity)
//			{
//				roadlibrary.remove(m);
//				s=roadlibrary.get(0);
//				point=convertStrToArray(s);
//			}
//		}
		
		for(int i1=1;i1<point.length-1;i1++)
		{
			int a = 0,b = 0;
			a=Integer.parseInt(point[i1]);
			//System.out.println(a+" ");
			b=Integer.parseInt(point[i1+1]);
			if(a!=b)
				linelist=linelist+RouteTest.problem.line_index[a][b]+"|";
		}
		return linelist.substring(4, linelist.length()-1);
	}
	public static  String[] convertStrToArray(String str){   
        String[] strArray = null;   
        strArray = str.split(" "); //拆分字符为"," ,然后把结果交给数组strArray 
        return strArray;
    }   
	public static void removeDuplicate(ArrayList arlList)
	 {
	   HashSet h = new HashSet(arlList);
	   arlList.clear();
	   arlList.addAll(h);
	  }
	//---------------------------------------------------------------------------------------------
	
	static void changeroad(classpath path)
	{
		classpath newpath=path;
		int num=randroadnum(newpath);
		while(num>0)
		{
			randroaddel(newpath,deldistance);
			num--;
		}
		if(newpath.completepath.length()>=(RouteTest.problem.Needpoint.size()+2)*2)
		{
				roadlibrary.add(newpath.completepath);
			
		}
		
		if(newpath.distance<findis)
			{
				findis=newpath.distance;
				fin=newpath.completepath;
			}
	}
	
	
	static void randroaddel(classpath path,double[][] distance)
	{
		String del;
		int[] delpoint=new int[path.completepath.length()];
		int i=0;
		int rand=(int)(Math.random()*(path.midpoint.size()));
		del=path.midpoint.get(rand);
		path.midpoint.remove(rand);
		String regex = "\\d*";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(del);
		while (m.find()) 
		{
			if (!"".equals(m.group()))
			{
				try 
				{
					delpoint[i] = Integer.parseInt(m.group());
					i++;
				} 
				catch (NumberFormatException e) 
				{
					e.printStackTrace();
				}
			}
		}
		for(int a=1;a<i-1;a++)
			for(int b=0;b<distance[0].length;b++)
			{
				distance[delpoint[a]][b]=distance[b][delpoint[a]]=99999;
			}
		path.midpoint.add(rand,dijkstra(delpoint[0],delpoint[i-1]));
	}
	
	
	
	static int randroadnum(classpath path)
	{
		return (int)(Math.random()*(path.midpoint.size()+1));
	}
	
	
	
	
	static String pathpickrand(ArrayList<String> library)
	{
		return library.get((int)(Math.random()*(library.size())));
	}
	
	
	static void needpointrand()
	{
		Collections.shuffle(RouteTest.problem.Needpoint);
	}
	
	
	
	
	static boolean resetrand()
	{
		return Math.random()>0.5? true:false; 
	}
	
	
	public static boolean isConnection(int start, int end) 
	{
		for(int i=0;i<RouteTest.problem.queue[start].size()-1;i++)
		{
			if(RouteTest.problem.queue[start].get(i)==end)
				return true;
		}
		for(int i=0;i<RouteTest.problem.queue[start].size()-1;i++)
		{
			boolean temp;
			temp=isConnection(RouteTest.problem.queue[start].get(i),end);
			if(temp)
				return true;
		}
		return false; 
	
	}


   

    //边距
    static class Edge{
        public Vertex dest;
        public double cost;
        public Edge(Vertex d,double c){
            this.dest = d;
            this.cost = c;
        }
        
    }
    
    //静态类：Vertex
    static class Vertex implements Comparable<Vertex>{
        public int name;
        public List<Edge> adj;
        public double dist;
        public Vertex prev;
        public int scratch;
        public boolean visited;
        public Vertex(int nm){
            this.name = nm;
            adj = new ArrayList<Edge>();
            reset();
        }
        public void reset(){
            visited = false;
            dist=BDPV.INFINITY;
        }
        @Override
        public int compareTo(Vertex o) {
            double c = o.dist;
            
            return dist < c ? -1:dist > c ? 1:0;
        }
        
    }
    
    //dijkstra算法实现:找到从startName点出发，到其他所有点的最短路径:选取自己定义的终点
    public static String dijkstra(int startName,int endName){
    	//if(isConnection(startName,endName))
    	//{
    	initdjs();
    	String road = null;
        PriorityQueue<Vertex> pq = new PriorityQueue<Vertex>();//该队列以权值升序排列，因为Vertex实现Comparable接口
        Vertex start = vertexMap.get(startName);
        start.dist = 0;
        for(Vertex v:vertexMap.values())
            pq.add(v);
        int seenNum = 0;
        while(!pq.isEmpty()&&seenNum < vertexMap.size()){
            Vertex v = pq.remove();
            if(v.name==endName){   //恰好是自己要找的那个点
            //System.out.println(startName + "---->" + v.name + ":" + v.dist);
            road=getPreNames(v);
            break;
            
            }
            if(v.scratch != 0)
                continue;
            v.scratch = 1;
            seenNum++;
            
            for(Edge e:v.adj){ 
                Vertex w = e.dest;
                double v_to_w = e.cost;
                if(w.dist > v.dist + v_to_w){
                    w.dist = v.dist + v_to_w;
                    w.prev = v;
                    pq.remove(w);//出队
                    pq.add(w);//按优先级插在队头，先插入的在队头，依次往后
                    
                }
            }
        }
//        System.out.println("hello!");
//        while(pq.peek() != null ){
//            System.out.println(pq.poll());
//        }
        return road;
    	//}
    	//else return "NA";
    }
    
    /**
     * 得到最短路径所经历的路线
     * seven
     * @param v
     * @return
     */
    public static String getPreNames(Vertex v){
    int routeEndName = v.name; 
    StringBuilder sb = new StringBuilder();
        while(v.prev != null){
        
        sb.append(v.prev.name + ",");
        v = v.prev;
        }
        String reverseRoute = routeEndName + "," + sb.toString();
        String[] reverseArray = reverseRoute.split(",");
        StringBuilder route = new StringBuilder();
        route.append(" ");
        for(int i=0;i<reverseArray.length;i++){
        
        			route.append(reverseArray[reverseArray.length-1-i]);
        			route.append(" ");
  
        }
       // System.out.println(route);
    return route.substring(0, route.length()-1);
    }
    public static String getLineIndex()
	/*
	 * Fuction: 由路径得到边的索引
	 * input： n-所有城市的个数
	 */
	{
    	return "NA";
	}
}

//-----------------------------------------------------------之前
	/*
	public static String dijkstra(int start, int end) 
	{ 
	
	 // 迪杰斯塔拉算法,在映射后的矩阵上进行计算
	
		String path=null;
		if(isConnection(start,end))
		{
			
			double findis=0;
			double[][] W1=RouteTest.problem.Distance_of_vindex;
			int[] prev = new int[RouteTest.problem.Map_of_vindex.size()];
			boolean[] isLabel = new boolean[W1[0].length];// 是否标号 
			int[] indexs = new int[W1[0].length];// 所有标号的点的下标集合，以标号的先后顺序进行存储，实际上是一个以数组表示的栈 
			int i_count = -1;//栈的顶点 
			double[] distance = W1[start].clone();// v0到各点的最短距离的初始值 
			int index = start;// 从初始点开始
			path=Integer.toString(index);
			double presentShortest = 0;//当前临时最短距离 
			indexs[++i_count] = index;// 把已经标号的下标存入下标集中 
			isLabel[index] = true; 
			while (i_count<W1[0].length) 
			{ 
				int temp = 0;
	            // 第一步：标号v0,即w[0][0]找到距离v0最近的点 
	            double min = Integer.MAX_VALUE; 
	            for (int i = 0; i < distance.length; i++) 
	            { 
	                if (!isLabel[i] && distance[i] != -1 && i != index)
	                { 
	                	temp=i;
                        prev[temp] = start;
	                    // 如果到这个点有边,并且没有被标号 
	                    if (distance[i] < min) 
	                    { 
	                        min = distance[i]; 
	                        index = i;// 把下标改为当前下标
	                    } 
	                } 
	            } 
	            if (index == end) 
	            {//已经找到当前点了，就结束程序
	                break; 
	            } 
	            isLabel[index] = true;//对点进行标号 
	            indexs[++i_count] = index;// 把已经标号的下标存入下标集中
	            if (W1[indexs[i_count - 1]][index] == -1
	                    || presentShortest + W1[indexs[i_count - 1]][index] > distance[index])
	            { 
	                // 如果两个点没有直接相连，或者两个点的路径大于最短路径 
	                presentShortest = distance[index]; 
	                
	            } 
	            else 
	            { 
	                presentShortest += W1[indexs[i_count - 1]][index]; 
	                prev[++temp]=i_count - 1;
	            } 
	 
	            // 第二步：将distance中的距离加入vi 
	            for (int i = 0; i < distance.length; i++) 
	            { 
	                // 如果vi到那个点有边，则v0到后面点的距离加 
	                if (distance[i] == -1 && W1[index][i] != -1) 
	                {// 如果以前不可达，则现在可达了 
	                    distance[i] = presentShortest + W1[index][i]; 
	                   
	                } 
	                else if (W1[index][i] != -1
	                        && presentShortest + W1[index][i] < distance[i]) 
	                { 
	                    // 如果以前可达，但现在的路径比以前更短，则更换成更短的路径 
	                    distance[i] = presentShortest + W1[index][i]; 
	                    
	                } 
	 
	            } 
	            for(int i=0;i<prev.length;i++){
	    			RouteTest.problem.Prev[start][i] = prev[i];
	            }
	        } 
	    
	        //如果全部点都遍历完，则distance中存储的是开始点到各个点的最短路径
	    	findis=distance[end]-distance[start];
	    	System.out.println("result: "+findis);
	    	for(int i=0;i<RouteTest.problem.Map_of_vindex.size();i++){
	    		System.out.println(RouteTest.problem.Prev[start][i]); 
	    	}
	    	return path;
	    	}
	    else
			System.out.println("No result");
		return "NA";
	
	} 
	 */

//	public static String dijkstra(int start,int end)
//	{
//		double[][] W1=RouteTest.problem.Distance_of_vindex;
//		double dist[]=new double[100];  
//		int prve[]=new int[60];
//		int n=dist.length-1;
//		//s[]:存储已经找到最短路径的顶点，false为未求得
//		boolean[]s=new boolean[n+1];
//		for(int i=1;i<=n;i++)
//		{
//		   //初始化dist[]数组
//		   dist[i]=W1[start][i];
//		   s[i]=false; 
//		   /*
//		    * prve[]数组存储源点到顶点vi之间的最短路径上该顶点的前驱顶点,
//		    * 若从源点到顶点vi之间无法到达，则前驱顶点为-1
//		    */
//		   if(dist[i]<Integer.MAX_VALUE)   
//		    prve[i]=start;
//		   else 
//		    prve[i]=-1;
//		  }
//		   
//		  dist[start]=0;   //初始化v0源点属于s集
//		  s[start]=true;   //表示v0源点已经求得最短路径
//		  for(int i=1;i<=n;i++){
//		   double temp=Integer.MAX_VALUE; //temp暂存v0源点到vi顶点的最短路径
//		   int u=start;
//		   for(int j=1;j<=n;j++){
//		    if((!s[j])&&dist[j]<temp){  //顶点vi不属于s集当前顶点不属于s集(未求得最短路径)并且距离v0更近
//		     u=j;           //更新当前源点,当前vi作为下一个路径的源点
//		     temp=dist[j];       //更新当前最短路径
//		    }
//		   }
//		   s[u]=true;          //顶点vi进s集
//		   //更新当前最短路径以及路径长度
//		   for(int j=0;j<=n;j++){     
//		    if((!s[j])&&W1[u][j]<Integer.MAX_VALUE){   //当前顶点不属于s集(未求得最短路径)并且当前顶点有前驱顶点
//		     double newDist=dist[u]+W1[u][j];        //累加更新最短路径
//		     if(newDist<dist[j]){
//		      dist[j]=newDist;             //更新后的最短路径
//		      prve[j]=u;               //当前顶点加入前驱顶点集
//		     }
//		    }
//		   }
//		 }
//		 for(int i=0;i<dist.length;i++)
//		 {
//			  //当前顶点已求得最短路径并且当前顶点不等于源点
//			   if(d[i]<Integer.MAX_VALUE&&i!=m){
//			    System.out.print("v"+i+"<--");
//			    int next=p[i];    //设置当前顶点的前驱顶点
//			    while(next!=m){  //若前驱顶点不为一个，循环求得剩余前驱顶点
//			     System.out.print("v"+next+"<--");
//			     next=p[next];
//			    }
//			    System.out.println("v"+m+":"+d[i]);
//			   }
//		return null;
//	}
//}
//		 
	
//	static boolean[] visited = null;
//	static int[] path = null;
//	static int[] bestpath = null;
//	static double pbestlength=Integer.MAX_VALUE,plength = 0;
//	static int Max_Num = RouteTest.Max_ofnum;
//	static double[][] StandDistance;
//	static List<Integer> StandmapPoint;







//-------------------------------------------最后一次在下面




//public static String dijkstra(int start, int end) {
//	if(isConnection(start,end))
//	{
//	double[][] W1=RouteTest.problem.Distance_of_vindex;
//	System.out.println("起点:" + start + "终点:" + end);
//	boolean[] isLabel = new boolean[W1[0].length];// 是否标号
//	int[] indexs = new int[W1[0].length];// 所有标号的点的下标集合，以标号的先后顺序进行存储，实际上是一个以数组表示的栈
//	int i_count = -1;// 栈的顶点
//	double[] distance = W1[start].clone();// v0到各点的最短距离的初始值
//	int index = start;// 从初始点开始
//	double presentShortest = 0;// 当前临时最短距离
//
//
//	indexs[++i_count] = index;// 把已经标号的下标存入下标集中
//	isLabel[index] = true;
//
//
//	while (i_count < W1[0].length) {
//	// 第一步：得到与原点最近的某个点
//	double min = Integer.MAX_VALUE;
//	for (int i = 0; i < distance.length; i++) {
//	if (!isLabel[i] && distance[i] != -1 && i != index) {
//	// 如果到这个点有边,并且没有被标号
//	if (distance[i] < min) {
//	min = distance[i];
//	index = i;// 把下标改为当前下标
//	}
//	}
//	}
//	i_count = i_count + 1;
//	if(i_count == W1[0].length){
//	break;
//	}
//	isLabel[index] = true;// 对点进行标号
//	indexs[i_count] = index;// 把已经标号的下标存入下标集中
//
//
//	if (W1[indexs[i_count - 1]][index] == -1
//	|| presentShortest + W1[indexs[i_count - 1]][index] > distance[index]) {
//	// 如果两个点没有直接相连，或者两个点的路径大于最短路径
//	presentShortest = distance[index];
//	} else {
//	presentShortest += W1[indexs[i_count - 1]][index];
//	}
//
//	// 第二步：加入vi后，重新计算distance中的距离
//	for (int i = 0; i < distance.length; i++) {
//
//
//	// 如果vi到那个点有边，则v0到后面点的距离加
//	if (distance[i] == -1 && W1[index][i] != -1) {// 如果以前不可达，则现在可达了
//	distance[i] = presentShortest + W1[index][i];
//	} else if (W1[index][i] != -1 && presentShortest + W1[index][i] < distance[i]) {
//	// 如果以前可达，但现在的路径比以前更短，则更换成更短的路径
//	distance[i] = presentShortest + W1[index][i];
//	}
//
//
//	}
//
//
//	}
//	getRoute(W1,indexs,end);
//
//	return "最短距离是：" + (distance[end] - distance[start]);}
//	else
//		System.out.println("No result");
//	return "NA";
//	}
//public static String getRoute(double[][] WW, int[] indexs, int end) {
//	String[] routeArray = new String[indexs.length];
//	for (int i = 0; i < routeArray.length; i++) {
//	routeArray[i] = "";
//	}
//
//	//自己的路线
//	routeArray[indexs[0]] = indexs[0] + "";
//	for (int i = 1; i < indexs.length; i++) {
//	//看该点与前面所有点的连接线中的最短路径，然后得到该最短路径到底是连接了哪个点，进而此点的route就是找出那点的route+此点
//	double[] thePointDis = WW[indexs[i]];  
//	int prePoint = 0;
//
//	double tmp = 9999;
//	for(int j=0;j<thePointDis.length;j++){
//
//	boolean chooseFlag = false;
//	//边的距离最短，而且，所连的点在前面的点当中
//	for(int m=0;m<i;m++){
//	if(j == indexs[m]){
//	chooseFlag = true;
//	}
//	}
//	if(chooseFlag == false){
//	continue;
//	}
//	if(thePointDis[j] <tmp && thePointDis[j] >0){
//	prePoint = j;
//	tmp = thePointDis[j];
//	}
//	}
//	routeArray[indexs[i]] = routeArray[prePoint] + indexs[i];
//	}
//	for (int i = 0; i < 4; i++) {
//	System.out.println(routeArray[i]);
//	}
//	return "";
//	}
