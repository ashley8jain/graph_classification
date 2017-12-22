package ctree.toolbox;

import java.util.*;
import java.io.*;


import ctree.graph.Graph;
import ctree.index.CTree;
import ctree.index.GraphFactory;
import ctree.index.GraphSim;
import ctree.index.Util;
import ctree.lgraph.LGraph;
import ctree.lgraph.LGraphFactory;
import ctree.lgraph.LGraphFile;
import ctree.lgraph.LGraphSim;
import ctree.lgraph.LGraphWeightMatrix;
import ctree.lgraph.LabelMap;
import ctree.mapper.GraphMapper;
import ctree.mapper.NeighborBiasedMapper;
import ctree.tool.BuildCTree;
import ctree.tool.SubQuery;
import ctree.util.DataSum;
import ctree.util.Opt;

public class subgraphFrequencyCounter {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	
	public static int count(LGraph[] db,LGraph query)
	{
		Vector<Graph> answers = new Vector<Graph>();
        int ans_size = 0;
        for (LGraph g : db) {
            if (Util.subIsomorphic(query, g)) {
                ans_size++;
                answers.add(g);
            }
        }
        return answers.size();
	}

	public static void main(String[] args) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		// TODO Auto-generated method stub
	        Opt opt = new Opt(args);
	        if (opt.args() < 2) {
	            SubQuery.usage();
	            return;
	        }
	        // LGraph[] graphs = LGraphFile.loadLGraphs(opt.getArg(0));
	        // LGraph[] queries = LGraphFile.loadLGraphs(opt.getArg(1));

	        LGraph[] train_graphs = LGraphFile.loadLGraphs(opt.getArg(0));
	        LGraph[] test_graphs = LGraphFile.loadLGraphs(opt.getArg(1));
	        LGraph[] freq_subgraphs = LGraphFile.loadLGraphs(opt.getArg(2));

	        String ca_file = opt.getArg(3);
	        String ci_file = opt.getArg(4);


	        HashMap hm = new HashMap();

	        BufferedReader br = null;
			FileReader fr = null;

			int label=1;

			int one_class = 0;
			int other_class = 0;

			try {

				fr = new FileReader(ca_file);
				br = new BufferedReader(fr);

				String sCurrentLine;

				while ((sCurrentLine = br.readLine()) != null) {
					hm.put(sCurrentLine,label);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (br != null)
						br.close();
					if (fr != null)
						fr.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}


			label=-1;

			try {

				fr = new FileReader(ci_file);
				br = new BufferedReader(fr);

				String sCurrentLine;

				while ((sCurrentLine = br.readLine()) != null) {
					hm.put(sCurrentLine,label);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (br != null)
						br.close();
					if (fr != null)
						fr.close();
				} catch (IOException ex){
					ex.printStackTrace();
				}
			}



			float [][] freq = new float[freq_subgraphs.length][4];

	        for(int i=0;i<train_graphs.length;i++){

	        	LGraph graph = train_graphs[i];
	        	String graph_ID = graph.getId();

	        	if(hm.containsKey(graph_ID)){

	        		if((int)hm.get(graph_ID)==1){
		        		one_class++;
		        	}
		        	else{
		        		other_class++;
		        	}
	        		
		        	for(int j=0;j<freq_subgraphs.length;j++){
		        		if(Util.subIsomorphic(freq_subgraphs[j],graph)){
		        			if((int)hm.get(graph_ID)==1){
		        				freq[j][0]++;
		        			}
		        			else{
		        				freq[j][1]++;
		        			}
		        		}
		        		else{
		        			if((int)hm.get(graph_ID)==1){
		        				freq[j][2]++;
		        			}
		        			else{
		        				freq[j][3]++;
		        			}
		        		}
		        	}
	        	}
	        }



	        //selecting discriminative frequent subgraph
	        Vector<LGraph> disc_freq_subg = new Vector();

	        int disc_count=0;
	        int condition_true=0;

	        // System.out.println(one_class+"  , "+other_class+"\n");
	        for(int i=0;i<freq_subgraphs.length;i++){
	        	
	        	freq[i][0]/=one_class;
	        	freq[i][1]/=other_class;
	        	freq[i][2]/=one_class;
	        	freq[i][3]/=other_class;

	        	
	        	

	        	if((freq[i][0]<=0.6 && freq[i][1]>=0.4) || (freq[i][1]<=0.6 && freq[i][0]>=0.4)){
	        		// condition_true++;
	        		// if(Math.abs(freq[i][0]-freq[i][3])<=0.4){
	        		
	        		disc_count++;
	        		disc_freq_subg.addElement(freq_subgraphs[i]);
	        		// System.out.println(freq[i][0]+" , "+freq[i][1]);
	        		// System.out.println(freq[i][2]+" , "+freq[i][3]+"\n\n\n");
	        	}
	        	
	       	}
	       	System.out.println("no. of disc:"+disc_count);
	       	// System.out.println("no. of cond true:"+condition_true);

	       	if(disc_count<3){
	       		disc_freq_subg = new Vector();
	       		for(int i=0;i<freq_subgraphs.length;i++){
	       			disc_freq_subg.addElement(freq_subgraphs[i]);
	       		}
	       	}


	       	//train.txt
	       	PrintWriter writer = new PrintWriter("train.txt", "UTF-8");			

	        for(int i=0;i<train_graphs.length;i++){

	        	LGraph graph = train_graphs[i];
	        	String graph_ID = graph.getId();
	        	
	        	if(hm.containsKey(graph_ID)){
	        		
	        		writer.print(hm.get(graph_ID)+" ");
		        	for(int j=0;j<disc_freq_subg.size();j++){
		        		if(Util.subIsomorphic(disc_freq_subg.elementAt(j),graph))
		        			writer.print((j+1)+":1 ");
		        		// else 
		        		// 	writer.print((j+1)+":0 ");
		        		
		        	}
		        	writer.print("\n");
	        	}
	        }
	        writer.close();




	        //test.txt
	        writer = new PrintWriter("test.txt", "UTF-8");			

	        for(int i=0;i<test_graphs.length;i++){
	        	for(int j=0;j<disc_freq_subg.size();j++){
	        		if(Util.subIsomorphic(disc_freq_subg.elementAt(j),test_graphs[i]))
	        			writer.print((j+1)+":1 ");
	        		// else 
		        	// 	writer.print((j+1)+":0 ");
	        		
	        	}
	        	writer.print("\n");
	        }
	        writer.close();

	        /*
	        SubQuery sq=new SubQuery();
	        System.err.println("Load ctree " + opt.getArg(0));
	        GraphMapper mapper=new NeighborBiasedMapper(new LGraphWeightMatrix());
	        LabelMap labelMap = new LabelMap(graphs);

	        // Dimensions for summarizing graphs
	        int dim1 = opt.getInt("dim1", 97);
	        int dim2 = opt.getInt("dim2", 97);

	        int L = labelMap.size();
	        if (dim1 > L) {
	            dim1 = L;
	        }
	        if (dim2 > L * L) {
	            dim2 = L * L;
	        }

	        GraphFactory factory = new LGraphFactory(labelMap, dim1, dim2);
	        GraphSim graphSim = new LGraphSim();

	        long time0 = System.currentTimeMillis();
	        System.err.println("Build ctree");
	        CTree ctree = BuildCTree.buildCTree(graphs, 20,39, mapper, graphSim, labelMap,
	                                 factory);

	        System.err.printf("Max depth = %d, Min depth = %d\n", ctree.maxDepth(),
	                          ctree.minDepth());
	        long time = System.currentTimeMillis() - time0;

	        System.out.println("Build time: " + time / 1000.0);


	        int nQ = opt.getInt("nQ", queries.length);
	        boolean usingHist = opt.getString("hist", "yes").equals("yes");

	        int pseudo_level = opt.getInt("pseudo", 1);

	        

			*/
	        // System.err.println("Query");
	        // for (int i = 0; i < queries.length; i++)
	        // {

	            // NN ranking for distance==0
	           

	            //Vector cand = isomQueryByRanker(ctree, metric, queries[i]);
	            /*Vector<Graph>
	                    cand = sq.subgraphQuery(ctree, queries[i], pseudo_level,
	                                         usingHist);

	            long time1 = System.currentTimeMillis() - time0;
	             */
				
	            // check isomorphism

	            //long time2 = System.currentTimeMillis() - time0;
	            // System.out.println(subgraphFrequencyCounter.count(graphs,queries[i]));
	        // } // end of query

	    
	}

}
