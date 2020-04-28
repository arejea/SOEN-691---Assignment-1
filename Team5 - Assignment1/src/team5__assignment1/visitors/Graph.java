
	package team5__assignment1.visitors;
	import java.util.*;

	public class Graph<T> {
		

		private Map<T, List<T>> map = new HashMap<>();

	    public void addVertex(T s) {
	        map.put(s, new LinkedList<T>());
	    }

	    public void addEdge(T source,
	                        T destination) {

	        if (!map.containsKey(source))
	            addVertex(source);

	        if (!map.containsKey(destination))
	            addVertex(destination);

	        map.get(source).add(destination);

	    }

	    public void getVertexCount() {
	        System.out.println("The graph has "
	                + map.keySet().size()
	                + " vertex");
	    }


	    public boolean hasVertex(T s) {
	        if (map.containsKey(s)) {
	           
	            return true;
	        } else {
	           
	            return false;
	        }
	    }

	    public void hasEdge(T s, T d) {
	        if (map.get(s).contains(d)) {
	            System.out.println("The graph has an edge between "
	                    + s + " and " + d + ".");
	        } else {
	            System.out.println("The graph has no edge between "
	                    + s + " and " + d + ".");
	        }
	    }

	    @Override
	    public String toString() {
	        StringBuilder builder = new StringBuilder();

	        for (T v : map.keySet()) {
	            builder.append(v.toString() + ": ");
	            for (T w : map.get(v)) {
	                builder.append(w.toString() + " ");
	            }
	            builder.append("\n");
	        }

	        return (builder.toString());
	    }

	    public Boolean isReachable(T s, T d)
	    {
	        LinkedList<T>temp;

	        Map<T, Boolean> visited = new HashMap<>();
	        map.forEach((t, ts) -> {
	       // 	System.out.println("here checking for each invoked in "+ t+" for "+ts );
	            visited.put(t, false);
	        });

	        LinkedList<T> queue = new LinkedList<>();
	        
	        visited.put(s, true);
	        
	        queue.add(s);

	        Iterator<T> i;
	        while (queue.size()!=0)
	        {
	        	
	            s = queue.poll();
	            T n;
	            i = map.get(s).iterator();

	            while (i.hasNext())
	            {
	                n = i.next();
	                
	                if (n==d) {
	                	return true;
	                }
	            	
	                    

	                if (!visited.get(n))
	                {
	                    visited.put(n, true);
	                    queue.add(n);
	                    
	                }
	            }
	        }

	        return false;
	    }
}


