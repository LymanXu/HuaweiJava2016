package com.routesearch.route;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Author: Wucheng
 * Date: 2017/3/4 16:42
 * Abstract:
 */
public class Path {
    static int[] truepath_vstan; /* 将环拆开的路径，元素为StandPoint 上的索引  */
    /* 真实路径 */
    static List<Integer> truePath;
    /* 调整路径中重复点，避免环 */
    static Map<Integer,Integer>  appenum_ofcity;

    /* 最终的路径（调整过环后的） */
    static List<Integer> path_end;

    static void getendRoute_ofV(int[] route_xuTsp){

        get_Vproute_fromTspRoute(route_xuTsp);
        getRealPath();

		/* 调整环  */
        getRealTour_avoidhuan();

    }
    static void get_Vproute_fromTspRoute(int[] route_xuTsp)
	/*
	 * input：智能算法得到的虚拟Tsp路径，
	 * out: 将Tsp环拆开，把（开始，结束）的虚拟点还原
	 */
    {
		/* 新的路线的长度 ,*/
        int num_point = route_xuTsp.length;
        truepath_vstan = new int[num_point];

        int index =0;
        for(int i=0;i<num_point-1;i++){
            if(route_xuTsp[i]==0){
                index = i;
            }
        }

        int step = 0;
        for(int i=index;i<num_point-1;i++){
            truepath_vstan[step] = route_xuTsp[i];
            step += 1;
        }
        for(int i=0;i<index;i++){
            truepath_vstan[step] = route_xuTsp[i];
            step += 1;
        }
        truepath_vstan[step] = num_point - 1;

		/* test  */
		/*
		System.out.println("将环拆开的路径：");
		for(int i=0;i<num_point;i++){
			System.out.print(truepath_vstan[i]+" ");
		}
		*/

    }



    public static void getRealPath()
	/*
	 * input : 满足条件的路径，得到的路径中城市间时可达的
	 * 得到真实路径，输入：V'（StandDistance）的index 组成的路径，扩展转换为真实路线
	 */
    {
        int n = truepath_vstan.length;
        int[] vrTruePath = new int[n];
        //test

        //System.out.print("VrTruePath:");
        for(int i=0;i<n;i++){
            vrTruePath[i] = RouteProblem.instance.StandmapPoint.get(truepath_vstan[i]);
            //		System.out.print(vrTruePath[i]+" ");
            //System.out.print(RouteProblem.instance.Map_of_vindex.get((vrTruePath[i]))+" ");
        }

		/* 由 V' 中索引组成的路径，扩展为真实路线  */
        int end,start;
        List<Integer> indexPath = new ArrayList<Integer>();
        for(int i=n-1;i>0;i--){
            end = vrTruePath[i];
            start = vrTruePath[i-1];
            //indexPath.add(end);
            //System.out.println("路路径扩展："+i);
            while(RouteProblem.instance.Prev[start][end]!=-1 && RouteProblem.instance.Prev[start][end]!=start){
                //	System.out.print(end);
                indexPath.add(end);
                end = RouteProblem.instance.Prev[start][end];
            }
            if(RouteProblem.instance.Prev[start][end]==start)
                indexPath.add(end);
        }
        indexPath.add(vrTruePath[0]);
        // test
        //System.out.println();
        //System.out.println("indexPath:"+indexPath);
        //真实路径
        truePath = new ArrayList<Integer>();
        int trueNum;
        for(int j=indexPath.size()-1;j>=0;j--){
            //trueNum = RouteProblem.instance.Map_of_vindex.get(indexPath.get(j));

			/* 基于原始的索引 */
            trueNum = indexPath.get(j);
            truePath.add(trueNum);
        }

        //   最终结果
        //System.out.println("最终结果："+truePath);
    }

    static void getRealTour_avoidhuan()
	/*
	 * input: 扩展后的路径 truePath
	 * output: 如果可以将输入路径调整无环则输出调整结果，如果无法调整则表示无解
	 */
    {
        appenum_ofcity = new HashMap<Integer,Integer>();

		/* 遍历 扩展路径 truepath,记录路径中城市出现的次数  */
        appenum_ofcity.put(truePath.get(0), 1);

        for(int i=1;i<truePath.size();i++){
            int city_num  = truePath.get(i);
            Set<Integer> keys = appenum_ofcity.keySet();

			/* 如果keys中没有该城市 */
            if(!keys.contains(city_num)){
                appenum_ofcity.put(city_num, 1);
            }else{
				/* 存在该城市，将城市的出现次数 +1 */
                int temp_appenum = appenum_ofcity.get(city_num);
                temp_appenum += 1;
                appenum_ofcity.put(city_num, temp_appenum);
            }
        }

		/* 调整得到最终的路径  */
        int num_truepath = truePath.size();
        Set<Integer> keys = appenum_ofcity.keySet();
        int num_appecity = keys.size();

		/* 判断 appenum_ofcity 中城市的出现次数是否有 > 1的 */
        if(num_truepath==num_appecity){
			/* 没有重复出现的城市 */
            path_end = truePath;
        }else{
			/* 进行环的调整  */
            path_end.add(truePath.get(0));

            for(int i=1;i<num_truepath-1;i++){

				/* 每次循环后 都可能不一样 */
                keys = appenum_ofcity.keySet();
                int index_city = truePath.get(i);
                int tempnum_appe = appenum_ofcity.get(index_city);

                if(tempnum_appe>1){
					/* 出现多次调整  */
                    int city_start = truePath.get(i-1);
                    int city_end = truePath.get(i+1);

                    List<Integer> temp_path;
					/* 如何可以调整就返回路径，否则返回 null */
                    temp_path = RouteProblem.adjustpath_bydij(city_start,city_end,keys);

                    if(temp_path!=null){
                        temp_path = temp_path.subList(1, temp_path.size()-1);
                        path_end.addAll(temp_path);

						/* 更新appenum_ofcity */
                        for(int jj=0;jj<temp_path.size();jj++){
                            int temp_jj = temp_path.get(jj);

                            if(!keys.contains(temp_jj))
                                appenum_ofcity.put(temp_jj, 1);
                        }
						/*调整后 数量  -1 */
                        appenum_ofcity.put(index_city, tempnum_appe-1);
                    }
                }else{
                    path_end.add(truePath.get(i));
                }

            }

			/* 环调整完毕，检查如何,各个的调整是否成功 */
            keys = appenum_ofcity.keySet();
            Iterator<Integer> iter_keys =keys.iterator();
            while(iter_keys.hasNext()){
                int temp_city_key = iter_keys.next();
                if(appenum_ofcity.get(temp_city_key)>1){
					/* path_end 为 null  时，表示没有解  */
                    path_end = null;
                    return;
                }
            }
            path_end.add(truePath.get(num_truepath-1));
        }

        //   最终结果
        System.out.println("调整环后的  最终结果："+path_end);
    }


    public static void main(String[] args){
        int[] path_xu = {11,20,10,12,14,19,1,2,8,3,0,16,7,17,9,15,6,4,5,18,13,11};
        get_Vproute_fromTspRoute(path_xu);
        //getRealPath();
    }
}
